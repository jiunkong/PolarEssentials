package dev.bukgeuk.polaressentials

import dev.bukgeuk.polaressentials.util.ColoredChat
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerChatEvent

class ChatEvent: Listener {
    @EventHandler
    fun onChat(e: AsyncPlayerChatEvent) {
        e.message = ColoredChat().hexToColor(e.message)
    }
}