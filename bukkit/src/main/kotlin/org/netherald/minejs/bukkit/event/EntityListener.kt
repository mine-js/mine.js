package org.netherald.minejs.bukkit.event

import com.eclipsesource.v8.V8Object
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
            if(event.damager is Player)
                add("used", V8Object(runtime).apply(ObjectUtils.createItemStackObject((event.damager as Player).inventory.itemInMainHand, runtime)))
            add("attacker", V8Object(runtime).apply(ObjectUtils.createEntityObject(event.damager, runtime)))

            registerEntityDamageEvent(event)
        }
    }

    @EventHandler
    fun onDamage(event: EntityDamageEvent) {
        ScriptLoader.invokeEvent("onEntityDamage") {
            registerEntityDamageEvent(event)
        }
    }

}