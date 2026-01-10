package us.timinc.mc.cobblemon.droploottables.condition

import com.cobblemon.mod.common.pokemon.Pokemon
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition
import us.timinc.mc.cobblemon.droploottables.DropLootTables
import us.timinc.mc.cobblemon.droploottables.api.condition.DropConditionType
import us.timinc.mc.cobblemon.timcore.LimitedList
import us.timinc.mc.cobblemon.timcore.PokemonMatcher

class PokemonMatcherCondition(
    val target: ResourceLocation = DropLootTables.DataKeys.LootParamKeys.FOCUS_POKEMON,
    val matcher: List<PokemonMatcher>,
    val antiMatcher: List<PokemonMatcher>,
) : LootItemCondition {
    companion object {
        val CODEC: MapCodec<PokemonMatcherCondition> = RecordCodecBuilder.mapCodec { instance ->
            instance.group(
                ResourceLocation.CODEC.optionalFieldOf("target", DropLootTables.DataKeys.LootParamKeys.FOCUS_POKEMON)
                    .forGetter(PokemonMatcherCondition::target),
                PokemonMatcher.STRING_CODEC.listOf().optionalFieldOf("matcher", emptyList())
                    .forGetter(PokemonMatcherCondition::matcher),
                PokemonMatcher.STRING_CODEC.listOf().optionalFieldOf("antiMatcher", emptyList())
                    .forGetter(PokemonMatcherCondition::antiMatcher),
            ).apply(instance, ::PokemonMatcherCondition)
        }

        val CONDITION_TYPE = DropConditionType(CODEC)
    }

    override fun getType(): DropConditionType<*, *> = DropLootTables.ConditionTypes.POKEMON_MATCHER

    fun matches(ctx: Pokemon): Boolean =
        LimitedList.PokemonMatcherList.matchesList(ctx, matcher.toSet(), antiMatcher.toSet())
}