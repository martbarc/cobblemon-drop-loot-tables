package us.timinc.mc.cobblemon.droploottables.api.condition

import com.cobblemon.mod.common.pokemon.Pokemon

object PokemonParamExtractor : ParamExtractor<Pokemon> {
    override fun convert(param: Any): Pokemon? {
        if (param is Pokemon) return param
        return null
    }
}