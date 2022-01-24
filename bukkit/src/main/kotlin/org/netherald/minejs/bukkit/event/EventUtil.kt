package org.netherald.minejs.bukkit.event

import com.eclipsesource.v8.JavaCallback
import com.eclipsesource.v8.V8Object
import org.bukkit.entity.Player
import org.bukkit.event.Cancellable
import org.bukkit.event.block.BlockEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.netherald.minejs.bukkit.utils.ObjectUtils
import org.netherald.minejs.bukkit.utils.getOrNull

fun V8Object.registerCancellable(event: Cancellable) {
    registerJavaMethod({ _, arguments ->
        event.isCancelled = arguments.getOrNull(0) == true
    }, "setCancelled")

    registerJavaMethod(JavaCallback { _, _ ->
        return@JavaCallback event.isCancelled
    }, "isCancelled")

    registerJavaMethod({ _, _ ->
        event.isCancelled = true
    }, "cancel")
}

fun V8Object.registerBlockEvent(event: BlockEvent, player: Player) {
    add("block", V8Object(runtime).apply(ObjectUtils.createBlockObject(event.block, runtime)))
    add("player", V8Object(runtime).apply(ObjectUtils.createPlayerObject(player, runtime)))
    if (event is Cancellable) {
        registerCancellable(event)
    }
}

fun V8Object.registerEntityDamageEvent(event: EntityDamageEvent) {
    add("reason", event.cause.toString().lowercase())
    add("victim", V8Object(runtime).apply(ObjectUtils.createEntityObject(event.entity, runtime)))
}

fun V8Object.registerPlayer(player: Player) {
    add("player", V8Object(runtime).apply(ObjectUtils.createPlayerObject(player, runtime)))
}