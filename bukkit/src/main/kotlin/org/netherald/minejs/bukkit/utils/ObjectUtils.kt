package org.netherald.minejs.bukkit.utils

import com.eclipsesource.v8.JavaCallback
import com.eclipsesource.v8.V8
import com.eclipsesource.v8.V8Array
import com.eclipsesource.v8.V8Object
import net.kyori.adventure.bossbar.BossBar
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
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
import org.bukkit.scoreboard.DisplaySlot
import org.bukkit.scoreboard.Scoreboard
import org.bukkit.scoreboard.ScoreboardManager
import org.bukkit.util.Vector
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

object ObjectUtils {

    val scoreboards = HashMap<UUID, Scoreboard>()
    val entries = HashMap<UUID, HashMap<Int, Component>>()

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
            registerJavaMethod(JavaCallback { receiver, parameters ->
                if(parameters.length() > 0) {
                    var scoreboard = scoreboards[player.uniqueId]
                    if(scoreboard == null) {
                        scoreboard = Bukkit.getScoreboardManager().newScoreboard
                        scoreboards[player.uniqueId] = scoreboard
                    }
                    var objective = scoreboard.getObjective(player.uniqueId.toString().split("-")[0])
                    if(objective == null)
                        objective = scoreboard.registerNewObjective(player.uniqueId.toString().split("-")[0], "dummy", MessageUtils.build(parameters[0] as String))

                    objective.displaySlot = DisplaySlot.SIDEBAR

                    player.scoreboard = scoreboard

                    fun updateScoreboard(scores: HashMap<Int, Component>) {
                        if(entries.containsKey(player.uniqueId)) {
                            for (i in entries[player.uniqueId]!!) {
                                if(scores[i.key] != i.value)
                                    scores[i.key] = i.value
                                else {
                                    scoreboards[player.uniqueId]!!.resetScores(LegacyComponentSerializer.builder().build().serialize(i.value))
                                }
                            }
                        }
                        val newList = scores.keys.sortedDescending()
                        for (key in newList) {
                            objective.getScore(LegacyComponentSerializer.builder().build().serialize(scores[key]!!)).score = newList.indexOf(key)
                        }
                    }

                    return@JavaCallback V8Object(runtime).apply {
                        val lines = HashMap<Int, Component>()
                        var blankTxt = " "
                        registerJavaMethod({ receiver, parameters ->
                            updateScoreboard(lines)
                        }, "update")
                        registerJavaMethod({ receiver, parameters ->
                            if(parameters.length() > 1) {
                                lines[parameters[0] as Int] = MessageUtils.build(parameters[1] as String)
                                updateScoreboard(lines)
                            }
                        }, "setLine")
                        registerJavaMethod({ receiver, parameters ->
                            if(parameters.length() > 0) {
                                lines[parameters[0] as Int] = Component.text(blankTxt)
                                blankTxt += " "
                                updateScoreboard(lines)
                            }
                        }, "setBlankLine")
                        registerJavaMethod({ receiver, parameters ->
                            val arrV8 = V8Array(runtime)
                            for (entry in lines) {
                                arrV8.push(MessageUtils.toMiniMessage(entry.value))
                            }
                        }, "lines")
                        registerJavaMethod({ receiver, parameters ->
                            objective.displayName(MessageUtils.build(parameters[0] as String))
                        }, "setDisplayName")
                    }
                }
                return@JavaCallback null
            }, "sidebar")
            registerJavaMethod(JavaCallback { receiver, parameters ->
                if (parameters.length() > 2) {
                    lateinit var bossbar: BossBar
                    if(parameters.length() > 3)
                        if(parameters[3] is Int)
                            bossbar = BossBar.bossBar(MessageUtils.build(parameters[0] as String), (parameters[3] as Int).toFloat(), BossBar.Color.valueOf((parameters[1] as String).uppercase()), BossBar.Overlay.valueOf((parameters[2] as String).uppercase()))
                        else
                            bossbar = BossBar.bossBar(MessageUtils.build(parameters[0] as String), (parameters[3] as Double).toFloat(), BossBar.Color.valueOf((parameters[1] as String).uppercase()), BossBar.Overlay.valueOf((parameters[2] as String).uppercase()))
                    else
                        bossbar = BossBar.bossBar(MessageUtils.build(parameters[0] as String), 0.0F, BossBar.Color.valueOf((parameters[1] as String).uppercase()), BossBar.Overlay.valueOf((parameters[2] as String).uppercase()))

                    player.showBossBar(bossbar)
                    return@JavaCallback V8Object(runtime).apply {
                        registerJavaMethod(JavaCallback { receiver, parameters ->
                            return@JavaCallback bossbar.color().toString().lowercase()
                        }, "color")
                        registerJavaMethod({ receiver, parameters ->
                            player.hideBossBar(bossbar)
                        }, "hide")
                        registerJavaMethod({ receiver, parameters ->
                            player.showBossBar(bossbar)
                        }, "show")
                        registerJavaMethod({ receiver, parameters ->
                            if(parameters.length() > 0) {
                                bossbar.color(BossBar.Color.valueOf((parameters[0] as String).uppercase()))
                            }
                        }, "setColor")
                        registerJavaMethod(JavaCallback { receiver, parameters ->
                            return@JavaCallback MessageUtils.toMiniMessage(bossbar.name())
                        }, "text")
                        registerJavaMethod({ receiver, parameters ->
                            if(parameters.length() > 0) {
                                bossbar.name(MessageUtils.build(parameters[0] as String))
                            }
                        }, "setText")
                    }
                }
                return@JavaCallback null
            }, "createBossbar")
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
            if(stack.lore() != null) {
                for (lo in stack.lore()!!) {
                    lore.push(MessageUtils.toMiniMessage(lo))
                }
            }
            val enchantments = V8Array(runtime)
            for (enchantment in stack.enchantments) {
                enchantments.add(enchantment.key.key.key, enchantment.value)
            }
            if (stack.itemMeta.displayName() != null)
                add("displayname", MessageUtils.toMiniMessage(stack.itemMeta.displayName()!!))
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