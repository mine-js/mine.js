package org.netherald.minejs.bukkit

import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import org.netherald.minejs.bukkit.command.ScriptReload
import org.netherald.minejs.bukkit.event.PlayerListener
import org.netherald.minejs.bukkit.impl.ConsoleImpl
import org.netherald.minejs.bukkit.impl.PlayerManagerImpl
import org.netherald.minejs.common.Platform
import org.netherald.minejs.common.ScriptLoader
import java.io.File

class
MineJsBukkit : JavaPlugin() {

    val scriptsDir = File("plugins${File.separator}scripts")

    override fun onEnable() {
        Bukkit.getPluginManager().registerEvents(PlayerListener(this), this)

        getCommand("minejs")!!.setExecutor(ScriptReload(this))

        logger.info("Loading scripts...")
        if(!scriptsDir.exists())
            scriptsDir.mkdir()
        ScriptLoader.load(scriptsDir, Platform.BUKKIT, PlayerManagerImpl(), ConsoleImpl(this))
    }

}