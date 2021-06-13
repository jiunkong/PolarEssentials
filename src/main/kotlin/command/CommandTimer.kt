package dev.bukgeuk.polaressentials.command

import dev.bukgeuk.polaressentials.util.Color
import dev.bukgeuk.polaressentials.util.ColoredChat
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.boss.BarColor
import org.bukkit.boss.BarStyle
import org.bukkit.boss.BossBar
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import kotlin.concurrent.timer

data class BossbarTimer(
    val Bossbar: BossBar,
    val Time: Long,
    var TimeNow: Long
)

class CommandTimer(): CommandExecutor {
    companion object {
        @JvmField var Bossbars: MutableMap<String, BossbarTimer> = mutableMapOf()
        @JvmField var Timer = timer(period = 1) {
            for (key in Bossbars.keys) {
                val item = Bossbars[key]
                item!!.TimeNow -= 1
                if (item!!.TimeNow >= 0) item.Bossbar.progress = item.TimeNow.toDouble() / item.Time.toDouble()

                if (item.TimeNow == (-1000).toLong()) {
                    CommandTimer().removeTimer(key)
                }
            }
        }
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (args.isNotEmpty()) {
            when (args[0]) {
                "start" -> {
                    if (args.size < 4) {
                        sender.sendMessage("사용법: /timer start <name> <color> <time> <title>")
                        return true
                    }

                    val name = args[1]
                    val time: Long
                    try { time = args[3].toLong() * 1000 } catch (e: NumberFormatException) {
                        sender.sendMessage("사용법: /timer start <name> <color> <time> <title>")
                        return true
                    }
                    val temp = args.copyOfRange(4, args.size)
                    val title = ColoredChat().hexToColor(temp.joinToString(" "))

                    val bar: BossBar
                    try {
                        bar = Bukkit.createBossBar(title, BarColor.valueOf(args[2]), BarStyle.SOLID)
                    } catch (e: IllegalArgumentException) {
                        sender.sendMessage("${ChatColor.RED}${ChatColor.BOLD}Error: ${ChatColor.RESET}알 수 없는 색: ${args[2]}")
                        return true
                    }
                    startTimer(name, bar, time)

                    sender.sendMessage("${Color.RED}${name}${Color.ORANGE} 타이머가 추가되었습니다")
                }
                "remove" -> {
                    if (args.size < 2) {
                        sender.sendMessage("사용법: /timer remove <name>")
                        return true
                    }

                    if(!Bossbars.containsKey(args[1])) {
                        sender.sendMessage("${ChatColor.RED}${ChatColor.BOLD}Error: ${ChatColor.RESET}타이머를 찾을 수 없습니다")
                        return true
                    }
                    removeTimer(args[1])

                    sender.sendMessage("${Color.RED}${args[1]}${Color.ORANGE} 타이머가 삭제되었습니다")
                }
                "list" -> {
                    sender.sendMessage(getTimerList())
                }
                else -> return false
            }
            return true
        }
        return false
    }

    private fun startTimer(name:String, bar: BossBar, time: Long) {
        bar.isVisible = true
        bar.progress = 1.0
        for (p in Bukkit.getOnlinePlayers()) {
            bar.addPlayer(p)
        }
        Bossbars[name] = BossbarTimer(bar, time, time)
    }

    private fun removeTimer(name:String) {
        Bossbars[name]!!.Bossbar.removeAll()
        Bossbars.remove(name)
    }

    private fun getTimerList(): String {
        var str = ""

        for (item in Bossbars.keys) {
            str += "${item}, "
        }

        return str.substring(0, str.length - 2)
    }
}