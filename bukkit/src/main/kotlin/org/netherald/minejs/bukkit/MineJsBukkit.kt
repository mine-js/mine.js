package org.netherald.minejs.bukkit

import org.bukkit.plugin.java.JavaPlugin
import org.netherald.minejs.bukkit.impl.ConsoleImpl
import org.netherald.minejs.bukkit.impl.PlayerManagerImpl
import org.netherald.minejs.common.Platform
import org.netherald.minejs.common.ScriptLoader
import java.io.File

class MineJsBukkit : JavaPlugin() {

    val scriptsDir = File("plugins${File.separator}scripts")

    override fun onEnable() {
        logger.info("Loading scripts...")
        if(!scriptsDir.exists())
            scriptsDir.mkdir()
        ScriptLoader.load(scriptsDir, Platform.BUKKIT, PlayerManagerImpl(), ConsoleImpl(this))
    }

}