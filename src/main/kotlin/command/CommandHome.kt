package dev.bukgeuk.polaressentials.command

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import dev.bukgeuk.polaressentials.util.Color
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.io.File

data class Home(
    val world: String,
    val location: List<Double>,
    val direction: List<Float>
)

class CommandSetHome(private val dataFolder: String, private val homeLimit: Int): CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender is Player) {
            val data: MutableMap<String, Home>
            val mapper = jacksonObjectMapper()

            val file = File("$dataFolder/home/${sender.uniqueId}.json")

            data = if (file.exists()) {
                mapper.readValue(file)
            } else {
                if (!File("$dataFolder/home/").exists()) File(dataFolder, "home").mkdir()
                mutableMapOf()
            }

            var name = "home"
            if (args.isNotEmpty()) name = args[0]

            if (data.keys.size >= homeLimit && data[name] == null) {
                sender.sendMessage("${Color.ORANGE}집은 최대 ${Color.RED}${homeLimit}개${Color.ORANGE}까지만 가질 수 있습니다")
                return true
            }

            data[name] = Home(
                sender.world.name,
                listOf(sender.location.x, sender.location.y, sender.location.z),
                listOf(sender.location.yaw, sender.location.pitch)
            )

            mapper.writerWithDefaultPrettyPrinter().writeValue(file, data)

            sender.sendMessage(
                "${Color.ORANGE}집 ${Color.RED}${name}${Color.ORANGE}을(를) " +
                        "${Color.RED}${String.format("%.1f", sender.location.x)}, ${String.format("%.1f", sender.location.y)}, ${String.format("%.1f", sender.location.z)}, ${sender.world.name}${Color.ORANGE} 로 설정했습니다"
            )

            return true
        }

        sender.sendMessage("Error: This command isn't available on the console")

        return true
    }
}

class CommandHome(private val dataFolder: String): CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender is Player) {
            val data: MutableMap<String, Home>
            val mapper = jacksonObjectMapper()

            val file = File("$dataFolder/home/${sender.uniqueId}.json")

            if (file.exists()) {
                data = mapper.readValue(file)
            } else {
                sender.sendMessage("집을 찾을 수 없습니다")
                return true
            }

            var name = "home"
            if (args.isNotEmpty()) name = args[0]

            if (data[name] == null) {
                sender.sendMessage("집을 찾을 수 없습니다")
                return true
            }

            val location = data[name]!!.location
            val direction = data[name]!!.direction
            sender.sendMessage("${Color.RED}${name}${Color.ORANGE}(으)로 이동합니다")
            sender.teleport(Location(Bukkit.getWorld(data[name]!!.world), location[0], location[1], location[2], direction[0], direction[1]))

            return true
        }

        sender.sendMessage("Error: This command isn't available on the console")

        return true
    }
}

class CommandDelHome(private val dataFolder: String): CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender is Player) {
            val data: MutableMap<String, Home>
            val mapper = jacksonObjectMapper()

            val file = File("$dataFolder/home/${sender.uniqueId}.json")

            if (file.exists()) {
                data = mapper.readValue(file)
            } else {
                sender.sendMessage("집을 찾을 수 없습니다")
                return true
            }

            var name = "home"
            if (args.isNotEmpty()) name = args[0]

            if (data[name] == null) {
                sender.sendMessage("집을 찾을 수 없습니다")
                return true
            }
            
            data.remove(name)

            mapper.writerWithDefaultPrettyPrinter().writeValue(file, data)

            sender.sendMessage(
                "${Color.ORANGE}집 ${Color.RED}${name}${Color.ORANGE}을(를) 삭제했습니다"
            )

            return true
        }

        sender.sendMessage("Error: This command isn't available on the console")

        return true
    }
}

class CommandHomes(private val dataFolder: String, private val homeLimit: Int): CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender is Player) {
            val data: MutableMap<String, Home>
            val mapper = jacksonObjectMapper()

            val file = File("$dataFolder/home/${sender.uniqueId}.json")

            if (file.exists()) {
                data = mapper.readValue(file)
            } else {
                sender.sendMessage("${Color.ORANGE}총 ${Color.RED}${homeLimit}개${Color.ORANGE} 중 ${Color.RED}0개${Color.ORANGE}의 집을 가지고 있습니다")
                return true
            }

            sender.sendMessage("${Color.ORANGE}총 ${Color.RED}${homeLimit}개${Color.ORANGE} 중 ${Color.RED}${data.keys.size}개${Color.ORANGE}의 집을 가지고 있습니다")

            for (item in data.keys) {
                val location = data[item]!!.location
                sender.sendMessage("  - ${Color.ORANGE}${item} " +
                        "${Color.RED}${String.format("%.1f", location[0])}, ${String.format("%.1f", location[1])}, ${String.format("%.1f", location[2])}, ${data[item]!!.world}")
            }

            return true
        }

        sender.sendMessage("Error: This command isn't available on the console")

        return true
    }
}