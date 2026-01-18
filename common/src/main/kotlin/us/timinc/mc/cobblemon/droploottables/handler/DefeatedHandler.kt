package us.timinc.mc.cobblemon.droploottables.handler

import com.cobblemon.mod.common.api.drop.DropEntry
import com.cobblemon.mod.common.api.drop.ItemDropEntry
import com.cobblemon.mod.common.util.asIdentifierDefaultingNamespace
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.item.ItemStack
import us.timinc.mc.cobblemon.droploottables.DropLootTables
import us.timinc.mc.cobblemon.droploottables.MOD_ID
import us.timinc.mc.cobblemon.droploottables.api.DropHandler
import us.timinc.mc.cobblemon.droploottables.api.DropTarget
import us.timinc.mc.cobblemon.droploottables.api.extension.buildItem
import us.timinc.mc.cobblemon.droploottables.dropper.DefeatedDropper
import us.timinc.mc.cobblemon.droploottables.droptarget.PlayerDropTarget
import us.timinc.mc.cobblemon.droploottables.droptarget.PokemonEntityDropTarget
import us.timinc.mc.cobblemon.droploottables.droptarget.PokemonHeldItemDropTarget
import us.timinc.mc.cobblemon.droploottables.event.SingleDefeatEvent
import java.util.*

object DefeatedHandler : DropHandler<DefeatedDropper.Context, DefeatedDropper, SingleDefeatEvent> {
    val baseDrops: MutableMap<UUID, List<DropEntry>> = mutableMapOf()

    override val dropperTypeId: ResourceLocation = DropLootTables.DataKeys.DropperTypes.DEFEATED

    override val dropTargetTypes: MutableMap<ResourceLocation, (evt: SingleDefeatEvent) -> DropTarget?> =
        mutableMapOf(
            DropLootTables.DataKeys.DropTargetTypes.PLAYER_INVENTORY to { evt -> PlayerDropTarget(evt.winner.effectedPokemon.getOwnerPlayer()!!) },
            DropLootTables.DataKeys.DropTargetTypes.POKEMON_WORLD_POSITION to { evt -> evt.loser.entity?.let(::PokemonEntityDropTarget) },
            DropLootTables.DataKeys.DropTargetTypes.POKEMON_HELD_ITEM to { evt -> PokemonHeldItemDropTarget(evt.winner.effectedPokemon) },
        )

    override val selectedDropTargetTypes: List<ResourceLocation>
        get() = DropLootTables.config.defeatedDropTargets.map { it.asIdentifierDefaultingNamespace(MOD_ID) }

    fun registerDropTargetType(id: ResourceLocation, getter: (evt: SingleDefeatEvent) -> DropTarget?) {
        dropTargetTypes[id] = getter
    }

    override fun getContext(evt: SingleDefeatEvent): DefeatedDropper.Context = DefeatedDropper.Context(
        getLevel(evt),
        evt.loser.effectedPokemon,
        evt.winner.effectedPokemon,
    )

    override fun getLevel(evt: SingleDefeatEvent): ServerLevel =
        evt.winner.effectedPokemon.getOwnerPlayer()!!.level() as ServerLevel

    override fun isRelevantEvent(evt: SingleDefeatEvent): Boolean =
        evt.winner.effectedPokemon.getOwnerPlayer() != null
                && evt.battle.isPvW
                && evt.winner.effectedPokemon.isPlayerOwned()

    override fun processOtherDrops(evt: SingleDefeatEvent): List<ItemStack> {
        val ctx = getContext(evt)
        val droppers = getDroppers(ctx) ?: emptyList()
        if (!droppers.any(DefeatedDropper::preserveBaseDrops)) return emptyList()

        val caughtBaseDrops = baseDrops[evt.winner.uuid] ?: emptyList()

        return caughtBaseDrops.mapNotNull { baseDrop ->
            if (baseDrop !is ItemDropEntry) {
                val pos = evt.loser.entity?.position() ?: return@mapNotNull null
                baseDrop.drop(evt.winner.entity, ctx.level, pos, evt.winner.effectedPokemon.getOwnerPlayer())
                return@mapNotNull null
            }

            baseDrop.buildItem(ctx.level)
        }
    }

    override fun cleanup(evt: SingleDefeatEvent) {
        baseDrops.remove(evt.winner.uuid)
    }
}