package org.netherald.minejs.bukkit.utils

import com.eclipsesource.v8.*
import net.kyori.adventure.text.Component
import net.projecttl.inventory.gui.gui
import net.projecttl.inventory.gui.utils.InventoryType
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.netherald.minejs.bukkit.MineJsBukkit

object UIUtils {

    fun create(player: Player, size: Int, title: Component, runtime: V8): V8Object {
        return V8Object(runtime).apply {

            val slots = HashMap<Int, ItemStack>()
            val actions = HashMap<Int, V8Function>()

            registerJavaMethod({ _, parameters ->
                slots[parameters[0] as Int] = ObjectUtils.fromV8ItemStack(parameters[1] as V8Object, runtime)
                actions[parameters[0] as Int] = parameters[2] as V8Function
            }, "slot")

            registerJavaMethod({ _, _ ->
                val type = when(size) {
                    9 -> InventoryType.CHEST_9
                    18 -> InventoryType.CHEST_18
                    27 -> InventoryType.CHEST_27
                    36 -> InventoryType.CHEST_36
                    45 -> InventoryType.CHEST_45
                    else -> InventoryType.CHEST_54
                }
                player.gui(MineJsBukkit.instance, type, title) {
                    for (entry in slots) {

                        slot(entry.key, entry.value) {
                            actions[entry.key]!!.call(
                                runtime,
                                V8Array(runtime).apply {
                                    push(
                                        V8Object(runtime).apply {
                                            add("click", click.toString().lowercase())
                                            add("action", action.toString().lowercase())

                                            registerJavaMethod({ _, parameters ->
                                                isCancelled = parameters[0] as Boolean
                                            }, "setCancelled")

                                            registerJavaMethod(JavaCallback { _, _ ->
                                                return@JavaCallback isCancelled
                                            }, "isCancelled")

                                            registerJavaMethod({ _, _ ->
                                                view.close()
                                            }, "close")
                                        }
                                    )
                                }
                            )
                        }

                    }
                }
            }, "open")

        }
    }

}