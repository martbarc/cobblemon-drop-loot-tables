package us.timinc.mc.cobblemon.droploottables.condition

import com.cobblemon.mod.common.util.asIdentifierDefaultingNamespace
import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.level.storage.loot.LootContext
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType
import us.timinc.mc.cobblemon.droploottables.DropLootTables
import us.timinc.mc.cobblemon.droploottables.MOD_ID
import us.timinc.mc.cobblemon.droploottables.paramextractor.TeamParamExtractor
import us.timinc.mc.cobblemon.timcore.LimitedList
import us.timinc.mc.cobblemon.timcore.PokemonMatcher

class TeamMatcherCondition(
    val targetTeam: ResourceLocation = DropLootTables.DataKeys.LootParamKeys.FOCUS_PLAYER,
    val matcher: Set<PokemonMatcher>,
    val antiMatcher: Set<PokemonMatcher>,
) : LootItemCondition {
    companion object {
        val CODEC: MapCodec<TeamMatcherCondition> = RecordCodecBuilder.mapCodec { instance ->
            instance.group(
                Codec.STRING.optionalFieldOf(
                    "target_team",
                    DropLootTables.DataKeys.LootParamKeys.FOCUS_POKEMON.toString()
                )
                    .forGetter { it.targetTeam.toString() },
                PokemonMatcher.STRING_CODEC.listOf().optionalFieldOf("matcher", emptyList())
                    .forGetter { it.matcher.toList() },
                PokemonMatcher.STRING_CODEC.listOf().optionalFieldOf("anti_matcher", emptyList())
                    .forGetter { it.antiMatcher.toList() },
            ).apply(instance) { target, matcher, antiMatcher ->
                TeamMatcherCondition(
                    target.asIdentifierDefaultingNamespace(MOD_ID),
                    matcher.toSet(),
                    antiMatcher.toSet()
                )
            }
        }
    }

    override fun getType(): LootItemConditionType = DropLootTables.LootItemConditionTypes.TEAM_MATCHER_CONDITION

    override fun test(ctx: LootContext): Boolean {
        val team = TeamParamExtractor.getFrom(ctx, targetTeam) ?: return false
        return team.any { pokemon -> LimitedList.PokemonMatcherList.matchesList(pokemon, matcher, antiMatcher) }
    }
}