package us.timinc.mc.cobblemon.droploottables.paramextractor

import com.cobblemon.mod.common.pokeball.PokeBall
import us.timinc.mc.cobblemon.droploottables.api.param.ParamExtractor

object PokeBallParamExtractor : ParamExtractor<PokeBall> {
    override fun convert(param: Any): PokeBall? {
        if (param !is PokeBall) {
            return null
        }
        return param
    }
}