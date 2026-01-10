package us.timinc.mc.cobblemon.droploottables.dropper

import com.cobblemon.mod.common.pokemon.Pokemon
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.level.storage.loot.LootParams
import net.minecraft.world.level.storage.loot.parameters.LootContextParams
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition
import us.timinc.mc.cobblemon.droploottables.DropLootTables
import us.timinc.mc.cobblemon.droploottables.api.DropContext
import us.timinc.mc.cobblemon.droploottables.api.Dropper
import us.timinc.mc.cobblemon.droploottables.api.Dropper.Companion.CodecPieces
import us.timinc.mc.cobblemon.droploottables.api.DropperType

class VictoryDropper(
    override val trigger: ResourceLocation,
    override val lootTables: List<ResourceLocation>,
    override val conditions: List<LootItemCondition>,
) : Dropper<VictoryDropper.Context>() {
    companion object {
        val CODEC: MapCodec<VictoryDropper> = RecordCodecBuilder.mapCodec { instance ->
            instance.group(
                CodecPieces.getTrigger(VictoryDropper::trigger),
                CodecPieces.getTables(VictoryDropper::lootTables),
                CodecPieces.getConditions(VictoryDropper::conditions),
            ).apply(instance, ::VictoryDropper)
        }

        val DROPPER_TYPE = DropperType(CODEC)
    }

    override fun getType(): DropperType<*, *> = DropLootTables.DropperTypes.VICTORY

    class Context(
        override val level: ServerLevel,
        val focusPokemon: Pokemon,
        val focusPlayer: ServerPlayer,
    ) : DropContext {
        override fun toLootParams(): LootParams = LootParams(
            level,
            mapOf(
                LootContextParams.ORIGIN to focusPlayer.position(),
                LootContextParams.THIS_ENTITY to focusPokemon.entity,
                DropLootTables.LootParams.FOCUS_POKEMON to focusPokemon,
                DropLootTables.LootParams.FOCUS_PLAYER to focusPlayer,
            ),
            mapOf(),
            focusPlayer.luck
        )
    }
}