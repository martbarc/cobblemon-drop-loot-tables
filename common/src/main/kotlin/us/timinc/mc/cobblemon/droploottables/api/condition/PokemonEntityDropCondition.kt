package us.timinc.mc.cobblemon.droploottables.api.condition

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity
import com.cobblemon.mod.common.pokemon.Pokemon

abstract class PokemonEntityDropCondition : DropCondition<PokemonEntity> {
    override fun convertToContext(param: Any): PokemonEntity? {
        if (param is PokemonEntity) return param
        if (param is Pokemon) return param.entity
        return null
    }
}