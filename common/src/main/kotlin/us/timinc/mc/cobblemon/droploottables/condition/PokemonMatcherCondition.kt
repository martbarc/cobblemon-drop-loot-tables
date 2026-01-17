package us.timinc.mc.cobblemon.droploottables.condition

import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.level.storage.loot.LootContext
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType
import us.timinc.mc.cobblemon.droploottables.DropLootTables
import us.timinc.mc.cobblemon.droploottables.api.condition.PokemonParamExtractor
import us.timinc.mc.cobblemon.timcore.LimitedList
import us.timinc.mc.cobblemon.timcore.PokemonMatcher

class PokemonMatcherCondition(
    val targetPokemon: ResourceLocation = DropLootTables.DataKeys.LootParamKeys.FOCUS_POKEMON,
    val matcher: Set<PokemonMatcher>,
    val antiMatcher: Set<PokemonMatcher>,
) : LootItemCondition {
    companion object {
        val CODEC: MapCodec<PokemonMatcherCondition> = RecordCodecBuilder.mapCodec { instance ->
            instance.group(
                ResourceLocation.CODEC.optionalFieldOf("targetPokemon", DropLootTables.DataKeys.LootParamKeys.FOCUS_POKEMON)
                    .forGetter(PokemonMatcherCondition::targetPokemon),
                PokemonMatcher.STRING_CODEC.listOf().optionalFieldOf("matcher", emptyList())
                    .forGetter { it.matcher.toList() },
                PokemonMatcher.STRING_CODEC.listOf().optionalFieldOf("antiMatcher", emptyList())
                    .forGetter { it.antiMatcher.toList() },
            ).apply(instance) { target, matcher, antiMatcher ->
                PokemonMatcherCondition(target, matcher.toSet(), antiMatcher.toSet())
            }
        }
    }

    override fun getType(): LootItemConditionType = DropLootTables.LootItemConditionTypes.POKEMON_MATCHER_CONDITION

    override fun test(ctx: LootContext): Boolean {
        val pokemon = PokemonParamExtractor.getFrom(ctx, targetPokemon) ?: return false
        return LimitedList.PokemonMatcherList.matchesList(pokemon, matcher, antiMatcher)
    }
}