package org.netherald.minejs.bukkit.impl

import org.bukkit.plugin.java.JavaPlugin

class ConsoleImpl(val plugin: JavaPlugin) : org.netherald.minejs.common.Console() {

    override fun log(content: Any, fileName: String) {
        plugin.logger.info("[$fileName] $content")
    }

}