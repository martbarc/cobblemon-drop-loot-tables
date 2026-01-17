package us.timinc.mc.cobblemon.droploottables

import com.cobblemon.mod.common.api.Priority
import com.cobblemon.mod.common.api.events.CobblemonEvents
import com.cobblemon.mod.common.api.reactive.EventObservable
import com.cobblemon.mod.common.pokeball.PokeBall
import com.cobblemon.mod.common.pokemon.Pokemon
import com.mojang.serialization.MapCodec
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.level.storage.loot.parameters.LootContextParam
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType
import us.timinc.mc.cobblemon.droploottables.api.DropContext
import us.timinc.mc.cobblemon.droploottables.api.Dropper
import us.timinc.mc.cobblemon.droploottables.api.DropperType
import us.timinc.mc.cobblemon.droploottables.condition.CaughtBallCondition
import us.timinc.mc.cobblemon.droploottables.condition.KnowledgeLevelCondition
import us.timinc.mc.cobblemon.droploottables.condition.PokemonMatcherCondition
import us.timinc.mc.cobblemon.droploottables.data.DropperDataManager
import us.timinc.mc.cobblemon.droploottables.dropper.CapturedDropper
import us.timinc.mc.cobblemon.droploottables.dropper.DefeatedDropper
import us.timinc.mc.cobblemon.droploottables.dropper.EvolvedDropper
import us.timinc.mc.cobblemon.droploottables.dropper.HatchedDropper
import us.timinc.mc.cobblemon.droploottables.dropper.KilledDropper
import us.timinc.mc.cobblemon.droploottables.dropper.ReleasedDropper
import us.timinc.mc.cobblemon.droploottables.dropper.ResurrectedDropper
import us.timinc.mc.cobblemon.droploottables.dropper.StarterChosenDropper
import us.timinc.mc.cobblemon.droploottables.dropper.TickedDropper
import us.timinc.mc.cobblemon.droploottables.dropper.VictoryDropper
import us.timinc.mc.cobblemon.droploottables.event.SingleDefeatEvent
import us.timinc.mc.cobblemon.droploottables.handler.BaseDropCatcher
import us.timinc.mc.cobblemon.droploottables.handler.CapturedHandler
import us.timinc.mc.cobblemon.droploottables.handler.DefeatedHandler
import us.timinc.mc.cobblemon.droploottables.handler.EvolvedHandler
import us.timinc.mc.cobblemon.droploottables.handler.HatchedHandler
import us.timinc.mc.cobblemon.droploottables.handler.KilledHandler
import us.timinc.mc.cobblemon.droploottables.handler.ReleasedHandler
import us.timinc.mc.cobblemon.droploottables.handler.ResurrectedHandler
import us.timinc.mc.cobblemon.droploottables.handler.StarterChosenHandler
import us.timinc.mc.cobblemon.droploottables.handler.TickedHandler
import us.timinc.mc.cobblemon.timcore.AbstractConfig
import us.timinc.mc.cobblemon.timcore.AbstractMod
import us.timinc.mc.cobblemon.timcore.TimCoreEvents

const val MOD_ID: String = "droploottables"

object DropLootTables : AbstractMod<DropLootTables.DropLootTablesConfig>(MOD_ID, DropLootTablesConfig::class.java) {
    class DropLootTablesConfig : AbstractConfig() {
        val tickedDropTargets: List<String> = listOf("pokemon_world_position")
        val capturedDropTargets: List<String> = listOf("player_inventory")
        val hatchedDropTargets: List<String> = listOf("player_inventory")
        val evolutionDropTargets: List<String> = listOf("player_inventory")
        val releasedDropTargets: List<String> = listOf("player_inventory")
        val defeatedDropTargets: List<String> = listOf("pokemon_world_position")
        val killedDropTargets: List<String> = listOf("pokemon_world_position")
        val resurrectedDropTargets: List<String> = listOf("player_inventory", "pokemon_world_position")
        val starterChosenDropTargets: List<String> = listOf("player_inventory")
    }

    object DataKeys {
        object RegistryKeys {
            val DROPPER_TYPES = modResource("dropper_types")
        }

        object DropperTypes {
            val CAPTURED = modResource("captured")
            val DEFEATED = modResource("defeated")
            val EVOLVED = modResource("evolved")
            val HATCHED = modResource("hatched")
            val KILLED = modResource("killed")
            val RELEASED = modResource("released")
            val RESURRECTED = modResource("resurrected")
            val STARTER_CHOSEN = modResource("starter_chosen")
            val TICKED = modResource("ticked")
            val VICTORY = modResource("victory")
        }

        object DropTargetTypes {
            val PLAYER_INVENTORY = modResource("player_inventory")
            val POKEMON_WORLD_POSITION = modResource("pokemon_world_position")
            val POKEMON_HELD_ITEM = modResource("pokemon_held_item")
        }

        object DropConditionKeys {
            val POKEMON_MATCHER = modResource("pokemon_matcher")
            val CAUGHT_BALL = modResource("caught_ball")
            val KNOWLEDGE_LEVEL = modResource("knowledge_level")
        }

        object LootParamKeys {
            val FOCUS_POKEMON = modResource("focus_pokemon")
            val FOCUS_PLAYER = modResource("focus_player")
            val FOCUS_POKEBALL = modResource("focus_pokeball")
            val ACTING_POKEMON = modResource("acting_pokemon")
            val PREVIOUS_POKEMON = modResource("previous_pokemon")
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
        val params: MutableMap<ResourceLocation, LootContextParam<*>> = mutableMapOf()

        val FOCUS_POKEMON: LootContextParam<Pokemon> = register(DataKeys.LootParamKeys.FOCUS_POKEMON)
        val FOCUS_PLAYER: LootContextParam<ServerPlayer> = register(DataKeys.LootParamKeys.FOCUS_PLAYER)
        val FOCUS_POKEBALL: LootContextParam<PokeBall> = register(DataKeys.LootParamKeys.FOCUS_POKEBALL)
        val ACTING_POKEMON: LootContextParam<Pokemon> = register(DataKeys.LootParamKeys.ACTING_POKEMON)
        val PREVIOUS_POKEMON: LootContextParam<Pokemon> = register(DataKeys.LootParamKeys.PREVIOUS_POKEMON)

        fun <T> register(resourceLocation: ResourceLocation): LootContextParam<T> {
            val lcp = LootContextParam<T>(resourceLocation)
            params[resourceLocation] = lcp
            return lcp
        }
    }

    object LootItemConditionTypes {
        val POKEMON_MATCHER_CONDITION =
            register(DataKeys.DropConditionKeys.POKEMON_MATCHER, PokemonMatcherCondition.CODEC)
        val CAUGHT_BALL_CONDITION = register(DataKeys.DropConditionKeys.CAUGHT_BALL, CaughtBallCondition.CODEC)
        val KNOWLEDGE_LEVEL_CONDITION =
            register(DataKeys.DropConditionKeys.KNOWLEDGE_LEVEL, KnowledgeLevelCondition.CODEC)

        fun <T : LootItemCondition> register(id: ResourceLocation, codec: MapCodec<T>): LootItemConditionType {
            return Registry.register(BuiltInRegistries.LOOT_CONDITION_TYPE, id, LootItemConditionType(codec))
        }
    }

    object Events {
        val SINGLE_DEFEAT = EventObservable<SingleDefeatEvent>()
    }

    init {
        DropperTypes
        LootItemConditionTypes

        registerReloadListener(DropperDataManager)

        CobblemonEvents.LOOT_DROPPED.subscribe(Priority.NORMAL, BaseDropCatcher::handle)
        CobblemonEvents.POKEMON_CAPTURED.subscribe(Priority.LOWEST, CapturedHandler::handle)
        CobblemonEvents.HATCH_EGG_POST.subscribe(Priority.LOWEST, HatchedHandler::handle)
        CobblemonEvents.EVOLUTION_COMPLETE.subscribe(Priority.LOWEST, EvolvedHandler::handle)
        CobblemonEvents.BATTLE_FAINTED.subscribe(Priority.LOWEST) { evt ->
            val loser = evt.killed
            val winners = loser.facedOpponents

            Events.SINGLE_DEFEAT.post(*(winners.map { winner -> SingleDefeatEvent(winner, loser, evt.battle) }
                .toTypedArray()))
        }
        CobblemonEvents.POKEMON_FAINTED.subscribe(Priority.LOWEST, KilledHandler::handle)
        Events.SINGLE_DEFEAT.subscribe(Priority.LOWEST, DefeatedHandler::handle)
        TimCoreEvents.POKEMON_TICKED.subscribe(Priority.LOWEST, TickedHandler::handle)
        CobblemonEvents.POKEMON_RELEASED_EVENT_POST.subscribe(Priority.LOWEST, ReleasedHandler::handle)
        CobblemonEvents.FOSSIL_REVIVED.subscribe(Priority.LOWEST, ResurrectedHandler::handle)
        CobblemonEvents.STARTER_CHOSEN.subscribe(Priority.LOWEST, StarterChosenHandler::handle)
    }
}