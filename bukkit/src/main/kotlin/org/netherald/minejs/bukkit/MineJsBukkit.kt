package org.netherald.minejs.bukkit

import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import org.netherald.minejs.bukkit.command.MineJSCommand
import org.netherald.minejs.bukkit.command.MineJSTabCompleter
import org.netherald.minejs.bukkit.event.BlockListener
import org.netherald.minejs.bukkit.event.EntityListener
import org.netherald.minejs.bukkit.event.MiscListener
import org.netherald.minejs.bukkit.event.PlayerListener
import org.netherald.minejs.bukkit.impl.CommandManagerImpl
import org.netherald.minejs.bukkit.impl.ConsoleImpl
import org.netherald.minejs.bukkit.impl.ItemManagerImpl
import org.netherald.minejs.bukkit.impl.PlayerManagerImpl
import org.netherald.minejs.common.Platform
import org.netherald.minejs.common.ScriptLoader
import java.io.File

class
MineJsBukkit : JavaPlugin() {

    val scriptsDir = File("plugins${File.separator}scripts")

    override fun onEnable() {
        Bukkit.getPluginManager().registerEvents(PlayerListener(this), this)
        Bukkit.getPluginManager().registerEvents(EntityListener(), this)
        Bukkit.getPluginManager().registerEvents(BlockListener(), this)
        Bukkit.getPluginManager().registerEvents(MiscListener(), this)

        getCommand("minejs")!!.setExecutor(MineJSCommand(this))
        getCommand("minejs")!!.tabCompleter = MineJSTabCompleter()

        logger.info("Loading scripts...")
        if(!scriptsDir.exists())
            scriptsDir.mkdir()
        load()
    }

    fun load() {
        ScriptLoader.load(scriptsDir, File(scriptsDir, "storage.json"), Platform.BUKKIT, PlayerManagerImpl(), ItemManagerImpl(), ConsoleImpl(this), CommandManagerImpl(this))
    }

}