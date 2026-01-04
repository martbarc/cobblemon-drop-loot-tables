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

class ReleasedDropper(
    override val trigger: ResourceLocation,
    override val matcher: List<PokemonMatcher>,
    override val antiMatcher: List<PokemonMatcher>,
    override val lootTables: List<ResourceLocation>,
) : Dropper<ReleasedDropper.Context>() {
    companion object {
        val CODEC: MapCodec<ReleasedDropper> = RecordCodecBuilder.mapCodec { instance ->
            instance.group(
                CodecPieces.getTrigger(ReleasedDropper::trigger),
                CodecPieces.getMatcher(ReleasedDropper::matcher),
                CodecPieces.getAntiMatcher(ReleasedDropper::antiMatcher),
                CodecPieces.getTables(ReleasedDropper::lootTables),
            ).apply(instance, ::ReleasedDropper)
        }

        val DROPPER_TYPE = DropperType(CODEC)
    }

    override fun getType(): DropperType<*, *> = DropLootTables.DropperTypes.RELEASED

    class Context(
        override val level: ServerLevel,
        override val pokemon: Pokemon,
        val player: ServerPlayer,
    ) : DropContext {
        override fun toLootParams(): LootParams {
            return LootParams(
                level,
                mapOf(
                    LootContextParams.ORIGIN to player.position(),
                    DropLootTables.LootParams.POKEMON_DETAILS to pokemon,
                    DropLootTables.LootParams.RELEVANT_PLAYER to player,
                ),
                mapOf(),
                player.luck
            )
        }
    }
}
