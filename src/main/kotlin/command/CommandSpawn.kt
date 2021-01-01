package dev.bukgeuk.polaressentials.command

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import dev.bukgeuk.polaressentials.util.Color
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Location
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.io.File
import kotlin.math.floor
import kotlin.math.roundToLong

data class World(
    val location: List<Double>, // [x, y, z]
    val direction: List<Float> // [yaw, pitch]
)

class CommandSetSpawn(private val dataFolder: String): CommandExecutor {

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender is Player) {
            val data: MutableMap<String, World>
            val mapper = jacksonObjectMapper()

            val file = File("$dataFolder/spawn/spawns.json")

            data = if (file.exists()) {
                mapper.readValue(file)
            } else {
                File(dataFolder, "spawn").mkdir()
                mutableMapOf()
            }

            data[sender.world.name] = World(
                    listOf(sender.location.x, sender.location.y, sender.location.z),
                    listOf(sender.location.yaw, sender.location.pitch)
            )

            mapper.writerWithDefaultPrettyPrinter().writeValue(file, data)

            sender.sendMessage(
                    "${Color.ORANGE}이제 ${Color.RED}${sender.world.name}${Color.ORANGE} 세계의 스폰은 " +
                            "${Color.RED}${String.format("%.1f", sender.location.x)}, ${String.format("%.1f", sender.location.y)}, ${String.format("%.1f", sender.location.z)}${Color.ORANGE} 입니다"
            )

            return true
        }

        sender.sendMessage("Error: This command isn't available on the console")

        return true
    }
}

class CommandSpawn(private val dataFolder: String): CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender is Player) {
            val data: MutableMap<String, World>
            val mapper = jacksonObjectMapper()

            val file = File("$dataFolder/spawn/spawns.json")

            if (file.exists()) {
                data = mapper.readValue(file)
            } else {
                File(dataFolder, "spawn").mkdir()
                sender.sendMessage("${Color.RED}스폰${Color.ORANGE}이 등록되지 않았습니다")
                return true
            }

            if (data[sender.world.name] == null) {
                sender.sendMessage("${Color.RED}스폰${Color.ORANGE}이 등록되지 않았습니다")
                return true
            }

            CommandBack().pushLocation(sender.uniqueId, sender.location)

            val location = data[sender.world.name]!!.location
            val direction = data[sender.world.name]!!.direction

            sender.sendMessage("${Color.RED}스폰${Color.ORANGE}으로 이동합니다")
            sender.teleport(Location(sender.world, location[0], location[1], location[2], direction[0], direction[1]))

            return true
        }

        sender.sendMessage("Error: This command isn't available on the console")

        return true
    }
}

