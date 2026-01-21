package us.timinc.mc.cobblemon.droploottables.paramextractor

import com.cobblemon.mod.common.pokemon.Pokemon
import us.timinc.mc.cobblemon.droploottables.api.param.ParamExtractor

object PokemonParamExtractor : ParamExtractor<Pokemon> {
    override fun convert(param: Any): Pokemon? {
        if (param is Pokemon) return param
        return null
    }
}