package us.timinc.mc.cobblemon.droploottables.event

import com.cobblemon.mod.common.api.battles.model.PokemonBattle
import com.cobblemon.mod.common.battles.pokemon.BattlePokemon

class SingleDefeatEvent(
    val winner: BattlePokemon,
    val loser: BattlePokemon,
    val battle: PokemonBattle
)