package us.timinc.mc.cobblemon.droploottables.api

import com.mojang.serialization.Lifecycle
import com.mojang.serialization.MapCodec
import net.minecraft.core.MappedRegistry
import net.minecraft.core.Registry
import net.minecraft.resources.ResourceKey
import us.timinc.mc.cobblemon.droploottables.DropLootTables

data class DropperType<C : DropContext, T : Dropper<C>>(
    val codec: MapCodec<T>,
) {
    companion object {
        val REGISTRY: Registry<DropperType<*, *>> = MappedRegistry(
            ResourceKey.createRegistryKey(DropLootTables.DataKeys.RegistryKeys.DROPPER_TYPES),
            Lifecycle.stable()
        )
    }
}
