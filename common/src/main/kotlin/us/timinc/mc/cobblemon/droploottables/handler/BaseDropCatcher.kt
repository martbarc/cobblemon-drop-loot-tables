package us.timinc.mc.cobblemon.droploottables.handler

import com.cobblemon.mod.common.api.events.drops.LootDroppedEvent
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity
import com.cobblemon.mod.common.util.party
import us.timinc.mc.cobblemon.timcore.AbstractHandler

object BaseDropCatcher : AbstractHandler<LootDroppedEvent>() {
    override fun handle(evt: LootDroppedEvent) {
        evt.player?.let { player ->
            player.party()
                .find { pokemon ->
                    pokemon.preEvolution?.let { preEvo -> preEvo.species.evolutions.any { it.drops == evt.table } }
                        ?: false
                }
                ?.let { evolvingPokemon ->
                    EvolvedHandler.baseDrops[evolvingPokemon.uuid] = evt.drops
                    evt.cancel()
                    return
                }
        }
        (evt.entity as? PokemonEntity)?.let { pokemonEntity ->
            pokemonEntity.battle?.let { _ ->
                DefeatedHandler.baseDrops[pokemonEntity.pokemon.uuid] = evt.drops
                evt.cancel()
                return
            } ?: run {
                KilledHandler.baseDrops[pokemonEntity.pokemon.uuid] = evt.drops
                evt.cancel()
            }
        }
    }
}