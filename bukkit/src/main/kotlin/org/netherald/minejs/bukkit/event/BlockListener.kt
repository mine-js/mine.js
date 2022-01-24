package org.netherald.minejs.bukkit.event

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.netherald.minejs.common.ScriptLoader

class BlockListener : Listener {
    @EventHandler
    fun onBlockBreak(event: BlockBreakEvent) {
        ScriptLoader.invokeEvent("onBreakBlock") {
            registerBlockEvent(event, event.player)
        }
    }

    @EventHandler
    fun onBlockPlace(event: BlockPlaceEvent) {
        ScriptLoader.invokeEvent("onPlaceBlock") {
            registerBlockEvent(event, event.player)
            event.blockPlaced
        }
    }
}