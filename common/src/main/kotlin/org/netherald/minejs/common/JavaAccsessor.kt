package org.netherald.minejs.common

import com.eclipsesource.v8.*
import org.reflections.util.Utils
import java.lang.reflect.Method
import java.lang.reflect.Modifier
import java.util.*

object JavaAccsessor {

    val objects = HashMap<String, Any>()
    val staticObjects = HashMap<String, Any>()

    fun javaObjFromObjId(id: String, static: Boolean) : Any? {
        if(static)
            return staticObjects[id]
        return objects[id]
    }

    fun javaObjToV8(obj: Any = Any(), staticClass: Class<*>? = null, static: Boolean, runtime: V8) : V8Object? {
        val generatedKey = UUID.randomUUID().toString()
        val tmp = V8Object(runtime)
        lateinit var tmp2: V8Object
        tmp.run {
            if(static)
                add("___minejs_static_flag___", true)
            if(!static) objects[generatedKey] = obj/* else staticObjects[generatedKey] = obj*/
            add("___minejs_javaType___", if(!static) obj::class.java.typeName else staticClass!!.typeName)
            add("___minejs_objId___", generatedKey)
            //val fields = if(!static) ReflectionUtils.getAllFields(obj::class.java, { s -> s.canAccess(obj) }) else ReflectionUtils.getAllFields(obj::class.java, { s -> s.canAccess(null) })
            //val methods = if(!static) ReflectionUtils.getAllMethods(obj::class.java, { s -> s.canAccess(obj) }) else ReflectionUtils.getAllMethods(obj::class.java, { s -> s.canAccess(null) })
            val methods = Utils.filter(
                Utils.filter(
                    if(!static) obj::class.java.declaredMethods else staticClass!!.declaredMethods,
                    if(static) { s -> Modifier.isStatic(s.modifiers) && s.canAccess(null) } else { s -> !Modifier.isStatic(s.modifiers) && s.canAccess(obj) }
                ))

            val fields = Utils.filter(
                Utils.filter(
                    if(!static) obj::class.java.fields else staticClass!!.fields,
                    if(static) { s -> s.name != "serialVersionUID" && Modifier.isStatic(s.modifiers) && s.canAccess(null) } else { s -> s.name != "serialVersionUID" && s.canAccess(obj) }
                ))
            for (field in fields) {
                if(field.type.name == "Boolean") {
                    add(field.name, if(!static) field.getBoolean(obj) else field.getBoolean(null))
                } else if(field.type.name == "Integer") {
                    add(field.name, if(!static) field.getInt(obj) else field.getInt(null))
                } else if(field.type.name == "String") {
                    add(field.name, if(!static) field.get(obj) as String else field.get(null) as String)
                } else if(field.type.name == "Double") {
                    add(field.name, if(!static) field.getDouble(obj) else field.getDouble(null))
                } else {
                    add(field.name, javaObjToV8(obj = field.get(null), static = false, runtime = runtime))
                }
            }
            tmp2 = V8Object(runtime)
            tmp2.add("tmp", this)
            var before: String? = null
            var beforeCount = 1
            for (method in methods) {
                var name = method.name + beforeCount
                if(method.name == before) {
                    beforeCount++
                    name = method.name + beforeCount
                } else {
                    before = method.name
                    beforeCount = 1
                }
                registerJavaMethod(JavaCallback { receiver, parameters ->
                    val madeParamTypes = ArrayList<Class<*>>()
                    val madeParamValues = ArrayList<Any>()
                    for(i in 0 until parameters.length()) {
                        val param = parameters[i]
                        //val jParam = method.parameters[i]
                        //println("jParam: ${jParam.type.typeName}, param: ${param::class.java.typeName}")
                            //if (jParam.type.typeName == param::class.java.typeName) {
                            if(param !is V8Object) {
                                madeParamTypes.add(param::class.java)
                                madeParamValues.add(param)
                                //madeParamValues.add(jParam.type.cast(param))
                            //} else if (param is V8Object) {
                            } else {
                                if (param.contains("___minejs_objId___")) {
                                    val j = javaObjFromObjId(param.getString("___minejs_objId___"), static)
                                    if (j != null) {
                                        madeParamTypes.add(j::class.java)
                                        madeParamValues.add(j)
                                    }
                                }
                            }
                    }

                    //println("madeParamValues: ${madeParamValues.size}")
                    //println("method: " + method.parameters.size)

                    val returnValue = if(static) method.invoke(null, *madeParamValues.toTypedArray()) else method.invoke(obj, *madeParamValues.toTypedArray())

                    if(returnValue is Boolean) {
                        return@JavaCallback returnValue
                    } else if(returnValue is Int) {
                        return@JavaCallback returnValue
                    } else if(returnValue is String) {
                        return@JavaCallback returnValue
                    } else if(returnValue is Boolean) {
                        return@JavaCallback returnValue
                    } else if(returnValue !is Unit && returnValue != null) {
                        return@JavaCallback javaObjToV8(obj = returnValue, static = false, runtime = runtime)
                    } else {
                        return@JavaCallback null
                    }

                }, name)
            }
        }
        return tmp2.getObject("tmp")
    }

    fun run(runtime: V8, className: String) : V8Object {
        val classReflect = Class.forName(className)
        val res = javaObjToV8(staticClass = classReflect, static = true, runtime = runtime)
        res!!.run {
            val objId = UUID.randomUUID()
            add("___minejs_javaType___", className)
            registerJavaMethod(JavaCallback { receiver, parameters ->
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
                                    values.add(staticObjects[parameter.getString("___minejs_objId___")]!!)
                                }
                            } else if(parameter.contains("___minejs_long_flag___")) {
                                types.add(Long::class.java)
                                values.add(parameter.getInteger("value").toLong())
                            }
                        }
                    }

                    val searchedConstructors = classReflect.getDeclaredConstructor(*types.toTypedArray())

                    val instance = searchedConstructors.newInstance(*values.toTypedArray())
                return@JavaCallback javaObjToV8(obj = instance, static = false, runtime = runtime)!!
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