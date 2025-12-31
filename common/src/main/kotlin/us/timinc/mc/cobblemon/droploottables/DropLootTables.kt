package us.timinc.mc.cobblemon.droploottables

import com.cobblemon.mod.common.api.Priority
import com.cobblemon.mod.common.api.events.CobblemonEvents
import com.cobblemon.mod.common.api.pokemon.evolution.Evolution
import com.cobblemon.mod.common.api.reactive.EventObservable
import com.cobblemon.mod.common.pokeball.PokeBall
import com.cobblemon.mod.common.pokemon.Pokemon
import net.minecraft.core.Registry
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.level.storage.loot.parameters.LootContextParam
import us.timinc.mc.cobblemon.droploottables.api.DropContext
import us.timinc.mc.cobblemon.droploottables.api.Dropper
import us.timinc.mc.cobblemon.droploottables.api.DropperType
import us.timinc.mc.cobblemon.droploottables.data.DropperDataManager
import us.timinc.mc.cobblemon.droploottables.dropper.*
import us.timinc.mc.cobblemon.droploottables.event.SingleBattleVictoryEvent
import us.timinc.mc.cobblemon.droploottables.handler.*
import us.timinc.mc.cobblemon.timcore.AbstractConfig
import us.timinc.mc.cobblemon.timcore.AbstractMod
import us.timinc.mc.cobblemon.timcore.TimCoreEvents

const val MOD_ID: String = "droploottables"

object DropLootTables : AbstractMod<DropLootTables.DropLootTablesConfig>(MOD_ID, DropLootTablesConfig::class.java) {
    class DropLootTablesConfig : AbstractConfig() {
        val tickedDropTargets: List<String> = listOf("pokemon")
        val capturedDropTargets: List<String> = listOf("owner")
        val hatchedDropTargets: List<String> = listOf("owner")
        val evolutionDropTargets: List<String> = listOf("owner")
        val victoryDropTargets: List<String> = listOf("owner")
    }

    object DataKeys {
        object RegistryKeys {
            val DROPPER_TYPES = modResource("dropper_types")
        }

        object DropperTypes {
            val CAPTURED = modResource("captured")
            val DEFEATED = modResource("defeated")
            val HATCHED = modResource("hatched")
            val EVOLVED = modResource("evolved")
            val RESURRECTED = modResource("resurrected")
            val KILLED = modResource("killed")
            val TICKED = modResource("ticked")
            val RELEASED = modResource("released")
            val STARTER_CHOSEN = modResource("starter_chosen")
            val VICTORY = modResource("victory")
        }

        object DropTargetTypes {
            val OWNER = modResource("owner")
            val POKEMON = modResource("pokemon")
        }
    }

    object DropperTypes {
        val CAPTURED = register(DataKeys.DropperTypes.CAPTURED, CapturedDropper.DROPPER_TYPE)
        val TICKED = register(DataKeys.DropperTypes.TICKED, TickedDropper.DROPPER_TYPE)
        val DEFEATED = register(DataKeys.DropperTypes.DEFEATED, DefeatedDropper.DROPPER_TYPE)
        val HATCHED = register(DataKeys.DropperTypes.HATCHED, HatchedDropper.DROPPER_TYPE)
        val EVOLVED = register(DataKeys.DropperTypes.EVOLVED, EvolvedDropper.DROPPER_TYPE)
        val RESURRECTED = register(DataKeys.DropperTypes.RESURRECTED, ResurrectedDropper.DROPPER_TYPE)
        val KILLED = register(DataKeys.DropperTypes.KILLED, KilledDropper.DROPPER_TYPE)
        val RELEASED = register(DataKeys.DropperTypes.RELEASED, ReleasedDropper.DROPPER_TYPE)
        val STARTER_CHOSEN = register(DataKeys.DropperTypes.STARTER_CHOSEN, StarterChosenDropper.DROPPER_TYPE)
        val VICTORY = register(DataKeys.DropperTypes.VICTORY, VictoryDropper.DROPPER_TYPE)

        fun <C : DropContext, T : Dropper<C>> register(
            id: ResourceLocation,
            dropperType: DropperType<C, T>,
        ): DropperType<C, T> = Registry.register(DropperType.REGISTRY, id, dropperType)
    }

    object LootParams {
        val POKE_BALL: LootContextParam<PokeBall> = LootContextParam(modResource("poke_ball"))
        val EVOLUTION: LootContextParam<Evolution> = LootContextParam(modResource("evolution"))
        val POKEMON_DETAILS: LootContextParam<Pokemon> = LootContextParam(modResource("pokemon"))
        val PARTICIPATED_IN_BATTLE: LootContextParam<Boolean> = LootContextParam(modResource("was_in_battle"))
        val RELEVANT_PLAYER: LootContextParam<ServerPlayer> = LootContextParam(modResource("relevant_player"))
    }

    object Event {
        val SINGLE_BATTLE_VICTORY = EventObservable<SingleBattleVictoryEvent>()
    }


    init {
        DropperTypes

        registerReloadListener(DropperDataManager)

        CobblemonEvents.POKEMON_CAPTURED.subscribe(Priority.LOWEST, CapturedHandler::handle)
        CobblemonEvents.HATCH_EGG_POST.subscribe(Priority.LOWEST, HatchedHandler::handle)
        CobblemonEvents.EVOLUTION_COMPLETE.subscribe(Priority.LOWEST, EvolvedHandler::handle)
        TimCoreEvents.POKEMON_TICKED.subscribe(Priority.LOWEST, TickedHandler::handle)
        Event.SINGLE_BATTLE_VICTORY.subscribe(Priority.LOWEST, VictoryHandler::handle)
        CobblemonEvents.BATTLE_VICTORY.subscribe { evt ->
            Event.SINGLE_BATTLE_VICTORY.post(
                *(evt.winners.flatMap { winner ->
                    winner.pokemonList.map { pokemon ->
                        SingleBattleVictoryEvent(
                            pokemon.effectedPokemon,
                            pokemon.facedOpponents.isNotEmpty(),
                            evt
                        )
                    }
                }.toTypedArray())
            )
        }
    }
}