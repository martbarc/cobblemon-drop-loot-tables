package us.timinc.mc.cobblemon.droploottables.droptarget

import com.cobblemon.mod.common.util.giveOrDropItemStack
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.item.ItemStack
import us.timinc.mc.cobblemon.droploottables.api.DropTarget

class PlayerDropTarget(
    val player: ServerPlayer,
) : DropTarget {
    override fun dropTo(stack: ItemStack) = player.giveOrDropItemStack(stack)
}