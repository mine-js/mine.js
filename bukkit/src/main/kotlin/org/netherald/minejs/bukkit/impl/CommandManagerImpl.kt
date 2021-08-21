package org.netherald.minejs.bukkit.impl

import org.bukkit.Bukkit
import org.bukkit.command.CommandMap
import org.bukkit.command.defaults.BukkitCommand
import org.bukkit.plugin.java.JavaPlugin
import org.netherald.minejs.bukkit.impl.command.CustomCommand
import org.netherald.minejs.common.Command
import org.netherald.minejs.common.CommandManager

class CommandManagerImpl(val plugin: JavaPlugin) : CommandManager {

    val arrayCommandsBukkit = ArrayList<BukkitCommand>()

    override fun registerCommands(arr: ArrayList<Command>) {
        for(command in arr) {
            val bukkitCommandMap = Bukkit.getServer().javaClass.getDeclaredField("commandMap")
            bukkitCommandMap.isAccessible = true
            val commandMap = bukkitCommandMap.get(Bukkit.getServer()) as CommandMap
            val bukkitCommand = CustomCommand(command)
            arrayCommandsBukkit.add(bukkitCommand)
            commandMap.register(command.name, bukkitCommand)
        }
    }

    override fun unloadCommands() {
        for(bukkitCommand in arrayCommandsBukkit) {
            val bukkitCommandMap = Bukkit.getServer().javaClass.getDeclaredField("commandMap")
            bukkitCommandMap.isAccessible = true
            val commandMap = bukkitCommandMap.get(Bukkit.getServer()) as CommandMap
            bukkitCommand.unregister(commandMap)
        }
    }
}