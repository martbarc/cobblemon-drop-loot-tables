package us.timinc.mc.cobblemon.droploottables.event

import com.cobblemon.mod.common.api.battles.model.PokemonBattle
import com.cobblemon.mod.common.pokemon.Pokemon

class SingleVictoryEvent(
    val winner: Pokemon,
    val loser: Pokemon,
    val battle: PokemonBattle,
)