package org.netherald.minejs.common

import com.eclipsesource.v8.JavaCallback
import com.eclipsesource.v8.V8
import com.eclipsesource.v8.V8Array
import com.eclipsesource.v8.V8Object
import java.util.*
import kotlin.collections.ArrayList

object JavaAccsessor {

    fun run(runtime: V8, className: String) : V8Object {
        val res = V8Object(runtime)
        val classReflect = Class.forName(className)
        res.run {
            add("___minejs_javaType___", className)
            registerJavaMethod(JavaCallback { receiver, parameters ->
                val objInstance = V8Object(runtime)
                objInstance.run {
                    val types = ArrayList<Class<*>>()
                    val values = ArrayList<Any>()
                    for (i in 0 until parameters.length()) {
                        val param = parameters[i]
                        if (param is V8Object) {
                            if (param.contains("___minejs_javaType___")) {
                                types.add(Class.forName(param.getString("___minejs_javaType___")))
                                values.add(param)
                            }
                        } else if (param !is V8Array) {
                            types.add(param.javaClass)
                            values.add(param)
                        }
                    }
                    val constructor = classReflect.getDeclaredConstructor(*types.toTypedArray())
                    val instance = constructor.newInstance(*values.toTypedArray())
                    for (field in classReflect.declaredFields) {
                        if (field.canAccess(instance)) {
                            if (field.type is Class<Boolean>) {
                                add(field.name, field.get(instance) as Boolean)
                            } else if (field.type.name.equals("Integer", true)) {
                                add(field.name, field.get(instance) as Int)
                            } else if (field.type.name.equals("String", true)) {
                                add(field.name, field.get(instance) as String)
                            } else if (field.type.name.equals("Double", true)) {
                                add(field.name, field.get(instance) as Double)
                            }
                        }
                    }
                    for (method in classReflect.declaredMethods) {
                        if (method.canAccess(instance)) {
                            registerJavaMethod({ receiver, arguments ->
                                if(arguments.length() > method.parameterCount - 1) {
                                    method.parameters.e
                                }
                            }, "method")
                        }
                    }
                }
                return@JavaCallback res
            }, "new")
        }
        return res
    }

}