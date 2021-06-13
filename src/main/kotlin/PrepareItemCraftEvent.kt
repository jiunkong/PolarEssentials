package dev.bukgeuk.polaressentials

import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.PrepareItemCraftEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import kotlin.math.max

class PrepareItemCraftEvent(private val limit: Int): Listener {
    @EventHandler
    fun onPrepareItemCraft(e: PrepareItemCraftEvent) {
        val inv = e.inventory
        val contents: Array<ItemStack?> = inv.matrix
        var temp: Array<ItemStack> = arrayOf()

        for (i in contents.indices) {
            if (contents[i] != null) {
                if (contents[i]!!.type != Material.AIR) {
                    temp += contents[i]!!
                }
            }
        }

        if (temp.size != 2) return
        else if (temp[0].type != temp[1].type) return

        val en1 = temp[0].enchantments.toMutableMap()
        val en2 = temp[1].enchantments.toMutableMap()
        val new = mutableMapOf<Enchantment, Int>()

        for (k in en1.keys) {
            if (en2[k] != null) {
                if (en1[k] == en2[k] && en1[k]!! < limit)
                    new[k] = en1[k]!! + 1
                else
                    new[k] = max(en1[k]!!, en2[k]!!)

                en2.remove(k)
            } else {
                new[k] = en1[k]!!
            }
        }

        for (k in en2.keys)
            new[k] = en2[k]!!

        val item = ItemStack(temp[0].type)
        val meta: ItemMeta? = item.itemMeta
        if (temp[0].itemMeta?.displayName == temp[0].itemMeta?.localizedName && temp[1].itemMeta?.displayName != temp[1].itemMeta?.localizedName)
            meta?.setDisplayName(temp[0].itemMeta?.displayName)
        item.itemMeta = meta
        item.addUnsafeEnchantments(new)

        inv.result = item
    }
}