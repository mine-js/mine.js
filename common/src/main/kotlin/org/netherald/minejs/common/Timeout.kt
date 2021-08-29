package org.netherald.minejs.common

import com.eclipsesource.v8.V8
import com.eclipsesource.v8.V8Function

interface Timeout {

    fun setTimeout(runtime: V8, function: V8Function, delay: Int) : Int

    fun setInterval(runtime: V8, function: V8Function, interval: Int) : Int

    fun clearInterval(runtime: V8, id: Int)

}