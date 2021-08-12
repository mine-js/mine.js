package org.netherald.minejs.bukkit.event

import io.papermc.paper.event.player.AsyncChatEvent
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextComponent
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.netherald.minejs.bukkit.utils.ObjectUtils
import org.netherald.minejs.common.ScriptLoader

class PlayerListener : Listener {
    @EventHandler
    fun playerMove(event: PlayerMoveEvent) {
        ScriptLoader.invokeEvent("onPlayerMove", ScriptLoader.createV8Object {
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
        })
    }

    @EventHandler
    fun playerJoin(event: PlayerJoinEvent) {
        ScriptLoader.invokeEvent("onPlayerJoin", ScriptLoader.createV8Object {
            add("player", ObjectUtils.createPlayerObject(event.player, runtime))
            add("joinMessage", (event.joinMessage() as TextComponent).content())
            registerJavaMethod({ receiver, arguments ->
                if (arguments.length() > 0) {
                    event.joinMessage(Component.text(arguments[0].toString()))
                }
            }, "setJoinMessage")
        })
    }

    @EventHandler
    fun playerQuit(event: PlayerQuitEvent) {
        ScriptLoader.invokeEvent("onPlayerQuit", ScriptLoader.createV8Object {
            add("player", ObjectUtils.createPlayerObject(event.player, runtime))
            add("quitMessage", (event.quitMessage() as TextComponent).content())
            registerJavaMethod({ receiver, arguments ->
                if (arguments.length() > 0) {
                    event.quitMessage(Component.text(arguments[0].toString()))
                }
            }, "setQuitMessage")
        })
    }

    @EventHandler
    fun asyncChat(event: AsyncChatEvent) {
        ScriptLoader.invokeEvent("onPlayerChat", ScriptLoader.createV8Object {
            add("player", ObjectUtils.createPlayerObject(event.player, runtime))
            add("message", (event.message() as TextComponent).content())
            registerJavaMethod({ receiver, arguments ->
                if (arguments.length() > 0) {
                    event.message(Component.text(arguments[0].toString()))
                }
            }, "setMessage")
        })
    }

    @EventHandler
    fun playerInteract(event: PlayerInteractEvent) {
        ScriptLoader.invokeEvent("onPlayerInteract", ScriptLoader.createV8Object {
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
        })
    }
}