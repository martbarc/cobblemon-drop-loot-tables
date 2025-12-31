package us.timinc.mc.cobblemon.droploottables.api

import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.item.ItemStack
import us.timinc.mc.cobblemon.droploottables.data.DropperDataManager

interface DropHandler<C : DropContext, D : Dropper<C>, E> {
    val dropperTypeId: ResourceLocation
    fun getContext(evt: E): C

    fun getDroppers(ctx: C) =
        DropperDataManager.getValidDroppers<C, D>(dropperTypeId, ctx)

    fun getLevel(evt: E): ServerLevel?

    fun handle(evt: E) {
        if (!isRelevantEvent(evt)) return
        val dropTarget = getDropTarget(evt) ?: return
        val ctx = getContext(evt)
        val drops = getDroppers(ctx)?.flatMap { dropper ->
            dropper.lootTables.flatMap { tableId ->
                dropFromTable(
                    tableId,
                    ctx.toLootParams(),
                    getLevel(evt) as ServerLevel
                )
            }
        } ?: return
        dropTarget.dropTo(drops)
    }

    val dropTargetTypes: MutableMap<ResourceLocation, (evt: E) -> DropTarget?>
    val selectedDropTargetTypes: List<ResourceLocation>

    fun getDropTarget(evt: E): DropTarget? =
        selectedDropTargetTypes.firstNotNullOfOrNull { id -> dropTargetTypes[id]?.invoke(evt) }

    fun lootTableExists(level: ServerLevel, tableId: ResourceLocation) =
        level.server.reloadableRegistries().getKeys(Registries.LOOT_TABLE).contains(tableId)

    fun dropFromTable(
        id: ResourceLocation,
        params: net.minecraft.world.level.storage.loot.LootParams,
        level: ServerLevel,
    ): List<ItemStack> {
        if (!lootTableExists(level, id)) {
            return emptyList()
        }

        val lootTable = level.server.reloadableRegistries().getLootTable(
            ResourceKey.create(Registries.LOOT_TABLE, id)
        )

        val results = lootTable.getRandomItems(
            params
        )
        return results
    }

    fun isRelevantEvent(evt: E): Boolean = getLevel(evt) != null
}