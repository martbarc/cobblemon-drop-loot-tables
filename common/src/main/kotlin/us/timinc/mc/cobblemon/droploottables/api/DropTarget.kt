package us.timinc.mc.cobblemon.droploottables.api

import net.minecraft.world.item.ItemStack

interface DropTarget {
    fun dropTo(stack: ItemStack): ItemStack
}