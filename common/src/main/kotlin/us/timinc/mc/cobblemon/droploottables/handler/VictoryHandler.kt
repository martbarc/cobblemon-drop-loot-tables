package us.timinc.mc.cobblemon.droploottables.handler

import com.cobblemon.mod.common.util.asIdentifierDefaultingNamespace
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel
import us.timinc.mc.cobblemon.droploottables.DropLootTables
import us.timinc.mc.cobblemon.droploottables.MOD_ID
import us.timinc.mc.cobblemon.droploottables.api.DropHandler
import us.timinc.mc.cobblemon.droploottables.api.DropTarget
import us.timinc.mc.cobblemon.droploottables.dropper.VictoryDropper
import us.timinc.mc.cobblemon.droploottables.droptarget.PlayerDropTarget
import us.timinc.mc.cobblemon.droploottables.droptarget.PokemonEntityDropTarget
import us.timinc.mc.cobblemon.droploottables.event.SingleBattleVictoryEvent

object VictoryHandler : DropHandler<VictoryDropper.Context, VictoryDropper, SingleBattleVictoryEvent> {
    override val dropperTypeId: ResourceLocation = DropLootTables.DataKeys.DropperTypes.VICTORY

    override fun getContext(evt: SingleBattleVictoryEvent): VictoryDropper.Context = VictoryDropper.Context(
        evt.winningPokemon.entity?.level()!! as ServerLevel,
        evt.winningPokemon,
        evt.winningPokemon.getOwnerPlayer()!!
    )

    override fun isRelevantEvent(evt: SingleBattleVictoryEvent): Boolean =
        evt.winningPokemon.entity?.level() as? ServerLevel != null

    override fun getLevel(evt: SingleBattleVictoryEvent): ServerLevel? =
        evt.winningPokemon.entity?.level() as? ServerLevel

    override val dropTargetTypes: MutableMap<ResourceLocation, (evt: SingleBattleVictoryEvent) -> DropTarget?> =
        mutableMapOf(
            DropLootTables.DataKeys.DropTargetTypes.OWNER to { evt ->
                evt.winningPokemon.getOwnerPlayer()?.let(::PlayerDropTarget)
            },
            DropLootTables.DataKeys.DropTargetTypes.POKEMON to { evt -> evt.winningPokemon.entity?.let(::PokemonEntityDropTarget) }
        )

    fun registerDropTargetType(id: ResourceLocation, getter: (evt: SingleBattleVictoryEvent) -> DropTarget?) {
        dropTargetTypes[id] = getter
    }

    override val selectedDropTargetTypes: List<ResourceLocation>
        get() = DropLootTables.config.victoryDropTargets.map { it.asIdentifierDefaultingNamespace(MOD_ID) }
}