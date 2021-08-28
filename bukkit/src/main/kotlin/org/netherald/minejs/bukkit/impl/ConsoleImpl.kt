package org.netherald.minejs.bukkit.impl

import com.eclipsesource.v8.V8Object
import org.bukkit.plugin.java.JavaPlugin

class ConsoleImpl(val plugin: JavaPlugin) : org.netherald.minejs.common.Console() {

    override fun log(content: Any, fileName: String) {
        var baked = content
        if(content is V8Object) {
            baked = ""
            for (key in content.keys) {
                baked += "$key, "
            }
        }
        plugin.logger.info("[$fileName] $baked")
    }

}