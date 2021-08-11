package org.netherald.minejs.bukkit.impl

import com.eclipsesource.v8.V8
import com.eclipsesource.v8.V8Array
import com.eclipsesource.v8.V8Object
import org.netherald.minejs.common.PlayerManager
import java.lang.UnsupportedOperationException

class PlayerManagerImpl : PlayerManager {

    override fun getPlayers(runtime: V8): V8Array {
        // V8 Array - V8Array()
        // array.push(Object) - Push obj
        TODO("Get Players with V8 Array")
    }

    override fun getPlayersOnServer(runtime: V8, server: String): V8Array {
        throw UnsupportedOperationException("Only bungeecord can do it!")
    }

    override fun playerOf(runtime: V8, name: String): V8Object {
        TODO("Get player with standard on 음챗only 채널")
    }

}