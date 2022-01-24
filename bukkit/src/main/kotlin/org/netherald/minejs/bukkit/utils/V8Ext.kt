package org.netherald.minejs.bukkit.utils

import com.eclipsesource.v8.V8Array
import com.eclipsesource.v8.V8Object
import com.eclipsesource.v8.V8Value

fun V8Object.addNullable(key: String, obj: V8Value?) {
    if (obj == null) {
        this.addUndefined(key)
    } else {
        this.add(key, obj)
    }
}

fun V8Array.getOrNull(index: Int): Any? {
    return if (index > length()) this.get(index) else null
}