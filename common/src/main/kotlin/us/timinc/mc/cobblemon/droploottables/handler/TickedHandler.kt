package us.timinc.mc.cobblemon.droploottables.handler

import com.cobblemon.mod.common.util.asIdentifierDefaultingNamespace
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel
import us.timinc.mc.cobblemon.droploottables.DropLootTables
import us.timinc.mc.cobblemon.droploottables.MOD_ID
import us.timinc.mc.cobblemon.droploottables.api.DropHandler
import us.timinc.mc.cobblemon.droploottables.api.DropTarget
import us.timinc.mc.cobblemon.droploottables.dropper.TickedDropper
import us.timinc.mc.cobblemon.droploottables.droptarget.PlayerDropTarget
import us.timinc.mc.cobblemon.droploottables.droptarget.PokemonEntityDropTarget
import us.timinc.mc.cobblemon.timcore.event.PokemonEntityTickedEvent

object TickedHandler : DropHandler<TickedDropper.Context, TickedDropper, PokemonEntityTickedEvent> {
    override val dropperTypeId: ResourceLocation = DropLootTables.DataKeys.DropperTypes.TICKED

    override val dropTargetTypes: MutableMap<ResourceLocation, (evt: PokemonEntityTickedEvent) -> DropTarget?> =
        mutableMapOf(
            DropLootTables.DataKeys.DropTargetTypes.OWNER_INVENTORY to { evt ->
                evt.entity.pokemon.getOwnerPlayer()?.let(::PlayerDropTarget)
            },
            DropLootTables.DataKeys.DropTargetTypes.POKEMON_WORLD_POSITION to { evt -> PokemonEntityDropTarget(evt.entity) }
        )

    override val selectedDropTargetTypes: List<ResourceLocation>
        get() = DropLootTables.config.tickedDropTargets.map { it.asIdentifierDefaultingNamespace(MOD_ID) }

    fun registerDropTargetType(id: ResourceLocation, getter: (evt: PokemonEntityTickedEvent) -> DropTarget?) {
        dropTargetTypes[id] = getter
    }

    override fun getContext(evt: PokemonEntityTickedEvent): TickedDropper.Context =
        TickedDropper.Context.fromEntity(evt.entity)

    override fun getLevel(evt: PokemonEntityTickedEvent): ServerLevel = evt.entity.level() as ServerLevel

    override fun isRelevantEvent(evt: PokemonEntityTickedEvent): Boolean = evt.entity.level() is ServerLevel
}