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

/**
 * Fires when a player chooses a starter.
 */
class StarterChosenDropper(
    override val trigger: ResourceLocation,
    override val matcher: List<PokemonMatcher>,
    override val antiMatcher: List<PokemonMatcher>,
    override val lootTables: List<ResourceLocation>,
) : Dropper<StarterChosenDropper.Context>() {
    companion object {
        val CODEC: MapCodec<StarterChosenDropper> = RecordCodecBuilder.mapCodec { instance ->
            instance.group(
                CodecPieces.getTrigger(StarterChosenDropper::trigger),
                CodecPieces.getMatcher(StarterChosenDropper::matcher),
                CodecPieces.getAntiMatcher(StarterChosenDropper::antiMatcher),
                CodecPieces.getTables(StarterChosenDropper::lootTables),
            ).apply(instance, ::StarterChosenDropper)
        }

        val DROPPER_TYPE = DropperType(CODEC)
    }

    override fun getType(): DropperType<*, *> = DropLootTables.DropperTypes.STARTER_CHOSEN

    class Context(
        override val level: ServerLevel,
        override val pokemon: Pokemon,
        val player: ServerPlayer,
    ) : DropContext {
        override fun toLootParams(): LootParams {
            val params = mutableMapOf<LootContextParam<*>, Any>()
            params[LootContextParams.ORIGIN] = player.position()
            pokemon.entity?.let { params[LootContextParams.THIS_ENTITY] = it }
            params[DropLootTables.LootParams.POKEMON_DETAILS] = pokemon
            params[DropLootTables.LootParams.RELEVANT_PLAYER] = player
            return LootParams(level, params, mapOf(), player.luck)
        }
    }
}
