package org.netherald.minejs.common.interfaces

import org.netherald.minejs.common.Command

interface CommandManager {

    fun registerCommands(arr: ArrayList<Command>)

    fun unloadCommands()

}