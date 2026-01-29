package us.timinc.mc.cobblemon.droploottables.condition

import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.world.level.storage.loot.LootContext
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType
import us.timinc.mc.cobblemon.droploottables.DropLootTables

@Deprecated ("")
class WasInBattleCondition(
    val value: Boolean = true,
) : LootItemCondition {
    companion object {
        val CODEC: MapCodec<WasInBattleCondition> = RecordCodecBuilder.mapCodec { instance ->
            instance.group(
                Codec.BOOL.fieldOf("value").orElse(true)
                    .forGetter(WasInBattleCondition::value)
            ).apply(instance, ::WasInBattleCondition)
        }
    }

    override fun getType(): LootItemConditionType = DropLootTables.LootItemConditionTypes.WAS_IN_BATTLE_CONDITION

    override fun test(ctx: LootContext): Boolean {
        return ctx.getParamOrNull(DropLootTables.LootParams.WAS_IN_BATTLE) == value
    }
}