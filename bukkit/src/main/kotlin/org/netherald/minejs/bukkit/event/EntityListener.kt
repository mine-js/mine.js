package org.netherald.minejs.bukkit.event

import com.eclipsesource.v8.V8Object
import org.bukkit.entity.FallingBlock
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.netherald.minejs.bukkit.utils.ObjectUtils
import org.netherald.minejs.common.ScriptLoader

class EntityListener : Listener {

    @EventHandler
    fun onDamageByEntity(event: EntityDamageByEntityEvent) {
        ScriptLoader.invokeEvent("onEntityDamage") {
            add("reason", event.cause.toString().lowercase())
            if(event.damager is Player)
                add("used", V8Object(runtime).apply(ObjectUtils.createItemStackObject((event.damager as Player).inventory.itemInMainHand, runtime)))
            add("victim", V8Object(runtime).apply(ObjectUtils.createEntityObject(event.entity, runtime)))
            add("attacker", V8Object(runtime).apply(ObjectUtils.createEntityObject(event.damager, runtime)))

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
    fun onDamage(event: EntityDamageEvent) {
        ScriptLoader.invokeEvent("onEntityDamage") {
            add("reason", event.cause.toString().lowercase())
            add("victim", V8Object(runtime).apply(ObjectUtils.createEntityObject(event.entity, runtime)))
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