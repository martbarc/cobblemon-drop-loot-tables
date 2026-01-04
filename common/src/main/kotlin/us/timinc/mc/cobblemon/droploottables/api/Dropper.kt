package us.timinc.mc.cobblemon.droploottables.api

import com.cobblemon.mod.common.pokemon.Pokemon
import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.resources.ResourceLocation
import us.timinc.mc.cobblemon.timcore.LimitedList
import us.timinc.mc.cobblemon.timcore.PokemonMatcher

abstract class Dropper<T : DropContext> {
    companion object {
        val CODEC: Codec<Dropper<*>> =
            DropperType.REGISTRY.byNameCodec()
                .dispatch("trigger", Dropper<*>::getType) {
                    @Suppress("UNCHECKED_CAST")
                    it.codec as MapCodec<Dropper<*>>
                }

        object CodecPieces {
            fun <T : Dropper<*>> getTrigger(getter: (dropper: T) -> ResourceLocation): RecordCodecBuilder<T, ResourceLocation> =
                ResourceLocation.CODEC.fieldOf("trigger").forGetter(getter)

            fun <T : Dropper<*>> getMatcher(getter: (dropper: T) -> List<PokemonMatcher>): RecordCodecBuilder<T, List<PokemonMatcher>> =
                PokemonMatcher.STRING_CODEC.listOf().optionalFieldOf("matcher", emptyList()).forGetter(getter)

            fun <T : Dropper<*>> getAntiMatcher(getter: (dropper: T) -> List<PokemonMatcher>): RecordCodecBuilder<T, List<PokemonMatcher>> =
                PokemonMatcher.STRING_CODEC.listOf().optionalFieldOf("anti_matcher", emptyList()).forGetter(getter)

            fun <T : Dropper<*>> getTables(getter: (dropper: T) -> List<ResourceLocation>): RecordCodecBuilder<T, List<ResourceLocation>> =
                ResourceLocation.CODEC.listOf().optionalFieldOf("tables", emptyList()).forGetter(getter)
        }
    }

    var id: ResourceLocation? = null

    abstract val trigger: ResourceLocation
    abstract val matcher: List<PokemonMatcher>
    abstract val antiMatcher: List<PokemonMatcher>
    abstract val lootTables: List<ResourceLocation>
    abstract fun getType(): DropperType<*, *>

    open fun canDrop(context: T): Boolean = matches(context.pokemon)

    fun matches(pokemon: Pokemon) =
        LimitedList.PokemonMatcherList.matchesList(pokemon, matcher.toSet(), antiMatcher.toSet())
}