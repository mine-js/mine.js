package org.netherald.minejs.bukkit.command

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.plugin.java.JavaPlugin
import org.netherald.minejs.bukkit.MineJsBukkit
import org.netherald.minejs.bukkit.impl.CommandManagerImpl
import org.netherald.minejs.bukkit.impl.ConsoleImpl
import org.netherald.minejs.bukkit.impl.ItemManagerImpl
import org.netherald.minejs.bukkit.impl.PlayerManagerImpl
import org.netherald.minejs.common.Platform
import org.netherald.minejs.common.ScriptLoader
import java.io.File

class MineJSCommand(private val plugin: MineJsBukkit) : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        if (args.isEmpty()) {
            sendErrorMessage(sender)
            return true
        }
        if (args[0] == "reload") {
            sender.sendMessage(Component.text("Loading scripts...", NamedTextColor.YELLOW))
            ScriptLoader.unload()
            JavaPlugin.getPlugin(MineJsBukkit::class.java).load()
            sender.sendMessage(Component.text("Complete loading scripts", NamedTextColor.GREEN))

            return true
        }
        sendErrorMessage(sender)
        return true;
    }

    private fun sendErrorMessage(sender: CommandSender) {
        sender.sendMessage("${ChatColor.RED}Syntax: /minejs [reload]")
    }
}
