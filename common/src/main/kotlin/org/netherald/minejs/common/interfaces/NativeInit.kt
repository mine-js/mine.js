package org.netherald.minejs.common.interfaces

import com.eclipsesource.v8.V8

interface NativeInit {

    companion object {
        class DefaultNativeInit: NativeInit {
            override fun init(): V8.() -> Unit {
                return {}
            }
        }
    }

    fun init(): V8.() -> Unit

}