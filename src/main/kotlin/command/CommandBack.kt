package dev.bukgeuk.polaressentials.command

import dev.bukgeuk.polaressentials.util.Color
import org.bukkit.Location
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.util.*

class CommandBack: CommandExecutor {
    companion object {
        @JvmField var BackLocation: MutableMap<UUID, Location> = mutableMapOf()
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender is Player) {
            if (BackLocation[sender.uniqueId] != null) {
                val location = sender.location

                sender.sendMessage("${Color.ORANGE}이전 장소로 돌아갑니다")
                sender.teleport(BackLocation[sender.uniqueId]!!)

                pushLocation(sender.uniqueId, location)
                return true
            }

            sender.sendMessage("${Color.ORANGE}돌아갈 장소가 없습니다")
            return true
        }

        sender.sendMessage("Error: This command isn't available on the console")

        return true
    }

    fun pushLocation(uuid: UUID, location: Location) {
        BackLocation[uuid] = location
    }
}