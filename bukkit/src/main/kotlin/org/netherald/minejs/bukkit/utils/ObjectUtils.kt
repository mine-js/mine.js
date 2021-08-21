package org.netherald.minejs.bukkit.utils

import com.eclipsesource.v8.JavaCallback
import com.eclipsesource.v8.V8
import com.eclipsesource.v8.V8Array
import com.eclipsesource.v8.V8Object
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.block.Block
import org.bukkit.command.BlockCommandSender
import org.bukkit.command.CommandSender
import org.bukkit.command.ConsoleCommandSender
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack

object ObjectUtils {

    fun createPlayerObject(player: Player, runtime: V8): V8Object {
        val res = V8Object(runtime)
        res.add("name", player.name)
        res.add("uuid", player.uniqueId.toString())
        res.add("location", createLocationObject(player.location, runtime))
        res.registerJavaMethod({ receiver, parameters ->
            if(parameters.length() > 0) {
                player.sendMessage(MessageUtils.build(parameters[0] as String))
            }
        }, "send")
        return res
    }

    fun createItemStackObject(stack : ItemStack, runtime : V8) : V8Object {
        val res = V8Object(runtime)
        var lore = V8Array(runtime)
        for(lo in stack.lore()!!) {
            lore.push(MessageUtils.toMiniMessage(lo))
        }
        val enchantments = V8Array(runtime)
        for (enchantment in stack.enchantments) {
            enchantments.add(enchantment.key.key.key,enchantment.value)
        }
        if(stack.itemMeta.displayName() != null)
            res.add("displayname", MessageUtils.toMiniMessage(stack.itemMeta.displayName()))
        res.add("amount",stack.amount)
        res.add("lore",lore)
        res.add("enchantment",enchantments)
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

    fun createCommandSenderObject(commandSender: CommandSender, runtime: V8) : V8Object {
        val res = V8Object(runtime)
        res.run {
            registerJavaMethod({ receiver, parameters ->
                if(parameters.length() > 0) {
                    commandSender.sendMessage(MessageUtils.build(parameters[0] as String))
                }
            }, "send")
            registerJavaMethod(JavaCallback { receiver, parameters ->
                return@JavaCallback createBlockObject((commandSender as BlockCommandSender).block, runtime)
            }, "getBlock")
            registerJavaMethod(JavaCallback { receiver, parameters ->
                return@JavaCallback createPlayerObject(commandSender as Player, runtime)
            }, "getPlayer")
            add("type", if(commandSender is ConsoleCommandSender) "console" else if(commandSender is BlockCommandSender) "block" else if(commandSender is Player) "player" else "unknown")
        }
        return res
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