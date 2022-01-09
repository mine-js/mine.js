package org.netherald.minejs.bukkit.event

import com.eclipsesource.v8.V8Object
import io.papermc.paper.event.player.AsyncChatEvent
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextComponent
import net.kyori.adventure.text.TranslatableComponent
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.FoodLevelChangeEvent
import org.bukkit.event.player.*
import org.bukkit.plugin.Plugin
import org.netherald.minejs.bukkit.utils.MessageUtils
import org.netherald.minejs.bukkit.utils.ObjectUtils
import org.netherald.minejs.common.ScriptLoader

class PlayerListener(val plugin: Plugin) : Listener {
    @EventHandler
    fun playerMove(event: PlayerMoveEvent) {
        ScriptLoader.invokeEvent("onPlayerMove") {
            add("from", V8Object(runtime).apply(ObjectUtils.createLocationObject(event.from, runtime)))
            add("to", V8Object(runtime).apply(ObjectUtils.createLocationObject(event.to, runtime)))
            add("player", V8Object(runtime).apply(ObjectUtils.createPlayerObject(event.player, runtime)))
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

    @EventHandler
    fun playerJoin(event: PlayerJoinEvent) {
        ScriptLoader.invokeEvent("onPlayerJoin") {
            add("player", V8Object(runtime).apply(ObjectUtils.createPlayerObject(event.player, runtime)))
            add("joinMessage", MessageUtils.toMiniMessage(event.joinMessage()!!))
            registerJavaMethod({ receiver, arguments ->
                if (arguments.length() > 0) {
                    event.joinMessage(MessageUtils.build(arguments[0].toString()))
                }
                if (arguments.length() == 0) {
                    event.joinMessage(null)
                }
            }, "setJoinMessage")
        }
    }

    @EventHandler
    fun worldChange(event: PlayerChangedWorldEvent) {
        ScriptLoader.invokeEvent("onPlayerWorldChange") {
            add("player", V8Object(runtime).apply(ObjectUtils.createPlayerObject(event.player, runtime)))
            add("from", V8Object(runtime).apply(ObjectUtils.createWorldObject(event.from, runtime)))
        }
    }

    @EventHandler
    fun onPlayerFoodLevelChange(event: FoodLevelChangeEvent) {
        ScriptLoader.invokeEvent("onFoodLevelChange") {
            add("player", V8Object(runtime).apply(ObjectUtils.createPlayerObject(Bukkit.getPlayer(event.entity.name)!!, runtime)))
            add("foodLevel", event.foodLevel)

            registerJavaMethod({ receiver, arguments ->
                if (arguments.length() > 0) {
                    event.foodLevel = arguments[0] as Int
                    add("foodLevel", event.foodLevel)
                }
            }, "setFoodLevel")

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

    @EventHandler
    fun playerQuit(event: PlayerQuitEvent) {
        ScriptLoader.invokeEvent("onPlayerQuit") {
            add("player", V8Object(runtime).apply(ObjectUtils.createPlayerObject(event.player, runtime)))
            add("quitMessage", MessageUtils.toMiniMessage(event.quitMessage()!!))
            registerJavaMethod({ receiver, arguments ->
                if (arguments.length() > 0) {
                    event.quitMessage(MessageUtils.build(arguments[0].toString()))
                }
                if (arguments.length() == 0) {
                    event.quitMessage(null)
                }
            }, "setQuitMessage")
        }
    }

    @EventHandler
    fun asyncChat(event: AsyncChatEvent) {
        Bukkit.getScheduler().runTask(plugin, Runnable {
            ScriptLoader.invokeEvent("onPlayerChat") {
                add("player", V8Object(runtime).apply(ObjectUtils.createPlayerObject(event.player, runtime)))
                add("message", MessageUtils.toMiniMessage(event.message()))
                registerJavaMethod({ receiver, arguments ->
                    if (arguments.length() > 0) {
                        event.message(MessageUtils.build(arguments[0] as String))
                    }
                }, "setMessage")
                registerJavaMethod({ receiver, arguments ->
                    event.isCancelled = true
                }, "cancel")
            }
        })
    }

    @EventHandler
    fun playerInteract(event: PlayerInteractEvent) {
        ScriptLoader.invokeEvent("onPlayerInteract") {
            add("player", V8Object(runtime).apply(ObjectUtils.createPlayerObject(event.player, runtime)))
            add("action", event.action.name)
            if (event.clickedBlock != null) add("clickedBlock", V8Object(runtime).apply(ObjectUtils.createBlockObject(event.clickedBlock!!, runtime)))
            else addUndefined("clickedBlock")
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
        ScriptLoader.invokeEvent("onClick") {
            add("player", V8Object(runtime).apply(ObjectUtils.createPlayerObject(event.player, runtime)))
            add("clickSide", if(event.action.toString().contains("RIGHT")) "right" else "left")
            add("isAir", event.action.toString().contains("AIR"))
            if (event.clickedBlock != null) add("clicked", V8Object(runtime).apply(ObjectUtils.createBlockObject(event.clickedBlock!!, runtime)))
            else addUndefined("clicked")
            registerJavaMethod({ receiver, arguments ->
                event.isCancelled = true
            }, "cancel")
        }
    }
}
