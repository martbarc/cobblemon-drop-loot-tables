package us.timinc.mc.cobblemon.droploottables.condition

import com.cobblemon.mod.common.api.pokedex.PokedexEntryProgress
import com.cobblemon.mod.common.util.pokedex
import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.level.storage.loot.LootContext
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType
import us.timinc.mc.cobblemon.droploottables.DropLootTables
import us.timinc.mc.cobblemon.droploottables.api.condition.PlayerParamExtractor
import us.timinc.mc.cobblemon.droploottables.api.condition.PokemonParamExtractor

class KnowledgeLevelCondition(
    val targetPokemon: ResourceLocation = DropLootTables.DataKeys.LootParamKeys.FOCUS_POKEMON,
    val targetPlayer: ResourceLocation = DropLootTables.DataKeys.LootParamKeys.FOCUS_PLAYER,
    val knowledge: PokedexEntryProgress,
) : LootItemCondition {
    companion object {
        val CODEC: MapCodec<KnowledgeLevelCondition> = RecordCodecBuilder.mapCodec { instance ->
            instance.group(
                ResourceLocation.CODEC.optionalFieldOf(
                    "targetPokemon",
                    DropLootTables.DataKeys.LootParamKeys.FOCUS_POKEMON
                )
                    .forGetter(KnowledgeLevelCondition::targetPokemon),
                ResourceLocation.CODEC.optionalFieldOf(
                    "targetPlayer",
                    DropLootTables.DataKeys.LootParamKeys.FOCUS_PLAYER
                )
                    .forGetter(KnowledgeLevelCondition::targetPlayer),
                Codec.STRING.fieldOf("knowledge").forGetter { it.knowledge.name }
            ).apply(instance) { pokemon, player, knowledge ->
                KnowledgeLevelCondition(
                    pokemon,
                    player,
                    PokedexEntryProgress.valueOf(knowledge.uppercase())
                )
            }
        }
    }

    override fun getType(): LootItemConditionType = DropLootTables.LootItemConditionTypes.KNOWLEDGE_LEVEL_CONDITION

    override fun test(ctx: LootContext): Boolean {
        val pokemon = PokemonParamExtractor.getFrom(ctx, targetPokemon) ?: return false
        val player = PlayerParamExtractor.getFrom(ctx, targetPlayer) ?: return false
        val playerKnowledge =
            player.pokedex().getSpeciesRecord(pokemon.species.resourceIdentifier)
                ?.getFormRecord(pokemon.form.name)?.knowledge
                ?: PokedexEntryProgress.NONE
        return playerKnowledge.ordinal >= knowledge.ordinal
    }
}