package org.netherald.minejs.common

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import java.io.File

object Storage {

    private val stor = HashMap<String, Any>()

    fun get(key: String) : Any? {
        return stor[key]
    }

    fun set(key: String, value: Any) {
        stor[key] = value
    }

    fun save(file: File) {
        val jsonObject = JsonObject()
        for (entry in stor) {
            if(entry.value is Boolean)
                jsonObject.addProperty(entry.key, entry.value as Boolean)
            else if(entry.value is Int)
                jsonObject.addProperty(entry.key, entry.value as Int)
            else if(entry.value is String)
                jsonObject.addProperty(entry.key, entry.value as String)
        }
        file.writeText(Gson().toJson(jsonObject))
    }

    fun read(file: File) {
        if(!file.exists())
            file.writeText("{}")
        stor.clear()
        val jsonObject = JsonParser().parse(file.readText()).asJsonObject
        for (entry in jsonObject.entrySet()) {
            val je = entry.value
            val jp = entry.value.asJsonPrimitive
            if(jp.isBoolean)
                stor[entry.key] = je.asBoolean
            else if(jp.isNumber)
                stor[entry.key] = je.asNumber
            else if(jp.isString)
                stor[entry.key] = je.asString
        }
    }

}