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
import us.timinc.mc.cobblemon.droploottables.DropLootTables
import us.timinc.mc.cobblemon.droploottables.api.DropContext
import us.timinc.mc.cobblemon.droploottables.api.Dropper
import us.timinc.mc.cobblemon.droploottables.api.Dropper.Companion.CodecPieces
import us.timinc.mc.cobblemon.droploottables.api.DropperType
import us.timinc.mc.cobblemon.timcore.PokemonMatcher
import java.util.*
import kotlin.jvm.optionals.getOrNull

/**
 * Fires when a Pokémon entity ticks in-world.
 */
class TickedDropper(
    override val trigger: ResourceLocation,
    override val matcher: List<PokemonMatcher>,
    override val antiMatcher: List<PokemonMatcher>,
    override val lootTables: List<ResourceLocation>,
    val ticks: Int,
    val isWild: Boolean?,
) : Dropper<TickedDropper.Context>() {
    companion object {
        val CODEC: MapCodec<TickedDropper> = RecordCodecBuilder.mapCodec { instance ->
            instance.group(
                CodecPieces.getTrigger(TickedDropper::trigger),
                CodecPieces.getMatcher(TickedDropper::matcher),
                CodecPieces.getAntiMatcher(TickedDropper::antiMatcher),
                CodecPieces.getTables(TickedDropper::lootTables),
                Codec.INT.fieldOf("ticks").forGetter(TickedDropper::ticks),
                Codec.BOOL.optionalFieldOf("is_wild").forGetter { Optional.ofNullable(it.isWild) }
            ).apply(instance) { trigger, matcher, antiMatcher, tables, ticks, isWild ->
                if (ticks <= 0) throw Exception("Ticks must be a positive number.")
                TickedDropper(trigger, matcher, antiMatcher, tables, ticks, isWild.getOrNull())
            }
        }

        val DROPPER_TYPE = DropperType(CODEC)
    }

    override fun getType(): DropperType<*, *> = DropLootTables.DropperTypes.TICKED
    override fun canDrop(context: Context): Boolean =
        !context.pokemonEntity.isBusy
                && context.pokemonEntity.ticksLived % ticks == 0
                && (isWild?.let { it == context.pokemon.isWild() } ?: true)
                && super.canDrop(context)

    class Context(
        override val pokemon: Pokemon,
        override val level: ServerLevel,
        val pokemonEntity: PokemonEntity,
    ) : DropContext {
        companion object {
            fun fromEntity(entity: PokemonEntity): Context = Context(
                entity.pokemon,
                entity.level() as ServerLevel,
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
