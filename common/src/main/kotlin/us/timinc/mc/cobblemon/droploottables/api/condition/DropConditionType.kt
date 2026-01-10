package us.timinc.mc.cobblemon.droploottables.api.condition

import com.mojang.serialization.Lifecycle
import com.mojang.serialization.MapCodec
import net.minecraft.core.MappedRegistry
import net.minecraft.core.Registry
import net.minecraft.resources.ResourceKey
import us.timinc.mc.cobblemon.droploottables.DropLootTables.modResource

data class DropConditionType<C, T : DropCondition<C>>(
    val codec: MapCodec<T>,
) {
    companion object {
        val REGISTRY: Registry<DropConditionType<*, *>> = MappedRegistry(
            ResourceKey.createRegistryKey(modResource("drop_condition")),
            Lifecycle.stable()
        )
    }
}