package org.netherald.minejs.bukkit.impl

import org.netherald.minejs.bukkit.MineJsBukkit
import org.netherald.minejs.common.interfaces.JavaManager
import java.io.File
import java.net.URLClassLoader

class JavaManagerImpl : JavaManager {

    private val externalLoaders = ArrayList<URLClassLoader>()

    init {
        val libraries = File(MineJsBukkit.instance.dataFolder, "libraries")
        if(!libraries.exists())
            libraries.mkdir()

        val files = libraries.listFiles()

        if(files != null) {
            for (file in files) {
                externalLoaders.add(
                    URLClassLoader(arrayOf(file.toURI().toURL()), this.javaClass.classLoader)
                )
            }
        }
    }

    fun getClass(import: String): Class<*> {
        for(loader in externalLoaders) {
            try {
                Class.forName(import, true, loader)
            } catch (_: ClassNotFoundException) {}
        }

        throw ClassNotFoundException("Class not found!")
    }

    override fun getImports(filename: String): ArrayList<Class<*>> {
        val config = MineJsBukkit.instance.config
        val section = config.getConfigurationSection("import") ?: config.createSection("import")
        val imports = section.getStringList(filename)
        val result = ArrayList<Class<*>>()
        for (import in imports) {
            result.add(try {
                Class.forName(import)
            } catch (ex: ClassNotFoundException) {
                getClass(import)
            })
        }

        return result
    }

    override fun unload() {
        for (loader in externalLoaders) {
            loader.close()
        }

        externalLoaders.clear()
    }

}
