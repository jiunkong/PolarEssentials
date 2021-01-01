package dev.bukgeuk.polaressentials.command

import dev.bukgeuk.polaressentials.util.Color
import net.md_5.bungee.api.chat.ClickEvent
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.util.*
import kotlin.concurrent.timer

class RequestExpirationTimer {
    companion object {
        var expirationTime: Long = 60
        var secondTable: MutableMap<UUID, Long> = mutableMapOf()
        @JvmField var Timer = timer(period = 1000) {
            for (item in secondTable.keys) {
                if (secondTable[item] != 0.toLong()) {
                    secondTable[item] = secondTable[item]!! - 1
                } else {
                    RequestExpirationTimer().removeTimer(item) // remove player in table
                    TeleportRequest().popRequest(item) // remove teleport request

                    for (p in Bukkit.getOnlinePlayers()) {
                        if (p.uniqueId == item) {
                            p.sendMessage("${Color.ORANGE}텔레포트 요청이 만료되었습니다")
                        }
                    }
                }
            }
        }
    }

    fun renewTimer(uuid: UUID) {
        secondTable[uuid] = expirationTime
    }

    fun removeTimer(uuid: UUID) {
        secondTable.remove(uuid)
    }
}

class TeleportRequest {
    enum class TeleportType() {
        TPA, TPAHERE;
    }

    data class Request(
        val type: TeleportType,
        val from: UUID
    )

    companion object {
        @JvmField var Requests: MutableMap<UUID, Request> = mutableMapOf()
    }

    fun pushRequest(uuid: UUID, request: Request) {
        Requests[uuid] = request
    }

    fun popRequest(uuid: UUID) {
        Requests.remove(uuid)
    }
}

class CommandTpa: CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender is Player) {
            if (args.isNotEmpty()) {
                for (target in Bukkit.getOnlinePlayers()) {
                    if (target.name == args[0]) {
                        if(TeleportRequest.Requests[target.uniqueId]?.from == sender.uniqueId) { // if last request is sender's request
                            sender.sendMessage("${Color.ORANGE}이미 텔레포트 요청을 보냈습니다")
                            return true
                        }

                        // sender
                        TeleportRequest().pushRequest(target.uniqueId, TeleportRequest.Request(TeleportRequest.TeleportType.TPA, sender.uniqueId))
                        RequestExpirationTimer().renewTimer(target.uniqueId)
                        sender.sendMessage("${Color.ORANGE}텔레포트 요청을 보냈습니다")

                        // target
                        target.sendMessage("${Color.RED}${sender.name}${Color.ORANGE}으로부터의 tpa 요청")
                        target.sendMessage("")
                        val accept = net.md_5.bungee.api.chat.TextComponent("${ChatColor.GREEN}${ChatColor.BOLD}[수락]")
                        accept.clickEvent = ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tpaccept")
                        val deny = net.md_5.bungee.api.chat.TextComponent("${ChatColor.RED}${ChatColor.BOLD}[거부]")
                        deny.clickEvent = ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tpdeny")
                        val msg = net.md_5.bungee.api.chat.TextComponent("        ")
                        msg.addExtra(accept)
                        msg.addExtra("      ")
                        msg.addExtra(deny)

                        target.spigot().sendMessage(msg)

                        return true
                    }
                }
            }

            return false
        }

        sender.sendMessage("Error: This command isn't available on the console")

        return true
    }
}

class CommandTpaccept: CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender is Player) {
            RequestExpirationTimer().removeTimer(sender.uniqueId)
            val req = TeleportRequest.Requests[sender.uniqueId]
            when {
                req == null -> {
                    sender.sendMessage("${Color.ORANGE}대기중인 텔레포트 요청이 없습니다")
                    return true
                }
                req.type == TeleportRequest.TeleportType.TPA -> {
                    for (p in Bukkit.getOnlinePlayers()) {
                        if (p.uniqueId == req.from) {
                            p.sendMessage("${Color.ORANGE}tpa 요청을 수락함")
                            p.sendMessage("${Color.RED}${sender.name}${Color.ORANGE}(으)로 이동합니다")
                            CommandBack().pushLocation(p.uniqueId, p.location)
                            p.teleport(sender)
                            TeleportRequest().popRequest(sender.uniqueId)
                            return true
                        }
                    }
                    sender.sendMessage("${Color.ORANGE}유저를 찾을 수 없습니다")
                    TeleportRequest().popRequest(sender.uniqueId)
                    return true
                }
                req.type == TeleportRequest.TeleportType.TPAHERE -> {
                    for (p in Bukkit.getOnlinePlayers()) {
                        if (p.uniqueId == req.from) {
                            p.sendMessage("${Color.ORANGE}tpahere 요청을 수락함")
                            sender.sendMessage("${Color.RED}${p.name}${Color.ORANGE}(으)로 이동합니다")
                            CommandBack().pushLocation(sender.uniqueId, sender.location)
                            sender.teleport(p)
                            TeleportRequest().popRequest(sender.uniqueId)
                            return true
                        }
                    }
                    sender.sendMessage("${Color.ORANGE}유저를 찾을 수 없습니다")
                    TeleportRequest().popRequest(sender.uniqueId)
                    return true
                }
            }
        }

        sender.sendMessage("Error: This command isn't available on the console")

        return true
    }
}

class CommandTpdeny: CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender is Player) {
            RequestExpirationTimer().removeTimer(sender.uniqueId)
            val req = TeleportRequest.Requests[sender.uniqueId]
            when {
                req == null -> {
                    sender.sendMessage("${Color.ORANGE}대기중인 텔레포트 요청이 없습니다")
                    return true
                }
                req.type == TeleportRequest.TeleportType.TPA -> {
                    for (p in Bukkit.getOnlinePlayers()) {
                        if (p.uniqueId == req.from) {
                            TeleportRequest().popRequest(sender.uniqueId)
                            p.sendMessage("${Color.ORANGE}tpa 요청을 거절함")
                            return true
                        }
                    }
                    TeleportRequest().popRequest(sender.uniqueId)
                    return true
                }
                req.type == TeleportRequest.TeleportType.TPAHERE -> {
                    for (p in Bukkit.getOnlinePlayers()) {
                        if (p.uniqueId == req.from) {
                            TeleportRequest().popRequest(sender.uniqueId)
                            p.sendMessage("${Color.ORANGE}tpahere 요청을 거절함")
                            return true
                        }
                    }
                    TeleportRequest().popRequest(sender.uniqueId)
                    return true
                }
            }
        }

        sender.sendMessage("Error: This command isn't available on the console")

        return true
    }
}

class CommandTpahere: CommandExecutor{
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender is Player) {
            if (args.isNotEmpty()) {
                for (target in Bukkit.getOnlinePlayers()) {
                    if (target.name == args[0]) {
                        if(TeleportRequest.Requests[target.uniqueId]?.from == sender.uniqueId) { // if last request is sender's request
                            sender.sendMessage("${Color.ORANGE}이미 텔레포트 요청을 보냈습니다")
                            return true
                        }

                        // sender
                        TeleportRequest().pushRequest(target.uniqueId, TeleportRequest.Request(TeleportRequest.TeleportType.TPAHERE, sender.uniqueId))
                        RequestExpirationTimer().renewTimer(target.uniqueId)
                        sender.sendMessage("${Color.ORANGE}텔레포트 요청을 보냈습니다")

                        // target
                        target.sendMessage("${Color.RED}${sender.name}${Color.ORANGE}으로부터의 tpahere 요청")
                        target.sendMessage("")
                        val accept = net.md_5.bungee.api.chat.TextComponent("${ChatColor.GREEN}${ChatColor.BOLD}[수락]")
                        accept.clickEvent = ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tpaccept")
                        val deny = net.md_5.bungee.api.chat.TextComponent("${ChatColor.RED}${ChatColor.BOLD}[거부]")
                        deny.clickEvent = ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tpdeny")
                        val msg = net.md_5.bungee.api.chat.TextComponent("        ")
                        msg.addExtra(accept)
                        msg.addExtra("      ")
                        msg.addExtra(deny)

                        target.spigot().sendMessage(msg)

                        return true
                    }
                }
            }

            return false
        }

        sender.sendMessage("Error: This command isn't available on the console")

        return true
    }
}