package us.timinc.mc.cobblemon.droploottables.api

import net.minecraft.world.item.ItemStack

interface DropTarget {
    fun dropTo(stack: List<ItemStack>) = stack.forEach(::dropTo)
    fun dropTo(stack: ItemStack)
}