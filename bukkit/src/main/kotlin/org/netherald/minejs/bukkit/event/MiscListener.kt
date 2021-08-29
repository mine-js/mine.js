package org.netherald.minejs.bukkit.event

import com.destroystokyo.paper.event.server.PaperServerListPingEvent
import com.eclipsesource.v8.JavaCallback
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin
import org.netherald.minejs.bukkit.utils.ObjectUtils
import org.netherald.minejs.common.ScriptLoader

class MiscListener(val plugin: JavaPlugin) : Listener {

    @EventHandler
    fun ping(event: PaperServerListPingEvent) {
        Bukkit.getScheduler().runTask(plugin, Runnable {
            ScriptLoader.invokeEvent("onServerListPing") {
                registerJavaMethod({ receiver, arguments ->
                    if (arguments.length() > 0) {
                        if (arguments[0] == true) {
                            event.isCancelled = true;
                        } else if (arguments[0] == false) {
                            event.isCancelled = false;
                        }
                    }
                }, "setCancelled")
                registerJavaMethod({ receiver, arguments ->
                    if (arguments.length() > 0)
                        event.numPlayers = arguments[0] as Int
                }, "setPlayers")
                registerJavaMethod(JavaCallback { receiver, arguments ->
                    return@JavaCallback event.numPlayers
                }, "players")
                registerJavaMethod({ receiver, arguments ->
                    if (arguments.length() > 0)
                        event.maxPlayers = arguments[0] as Int
                }, "setMaxPlayers")
                registerJavaMethod(JavaCallback { receiver, arguments ->
                    return@JavaCallback event.maxPlayers
                }, "maxPlayers")
                registerJavaMethod(JavaCallback { receiver, arguments ->
                    if(event.shouldHidePlayers())
                        event.setHidePlayers(false)
                    else
                        event.setHidePlayers(true)
                }, "hidePlayer")
            }
        })
    }

}