package us.timinc.mc.cobblemon.droploottables.handler

import com.cobblemon.mod.common.api.events.pokemon.LevelUpEvent
import com.cobblemon.mod.common.util.asIdentifierDefaultingNamespace
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel
import us.timinc.mc.cobblemon.droploottables.DropLootTables
import us.timinc.mc.cobblemon.droploottables.MOD_ID
import us.timinc.mc.cobblemon.droploottables.api.DropHandler
import us.timinc.mc.cobblemon.droploottables.api.DropTarget
import us.timinc.mc.cobblemon.droploottables.dropper.LevelUpDropper
import us.timinc.mc.cobblemon.droploottables.droptarget.PlayerDropTarget
import us.timinc.mc.cobblemon.droploottables.droptarget.PlayerEnderChestDropTarget
import us.timinc.mc.cobblemon.droploottables.droptarget.PokemonEntityDropTarget
import us.timinc.mc.cobblemon.droploottables.droptarget.PokemonHeldItemDropTarget
import us.timinc.mc.cobblemon.droploottables.droptarget.PokemonHeldItemReplaceDropTarget

object LevelUpHandler : DropHandler<LevelUpDropper.Context, LevelUpDropper, LevelUpEvent> {
    override val dropperTypeId: ResourceLocation = DropLootTables.DataKeys.DropperTypes.LEVELED

    override fun getContext(evt: LevelUpEvent): LevelUpDropper.Context = LevelUpDropper.Context(
        evt.pokemon.getOwnerPlayer()!!.level() as ServerLevel,
        evt.oldLevel,
        evt.newLevel,
        evt.pokemon,
        evt.pokemon.getOwnerPlayer()!!
    )

    override fun getLevel(evt: LevelUpEvent): ServerLevel? =
        evt.pokemon.getOwnerPlayer()?.level() as? ServerLevel

    override val dropTargetTypes: MutableMap<ResourceLocation, (evt: LevelUpEvent) -> DropTarget?> =
        mutableMapOf(
            DropLootTables.DataKeys.DropTargetTypes.PLAYER_ENDER_STORAGE to { evt ->
                evt.pokemon.getOwnerPlayer()?.let(::PlayerEnderChestDropTarget)
            },
            DropLootTables.DataKeys.DropTargetTypes.PLAYER_INVENTORY to { evt ->
                evt.pokemon.getOwnerPlayer()?.let(::PlayerDropTarget)
            },
            DropLootTables.DataKeys.DropTargetTypes.POKEMON_WORLD_POSITION to { evt ->
                evt.pokemon.entity?.let(::PokemonEntityDropTarget)
            },
            DropLootTables.DataKeys.DropTargetTypes.POKEMON_HELD_ITEM to { evt ->
                evt.pokemon.let(::PokemonHeldItemDropTarget)
            },
            DropLootTables.DataKeys.DropTargetTypes.POKEMON_HELD_ITEM_REPLACE to { evt ->
                evt.pokemon.let(::PokemonHeldItemReplaceDropTarget)
            },
        )

    @Suppress("unused")
    fun registerDropTargetType(id: ResourceLocation, getter: (evt: LevelUpEvent) -> DropTarget?) {
        dropTargetTypes[id] = getter
    }

    override val selectedDropTargetTypes: List<ResourceLocation>
        get() = DropLootTables.config.levelUpDropTargets.map { it.asIdentifierDefaultingNamespace(MOD_ID) }
}