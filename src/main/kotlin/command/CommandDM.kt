package dev.bukgeuk.polaressentials.command

import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import dev.bukgeuk.polaressentials.util.Color

class CommandDM: CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender is Player) {
            if (args.size > 1) {
                val p = Bukkit.getPlayer(args[0])
                if (p != null) {
                    val msg = args.copyOfRange(1, args.size).joinToString(" ")

                    sender.sendMessage("${Color.ORANGE}[${Color.RED}me${Color.ORANGE} -> ${Color.RED}${sender.name}${Color.ORANGE}] ${ChatColor.RESET}$msg")
                    p.sendMessage("${Color.ORANGE}[${Color.RED}${sender.name}${Color.ORANGE} -> ${Color.RED}me${Color.ORANGE}] ${ChatColor.RESET}$msg")

                    return true
                }

                sender.sendMessage("${ChatColor.RED}${ChatColor.BOLD}Error: ${ChatColor.RESET}유저를 찾을 수 없습니다")

                return true
            }

            return false
        }

        sender.sendMessage("${ChatColor.RED}${ChatColor.BOLD}Error: ${ChatColor.RESET}This command isn't available on the console")
        return true
    }
}