package us.timinc.mc.cobblemon.droploottables.dropper

import com.cobblemon.mod.common.pokemon.Pokemon
import com.mojang.serialization.Codec
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
import us.timinc.mc.cobblemon.timcore.LimitedList
import us.timinc.mc.cobblemon.timcore.PokemonMatcher

class EvolvedDropper(
    override val trigger: ResourceLocation,
    override val matcher: List<PokemonMatcher>,
    override val antiMatcher: List<PokemonMatcher>,
    override val lootTables: List<ResourceLocation>,
    val prevMatcher: List<PokemonMatcher> = emptyList(),
    val prevAntiMatcher: List<PokemonMatcher> = emptyList(),
    val preserveBaseDrops: Boolean = false,
) : Dropper<EvolvedDropper.Context>() {
    companion object {
        val CODEC: MapCodec<EvolvedDropper> = RecordCodecBuilder.mapCodec { instance ->
            instance.group(
                CodecPieces.getTrigger(EvolvedDropper::trigger),
                CodecPieces.getMatcher(EvolvedDropper::matcher),
                CodecPieces.getAntiMatcher(EvolvedDropper::antiMatcher),
                CodecPieces.getTables(EvolvedDropper::lootTables),
                PokemonMatcher.STRING_CODEC.listOf().optionalFieldOf("prevMatcher", emptyList())
                    .forGetter(EvolvedDropper::prevMatcher),
                PokemonMatcher.STRING_CODEC.listOf().optionalFieldOf("prevAntiMatcher", emptyList())
                    .forGetter(EvolvedDropper::prevAntiMatcher),
                Codec.BOOL.optionalFieldOf("preserveBaseDrops", false).forGetter(EvolvedDropper::preserveBaseDrops),
            ).apply(instance, ::EvolvedDropper)
        }

        val DROPPER_TYPE = DropperType(CODEC)
    }

    override fun getType(): DropperType<*, *> = DropLootTables.DropperTypes.EVOLVED

    class Context(
        override val pokemon: Pokemon,
        override val level: ServerLevel,
        val player: ServerPlayer,
        val previous: Pokemon,
    ) : DropContext {
        override fun toLootParams(): LootParams = LootParams(
            level,
            mapOf(
                LootContextParams.ORIGIN to player.position(),
                LootContextParams.THIS_ENTITY to pokemon.entity,
                DropLootTables.LootParams.POKEMON_DETAILS to pokemon,
                DropLootTables.LootParams.RELEVANT_PLAYER to player,
            ),
            mapOf(),
            player.luck
        )
    }

    override fun canDrop(context: Context): Boolean =
        super.canDrop(context)
                && LimitedList.PokemonMatcherList.matchesList(
            context.previous,
            prevMatcher.toSet(),
            prevAntiMatcher.toSet()
        )
}
