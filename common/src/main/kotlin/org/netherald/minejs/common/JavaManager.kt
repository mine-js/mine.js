package org.netherald.minejs.common

interface JavaManager {

    fun getImports(filename: String): ArrayList<Class<*>>

    fun unload()

}