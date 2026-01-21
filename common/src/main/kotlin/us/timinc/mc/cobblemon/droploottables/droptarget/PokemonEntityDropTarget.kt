package us.timinc.mc.cobblemon.droploottables.droptarget

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.item.ItemStack
import us.timinc.mc.cobblemon.droploottables.api.DropTarget

class PokemonEntityDropTarget(
    val pokemonEntity: PokemonEntity,
) : DropTarget {
    override fun dropTo(stack: ItemStack): ItemStack {
        val level = pokemonEntity.level() as ServerLevel
        val stackEntity = ItemEntity(
            level,
            pokemonEntity.position().x,
            pokemonEntity.position().y,
            pokemonEntity.position().z,
            stack
        )
        level.addFreshEntity(stackEntity)
        return ItemStack.EMPTY
    }
}