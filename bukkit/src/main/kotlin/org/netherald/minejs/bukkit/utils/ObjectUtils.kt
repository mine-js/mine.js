package org.netherald.minejs.bukkit.utils

import com.eclipsesource.v8.V8
import com.eclipsesource.v8.V8Array
import com.eclipsesource.v8.V8Object
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.block.Block
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack

object ObjectUtils {

    fun createPlayerObject(player: Player, runtime: V8): V8Object {
        val res = V8Object(runtime)
        res.add("name", player.name)
        res.add("uuid", player.uniqueId.toString())
        res.add("location", createLocationObject(player.location, runtime))
        res.add("inventory", createInventoryObject(player.inventory, runtime))
        res.registerJavaMethod({ receiver, parameters ->
            if(parameters.length() > 0) {
                player.sendMessage(parameters[0] as String)
            }
        }, "send")
        return res
    }

    fun createItemStackObject(stack : ItemStack, runtime : V8) : V8Object {
        val res = V8Object(runtime)
        var lore = ""
        for (i in 0 until stack.lore!!.size) {
            lore+=stack.lore!![i]
            if (i != stack.lore!!.size-1) {
                lore+="\n"
            }
        }
        val enchantments = V8Array(runtime)
        for (enchantment in stack.enchantments) {
            enchantments.add(enchantment.key.name,enchantment.value)
        }
        res.add("displayname",stack.itemMeta.displayName)
        res.add("amount",stack.amount)
        res.add("lore",lore)
        res.add("enchantment",enchantments)
        return res
    }

    fun createInventoryObject(inventory: Inventory, runtime : V8) : V8Object {
        val res = V8Object(runtime)
        val contents = V8Object(runtime)
        for (i in 0 until inventory.contents.size) {
            contents.add(i.toString(), createItemStackObject(inventory.contents[i], runtime));
        }
        res.add("contents", contents)
        res.add("type", inventory.type.name)
        return res
    }

    fun createLocationObject(location: Location, runtime: V8): V8Object {
        val res = V8Object(runtime)
        res.add("x", location.x);
        res.add("y", location.y);
        res.add("z", location.z);
        res.add("blockX", location.blockX);
        res.add("blockY", location.blockY);
        res.add("blockZ" +
                "", location.blockZ);
        res.add("world", createWorldObject(location.world, runtime));
        return res;
    }

    fun createWorldObject(world: World, runtime: V8): V8Object {
        val res = V8Object(runtime)
        // 프젝이 해주겠죠?
        return res;
    }

    fun createBlockObject(block: Block, runtime: V8?): V8Object {
        val res = V8Object(runtime)
        // 프젝이 해주겠죠?
        return res;
    }

}