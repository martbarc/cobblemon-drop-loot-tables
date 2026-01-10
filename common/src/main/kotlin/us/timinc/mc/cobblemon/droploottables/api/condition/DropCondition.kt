package us.timinc.mc.cobblemon.droploottables.api.condition

import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.level.storage.loot.LootContext
import us.timinc.mc.cobblemon.droploottables.DropLootTables

interface DropCondition<Context> {
    companion object {
        val CODEC: Codec<DropCondition<*>> = DropConditionType.REGISTRY.byNameCodec().dispatch(
            "type",
            DropCondition<*>::getType
        ) {
            @Suppress("UNCHECKED_CAST")
            it.codec as MapCodec<DropCondition<*>>
        }
    }

    val target: ResourceLocation

    fun getType(): DropConditionType<*, *>
    fun matches(params: LootContext): Boolean {
        val targetLcp = DropLootTables.LootParams.params[target]
        val param = params.getParamOrNull(targetLcp) ?: return false
        val convertedParam = convertToContext(param) ?: return false
        return matches(convertedParam)
    }

    fun convertToContext(param: Any): Context?
    fun matches(ctx: Context): Boolean
}