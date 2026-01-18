package us.timinc.mc.cobblemon.droploottables.droptarget

import com.cobblemon.mod.common.pokemon.Pokemon
import net.minecraft.world.item.ItemStack
import us.timinc.mc.cobblemon.droploottables.api.DropTarget

class PokemonHeldItemDropTarget(
    val pokemon: Pokemon,
) : DropTarget {
    override fun dropTo(stack: ItemStack) {
        if (!pokemon.heldItem().isEmpty) return
        pokemon.swapHeldItem(stack, false)
    }
}