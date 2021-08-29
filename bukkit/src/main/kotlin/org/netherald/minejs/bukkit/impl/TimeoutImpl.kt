package org.netherald.minejs.bukkit.impl

import com.eclipsesource.v8.V8
import com.eclipsesource.v8.V8Array
import com.eclipsesource.v8.V8Function
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import org.netherald.minejs.common.Timeout

class TimeoutImpl(val plugin: JavaPlugin) : Timeout {

    override fun setTimeout(runtime: V8, function: V8Function, delay: Int): Int = Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, { function.call(runtime, V8Array(runtime)) }, delay.toLong())

    override fun setInterval(runtime: V8, function: V8Function, interval: Int): Int = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, { function.call(runtime, V8Array(runtime)) }, 0, interval.toLong())

    override fun clearInterval(runtime: V8, id: Int) {
        Bukkit.getScheduler().cancelTask(id)
    }

}