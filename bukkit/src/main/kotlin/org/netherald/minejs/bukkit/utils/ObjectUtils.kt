package org.netherald.minejs.bukkit.utils

import com.eclipsesource.v8.JavaCallback
import com.eclipsesource.v8.V8
import com.eclipsesource.v8.V8Array
import com.eclipsesource.v8.V8Object
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.block.Block
import org.bukkit.command.BlockCommandSender
import org.bukkit.command.CommandSender
import org.bukkit.command.ConsoleCommandSender
import org.bukkit.entity.Entity
import org.bukkit.entity.FallingBlock
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import org.bukkit.util.Vector

object ObjectUtils {

    fun createFallingBlockObject(fallingBlock: FallingBlock, runtime: V8, fromEntity: Boolean = false): V8Object.() -> Unit {
        return {
            if(!fromEntity)
                apply(createEntityObject(fallingBlock, runtime))
            registerJavaMethod(JavaCallback { receiver, parameters ->
                return@JavaCallback fallingBlock.blockData.material.toString().lowercase()
            }, "blockType")
            registerJavaMethod({ receiver, parameters ->
                fallingBlock.dropItem = !fallingBlock.dropItem
            }, "dropItem")
            registerJavaMethod({ receiver, parameters ->
                fallingBlock.dropItem = !fallingBlock.dropItem
            }, "hurtEntity")
        }
    }

    fun createPlayerObject(player: Player, runtime: V8, fromEntity: Boolean = false): V8Object.() -> Unit {
        return {
            /*
            if(!fromEntity)
                apply(createEntityObject(player, runtime))
             */
            add("name", player.name)
            registerJavaMethod({ receiver, parameters ->
                if (parameters.length() > 0) {
                    player.sendMessage(MessageUtils.build(parameters[0] as String))
                }
            }, "send")
        }
    }

    fun fromV8Location(v8Location: V8Object, runtime: V8) : Location {
        val res = Location(Bukkit.getWorld(v8Location.getObject("world").getString("name"))!!, v8Location.getDouble("x"), v8Location.getDouble("y"), v8Location.getDouble("z"))
        res.yaw = v8Location.getDouble("yaw").toFloat()
        res.pitch = v8Location.getDouble("pitch").toFloat()
        return res
    }

    fun createEntityObject(entity: Entity, runtime: V8) : V8Object.() -> Unit {
        return {
            if (entity is Player)
                apply(createPlayerObject(entity, runtime, true))
            else if(entity is FallingBlock)
                apply(createFallingBlockObject(entity, runtime, true))
            add("uuid", entity.uniqueId.toString())
            registerJavaMethod(JavaCallback { receiver, parameters ->
                return@JavaCallback V8Object(runtime).apply(createLocationObject(entity.location, runtime))
            }, "location")
            registerJavaMethod(JavaCallback { receiver, arguments ->
                return@JavaCallback V8Object(runtime).apply(createVector(entity.velocity, runtime))
            }, "velocity")
            registerJavaMethod({ receiver, parameters ->
                if(parameters.length() > 0)
                    entity.velocity = fromV8Vector(parameters[0] as V8Object, runtime)
            }, "setVelocity")
        }
    }

    fun createVector(vector: Vector, runtime : V8) : V8Object.() -> Unit {
        return {
            add("x", vector.x)
            add("y", vector.y)
            add("z", vector.z)
        }
    }
    fun fromV8Vector(v8Vector: V8Object, runtime : V8) : Vector {
        return Vector(v8Vector.getDouble("x"), v8Vector.getDouble("y"), v8Vector.getDouble("z"))
    }

    fun createItemStackObject(stack : ItemStack, runtime : V8) : V8Object.() -> Unit {
        return {
            var lore = V8Array(runtime)
            for (lo in stack.lore()!!) {
                lore.push(MessageUtils.toMiniMessage(lo))
            }
            val enchantments = V8Array(runtime)
            for (enchantment in stack.enchantments) {
                enchantments.add(enchantment.key.key.key, enchantment.value)
            }
            if (stack.itemMeta.displayName() != null)
                add("displayname", MessageUtils.toMiniMessage(stack.itemMeta.displayName()))
            add("amount", stack.amount)
            add("lore", lore)
            add("enchantment", enchantments)
        }
    }

    fun createLocationObject(location: Location, runtime: V8): V8Object.() -> Unit {
        return {
            registerJavaMethod(JavaCallback { receiver, parameters ->
                if(parameters.length() > 0) {
                    location.x = parameters[0] as Double
                }
                return@JavaCallback location.x
            }, "x")
            registerJavaMethod(JavaCallback { receiver, parameters ->
                if(parameters.length() > 0) {
                    location.y = parameters[0] as Double
                }
                return@JavaCallback location.y
            }, "y")
            registerJavaMethod(JavaCallback { receiver, parameters ->
                if(parameters.length() > 0) {
                    location.z = parameters[0] as Double
                }
                return@JavaCallback location.z
            }, "z")
            registerJavaMethod(JavaCallback { receiver, parameters ->
                return@JavaCallback location.blockX
            }, "blockX")
            registerJavaMethod(JavaCallback { receiver, parameters ->
                return@JavaCallback location.blockY
            }, "blockY")
            registerJavaMethod(JavaCallback { receiver, parameters ->
                return@JavaCallback location.blockZ
            }, "blockZ")
            registerJavaMethod(JavaCallback { receiver, parameters ->
                if(parameters.length() > 0) {
                    location.pitch = (parameters[0] as Double).toFloat()
                }
                return@JavaCallback location.pitch.toDouble()
            }, "pitch")
            registerJavaMethod(JavaCallback { receiver, parameters ->
                return@JavaCallback V8Object(runtime).apply { createBlockObject(location.block, runtime) }
            }, "block")
            registerJavaMethod(JavaCallback { receiver, parameters ->
                if(parameters.length() > 0) {
                    location.yaw = (parameters[0] as Double).toFloat()
                }
                return@JavaCallback location.yaw.toDouble()
            }, "yaw")
            add("world", V8Object(runtime).apply { createWorldObject(location.world, runtime) });
        }
    }

    fun createCommandSenderObject(commandSender: CommandSender, runtime: V8) : V8Object.() -> Unit {
        return {
            registerJavaMethod({ receiver, parameters ->
                if(parameters.length() > 0) {
                    commandSender.sendMessage(MessageUtils.build(parameters[0] as String))
                }
            }, "send")
            registerJavaMethod(JavaCallback { receiver, parameters ->
                return@JavaCallback V8Object(runtime).apply(createBlockObject((commandSender as BlockCommandSender).block, runtime))
            }, "block")
            registerJavaMethod(JavaCallback { receiver, parameters ->
                return@JavaCallback V8Object(runtime).apply(createPlayerObject(commandSender as Player, runtime))
            }, "player")
            add("type", if(commandSender is ConsoleCommandSender) "console" else if(commandSender is BlockCommandSender) "block" else if(commandSender is Player) "player" else "unknown")
        }
    }

    fun createWorldObject(world: World, runtime: V8): V8Object.() -> Unit {
        return {
            add("name", world.name)
            registerJavaMethod(JavaCallback { receiver, parameters ->
                return@JavaCallback V8Object(runtime).apply(createBlockObject(world.getHighestBlockAt(fromV8Location(
                    parameters[0] as V8Object, runtime)), runtime))
            }, "highestBlock")
        }
    }
    
    fun createBlockObject(block: Block, runtime: V8): V8Object.() -> Unit {
        return {
            add("location", V8Object(runtime).apply(createLocationObject(block.location, runtime)))
            registerJavaMethod({ receiver, parameters ->
                block.type = Material.values().find { mat -> mat.toString().equals((parameters[0] as String).replace(" ", "_"), true) }!!
            }, "set")
        }
    }

}