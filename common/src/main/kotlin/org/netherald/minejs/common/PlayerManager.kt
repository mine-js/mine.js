package org.netherald.minejs.common

import com.eclipsesource.v8.V8
import com.eclipsesource.v8.V8Array
import com.eclipsesource.v8.V8Object

interface PlayerManager {

    fun getPlayers(runtime: V8) : V8Array

    // only on bungee
    fun getPlayersOnServer(runtime: V8, server: String) : V8Array

    fun playerOf(runtime: V8, name: String) : V8Object

}