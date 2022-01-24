package org.netherald.minejs.bukkit.linker

import com.eclipsesource.v8.V8
import com.eclipsesource.v8.V8Array
import com.eclipsesource.v8.V8Object
import org.netherald.minejs.bukkit.linker.annotation.JsLinkMethod
import org.netherald.minejs.bukkit.linker.annotation.JsLinkValue

object V8Converter {
    fun fromV8(js: V8Object): JsLinkable {
        val clazz = Class.forName(js.getString("link_class"))
        val values = clazz.declaredFields.map { js.get(it.name) }.toTypedArray()
        return clazz.getDeclaredConstructor(*values.map { it::class.java }.toTypedArray()).newInstance(*values) as JsLinkable
    }

    fun toV8(jvm: JsLinkable, runtime: V8): V8Object {
        val obj = V8Object(runtime)
        obj.add("link_class", jvm::class.java.name)
        for (field in jvm::class.java.declaredFields) {
            field.isAccessible = true
            if (field.isAnnotationPresent(JsLinkValue::class.java)) {
                safeAdd(obj, field.name, field.get(jvm), runtime)
            }
        }
        for (method in jvm::class.java.declaredMethods) {
            obj.registerJavaMethod({ _, parameters ->
                method.isAccessible = true
                if (method.isAnnotationPresent(JsLinkMethod::class.java)) {
                    method.invoke(jvm, *retrieveArray(parameters));
                }
            }, method.name)
        }
        return obj
    }

    fun safeAdd(obj: V8Object, key: String, value: Any, runtime: V8) {
        when (value) {
            is String -> obj.add(key, value)
            is Boolean -> obj.add(key, value)
            is Double -> obj.add(key, value)
            is Int -> obj.add(key, value)
            is JsLinkable -> obj.add(key, toV8(value, runtime))
        }
    }

    fun retrieveArray(array: V8Array): Array<Any> {
        return Array(array.length()) {
            array.get(it)
        }
    }
}