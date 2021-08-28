package org.netherald.minejs.bukkit.impl.command

import com.eclipsesource.v8.V8Array
import com.eclipsesource.v8.V8Function
import com.eclipsesource.v8.V8Object
import org.bukkit.command.CommandSender
import org.bukkit.command.defaults.BukkitCommand
import org.netherald.minejs.bukkit.utils.ObjectUtils
import org.netherald.minejs.common.Command

class CustomCommand(val command: Command) : BukkitCommand(command.name) {

    init {
        setAliases(aliases)
    }

    override fun execute(sender: CommandSender, commandLabel: String, args: Array<out String>): Boolean {
        val argsV8 = V8Array(command.runtime)
        for(arg in args) {
            argsV8.push(arg)
        }
        command.callback.call(command.runtime, V8Array(command.runtime).push(argsV8).push(V8Object(command.runtime).apply(ObjectUtils.createCommandSenderObject(sender, command.runtime))))
        return true
    }
}