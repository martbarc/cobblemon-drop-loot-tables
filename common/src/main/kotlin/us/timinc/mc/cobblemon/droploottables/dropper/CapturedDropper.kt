package us.timinc.mc.cobblemon.droploottables.dropper

import com.cobblemon.mod.common.pokeball.PokeBall
import com.cobblemon.mod.common.pokemon.Pokemon
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.level.storage.loot.LootParams
import net.minecraft.world.level.storage.loot.parameters.LootContextParams
import us.timinc.mc.cobblemon.droploottables.DropLootTables
import us.timinc.mc.cobblemon.droploottables.api.DropContext
import us.timinc.mc.cobblemon.droploottables.api.Dropper
import us.timinc.mc.cobblemon.droploottables.api.Dropper.Companion.CodecPieces
import us.timinc.mc.cobblemon.droploottables.api.DropperType
import us.timinc.mc.cobblemon.timcore.PokemonMatcher
import java.util.Optional
import kotlin.jvm.optionals.getOrNull

class CapturedDropper(
    override val trigger: ResourceLocation,
    override val matcher: List<PokemonMatcher>,
    override val antiMatcher: List<PokemonMatcher>,
    override val lootTables: List<ResourceLocation>,
    val pokeball: ResourceLocation? = null,
) : Dropper<CapturedDropper.Context>() {
    companion object {
        val CODEC: MapCodec<CapturedDropper> = RecordCodecBuilder.mapCodec { instance ->
            instance.group(
                CodecPieces.getTrigger(CapturedDropper::trigger),
                CodecPieces.getMatcher(CapturedDropper::matcher),
                CodecPieces.getAntiMatcher(CapturedDropper::antiMatcher),
                CodecPieces.getTables(CapturedDropper::lootTables),
                ResourceLocation.CODEC.optionalFieldOf("pokeball").forGetter { Optional.ofNullable(it.pokeball) }
            ).apply(instance) { trigger, matcher, antiMatcher, lootTables, pokeball ->
                CapturedDropper(trigger, matcher, antiMatcher, lootTables, pokeball.getOrNull())
            }
        }

        val DROPPER_TYPE = DropperType(CODEC)
    }

    override fun getType(): DropperType<*, *> = DropLootTables.DropperTypes.CAPTURED

    class Context(
        override val pokemon: Pokemon,
        override val level: ServerLevel,
        val player: ServerPlayer,
        val pokeball: PokeBall,
    ) : DropContext {
        override fun toLootParams(): LootParams = LootParams(
            level,
            mapOf(
                LootContextParams.ORIGIN to player.position(),
                LootContextParams.THIS_ENTITY to pokemon.entity,
                DropLootTables.LootParams.POKEMON_DETAILS to pokemon,
                DropLootTables.LootParams.RELEVANT_PLAYER to player,
                DropLootTables.LootParams.POKE_BALL to pokeball,
            ),
            mapOf(),
            player.luck
        )
    }

    override fun canDrop(context: Context): Boolean =
        super.canDrop(context)
                && (pokeball?.let { context.pokeball.name == it } ?: true)
}