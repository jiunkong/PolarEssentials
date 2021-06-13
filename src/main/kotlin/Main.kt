package dev.bukgeuk.polaressentials

import org.bukkit.plugin.java.JavaPlugin
import dev.bukgeuk.polaressentials.command.*

class PolarEssentials: JavaPlugin() {
    override fun onEnable() {
        logger.info("Plugin Enabled")

        config.addDefault("home-limit", 3)
        config.addDefault("tpa-request-expiration", 60)
        config.addDefault("use-colored-chat", false)
        config.addDefault("motd", "A Minecraft Server")
        config.addDefault("welcome-message", "&dWelcome to the Server!")
        config.addDefault("use-combine-enchantments", false)
        config.addDefault("combine-enchantments-limit", 10)
        config.options().copyDefaults(true)
        saveConfig()

        getCommand("now")?.setExecutor(CommandNow())
        getCommand("dm")?.setExecutor(CommandDM())
        getCommand("spawn")?.setExecutor(CommandSpawn(dataFolder.absolutePath))
        getCommand("setspawn")?.setExecutor(CommandSetSpawn(dataFolder.absolutePath))
        getCommand("back")?.setExecutor(CommandBack())
        getCommand("tpa")?.setExecutor(CommandTpa())
        getCommand("tpaccept")?.setExecutor(CommandTpaccept())
        getCommand("tpahere")?.setExecutor(CommandTpahere())
        getCommand("tpdeny")?.setExecutor(CommandTpdeny())
        getCommand("sethome")?.setExecutor(CommandSetHome(dataFolder.absolutePath, config.getInt("home-limit")))
        getCommand("home")?.setExecutor(CommandHome(dataFolder.absolutePath))
        getCommand("delhome")?.setExecutor(CommandDelHome(dataFolder.absolutePath))
        getCommand("homes")?.setExecutor(CommandHomes(dataFolder.absolutePath, config.getInt("home-limit")))
        getCommand("motd")?.setExecutor(CommandMotd(this))
        getCommand("timer")?.setExecutor(CommandTimer())

        ServerListPingEvent().setMotd(config.getString("motd") ?: "A Minecraft Server")

        server.pluginManager.registerEvents(ServerListPingEvent(), this)
        server.pluginManager.registerEvents(PlayerJoinEvent(dataFolder.absolutePath,
            config.getString("welcome-message") ?: "&dWelcome to Server!"), this)
        if (config.getBoolean("use-colored-chat")) {
            server.pluginManager.registerEvents(ChatEvent(), this)
        }
        if (config.getBoolean("use-combine-enchantments")) {
            server.pluginManager.registerEvents(PrepareItemCraftEvent(config.getInt("combine-enchantments-limit")), this)
        }

        RequestExpirationTimer.expirationTime = config.getLong("tpa-request-expiration")
    }

    override fun onDisable() {
        logger.info("Plugin Disabled")
    }
}