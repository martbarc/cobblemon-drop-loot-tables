package us.timinc.mc.cobblemon.droploottables.handler

import com.cobblemon.mod.common.api.events.pokemon.evolution.EvolutionEvent
import com.cobblemon.mod.common.util.asIdentifierDefaultingNamespace
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel
import us.timinc.mc.cobblemon.droploottables.DropLootTables
import us.timinc.mc.cobblemon.droploottables.MOD_ID
import us.timinc.mc.cobblemon.droploottables.api.DropHandler
import us.timinc.mc.cobblemon.droploottables.api.DropTarget
import us.timinc.mc.cobblemon.droploottables.dropper.EvolvedDropper
import us.timinc.mc.cobblemon.droploottables.droptarget.PlayerDropTarget
import us.timinc.mc.cobblemon.droploottables.droptarget.PokemonEntityDropTarget

object EvolvedHandler : DropHandler<EvolvedDropper.Context, EvolvedDropper, EvolutionEvent> {
    override val dropperTypeId: ResourceLocation = DropLootTables.DataKeys.DropperTypes.EVOLVED

    override fun getContext(evt: EvolutionEvent): EvolvedDropper.Context =
        EvolvedDropper.Context(
            evt.pokemon,
            getLevel(evt),
            evt.pokemon.getOwnerPlayer()!!
        )

    override fun getLevel(evt: EvolutionEvent): ServerLevel =
        (evt.pokemon.entity?.level() ?: evt.pokemon.getOwnerPlayer()?.level()) as? ServerLevel
            ?: throw Exception("Could not get the level for an evolution event.")

    override val dropTargetTypes: MutableMap<ResourceLocation, (evt: EvolutionEvent) -> DropTarget?> =
        mutableMapOf(
            DropLootTables.DataKeys.DropTargetTypes.OWNER to { evt -> PlayerDropTarget(evt.pokemon.getOwnerPlayer()!!) },
            DropLootTables.DataKeys.DropTargetTypes.POKEMON to { evt -> evt.pokemon.entity?.let(::PokemonEntityDropTarget) }
        )

    override val selectedDropTargetTypes: List<ResourceLocation>
        get() = DropLootTables.config.evolutionDropTargets.map { it.asIdentifierDefaultingNamespace(MOD_ID) }

    override fun isRelevantEvent(evt: EvolutionEvent): Boolean = evt.pokemon.getOwnerPlayer() != null
}