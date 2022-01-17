package xyz.minejs.addon.test

import com.gmail.filoghost.holographicdisplays.api.HologramsAPI
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEvent
import org.netherald.minejs.bukkit.MineJsBukkit
import org.netherald.minejs.bukkit.native.NativeAddon
import org.netherald.minejs.common.ScriptLoader

class TestAddon: NativeAddon() {
    override fun init() {
        registerFunction("asdf") { _, _ ->
            println("Called asdf@!!!")
        }

        registerListener(object: Listener {

            @EventHandler
            fun test(event: PlayerInteractEvent) {
                val item = event.player.inventory.itemInMainHand

                if(item.type == Material.DIAMOND) {
                    ScriptLoader.invokeEvent("onTestInvoked") {
                        add("test", 12345)
                    }
                }
            }

        })
    }
}