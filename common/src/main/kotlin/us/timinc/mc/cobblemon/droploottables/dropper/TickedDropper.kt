package us.timinc.mc.cobblemon.droploottables.dropper

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity
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
import java.util.*
import kotlin.jvm.optionals.getOrNull

class TickedDropper(
    override val trigger: ResourceLocation,
    override val lootTables: List<ResourceLocation>,
    override val conditions: List<LootItemCondition>,
    val ticks: Int,
    val isWild: Boolean?,
) : Dropper<TickedDropper.Context>() {
    companion object {
        val CODEC: MapCodec<TickedDropper> = RecordCodecBuilder.mapCodec { instance ->
            instance.group(
                CodecPieces.getTrigger(TickedDropper::trigger),
                CodecPieces.getTables(TickedDropper::lootTables),
                CodecPieces.getConditions(TickedDropper::conditions),
                Codec.INT.fieldOf("ticks").forGetter(TickedDropper::ticks),
                Codec.BOOL.optionalFieldOf("is_wild").forGetter { Optional.ofNullable(it.isWild) }
            ).apply(instance) { trigger, conditions, tables, ticks, isWild ->
                if (ticks <= 0) throw Exception("Ticks must be a positive number.")
                TickedDropper(trigger, conditions, tables, ticks, isWild.getOrNull())
            }
        }

        val DROPPER_TYPE = DropperType(CODEC)
    }

    override fun getType(): DropperType<*, *> = DropLootTables.DropperTypes.TICKED
    override fun canDrop(context: Context): Boolean =
        !context.pokemonEntity.isBusy
                && context.pokemonEntity.ticksLived % ticks == 0
                && (isWild?.let { it == context.focusPokemon.isWild() } ?: true)
                && super.canDrop(context)

    class Context(
        override val level: ServerLevel,
        val focusPokemon: Pokemon,
        val pokemonEntity: PokemonEntity,
    ) : DropContext {
        companion object {
            fun fromEntity(entity: PokemonEntity): Context = Context(
                entity.level() as ServerLevel,
                entity.pokemon,
                entity
            )
        }

        override fun toLootParams(): LootParams = LootParams(
            level,
            mapOf(
                LootContextParams.ORIGIN to pokemonEntity.position(),
                LootContextParams.THIS_ENTITY to pokemonEntity
            ),
            mapOf(),
            0F
        )
    }
}
