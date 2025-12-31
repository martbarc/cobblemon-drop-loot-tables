package us.timinc.mc.cobblemon.droploottables.handler

import com.cobblemon.mod.common.api.events.pokemon.HatchEggEvent
import com.cobblemon.mod.common.util.asIdentifierDefaultingNamespace
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel
import us.timinc.mc.cobblemon.droploottables.DropLootTables
import us.timinc.mc.cobblemon.droploottables.MOD_ID
import us.timinc.mc.cobblemon.droploottables.api.DropHandler
import us.timinc.mc.cobblemon.droploottables.api.DropTarget
import us.timinc.mc.cobblemon.droploottables.dropper.HatchedDropper
import us.timinc.mc.cobblemon.droploottables.droptarget.PlayerDropTarget

object HatchedHandler : DropHandler<HatchedDropper.Context, HatchedDropper, HatchEggEvent.Post> {
    override val dropperTypeId: ResourceLocation = DropLootTables.DataKeys.DropperTypes.HATCHED

    override fun getContext(evt: HatchEggEvent.Post): HatchedDropper.Context = HatchedDropper.Context(
        evt.player.level() as ServerLevel,
        evt.pokemon,
        evt.player
    )

    override fun getLevel(evt: HatchEggEvent.Post): ServerLevel = evt.player.level() as ServerLevel

    override val dropTargetTypes: MutableMap<ResourceLocation, (evt: HatchEggEvent.Post) -> DropTarget?> =
        mutableMapOf(
            DropLootTables.DataKeys.DropTargetTypes.OWNER to { evt -> PlayerDropTarget(evt.player) }
        )

    override val selectedDropTargetTypes: List<ResourceLocation>
        get() = DropLootTables.config.hatchedDropTargets.map { it.asIdentifierDefaultingNamespace(MOD_ID) }
}