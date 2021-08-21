package org.netherald.minejs.common

import com.eclipsesource.v8.*
import java.io.File
import java.lang.UnsupportedOperationException

object ScriptLoader {

    val runtimes = HashMap<File, V8>()
    val commands = ArrayList<Command>()

    var libStr = ""

    private var commandManager: CommandManager? = null

    @Deprecated("Non used method!", ReplaceWith("invokeEvent", "org.netherald.minejs.common.ScriptLoader"))
    fun createV8Object(callback: V8Object.() -> Unit) : V8Object {
        /*
        for (runtime in runtimes) {
            val baked = V8Object(runtime.value)
            baked.run(callback)
            return baked
        }

         */
        throw UnsupportedOperationException("Unused function!")
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

    fun invokeEvent(name: String, v8Object: V8Object.() -> Unit) {
        for (runtime in runtimes) {
            try {
                val obj = V8Object(runtime.value)
                obj.run(v8Object)
                runtime.value.executeVoidFunction(name, V8Array(runtime.value).push(obj))
            } catch(e: V8ScriptExecutionException) {
                if(!e.jsMessage.startsWith("TypeError: undefined")) {
                    e.printStackTrace()
                }
            }
        }
    }

    var alreadyLoadStorage = false

    fun load(scriptDirectory: File, storageFile: File, platform: Platform, playerManager: PlayerManager, itemManager: ItemManager, console: Console, commandManager: CommandManager) {
        if(scriptDirectory.isDirectory) {
            val files = scriptDirectory.listFiles()
            files.sort()
            for (file in files) {
                if(file.name.endsWith(".lib.js")) {
                    console.log("Loading library ${file.name}", "core")
                    libStr += file.readText() + "\n"
                } else if(file.name.endsWith(".js")) {
                    if(file.name.endsWith(".internal.js")) console.log("Loading internal ${file.name}", "core") else console.log("Loading ${file.name}", "core")
                    lateinit var runtime: V8
                    if(runtimes.contains(file))
                        runtime = runtimes[file]!!
                    else {
                        runtime = V8.createV8Runtime()
                        runtimes[file] = runtime
                    }
                    runtime.executeVoidScript("\"use strict\";\n" + libStr + "\n" + file.readText())

                    val consoleObject = V8Object(runtime)
                    val storageObject = V8Object(runtime)

                    runtime.add("console", consoleObject)
                    runtime.add("storage", storageObject)

                    storageObject.registerJavaMethod(JavaCallback { receiver, parameters ->
                        if(parameters.length() > 0) {
                            if(!alreadyLoadStorage) {
                                alreadyLoadStorage = true
                                Storage.read(storageFile)
                            }
                            val returnValue = Storage.get(parameters[0] as String)
                            if (returnValue is Boolean) {
                                return@JavaCallback returnValue
                            } else if (returnValue is Int) {
                                return@JavaCallback returnValue
                            } else if (returnValue is String) {
                                return@JavaCallback returnValue
                            }
                        }
                        return@JavaCallback null
                    }, "get")
                    storageObject.registerJavaMethod(JavaCallback { receiver, parameters ->
                        if(parameters.length() > 1) {
                            Storage.set(parameters[0] as String, parameters[1])
                            Storage.save(storageFile)
                        }
                        return@JavaCallback null
                    }, "set")

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
                            return@JavaCallback JavaAccsessor.run(runtime, parameters[0] as String)
                        else
                            return@JavaCallback null
                    }, "jclass")
                    runtime.registerJavaMethod(JavaCallback { receiver, parameters ->
                        if(parameters.length() > 0)
                            return@JavaCallback playerManager.playerOf(runtime, parameters[0] as String)
                        else
                            return@JavaCallback null
                    }, "playerOf")
                    runtime.registerJavaMethod(JavaCallback { receiver, parameters ->
                        if (parameters.length() > 0)
                            return@JavaCallback itemManager.itemOf(runtime,parameters[0] as String)
                        else
                            return@JavaCallback null
                    },"itemOf")
                    runtime.registerJavaMethod(JavaCallback { receiver, parameters ->
                        if (parameters.length() > 0)
                            return@JavaCallback V8Object(runtime).run {
                                add("___minejs_long_flag___", true)
                                add("value", parameters[0] as Int)
                            }
                        else
                            return@JavaCallback null
                    }, "jlong")
                    runtime.registerJavaMethod({ receiver, parameters ->
                        // createCommand("test", ["alias1", "alias2"], (ctx) => { // ... })
                        if (parameters.length() > 2) {
                            console.log("Callback - ${parameters[2].javaClass.name}", "core")
                            val aliases = ArrayList<String>()
                            val aliasesRaw = (parameters[1] as V8Array)
                            for(i in 0 until aliasesRaw.length()) {
                                aliases.add(aliasesRaw.getString(i))
                            }
                            commands.add(Command(parameters[0] as String, aliases, parameters[2] as V8Function, runtime))
                        }
                    },"createCommand")

                    try {
                        runtime.executeVoidFunction("onInit", V8Array(runtime))
                    } catch (ex: V8ScriptExecutionException) {
                        if(ex.jsMessage != "TypeError: undefined is not a function") {
                            ex.printStackTrace()
                        }
                    }

                    this.commandManager = commandManager

                    commandManager.registerCommands(commands)
                }
            }
        }
    }

    fun unload() {
        commandManager?.unloadCommands()
        //commandManager = null
        libStr = ""
        commands.clear()
        runtimes.clear()
        /*
        for(itr in runtimes.iterator()) {
            if (runtimes.iterator().hasNext()) {
                runtimes.iterator().next().value.release()
                runtimes.iterator().remove()
            }
        }*/
    }

}