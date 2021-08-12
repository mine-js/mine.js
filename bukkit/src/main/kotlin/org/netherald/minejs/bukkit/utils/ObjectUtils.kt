package org.netherald.minejs.bukkit.utils

import com.eclipsesource.v8.V8
import com.eclipsesource.v8.V8Object
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.block.Block
import org.bukkit.entity.Player

class ObjectUtils {
    companion object {
        fun createPlayerObject(player: Player, runtime: V8): V8Object {
            val res = V8Object(runtime)
            res.add("name", player.name)
            res.add("uuid", player.uniqueId.toString())
            res.add("location", createLocationObject(player.location, runtime))
            res.registerJavaMethod({ receiver, parameters ->
                if(parameters.length() > 0) {
                    player.sendMessage(parameters[0] as String)
                }
            }, "send")
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
}