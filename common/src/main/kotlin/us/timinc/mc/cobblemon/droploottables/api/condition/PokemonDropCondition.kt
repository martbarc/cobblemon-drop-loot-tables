package us.timinc.mc.cobblemon.droploottables.api.condition

import com.cobblemon.mod.common.pokemon.Pokemon

abstract class PokemonDropCondition : DropCondition<Pokemon> {
    override fun convertToContext(param: Any): Pokemon? {
        if (param is Pokemon) return param
        return null
    }
}