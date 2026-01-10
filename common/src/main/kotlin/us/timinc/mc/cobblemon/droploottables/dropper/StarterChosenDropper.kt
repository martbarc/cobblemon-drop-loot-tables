package us.timinc.mc.cobblemon.droploottables.dropper

import com.cobblemon.mod.common.pokemon.Pokemon
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.level.storage.loot.LootParams
import net.minecraft.world.level.storage.loot.parameters.LootContextParam
import net.minecraft.world.level.storage.loot.parameters.LootContextParams
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition
import us.timinc.mc.cobblemon.droploottables.DropLootTables
import us.timinc.mc.cobblemon.droploottables.api.DropContext
import us.timinc.mc.cobblemon.droploottables.api.Dropper
import us.timinc.mc.cobblemon.droploottables.api.Dropper.Companion.CodecPieces
import us.timinc.mc.cobblemon.droploottables.api.DropperType

class StarterChosenDropper(
    override val trigger: ResourceLocation,
    override val lootTables: List<ResourceLocation>,
    override val conditions: List<LootItemCondition>,
) : Dropper<StarterChosenDropper.Context>() {
    companion object {
        val CODEC: MapCodec<StarterChosenDropper> = RecordCodecBuilder.mapCodec { instance ->
            instance.group(
                CodecPieces.getTrigger(StarterChosenDropper::trigger),
                CodecPieces.getTables(StarterChosenDropper::lootTables),
                CodecPieces.getConditions(StarterChosenDropper::conditions),
            ).apply(instance, ::StarterChosenDropper)
        }

        val DROPPER_TYPE = DropperType(CODEC)
    }

    override fun getType(): DropperType<*, *> = DropLootTables.DropperTypes.STARTER_CHOSEN

    class Context(
        override val level: ServerLevel,
        val focusPokemon: Pokemon,
        val focusPlayer: ServerPlayer,
    ) : DropContext {
        override fun toLootParams(): LootParams {
            val params = mutableMapOf<LootContextParam<*>, Any>(
                LootContextParams.ORIGIN to focusPlayer.position(),
                DropLootTables.LootParams.FOCUS_POKEMON to focusPokemon,
                DropLootTables.LootParams.FOCUS_PLAYER to focusPlayer,
            )
            focusPokemon.entity?.let { params[LootContextParams.THIS_ENTITY] = it }
            return LootParams(level, params, mapOf(), focusPlayer.luck)
        }
    }
}
