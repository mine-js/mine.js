package org.netherald.minejs.bukkit.utils

import com.comphenix.protocol.wrappers.BlockPosition
import com.comphenix.protocol.wrappers.ChunkPosition
import com.comphenix.protocol.wrappers.WrappedBlockData
import com.eclipsesource.v8.JavaCallback
import com.eclipsesource.v8.V8
import com.eclipsesource.v8.V8Array
import com.eclipsesource.v8.V8Object
import io.alicorn.v8.V8JavaAdapter
import net.kyori.adventure.bossbar.BossBar
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.ComponentLike
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import org.bukkit.*
import org.bukkit.block.Block
import org.bukkit.command.BlockCommandSender
import org.bukkit.command.CommandSender
import org.bukkit.command.ConsoleCommandSender
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Entity
import org.bukkit.entity.FallingBlock
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import org.bukkit.scoreboard.DisplaySlot
import org.bukkit.scoreboard.Scoreboard
import org.bukkit.scoreboard.ScoreboardManager
import org.bukkit.util.Vector
import org.netherald.minejs.bukkit.MineJsBukkit
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

object ObjectUtils {

    val scoreboards = HashMap<UUID, Scoreboard>()
    val entries = HashMap<UUID, HashMap<Int, Component>>()

    fun createFallingBlockObject(fallingBlock: FallingBlock, runtime: V8, fromEntity: Boolean = false): V8Object.() -> Unit {
        return {
            if(!fromEntity)
                apply(createEntityObject(fallingBlock, runtime, true))
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
            if(!fromEntity)
                apply(createEntityObject(player, runtime, true))
            add("name", player.name)
            add("foodLevel", player.foodLevel)
            registerJavaMethod({ receiver, parameters ->
                if (parameters.length() > 0) {
                    player.sendMessage(MessageUtils.build(parameters[0] as String))
                }
            }, "send")
            registerJavaMethod({ receiver, parameters ->
                player.foodLevel = parameters[0] as Int
                add("foodLevel", player.foodLevel)
            }, "setFoodLevel")
            registerJavaMethod({ receiver, parameters ->
                player.hidePlayer(MineJsBukkit.instance, Bukkit.getPlayer((parameters[0] as V8Object).getString("name"))!!)
            }, "hidePlayer")
            registerJavaMethod({ receiver, parameters ->
                player.showPlayer(MineJsBukkit.instance, Bukkit.getPlayer((parameters[0] as V8Object).getString("name"))!!)
            }, "showPlayer")
            registerJavaMethod(JavaCallback { receiver, parameters ->
                val packetContainer = ProtocolUtil.createPacketContainer(parameters[0] as Boolean, parameters[1] as String, parameters[2] as String)

                fun <T> v8ListChunked(parameters: V8Array): ArrayList<Pair<Int, T>> {
                    val kotlinArgs = ArrayList<Any>()
                    for(i in 0 until parameters.length()) {
                        kotlinArgs.add(parameters[i])
                    }

                    val chunked = kotlinArgs.chunked(2)
                    val pairList = ArrayList<Pair<Int, T>>()

                    for(chunk in chunked) {
                        pairList.add(Pair(chunk[0] as Int, chunk[1] as T))
                    }

                    return pairList
                }

                return@JavaCallback V8Object(runtime).apply {

                    registerJavaMethod({ receiver, parameters ->
                        val chunked = v8ListChunked<Int>(parameters)
                        for (pair in chunked) {
                            packetContainer.bytes.write(pair.first, pair.second.toByte())
                        }
                    }, "writeBytes")
                    registerJavaMethod({ receiver, parameters ->
                        val chunked = v8ListChunked<Boolean>(parameters)
                        for (pair in chunked) {
                            packetContainer.booleans.write(pair.first, pair.second)
                        }
                    }, "writeBooleans")
                    registerJavaMethod({ receiver, parameters ->
                        val chunked = v8ListChunked<Int>(parameters)
                        for (pair in chunked) {
                            packetContainer.shorts.write(pair.first, pair.second.toShort())
                        }
                    }, "writeShorts")
                    registerJavaMethod({ receiver, parameters ->
                        val chunked = v8ListChunked<Int>(parameters)
                        for (pair in chunked) {
                            packetContainer.integers.write(pair.first, pair.second)
                        }
                    }, "writeIntegers")
                    registerJavaMethod({ receiver, parameters ->
                        val chunked = v8ListChunked<Int>(parameters)
                        for (pair in chunked) {
                            packetContainer.longs.write(pair.first, pair.second.toLong())
                        }
                    }, "writeLongs")
                    registerJavaMethod({ receiver, parameters ->
                        val chunked = v8ListChunked<Double>(parameters)
                        for (pair in chunked) {
                            packetContainer.float.write(pair.first, pair.second.toFloat())
                        }
                    }, "writeFloats")
                    registerJavaMethod({ receiver, parameters ->
                        val chunked = v8ListChunked<Double>(parameters)
                        for (pair in chunked) {
                            packetContainer.doubles.write(pair.first, pair.second)
                        }
                    }, "writeDoubles")
                    registerJavaMethod({ receiver, parameters ->
                        val chunked = v8ListChunked<String>(parameters)
                        for (pair in chunked) {
                            packetContainer.strings.write(pair.first, pair.second)
                        }
                    }, "writeStrings")
                    registerJavaMethod({ receiver, parameters ->
                        val chunked = v8ListChunked<String>(parameters)
                        for (pair in chunked) {
                            packetContainer.uuiDs.write(pair.first, UUID.fromString(pair.second))
                        }
                    }, "writeUuids")
                    registerJavaMethod({ receiver, parameters ->
                        val chunked = v8ListChunked<V8Object>(parameters)
                        for (pair in chunked) {
                            packetContainer.itemModifier.write(pair.first, fromV8ItemStack(pair.second, pair.second.runtime))
                        }
                    }, "writeItemModifiers")
                    registerJavaMethod({ receiver, parameters ->
                        val chunked = v8ListChunked<String>(parameters)
                        for (pair in chunked) {
                            packetContainer.worldTypeModifier.write(pair.first, WorldType.valueOf(pair.second))
                        }
                    }, "writeWorldTypeModifier")
                    registerJavaMethod({ receiver, parameters ->
                        val chunked = v8ListChunked<String>(parameters)
                        for (pair in chunked) {
                            packetContainer.blockData.write(pair.first, WrappedBlockData.createData(Material.getMaterial(pair.second.uppercase())))
                        }
                    }, "writeBlockData")
                    registerJavaMethod({ receiver, parameters ->
                        val kotlinArgs = ArrayList<Any>()
                        for(i in 0 until parameters.length()) {
                            kotlinArgs.add(parameters[i])
                        }

                        val chunked = kotlinArgs.chunked(4)

                        for (pair in chunked) {
                            packetContainer.positionModifier.write(pair[0] as Int, ChunkPosition(pair[1] as Int, pair[2] as Int, pair[3] as Int))
                        }
                    }, "writePositionModifier")
                    registerJavaMethod({ receiver, parameters ->
                        val kotlinArgs = ArrayList<Any>()
                        for(i in 0 until parameters.length()) {
                            kotlinArgs.add(parameters[i])
                        }

                        val chunked = kotlinArgs.chunked(4)

                        for (pair in chunked) {
                            packetContainer.blockPositionModifier.write(pair[0] as Int, BlockPosition(pair[1] as Int, pair[2] as Int, pair[3] as Int))
                        }
                    }, "writeBlockPositionModifier")

                    registerJavaMethod({ receiver, parameters ->
                        ProtocolUtil.sendPacket(player, packetContainer)
                    }, "sendPacket")
                }
            }, "packet")
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
            registerJavaMethod({ receiver, parameters ->
                when(parameters[0] as String) {
                    "survival" -> player.gameMode = GameMode.SURVIVAL
                    "creative" -> player.gameMode = GameMode.CREATIVE
                    "adventure" -> player.gameMode = GameMode.ADVENTURE
                    "spectator" -> player.gameMode = GameMode.SPECTATOR
                }
            }, "setGameMode")
            registerJavaMethod({ receiver, parameters ->
                when(parameters[0] as String) {
                    "survival" -> player.gameMode = GameMode.SURVIVAL
                    "creative" -> player.gameMode = GameMode.CREATIVE
                    "adventure" -> player.gameMode = GameMode.ADVENTURE
                    "spectator" -> player.gameMode = GameMode.SPECTATOR
                }
            }, "setGameMode")
            registerJavaMethod(JavaCallback { _, parameters ->
                return@JavaCallback UIUtils.create(
                    player,
                    parameters[0] as Int,
                    MessageUtils.build(parameters[1] as String),
                    runtime
                )
            }, "createUI")
        }
    }

    fun fromV8Location(v8Location: V8Object, runtime: V8) : Location {
        val res = Location(Bukkit.getWorld(v8Location.getObject("world").getString("name"))!!, v8Location.getDouble("x"), v8Location.getDouble("y"), v8Location.getDouble("z"))
        res.yaw = v8Location.getDouble("yaw").toFloat()
        res.pitch = v8Location.getDouble("pitch").toFloat()
        return res
    }

    fun createEntityObject(entity: Entity, runtime: V8, fromOther: Boolean = false) : V8Object.() -> Unit {
        return {
            if (entity is Player && !fromOther)
                apply(createPlayerObject(entity, runtime, true))
            else if(entity is FallingBlock && !fromOther)
                apply(createFallingBlockObject(entity, runtime, true))

            //V8JavaAdapter.injectObject("java", entity, this)
            add("uuid", entity.uniqueId.toString())
            registerJavaMethod(JavaCallback { receiver, parameters ->
                return@JavaCallback V8Object(runtime).apply(createLocationObject(entity.location, runtime))
            }, "location")
            registerJavaMethod({ receiver, parameters ->
                entity.teleport(fromV8Location(parameters[0] as V8Object, runtime))
            }, "teleport")
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

    fun fromV8ItemStack(v8ItemStack: V8Object, runtime: V8): ItemStack {
        return ItemStack(Material.valueOf(v8ItemStack.getString("type").uppercase()), v8ItemStack.getInteger("amount")).apply {
            val v8Enchantments = v8ItemStack.getArray("enchantment")
            for(i in 0 until v8Enchantments.length()) {
                val type = v8Enchantments.keys[i] as String
                val level = v8Enchantments[i] as Int
                addUnsafeEnchantment(Enchantment.values().find { e -> e.key.key == type }!!, level)
            }

            itemMeta = itemMeta.apply {
                if(v8ItemStack.contains("lore")) {
                    val array = v8ItemStack.getArray("lore")
                    val list = ArrayList<Component>()
                    for(i in 0 until array.length()) {
                        list.add(MessageUtils.build(array[i] as String))
                    }
                    lore(list)
                }

                if(v8ItemStack.contains("displayname")) {
                    displayName(MessageUtils.build(v8ItemStack.getString("displayname")))
                }
            }
        }
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
            if (stack.itemMeta != null && stack.itemMeta.displayName() != null)
                add("displayname", MessageUtils.toMiniMessage(stack.itemMeta.displayName()!!))
            add("amount", stack.amount)
            add("lore", lore)
            add("enchantment", enchantments)
            add("type", stack.type.toString().lowercase())
        }
    }

    fun createLocationObject(location: Location, runtime: V8): V8Object.() -> Unit {
        return {
            //V8JavaAdapter.injectObject("java", location, this)
            add("x", location.x)
            add("y", location.y)
            add("z", location.z)
            add("yaw", location.yaw.toDouble())
            add("pitch", location.pitch.toDouble())
            add("blockX", location.blockX)
            add("blockY", location.blockY)
            add("blockZ", location.blockZ)
            /*
            registerJavaMethod({ receiver, parameters ->
                location.x = parameters[0] as Double
            }, "x")
            registerJavaMethod(JavaCallback { receiver, parameters ->
                location.y = parameters[0] as Double
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
             */
            add("world", V8Object(runtime).apply(createWorldObject(location.world, runtime)));
        }
    }

    fun createCommandSenderObject(commandSender: CommandSender, runtime: V8) : V8Object.() -> Unit {
        return {
            registerJavaMethod(JavaCallback { receiver, parameters ->
                return@JavaCallback commandSender.isOp
            }, "isOp")
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
            //V8JavaAdapter.injectObject("java", world, this)
            add("name", world.name)
            registerJavaMethod(JavaCallback { receiver, parameters ->
                return@JavaCallback V8Object(runtime).apply(createBlockObject(world.getHighestBlockAt(fromV8Location(
                    parameters[0] as V8Object, runtime)), runtime))
            }, "highestBlock")
            registerJavaMethod(JavaCallback { receiver, parameters ->
                return@JavaCallback V8Array(runtime).apply {
                    for(player in world.players) {
                        push(V8Object(runtime).apply(createPlayerObject(player, runtime)))
                    }
                }
            }, "players")
        }
    }
    
    fun createBlockObject(block: Block, runtime: V8): V8Object.() -> Unit {
        return {
            //V8JavaAdapter.injectObject("java", block, this)
            add("location", V8Object(runtime).apply(createLocationObject(block.location, runtime)))
            registerJavaMethod({ receiver, parameters ->
                block.type = Material.values().find { mat -> mat.toString().equals((parameters[0] as String).replace(" ", "_"), true) }!!
            }, "set")
        }
    }

}