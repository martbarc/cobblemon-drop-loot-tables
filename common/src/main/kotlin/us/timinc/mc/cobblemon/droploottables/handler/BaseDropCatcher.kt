package us.timinc.mc.cobblemon.droploottables.handler

import com.cobblemon.mod.common.api.events.drops.LootDroppedEvent
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity
import com.cobblemon.mod.common.util.party
import us.timinc.mc.cobblemon.timcore.AbstractHandler
import us.timinc.mc.cobblemon.timcore.getBooleanOrNull

object BaseDropCatcher : AbstractHandler<LootDroppedEvent>() {
    override fun handle(evt: LootDroppedEvent) {
        (evt.entity as? PokemonEntity)?.let { pokemonEntity ->
            if (pokemonEntity.isEvolving) {
                EvolvedHandler.baseDrops[pokemonEntity.pokemon.uuid] = evt.drops
            } else {
                pokemonEntity.battle?.let { _ ->
                    DefeatedHandler.baseDrops[pokemonEntity.pokemon.uuid] = evt.drops
                } ?: run {
                    KilledHandler.baseDrops[pokemonEntity.pokemon.uuid] = evt.drops
                }
            }
            evt.cancel()
            return
        }
        evt.player?.let { player ->
            EvolvedHandler.whoEvolvingWho.remove(player.uuid)?.let { evolvingPokemonUuid ->
                EvolvedHandler.baseDrops[evolvingPokemonUuid] = evt.drops
            }
        }
    }
}