package org.netherald.minejs.bukkit.utils

import com.eclipsesource.v8.JavaCallback
import com.eclipsesource.v8.V8Object

class GetterSetter<T>(init: GetterSetter<T>.() -> Unit) {
    private lateinit var getterValue: () -> T
    private lateinit var setterValue: (T) -> Unit

    init {
        init(this)
    }

    fun getter(getter: () -> T) {
        this.getterValue = getter
    }

    fun setter(setter: (T) -> Unit) {
        this.setterValue = setter
    }

    fun register(name: String, obj: V8Object) {
        if(this::getterValue.isInitialized) {
            obj.registerJavaMethod(JavaCallback { _, _ ->
                this.getterValue()
            }, "get${name.replaceFirstChar { it.uppercaseChar() }}")
        }

        if(this::setterValue.isInitialized) {
            obj.registerJavaMethod(JavaCallback { _, parameters ->
                @Suppress("unchecked_cast")
                this.setterValue(parameters.getOrNull(0) as T)
            }, "set${name.replaceFirstChar { it.uppercaseChar() }}")
        }
    }
}

@Suppress("FunctionName")
fun <T> V8Object.GetterSetter(name: String, init: GetterSetter<T>.() -> Unit): GetterSetter<T> {
    return GetterSetter(init).also {
        it.register(name, this)
    }
}