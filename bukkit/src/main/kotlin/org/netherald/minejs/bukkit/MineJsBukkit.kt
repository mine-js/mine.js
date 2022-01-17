package org.netherald.minejs.bukkit

import com.comphenix.protocol.ProtocolManager
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import org.netherald.minejs.bukkit.command.MineJSCommand
import org.netherald.minejs.bukkit.command.MineJSTabCompleter
import org.netherald.minejs.bukkit.event.BlockListener
import org.netherald.minejs.bukkit.event.EntityListener
import org.netherald.minejs.bukkit.event.MiscListener
import org.netherald.minejs.bukkit.event.PlayerListener
import org.netherald.minejs.bukkit.impl.*
import org.netherald.minejs.bukkit.native.NativeLoader
import org.netherald.minejs.bukkit.utils.ProtocolUtil
import org.netherald.minejs.common.Platform
import org.netherald.minejs.common.ScriptLoader
import java.io.File

var protocolEnabled = false

class MineJsBukkit : JavaPlugin() {

    val scriptsDir = File("plugins${File.separator}scripts")

    companion object {
        lateinit var instance: MineJsBukkit
    }

    override fun onEnable() {
        instance = this
        saveDefaultConfig()
        Bukkit.getPluginManager().registerEvents(PlayerListener(this), this)
        Bukkit.getPluginManager().registerEvents(EntityListener(), this)
        Bukkit.getPluginManager().registerEvents(BlockListener(), this)
        Bukkit.getPluginManager().registerEvents(MiscListener(this), this)

        getCommand("minejs")!!.setExecutor(MineJSCommand(this))
        getCommand("minejs")!!.tabCompleter = MineJSTabCompleter()

        protocolEnabled = Bukkit.getPluginManager().isPluginEnabled("ProtocolLib")
        if(protocolEnabled)
            ProtocolUtil.init()
        else
            logger.warning("You may not use packet feature. Install ProtocolLib to use packet feature!")

        logger.info("Loading scripts...")
        if(!scriptsDir.exists())
            scriptsDir.mkdir()
        load()
    }

    override fun onDisable() {
        NativeLoader.disableAll()
        ScriptLoader.unload()
    }

    fun load() {
        Bukkit.getScheduler().cancelTasks(this)
        logger.info("Loading native addons...")
        NativeLoader.enableAll(File(dataFolder, "addons"))
        ScriptLoader.load(scriptsDir, File(scriptsDir, "storage.json"), Platform.BUKKIT, PlayerManagerImpl(), ItemManagerImpl(), ConsoleImpl(this), CommandManagerImpl(this), TimeoutImpl(this), JavaManagerImpl(), NativeInitImpl())
    }

}