package us.timinc.mc.cobblemon.droploottables.droptarget

import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.item.ItemStack
import us.timinc.mc.cobblemon.droploottables.api.DropTarget

class PlayerEnderChestDropTarget(
    val player: ServerPlayer
) : DropTarget {
    override fun dropTo(stack: ItemStack): ItemStack =
        player.enderChestInventory.addItem(stack)
}