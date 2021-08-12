package org.netherald.minejs.common

import com.eclipsesource.v8.V8Array

interface CommandManager {

    fun registerCommands(arr: ArrayList<Command>)

    fun unloadCommands()

}