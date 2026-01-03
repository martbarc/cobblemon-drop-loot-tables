package us.timinc.mc.cobblemon.droploottables.api

import com.cobblemon.mod.common.api.drop.ItemDropEntry
import net.minecraft.core.component.DataComponentPatch
import net.minecraft.core.registries.Registries
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.item.ItemStack
import us.timinc.mc.cobblemon.droploottables.DropLootTables

fun ItemDropEntry.buildItem(level: ServerLevel): ItemStack? {
    val item = level.registryAccess().registryOrThrow(Registries.ITEM).get(item)
        ?: run {
            DropLootTables.debugger.debug("Unable to load drop item: $item", true)
            return null
        }
    val stack = ItemStack(item, quantityRange?.random() ?: quantity)
    val builder = DataComponentPatch.builder()
    components?.forEach {
        builder.set(it)
    }
    stack.applyComponentsAndValidate(builder.build())

    return stack
}