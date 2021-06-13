package dev.bukgeuk.polaressentials.command

import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import dev.bukgeuk.polaressentials.util.Color

class CommandNow: CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (args.isEmpty() && sender is Player) {
            val time: Int = ((Bukkit.getWorld(sender.world.name)!!.time + 6000) % 24000).toInt()

            var hour = time / 1000
            var minute = (time % 1000) / (1000 / 60)
            if (minute >= 60) {
                hour += 1
                minute -= 60
            }
            if (hour > 24) hour -= 24

            val str = when (hour) {
                0 -> "오전 12시 ${minute}분"
                in 1..11 -> "오전 ${hour}시 ${minute}분"
                12 -> "오후 12시 ${minute}분"
                else -> "오후 ${hour - 12}시 ${minute}분"
            }
            sender.sendMessage("${Color.RED}${sender.world.name}${Color.ORANGE} 세계의 현재 시각은 ${Color.RED}$str${Color.ORANGE} 입니다")

            return true
        }

        return false
    }
}