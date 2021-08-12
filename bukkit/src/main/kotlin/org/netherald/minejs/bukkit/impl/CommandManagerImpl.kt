package org.netherald.minejs.bukkit.impl

import org.bukkit.plugin.java.JavaPlugin
import org.netherald.minejs.common.Command
import org.netherald.minejs.common.CommandManager

class CommandManagerImpl(val plugin: JavaPlugin) : CommandManager {
    override fun registerCommands(arr: ArrayList<Command>) {
        TODO("Register command without plugin.yml (Refer skript source!)")
    }

    override fun unloadCommands() {
        TODO("Unload commands!")
    }
}