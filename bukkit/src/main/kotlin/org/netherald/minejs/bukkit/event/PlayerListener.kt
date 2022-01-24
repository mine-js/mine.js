package org.netherald.minejs.bukkit.event

import com.eclipsesource.v8.V8Object
import io.papermc.paper.event.player.AsyncChatEvent
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.FoodLevelChangeEvent
import org.bukkit.event.player.*
import org.netherald.minejs.bukkit.MineJsBukkit
import org.netherald.minejs.bukkit.utils.*
import org.netherald.minejs.common.ScriptLoader

class PlayerListener : Listener {
    /*
    @EventHandler
    fun playerMove(event: PlayerMoveEvent) {
        ScriptLoader.invokeEvent("onPlayerMove") {
            add("from", V8Object(runtime).apply(ObjectUtils.createLocationObject(event.from, runtime)))
            add("to", V8Object(runtime).apply(ObjectUtils.createLocationObject(event.to, runtime)))
            add("player", V8Object(runtime).apply(ObjectUtils.createPlayerObject(event.player, runtime, true)))
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
                event.isCancelled = true
            }, "cancel")
        }
    }
     */

    @EventHandler
    fun playerJoin(event: PlayerJoinEvent) {
        ScriptLoader.invokeEvent("onPlayerJoin") {
            GetterSetter<String>("joinMessage") {
                getter {
                    MessageUtils.toMiniMessage(event.joinMessage()!!)
                }

                setter {
                    event.joinMessage(MessageUtils.build(it))
                }
            }
            registerPlayer(event.player)
        }
    }

    @EventHandler
    fun worldChange(event: PlayerChangedWorldEvent) {
        ScriptLoader.invokeEvent("onPlayerWorldChange") {
            add("from", V8Object(runtime).apply(ObjectUtils.createWorldObject(event.from, runtime)))
            registerPlayer(event.player)
        }
    }

    @EventHandler
    fun onPlayerFoodLevelChange(event: FoodLevelChangeEvent) {
        ScriptLoader.invokeEvent("onFoodLevelChange") {
            GetterSetter<Int>("foodLevel") {
                getter {
                    event.foodLevel
                }

                setter {
                    event.foodLevel = it
                }
            }
            registerPlayer(Bukkit.getPlayer(event.entity.name)!!)
            registerCancellable(event)
        }
    }

    @EventHandler
    fun playerQuit(event: PlayerQuitEvent) {
        ScriptLoader.invokeEvent("onPlayerQuit") {
            GetterSetter<String>("quitMessage") {
                getter {
                    MessageUtils.toMiniMessage(event.quitMessage()!!)
                }

                setter {
                    event.quitMessage(MessageUtils.build(it))
                }
            }
            registerPlayer(event.player)
        }
    }

    @EventHandler
    fun asyncChat(event: AsyncChatEvent) {
        Bukkit.getScheduler().runTask(MineJsBukkit.instance, Runnable {
            ScriptLoader.invokeEvent("onPlayerChat") {
                GetterSetter<String>("message") {
                    getter {
                        MessageUtils.toMiniMessage(event.message())
                    }

                    setter {
                        event.message(MessageUtils.build(it))
                    }
                }

                registerPlayer(event.player)
                registerCancellable(event)
            }
        })
    }

    @EventHandler
    fun playerInteract(event: PlayerInteractEvent) {
        ScriptLoader.invokeEvent("onPlayerInteract") {
            add("action", event.action.name)
            addNullable("clickedBlock", event.clickedBlock?.let { V8Object(runtime).apply(ObjectUtils.createBlockObject(it, runtime)) })
            registerPlayer(event.player)
            registerCancellable(event)
        }

        ScriptLoader.invokeEvent("onClick") {
            add("clickSide", if(event.action.toString().contains("RIGHT")) "right" else "left")
            add("isAir", event.action.toString().contains("AIR"))
            addNullable("clickedBlock", event.clickedBlock?.let { V8Object(runtime).apply(ObjectUtils.createBlockObject(it, runtime)) })
            registerPlayer(event.player)
            registerCancellable(event)
        }
    }
}
