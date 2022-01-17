package org.netherald.minejs.bukkit.impl

import com.eclipsesource.v8.V8
import org.netherald.minejs.bukkit.native.NativeLoader
import org.netherald.minejs.common.interfaces.NativeInit

class NativeInitImpl : NativeInit {
    override fun init(): V8.() -> Unit {
        return {
            for (addon in NativeLoader.addons) {
                for (function in addon.value.voidFunctions) {
                    registerJavaMethod(function.value, function.key)
                }
                for (function in addon.value.functions) {
                    registerJavaMethod(function.value, function.key)
                }
            }
        }
    }

}
