package org.netherald.minejs.common.interfaces

interface JavaManager {

    fun getImports(filename: String): ArrayList<Class<*>>

    fun unload()

}