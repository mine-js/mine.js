package org.netherald.minejs.bukkit.event

import io.papermc.paper.event.player.AsyncChatEvent
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextComponent
import net.kyori.adventure.text.TranslatableComponent
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.plugin.Plugin
import org.netherald.minejs.bukkit.utils.MessageUtils
import org.netherald.minejs.bukkit.utils.ObjectUtils
import org.netherald.minejs.common.ScriptLoader

class PlayerListener(val plugin: Plugin) : Listener {
    @EventHandler
    fun playerMove(event: PlayerMoveEvent) {
        ScriptLoader.invokeEvent("onPlayerMove") {
            add("from", ObjectUtils.createLocationObject(event.from, runtime))
            add("to", ObjectUtils.createLocationObject(event.to, runtime))
            add("player", ObjectUtils.createPlayerObject(event.player, runtime))
            registerJavaMethod({ receiver, arguments ->
                if (arguments.length() > 0) {
                    if (arguments[0] == true) {
                        event.isCancelled = true;
                    } else if (arguments[0] == true) {
                        event.isCancelled = false;
                    }
                }
            }, "setCancelled")
        }
    }

    @EventHandler
    fun playerJoin(event: PlayerJoinEvent) {
        ScriptLoader.invokeEvent("onPlayerJoin") {
            add("player", ObjectUtils.createPlayerObject(event.player, runtime))
            add("joinMessage", MessageUtils.toMiniMessage(event.joinMessage()))
            registerJavaMethod({ receiver, arguments ->
                if (arguments.length() > 0) {
                    event.joinMessage(MessageUtils.build(arguments[0].toString()))
                }
            }, "setJoinMessage")
        }
    }

    @EventHandler
    fun playerQuit(event: PlayerQuitEvent) {
        ScriptLoader.invokeEvent("onPlayerQuit") {
            add("player", ObjectUtils.createPlayerObject(event.player, runtime))
            add("quitMessage", MessageUtils.toMiniMessage(event.quitMessage()))
            registerJavaMethod({ receiver, arguments ->
                if (arguments.length() > 0) {
                    event.quitMessage(MessageUtils.build(arguments[0].toString()))
                }
            }, "setQuitMessage")
        }
    }

    @EventHandler
    fun asyncChat(event: AsyncChatEvent) {
        Bukkit.getScheduler().runTask(plugin, Runnable {
            ScriptLoader.invokeEvent("onPlayerChat") {
                add("player", ObjectUtils.createPlayerObject(event.player, runtime))
                add("message", MessageUtils.toMiniMessage(event.message()))
                registerJavaMethod({ receiver, arguments ->
                    if (arguments.length() > 0) {
                        event.message(MessageUtils.build(arguments[0] as String))
                    }
                }, "setMessage")
            }
        })
    }

    @EventHandler
    fun playerInteract(event: PlayerInteractEvent) {
        ScriptLoader.invokeEvent("onPlayerInteract") {
            add("player", ObjectUtils.createPlayerObject(event.player, runtime))
            add("action", event.action.name)
            if (event.clickedBlock != null) add("clickedBlock", ObjectUtils.createBlockObject(event.clickedBlock!!, runtime))
            else addUndefined("clickedBlock")
            registerJavaMethod({ receiver, arguments ->
                if (arguments.length() > 0) {
                    if (arguments[0] == true) {
                        event.isCancelled = true;
                    } else if (arguments[0] == true) {
                        event.isCancelled = false;
                    }
                }
            }, "setCancelled")
        }
    }
}