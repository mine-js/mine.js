package org.netherald.minejs.bukkit.command

import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.netherald.minejs.bukkit.MineJsBukkit

class MineJSTabCompleter : TabCompleter {
    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        alias: String,
        args: Array<out String>
    ): List<String> {
        val list: MutableList<String> = ArrayList()
        if (args.isEmpty()) {
            list.add("reload")
        } else if (args[0] == "reload") {
            list.clear()
        }
        return list
    }
}