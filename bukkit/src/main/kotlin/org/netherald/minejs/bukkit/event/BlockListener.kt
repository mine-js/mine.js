package org.netherald.minejs.bukkit.event

import com.eclipsesource.v8.V8Object
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.netherald.minejs.bukkit.utils.ObjectUtils
import org.netherald.minejs.common.ScriptLoader

class BlockListener : Listener {
    @EventHandler
    fun onBlockBreak(event: BlockBreakEvent) {
        ScriptLoader.invokeEvent("onBreakBlock") {
            add("block", V8Object(runtime).apply(ObjectUtils.createBlockObject(event.block, runtime)))
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
    fun onBlockPlace(event: BlockPlaceEvent) {
        ScriptLoader.invokeEvent("onPlaceBlock") {
            add("block", V8Object(runtime).apply(ObjectUtils.createBlockObject(event.block, runtime)))
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
}