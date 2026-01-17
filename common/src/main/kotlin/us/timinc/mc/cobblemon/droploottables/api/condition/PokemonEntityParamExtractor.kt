package us.timinc.mc.cobblemon.droploottables.api.condition

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity
import com.cobblemon.mod.common.pokemon.Pokemon

object PokemonEntityParamExtractor : ParamExtractor<PokemonEntity> {
    override fun convert(param: Any): PokemonEntity? {
        if (param is PokemonEntity) return param
        if (param is Pokemon) return param.entity
        return null
    }
}