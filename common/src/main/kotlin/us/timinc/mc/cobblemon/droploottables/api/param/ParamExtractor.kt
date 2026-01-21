package us.timinc.mc.cobblemon.droploottables.api.param

import net.minecraft.resources.ResourceLocation
import net.minecraft.world.level.storage.loot.LootContext
import net.minecraft.world.level.storage.loot.parameters.LootContextParam
import us.timinc.mc.cobblemon.droploottables.DropLootTables

interface ParamExtractor<T> {
    fun getFrom(ctx: LootContext, target: ResourceLocation): T? {
        val targetLcp = DropLootTables.LootParams.params[target] ?: return null
        return getFrom(ctx, targetLcp)
    }

    fun getFrom(ctx: LootContext, target: LootContextParam<*>): T? {
        val param = ctx.getParamOrNull(target) ?: return null
        return convert(param)
    }

    fun convert(param: Any): T?
}