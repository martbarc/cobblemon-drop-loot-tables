package us.timinc.mc.cobblemon.droploottables.handler

import com.cobblemon.mod.common.api.events.battles.BattleFaintedEvent
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel
import us.timinc.mc.cobblemon.droploottables.DropLootTables
import us.timinc.mc.cobblemon.droploottables.api.DropHandler
import us.timinc.mc.cobblemon.droploottables.api.DropTarget
import us.timinc.mc.cobblemon.droploottables.dropper.DefeatedDropper

object DefeatedHandler : DropHandler<DefeatedDropper.Context, DefeatedDropper, BattleFaintedEvent> {
    override val dropperTypeId: ResourceLocation = DropLootTables.DataKeys.DropperTypes.DEFEATED

    override fun handle(evt: BattleFaintedEvent) {

    }

    override fun getContext(evt: BattleFaintedEvent): DefeatedDropper.Context = DefeatedDropper.Context(
        evt.killed.effectedPokemon,
        evt.killed.facedOpponents.first {  }
    )

    override fun getLevel(evt: BattleFaintedEvent): ServerLevel? {
        TODO("Not yet implemented")
    }

    override val dropTargetTypes: MutableMap<ResourceLocation, (evt: BattleFaintedEvent) -> DropTarget?>
        get() = TODO("Not yet implemented")
    override val selectedDropTargetTypes: List<ResourceLocation>
        get() = TODO("Not yet implemented")
}