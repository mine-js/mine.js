package org.netherald.minejs.common

import com.eclipsesource.v8.*
import com.eclipsesource.v8.utils.V8ObjectUtils
import org.reflections.ReflectionUtils
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

object JavaAccsessor {

    val objects = HashMap<String, Any>()
    val staticObjects = HashMap<String, Any>()

    fun run(runtime: V8, className: String) : V8Object {
        val res = V8Object(runtime)
        val classReflect = Class.forName(className)
        res.run {
            val objId = UUID.randomUUID()
            add("___minejs_javaType___", className)
            registerJavaMethod(JavaCallback { receiver, parameters ->
                val objInstance = V8Object(runtime)
                lateinit var tmp: V8Object
                objInstance.run {
                    val types = ArrayList<Class<*>>()
                    val values = ArrayList<Any>()

                    for(i in 0 until parameters.length()) {
                        val parameter = parameters[i]
                        if(parameter !is V8Object) {
                            types.add(parameter.javaClass)
                            values.add(parameter)
                        } else {
                            if(parameter.contains("___minejs_javaType___") && parameter.contains("___minejs_objId___")) {
                                if(!parameter.contains("__minejs_static_flag___")) {
                                    types.add(Class.forName(parameter.getString("___minejs_javaType___")))
                                    values.add(objects[parameter.getString("___minejs_objId___")]!!)
                                } else {
                                    types.add(Class.forName(parameter.getString("___minejs_javaType___")))
                                    values.add(staticObjects[parameter.getString("___minejs_static_objId___")]!!)
                                }
                            }
                        }
                    }

                    tmp = V8Object(runtime)
                    tmp.add("bak", objInstance)

                    val searchedConstructors = classReflect.getDeclaredConstructor(*types.toTypedArray())

                    val instance = searchedConstructors.newInstance(*values.toTypedArray())
                    for (field in classReflect.fields) {
                        if (field.canAccess(instance)) {
                            if (field.type.name.equals("Boolean")) {
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
                    for (methodRaw in classReflect.methods) {
                        if (methodRaw.canAccess(instance)) {
                            /*
                            val bakedTypes = method.parameterTypes
                            for (i in 0 until bakedTypes.size) {
                                val bakedType = bakedTypes[i]
                                if(!bakedType.name.equals("Boolean", true) || !bakedType.name.equals("Integer", true) || !bakedType.name.equals("String", true) || !bakedType.name.equals("Double", true)) {
                                    bakedTypes[i] = V8Object::class.java
                                }
                            }
                             */
                            registerJavaMethod(JavaCallback { receiver, parameters ->
                                    val baked = arrayListOf<Any>()
                                    val bakedTypes = arrayListOf<Class<*>>()
                                    for(i in 0 until parameters.length()) {
                                        val parameter = parameters[i]
                                        if(parameter !is V8Object) {
                                            bakedTypes.add(parameter.javaClass)
                                            baked.add(parameter)
                                        } else {
                                            if(parameter.contains("___minejs_javaType___") && parameter.contains("___minejs_objId___")) {
                                                if(!parameter.contains("__minejs_static_flag___")) {
                                                    bakedTypes.add(Class.forName(parameter.getString("___minejs_javaType___")))
                                                    baked.add(objects[parameter.getString("___minejs_objId___")]!!)
                                                } else {
                                                    bakedTypes.add(Class.forName(parameter.getString("___minejs_javaType___")))
                                                    baked.add(staticObjects[parameter.getString("___minejs_static_objId___")]!!)
                                                }
                                            }
                                        }
                                    }
                                    /*
                                    for (i in 0 until parameters.length()) {
                                        val type = method.parameterTypes[i]
                                        println(type.name)
                                        if(!type.name.equals("Boolean", true) || !type.name.equals("Integer", true) || !type.name.equals("String", true) || !type.name.equals("Double", true)) {
                                            if(parameters[i] is V8Object) {
                                                val parameter = parameters[i] as V8Object
                                                if (parameter.contains("___minejs_javaType___") && parameter.contains("___minejs_objId___")) {
                                                    if (!parameter.contains("__minejs_static_flag___")) {
                                                        baked.add(objects[parameter.getString("___minejs_objId___")]!!)
                                                    } else {
                                                        baked.add(staticObjects[parameter.getString("___minejs_static_objId___")]!!)
                                                    }
                                                }
                                            }
                                        } else {
                                            println(parameters[i])
                                            baked.add(parameters[i])
                                        }
                                    }

                                     */
                                    val methodFound = ReflectionUtils.getAllMethods(classReflect,
                                        ReflectionUtils.withParameters(*bakedTypes.toTypedArray()),
                                        ReflectionUtils.withName(methodRaw.name))
                                println("NAME: ${methodRaw.name}")
                                for (bakedType in bakedTypes) {
                                    println("BTA: ${bakedType.name}")
                                }
                                    if(!methodFound.isNullOrEmpty()) {
                                        val method = methodFound.first()
                                        val returnRes = method.invoke(instance, *baked.toTypedArray())
                                        for (any in baked.toTypedArray()) {
                                            print(any)
                                        }
                                        println()
                                        println(method.name)
                                        println(returnRes)
                                        return@JavaCallback returnRes
                                    }
                                return@JavaCallback null
                            }, methodRaw.name)
                        }
                    }
                    objects[objId.toString()] = instance
                }
                return@JavaCallback tmp.getObject("bak")
            }, "new")
            add("___minejs_objId___", objId.toString())
        }
        return res
    }

}

/*
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

 */