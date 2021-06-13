package dev.bukgeuk.polaressentials

import dev.bukgeuk.polaressentials.util.ColoredChat
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.server.ServerListPingEvent
import org.bukkit.plugin.java.JavaPlugin

class CommandMotd(private val plugin: JavaPlugin): CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (args.isNotEmpty()) {
            val str = args.joinToString(" ")
            plugin.config.set("motd", str)
            plugin.saveConfig()

            ServerListPingEvent().setMotd(str)

            sender.sendMessage("${dev.bukgeuk.polaressentials.util.Color.ORANGE}MOTD가 변경되었습니다")

            return true
        }

        return false
    }
}

class ServerListPingEvent: Listener {
    companion object {
        lateinit var motd: String
    }

    @EventHandler
    fun onServerListPingEvent(e: ServerListPingEvent) {
        var str = ColoredChat().hexToColor(motd)
        str = str.replace("\${numPlayers}", e.numPlayers.toString())

        e.motd = str
    }

    fun setMotd(text: String) {
        motd = text
    }
}