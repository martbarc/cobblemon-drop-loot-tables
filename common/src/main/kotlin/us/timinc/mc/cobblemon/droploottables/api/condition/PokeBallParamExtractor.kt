package us.timinc.mc.cobblemon.droploottables.api.condition

import com.cobblemon.mod.common.pokeball.PokeBall

object PokeBallParamExtractor : ParamExtractor<PokeBall> {
    override fun convert(param: Any): PokeBall? {
        if (param !is PokeBall) {
            return null
        }
        return param
    }
}