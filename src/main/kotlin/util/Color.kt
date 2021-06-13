package dev.bukgeuk.polaressentials.util

import net.md_5.bungee.api.ChatColor

class Color {
    companion object {
        val RED: ChatColor = ChatColor.of("#FF4040")
        val ORANGE: ChatColor = ChatColor.of("#FF7F50")
    }
}

class ColoredChat {
    private val pattern = Regex("""\$\{(#[a-zA-Z0-9]{6})}""")

    fun hexToColor(text: String): String {
        var str = text
        var res = pattern.find(str)
        while (res != null) {
            str = str.replace(res.value, "${ChatColor.of(res.groupValues[1])}")
            res = pattern.find(str)
        }

        return ChatColor.translateAlternateColorCodes('&', str)
    }
}