package org.netherald.minejs.common

import com.eclipsesource.v8.*
import java.io.File
import java.lang.UnsupportedOperationException

object ScriptLoader {

    val runtimes = HashMap<File, V8>()

    fun createV8Object(callback: V8Object.() -> Unit) : V8Object {
        for (runtime in runtimes) {
            val baked = V8Object(runtime.value)
            baked.run(callback)
            return baked
        }
        throw UnsupportedOperationException("unknown error!")
    }

    /*
    fun invokeEventWithBaked(name: String, vararg pair: V8ObjectKeyPair, iLike: String = "빵 맛있겠다 - NamuTree0345") {
        for (runtime in runtimes) {
            val baked = V8Object(runtime.value)
            for (keyPair in pair) {
                baked.add(keyPair.name, keyPair.value)
            }
            runtime.value.executeVoidFunction(name, V8Array(runtime.value).push(baked))
        }
    }
     */

    fun invokeEvent(name: String, v8Object: V8Object) {
        for (runtime in runtimes) {
            runtime.value.executeVoidFunction(name, V8Array(runtime.value).push(v8Object))
        }
    }

    fun load(scriptDirectory: File, platform: Platform, playerManager: PlayerManager, console: Console) {
        if(scriptDirectory.isDirectory) {
            for (file in scriptDirectory.listFiles()) {
                if(file.name.endsWith(".js")) {
                    lateinit var runtime: V8
                    if(runtimes.contains(file))
                        runtime = runtimes[file]!!
                    else {
                        runtime = V8.createV8Runtime()
                        runtimes[file] = runtime
                    }
                    runtime.executeVoidScript(file.readText())

                    val consoleObject = V8Object(runtime)

                    runtime.add("console", consoleObject)

                    runtime.registerJavaMethod(JavaCallback { receiver, parameters ->
                        return@JavaCallback platform.name
                    }, "getPlatform")
                    consoleObject.registerJavaMethod({ receiver, parameters ->
                        if(parameters.length() > 0) {
                            console.log(parameters[0], file.name.replace(".js", ""))
                        }
                    }, "log")
                    runtime.registerJavaMethod(JavaCallback { receiver, parameters ->
                        if(parameters.length() > 0)
                            return@JavaCallback playerManager.playerOf(runtime, parameters[0] as String)
                        else
                            return@JavaCallback null
                    }, "playerOf")
                    runtime.executeVoidFunction("onInit", V8Array(runtime))
                }
            }
        }
    }

    fun unload() {
        for (runtime in runtimes) {
            runtime.value.release()
            runtimes.remove(runtime.key)
        }
    }

}