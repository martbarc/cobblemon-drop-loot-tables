package us.timinc.mc.cobblemon.droploottables.handler

import com.cobblemon.mod.common.api.drop.DropEntry
import com.cobblemon.mod.common.api.drop.ItemDropEntry
import com.cobblemon.mod.common.api.events.pokemon.evolution.EvolutionAcceptedEvent
import com.cobblemon.mod.common.api.events.pokemon.evolution.EvolutionCompleteEvent
import com.cobblemon.mod.common.api.events.pokemon.evolution.EvolutionEvent
import com.cobblemon.mod.common.pokemon.Pokemon
import com.cobblemon.mod.common.util.asIdentifierDefaultingNamespace
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.item.ItemStack
import us.timinc.mc.cobblemon.droploottables.DropLootTables
import us.timinc.mc.cobblemon.droploottables.MOD_ID
import us.timinc.mc.cobblemon.droploottables.api.DropHandler
import us.timinc.mc.cobblemon.droploottables.api.DropTarget
import us.timinc.mc.cobblemon.droploottables.api.buildItem
import us.timinc.mc.cobblemon.droploottables.dropper.EvolvedDropper
import us.timinc.mc.cobblemon.droploottables.droptarget.PlayerDropTarget
import us.timinc.mc.cobblemon.droploottables.droptarget.PokemonEntityDropTarget
import us.timinc.mc.cobblemon.droploottables.droptarget.PokemonHeldItemTarget
import java.util.*

object EvolvedHandler : DropHandler<EvolvedDropper.Context, EvolvedDropper, EvolutionCompleteEvent> {
    val baseDrops: MutableMap<UUID, List<DropEntry>> = mutableMapOf()

    override val dropperTypeId: ResourceLocation = DropLootTables.DataKeys.DropperTypes.EVOLVED

    override fun getContext(evt: EvolutionCompleteEvent): EvolvedDropper.Context =
        EvolvedDropper.Context(
            evt.pokemon,
            getLevel(evt),
            evt.pokemon.getOwnerPlayer()!!,
            evt.sourcePokemon
        )

    override fun getLevel(evt: EvolutionCompleteEvent): ServerLevel =
        (evt.pokemon.entity?.level() ?: evt.pokemon.getOwnerPlayer()?.level()) as? ServerLevel
            ?: throw Exception("Could not get the level for an evolution event.")

    override val dropTargetTypes: MutableMap<ResourceLocation, (evt: EvolutionCompleteEvent) -> DropTarget?> =
        mutableMapOf(
            DropLootTables.DataKeys.DropTargetTypes.PLAYER_INVENTORY to { evt -> PlayerDropTarget(evt.pokemon.getOwnerPlayer()!!) },
            DropLootTables.DataKeys.DropTargetTypes.POKEMON_WORLD_POSITION to { evt -> evt.pokemon.entity?.let(::PokemonEntityDropTarget) },
            DropLootTables.DataKeys.DropTargetTypes.POKEMON_HELD_ITEM to { evt -> PokemonHeldItemTarget(evt.pokemon) }
        )

    fun registerDropTargetType(id: ResourceLocation, getter: (evt: EvolutionCompleteEvent) -> DropTarget?) {
        dropTargetTypes[id] = getter
    }

    override val selectedDropTargetTypes: List<ResourceLocation>
        get() = DropLootTables.config.evolutionDropTargets.map { it.asIdentifierDefaultingNamespace(MOD_ID) }

    override fun isRelevantEvent(evt: EvolutionCompleteEvent): Boolean =
        evt.pokemon.getOwnerPlayer() != null

    override fun processOtherDrops(evt: EvolutionCompleteEvent): List<ItemStack> {
        val ctx = getContext(evt)
        val droppers = getDroppers(ctx) ?: emptyList()
        if (!droppers.any(EvolvedDropper::preserveBaseDrops)) return emptyList()

        val caughtBaseDrops = baseDrops[evt.pokemon.uuid] ?: emptyList()

        return caughtBaseDrops.mapNotNull { baseDrop ->
            if (baseDrop !is ItemDropEntry) {
                val pos = evt.pokemon.entity?.position() ?: return@mapNotNull null
                baseDrop.drop(evt.pokemon.entity, ctx.level, pos, evt.pokemon.getOwnerPlayer())
                return@mapNotNull null
            }

            baseDrop.buildItem(ctx.level)
        }
    }

    override fun cleanup(evt: EvolutionCompleteEvent) {
        baseDrops.remove(evt.pokemon.uuid)
    }
}