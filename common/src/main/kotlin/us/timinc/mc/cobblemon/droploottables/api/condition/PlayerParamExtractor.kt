package us.timinc.mc.cobblemon.droploottables.api.condition

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity
import com.cobblemon.mod.common.pokemon.Pokemon
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.player.Player

object PlayerParamExtractor : ParamExtractor<ServerPlayer> {
    override fun convert(param: Any): ServerPlayer? {
        if (param is Player) return param as? ServerPlayer
        if (param is Pokemon) return param.getOwnerPlayer()
        if (param is PokemonEntity) return param.pokemon.getOwnerPlayer()
        return null
    }
}