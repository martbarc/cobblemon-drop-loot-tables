package us.timinc.mc.cobblemon.droploottables.paramextractor

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity
import com.cobblemon.mod.common.pokemon.Pokemon
import com.cobblemon.mod.common.util.party
import net.minecraft.server.level.ServerPlayer
import us.timinc.mc.cobblemon.droploottables.api.param.ParamExtractor

object TeamParamExtractor : ParamExtractor<List<Pokemon>> {
    override fun convert(param: Any): List<Pokemon>? {
        if (param is ServerPlayer) return param.party().toList()
        if (param is Pokemon) return param.getOwnerPlayer()?.party()?.toList()
        if (param is PokemonEntity) return param.pokemon.getOwnerPlayer()?.party()?.toList()

        @Suppress("UNCHECKED_CAST")
        return param as? List<Pokemon>
    }
}