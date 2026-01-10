package us.timinc.mc.cobblemon.droploottables.dropper

import com.cobblemon.mod.common.pokemon.Pokemon
import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.storage.loot.LootParams
import net.minecraft.world.level.storage.loot.parameters.LootContextParams
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition
import us.timinc.mc.cobblemon.droploottables.DropLootTables
import us.timinc.mc.cobblemon.droploottables.api.DropContext
import us.timinc.mc.cobblemon.droploottables.api.Dropper
import us.timinc.mc.cobblemon.droploottables.api.Dropper.Companion.CodecPieces
import us.timinc.mc.cobblemon.droploottables.api.DropperType

class DefeatedDropper(
    override val trigger: ResourceLocation,
    override val lootTables: List<ResourceLocation>,
    override val conditions: List<LootItemCondition>,
    val preserveBaseDrops: Boolean = false,
) : Dropper<DefeatedDropper.Context>() {
    companion object {
        val CODEC: MapCodec<DefeatedDropper> = RecordCodecBuilder.mapCodec { instance ->
            instance.group(
                CodecPieces.getTrigger(DefeatedDropper::trigger),
                CodecPieces.getTables(DefeatedDropper::lootTables),
                CodecPieces.getConditions(DefeatedDropper::conditions),
                Codec.BOOL.optionalFieldOf("preserveBaseDrops", false)
                    .forGetter(DefeatedDropper::preserveBaseDrops)
            ).apply(instance, ::DefeatedDropper)
        }

        val DROPPER_TYPE = DropperType(CODEC)
    }

    override fun getType(): DropperType<*, *> = DropLootTables.DropperTypes.DEFEATED

    class Context(
        override val level: ServerLevel,
        val focusPokemon: Pokemon,
        val actingPokemon: Pokemon,
    ) : DropContext {
        override fun toLootParams(): LootParams = LootParams(
            level,
            mapOf(
                LootContextParams.ORIGIN to focusPokemon.entity!!.position(),
                LootContextParams.THIS_ENTITY to focusPokemon.entity,
                DropLootTables.LootParams.FOCUS_POKEMON to focusPokemon,
                DropLootTables.LootParams.ACTING_POKEMON to actingPokemon,
            ),
            mapOf(),
            focusPokemon.getOwnerPlayer()?.luck ?: 0F
        )
    }
}