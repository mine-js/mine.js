package org.netherald.minejs.bukkit.command

import net.kyori.adventure.text.Component
import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.netherald.minejs.bukkit.MineJsBukkit
import org.netherald.minejs.bukkit.impl.ConsoleImpl
import org.netherald.minejs.bukkit.impl.PlayerManagerImpl
import org.netherald.minejs.common.Platform
import org.netherald.minejs.common.ScriptLoader

class MineJSCommand(private val plugin: MineJsBukkit) : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        if (args[0] == "reload") {
            sender.sendMessage(Component.text("${ChatColor.RED}Loading scripts..."))
            ScriptLoader.unload()
            ScriptLoader.load(plugin.scriptsDir, Platform.BUKKIT, PlayerManagerImpl(), ConsoleImpl(plugin))
            sender.sendMessage(Component.text("${ChatColor.GREEN}Complete loading scripts"))

            return true
        }
        return false
    }
}