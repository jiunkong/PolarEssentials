package dev.bukgeuk.polaressentials

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import dev.bukgeuk.polaressentials.command.World
import dev.bukgeuk.polaressentials.util.Color
import dev.bukgeuk.polaressentials.util.ColoredChat
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import java.io.File
import java.util.*

class PlayerJoinEvent(private val dataFolder: String, private val message: String): Listener {
    @EventHandler
    fun onPlayerJoinEvent(e: PlayerJoinEvent) {
        val data: MutableList<UUID>
        val mapper = jacksonObjectMapper()

        val file = File("$dataFolder/user/users.json")

        data = if (file.exists()) {
            mapper.readValue(file)
        } else {
            File(dataFolder, "user").mkdir()
            mutableListOf()
        }

        if(!data.contains(e.player.uniqueId)) {
            data.add(e.player.uniqueId)
            mapper.writerWithDefaultPrettyPrinter().writeValue(file, data)

            e.player.sendMessage(ColoredChat().hexToColor(message))
        }
    }
}