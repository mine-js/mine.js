package org.netherald.minejs.cli

import com.eclipsesource.v8.V8
import com.eclipsesource.v8.V8Array
import com.eclipsesource.v8.V8Object
import org.netherald.minejs.common.*
import java.io.File
import java.lang.UnsupportedOperationException
import java.util.*
import kotlin.collections.ArrayList

lateinit var commands: ArrayList<Command>

fun main() {
    println("Loading scripts(${File(System.getProperty("user.dir") + File.separator + "scripts").absolutePath})...")
    ScriptLoader.load(File(System.getProperty("user.dir") + File.separator + "scripts"), File(System.getProperty("user.dir") + File.separator + "scripts${File.separator}storage.json"), Platform.CLI, PlayerManagerImpl(),ItemManagerImpl() ,ConsoleImpl(), CommandManagerImpl())
    var exited = false
    val scanner = Scanner(System.`in`)

    while(!exited) {
        val read = scanner.nextLine()
        if(read.startsWith("/")) {
            for (item in commands) {
                val args = read.split(" ").toTypedArray().copyOfRange(1, read.split(" ").size)
                val argsV8 = V8Array(item.runtime)
                for(arg in args) {
                    argsV8.push(arg)
                }
                val commandName = read.split(" ")[0]
                println("CommandName: $commandName, ItemName: /${item.name}")
                if("/${item.name}" == commandName || !item.alias.filter { str -> "/$str" == commandName }.isNullOrEmpty()) {
                    val param = V8Object(item.runtime)
                    param.run {
                        add("sender", createObjectForPlayer(Player("netherald"), runtime))
                        add("args", argsV8)
                    }
                    item.callback.call(item.runtime, V8Array(item.runtime).push(param))
                } else {
                    println("Invalid Command!")
                }
            }
            continue
        }
        if(read.startsWith("move_event")) {
            ScriptLoader.invokeEvent("onPlayerMove") {
                add("from", V8Object(runtime).run {
                    add("x", 1)
                    add("y", 1)
                    add("z", 1)
                })
                add("to", V8Object(runtime).run {
                    add("x", 2)
                    add("y", 2)
                    add("z", 2)
                })
                add("player", createObjectForPlayer(Player("netherald"), runtime))
                registerJavaMethod({ receiver, arguments ->
                    if(arguments.length() > 0) {
                        if(arguments[0] == true) {
                            println("Event Cancelled")
                        }
                    }
                }, "setCancelled")
            }
        } else if(read.startsWith("reload")) {
            ScriptLoader.unload()
            ScriptLoader.load(File(System.getProperty("user.dir") + File.separator + "scripts"), File(System.getProperty("user.dir") + File.separator + "scripts${File.separator}storage.json"), Platform.CLI, PlayerManagerImpl(),ItemManagerImpl() ,ConsoleImpl(), CommandManagerImpl())
        } else if(read.startsWith("stop")) {
            ScriptLoader.unload()
            exited = true
        }
    }
}

fun createObjectForPlayer(player: Player, runtime: V8) : V8Object {
    val res = V8Object(runtime)
    res.add("name", player.name)
    res.registerJavaMethod({ receiver, parameters ->
        if(parameters.length() > 0) {
            player.sendMessage(parameters[0] as String)
        }
    }, "send")
    return res
}

fun createObjectForItem(item : Item, runtime : V8) : V8Object {
    val res = V8Object(runtime)
    res.add("name",item.name)
    return res
}

class CommandManagerImpl : CommandManager {
    override fun registerCommands(arr: ArrayList<Command>) {
        commands = arr
    }

    override fun unloadCommands() {
        commands.clear()
    }

}

class ConsoleImpl : Console() {
    override fun log(content: Any, fileName: String) {
        var baked = content
        if(content is V8Object) {
            baked = ""
            for (key in content.keys) {
                baked += key
            }
        }
        println("[LOG] [$fileName] $baked")
    }
}

class PlayerManagerImpl : PlayerManager {
    override fun getPlayers(runtime: V8): V8Array {
        return V8Array(runtime).push(createObjectForPlayer(Player("test"), runtime))
    }

    override fun getPlayersOnServer(runtime: V8, server: String): V8Array {
        throw UnsupportedOperationException("This is not bungee!")
    }

    override fun playerOf(runtime: V8, name: String): V8Object {
        return createObjectForPlayer(Player("netherald"), runtime)
    }
}

class ItemManagerImpl : ItemManager {
    override fun itemOf(runtime: V8, material: String): V8Object {
        return V8Array(runtime).push(createObjectForItem(Item("netherald"),runtime))
    }
}