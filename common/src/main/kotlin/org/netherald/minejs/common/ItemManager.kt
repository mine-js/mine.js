package org.netherald.minejs.common

import com.eclipsesource.v8.V8
import com.eclipsesource.v8.V8Object

interface ItemManager {
    fun itemOf(runtime : V8, material : String) : V8Object
}