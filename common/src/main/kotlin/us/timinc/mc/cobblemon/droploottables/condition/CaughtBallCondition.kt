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
import us.timinc.mc.cobblemon.droploottables.paramextractor.PokeBallParamExtractor

class CaughtBallCondition(
    val balls: List<ResourceLocation>,
) : LootItemCondition {
    companion object {
        val CODEC: MapCodec<CaughtBallCondition> = RecordCodecBuilder.mapCodec { instance ->
            instance.group(
                Codec.STRING.listOf().fieldOf("balls").forGetter { it.balls.map(ResourceLocation::toString) }
            ).apply(instance) { CaughtBallCondition(it.map(String::asIdentifierDefaultingNamespace)) }
        }
    }

    override fun getType(): LootItemConditionType = DropLootTables.LootItemConditionTypes.CAUGHT_BALL_CONDITION

    override fun test(ctx: LootContext): Boolean {
        val pokeBall = PokeBallParamExtractor.getFrom(ctx, DropLootTables.LootParams.FOCUS_POKEBALL) ?: return false
        return balls.contains(pokeBall.name)
    }
}