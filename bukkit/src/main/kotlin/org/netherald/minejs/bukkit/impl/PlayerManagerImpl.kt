package org.netherald.minejs.bukkit.impl

import com.eclipsesource.v8.V8
import com.eclipsesource.v8.V8Array
import com.eclipsesource.v8.V8Object
import org.bukkit.Bukkit
import org.netherald.minejs.bukkit.utils.ObjectUtils.createPlayerObject
import org.netherald.minejs.common.PlayerManager
import java.lang.UnsupportedOperationException

class PlayerManagerImpl : PlayerManager {

    override fun getPlayers(runtime: V8): V8Array {
        val array = V8Array(runtime)
        for (player in Bukkit.getOnlinePlayers()) {
            val playerArray = V8Object(runtime).apply(createPlayerObject(player, runtime))
            array.push(playerArray)
            playerArray.release()
        }

        return array
    }

    override fun getPlayersOnServer(runtime: V8, server: String): V8Array {
        throw UnsupportedOperationException("Only bungeecord can do it!")
    }

    override fun playerOf(runtime: V8, name: String): V8Object {
        val player = Bukkit.getPlayer(name)
        return if (player != null) V8Object(runtime).apply(createPlayerObject(player, runtime))
            else V8Object(runtime)
    }
}