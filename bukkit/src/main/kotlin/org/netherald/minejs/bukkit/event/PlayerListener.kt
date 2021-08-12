package org.netherald.minejs.bukkit.event

import com.eclipsesource.v8.V8
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerMoveEvent
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
}