package org.netherald.minejs.bukkit.native

import com.google.gson.JsonParser
import org.bukkit.Bukkit
import org.bukkit.event.HandlerList
import org.netherald.minejs.bukkit.MineJsBukkit
import java.io.File
import java.io.FilenameFilter
import java.net.URL
import java.net.URLClassLoader
import java.util.jar.JarFile

object NativeLoader {

    val addons = HashMap<String, NativeAddon>()
    val loaders = HashMap<String, URLClassLoader>()

    fun enableAll(directory: File) {
        if(!directory.exists())
            directory.mkdir()

        val files = directory.listFiles { _, name -> name.endsWith(".jar") }

        if(files != null) {
            for(file in files) {
                println("Loading native addon ${file.name}...")
                enable(file)
            }
        }
    }

    fun disableAll() {
        val it = addons.iterator()

        while(it.hasNext()) {
            val entry = it.next()
            val name = entry.key
            val loader = loaders[name]!!
            val addon = entry.value
            for (listener in addon.listeners) {
                HandlerList.unregisterAll(listener)
            }
            loader.close()

            loaders.remove(name)
            it.remove()
        }
    }

    fun enable(jar: File) {
        val loader = URLClassLoader(arrayOf(jar.toURI().toURL()), javaClass.classLoader)
        val descriptionStream = loader.getResourceAsStream("addon.json")
        val description = JsonParser.parseReader(descriptionStream.reader()).asJsonObject

        val name = description.get("name").asString
        val main = description.get("main").asString

        if(description.has("depends")) {
            val depends = description.get("depends").asJsonArray
            for (depend in depends) {
                if(Bukkit.getPluginManager().getPlugin(depend.asString) == null) {
                    MineJsBukkit.instance.logger.warning("Native Addon Dependency Plugin ${depend.asString} is invalid!")
                } else {
                    println("Loaded dependency ${depend.asString}")
                }
            }
        }

        val mainClazz = loader.loadClass(main)
        val instance = mainClazz.constructors[0].newInstance() as NativeAddon
        val method = mainClazz.getDeclaredMethod("init")
        method.invoke(instance)

        addons[name] = instance
        loaders[name] = loader

        println("Enabled addon $name!")
    }
}