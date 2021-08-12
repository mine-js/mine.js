package org.netherald.minejs.common

import com.eclipsesource.v8.V8
import com.eclipsesource.v8.V8Function

data class Command(val name: String, val alias: ArrayList<String>, val callback: V8Function, val runtime: V8)
