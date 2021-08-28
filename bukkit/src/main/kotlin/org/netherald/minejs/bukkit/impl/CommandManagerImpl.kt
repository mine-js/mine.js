package org.netherald.minejs.bukkit.impl

import org.bukkit.Bukkit
import org.bukkit.command.CommandMap
import org.bukkit.command.SimpleCommandMap
import org.bukkit.command.defaults.BukkitCommand
import org.bukkit.plugin.java.JavaPlugin
import org.netherald.minejs.bukkit.impl.command.CustomCommand
import org.netherald.minejs.common.Command
import org.netherald.minejs.common.CommandManager
import java.lang.reflect.Field
import java.util.HashMap


class CommandManagerImpl(val plugin: JavaPlugin) : CommandManager {

    private val arrayCommandsBukkit = ArrayList<BukkitCommand>()
    lateinit var commandMap: SimpleCommandMap
    //private lateinit var overriden: org.bukkit.command.Command

    override fun registerCommands(arr: ArrayList<Command>) {
        for(command in arr) {
            commandMap = Bukkit.getCommandMap() as SimpleCommandMap
            val bukkitCommand = CustomCommand(command)
            val f: Field = SimpleCommandMap::class.java.getDeclaredField("knownCommands")
            f.isAccessible = true
            val commandsBukkit = f.get(commandMap) as HashMap<String, org.bukkit.command.Command>
            //overriden = commandsBukkit.put(command.name, bukkitCommand)!!
            commandsBukkit[command.name] = bukkitCommand
            arrayCommandsBukkit.add(bukkitCommand)
            commandsBukkit.put(bukkitCommand.name, bukkitCommand)
            commandMap.register("minecraft", bukkitCommand)
        }
    }

    override fun unloadCommands() {
        for(bukkitCommand in arrayCommandsBukkit) {
            /*
            val bukkitCommandMap = Bukkit.getServer().javaClass.getDeclaredField("commandMap")
            bukkitCommandMap.isAccessible = true
            val commandMap = bukkitCommandMap.get(Bukkit.getServer()) as CommandMap
            bukkitCommand.unregister(commandMap)
             */
            unRegisterBukkitCommand(bukkitCommand)
        }
        arrayCommandsBukkit.clear()
    }

    private fun unRegisterBukkitCommand(cmd: org.bukkit.command.Command) {
        try {
            val f: Field = SimpleCommandMap::class.java.getDeclaredField("knownCommands")
            f.isAccessible = true
            val commands = f.get(commandMap) as HashMap<*, *>
            cmd.unregister(commandMap)
            commands.remove(cmd.name)
            commands.remove("minecraft:${cmd.name}")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}