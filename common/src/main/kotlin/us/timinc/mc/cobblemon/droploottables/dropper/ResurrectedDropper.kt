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
import us.timinc.mc.cobblemon.droploottables.DropLootTables
import us.timinc.mc.cobblemon.droploottables.api.DropContext
import us.timinc.mc.cobblemon.droploottables.api.Dropper
import us.timinc.mc.cobblemon.droploottables.api.Dropper.Companion.CodecPieces
import us.timinc.mc.cobblemon.droploottables.api.DropperType
import us.timinc.mc.cobblemon.timcore.PokemonMatcher

class ResurrectedDropper(
    override val trigger: ResourceLocation,
    override val matcher: List<PokemonMatcher>,
    override val antiMatcher: List<PokemonMatcher>,
    override val lootTables: List<ResourceLocation>,
) : Dropper<ResurrectedDropper.Context>() {
    companion object {
        val CODEC: MapCodec<ResurrectedDropper> = RecordCodecBuilder.mapCodec { instance ->
            instance.group(
                CodecPieces.getTrigger(ResurrectedDropper::trigger),
                CodecPieces.getMatcher(ResurrectedDropper::matcher),
                CodecPieces.getAntiMatcher(ResurrectedDropper::antiMatcher),
                CodecPieces.getTables(ResurrectedDropper::lootTables),
            ).apply(instance, ::ResurrectedDropper)
        }

        val DROPPER_TYPE = DropperType(CODEC)
    }

    override fun getType(): DropperType<*, *> = DropLootTables.DropperTypes.RESURRECTED

    class Context(
        override val level: ServerLevel,
        override val pokemon: Pokemon,
        val player: ServerPlayer?,
        val wasInBattle: Boolean?,
    ) : DropContext {
        override fun toLootParams(): LootParams {
            val params = mutableMapOf<LootContextParam<*>, Any>()
            val origin = player?.position() ?: pokemon.entity?.position()
            origin?.let { params[LootContextParams.ORIGIN] = it }
            pokemon.entity?.let { params[LootContextParams.THIS_ENTITY] = it }
            params[DropLootTables.LootParams.POKEMON_DETAILS] = pokemon
            player?.let {
                params[DropLootTables.LootParams.RELEVANT_PLAYER] = it
            }
            wasInBattle?.let {
                params[DropLootTables.LootParams.PARTICIPATED_IN_BATTLE] = it
            }
            return LootParams(level, params, mapOf(), player?.luck ?: 0F)
        }
    }
}
