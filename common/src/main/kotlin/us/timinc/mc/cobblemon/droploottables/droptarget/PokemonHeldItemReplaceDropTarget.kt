package us.timinc.mc.cobblemon.droploottables.droptarget

import com.cobblemon.mod.common.pokemon.Pokemon
import net.minecraft.world.item.ItemStack
import us.timinc.mc.cobblemon.droploottables.api.DropTarget

class PokemonHeldItemReplaceDropTarget(
    val pokemon: Pokemon,
) : DropTarget {
    override fun dropTo(stack: ItemStack): ItemStack {
        pokemon.swapHeldItem(stack, false)
        return ItemStack.EMPTY
    }
}