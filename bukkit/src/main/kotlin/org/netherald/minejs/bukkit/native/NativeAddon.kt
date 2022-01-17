package org.netherald.minejs.bukkit.native

import com.eclipsesource.v8.JavaCallback
import com.eclipsesource.v8.JavaVoidCallback
import org.bukkit.Bukkit
import org.bukkit.event.Listener
import org.netherald.minejs.bukkit.MineJsBukkit
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

abstract class NativeAddon {

    val listeners = ArrayList<Listener>()
    val voidFunctions = ConcurrentHashMap<String, JavaVoidCallback>()
    val functions = ConcurrentHashMap<String, JavaCallback>()

    abstract fun init()

    fun registerFunction(name: String, callback: JavaVoidCallback) {
        voidFunctions[name] = callback
    }

    fun registerFunction(name: String, callback: JavaCallback) {
        functions[name] = callback
    }

    fun registerListener(listener: Listener) {
        Bukkit.getPluginManager().registerEvents(listener, MineJsBukkit.instance)
        listeners.add(listener)
    }
}