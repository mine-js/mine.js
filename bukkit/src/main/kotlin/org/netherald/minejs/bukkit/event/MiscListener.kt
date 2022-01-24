package org.netherald.minejs.bukkit.event

import com.destroystokyo.paper.event.server.PaperServerListPingEvent
import com.eclipsesource.v8.JavaCallback
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin
import org.netherald.minejs.bukkit.MineJsBukkit
import org.netherald.minejs.bukkit.utils.GetterSetter
import org.netherald.minejs.bukkit.utils.ObjectUtils
import org.netherald.minejs.bukkit.utils.getOrNull
import org.netherald.minejs.common.ScriptLoader

class MiscListener : Listener {
    @EventHandler
    fun ping(event: PaperServerListPingEvent) {
        Bukkit.getScheduler().runTask(MineJsBukkit.instance, Runnable {
            ScriptLoader.invokeEvent("onServerListPing") {
                GetterSetter<Int>("numPlayers") {
                    getter {
                        event.numPlayers
                    }

                    setter {
                        event.numPlayers = it
                    }
                }

                GetterSetter<Int>("maxPlayers") {
                    getter {
                        event.maxPlayers
                    }

                    setter {
                        event.maxPlayers = it
                    }
                }

                registerJavaMethod(JavaCallback { _, _ ->
                    event.setHidePlayers(!event.shouldHidePlayers())
                }, "hidePlayer")

                registerCancellable(event)

            }
        })
    }

}