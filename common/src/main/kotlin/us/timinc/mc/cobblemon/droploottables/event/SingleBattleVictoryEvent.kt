package us.timinc.mc.cobblemon.droploottables.event

import com.cobblemon.mod.common.api.events.battles.BattleVictoryEvent
import com.cobblemon.mod.common.pokemon.Pokemon

class SingleBattleVictoryEvent(
    val winningPokemon: Pokemon,
    val involvedInBattle: Boolean,
    val originalEvent: BattleVictoryEvent,
)