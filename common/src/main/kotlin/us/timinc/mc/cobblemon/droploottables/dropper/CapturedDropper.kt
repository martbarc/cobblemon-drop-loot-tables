package us.timinc.mc.cobblemon.droploottables.dropper

import com.cobblemon.mod.common.pokeball.PokeBall
import com.cobblemon.mod.common.pokemon.Pokemon
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.level.storage.loot.LootParams
import net.minecraft.world.level.storage.loot.parameters.LootContextParams
import us.timinc.mc.cobblemon.droploottables.DropLootTables
import us.timinc.mc.cobblemon.droploottables.api.DropContext
import us.timinc.mc.cobblemon.droploottables.api.Dropper
import us.timinc.mc.cobblemon.droploottables.api.Dropper.Companion.CodecPieces
import us.timinc.mc.cobblemon.droploottables.api.DropperType
import us.timinc.mc.cobblemon.timcore.PokemonMatcher

class CapturedDropper(
    override val trigger: ResourceLocation,
    override val matcher: List<PokemonMatcher>,
    override val antiMatcher: List<PokemonMatcher>,
    override val lootTables: List<ResourceLocation>,
) : Dropper<CapturedDropper.Context>() {
    companion object {
        val CODEC: MapCodec<CapturedDropper> = RecordCodecBuilder.mapCodec { instance ->
            instance.group(
                CodecPieces.getTrigger(CapturedDropper::trigger),
                CodecPieces.getMatcher(CapturedDropper::matcher),
                CodecPieces.getAntiMatcher(CapturedDropper::antiMatcher),
                CodecPieces.getTables(CapturedDropper::lootTables),
            ).apply(instance, ::CapturedDropper)
        }

        val DROPPER_TYPE = DropperType(CODEC)
    }

    override fun getType(): DropperType<*, *> = DropLootTables.DropperTypes.CAPTURED

    class Context(
        override val pokemon: Pokemon,
        override val level: ServerLevel,
        val player: ServerPlayer,
        val pokeBall: PokeBall,
    ) : DropContext {
        override fun toLootParams(): LootParams = LootParams(
            level,
            mapOf(
                LootContextParams.ORIGIN to player.position(),
                LootContextParams.THIS_ENTITY to pokemon.entity,
                DropLootTables.LootParams.POKEMON_DETAILS to pokemon,
                DropLootTables.LootParams.RELEVANT_PLAYER to player,
                DropLootTables.LootParams.POKE_BALL to pokeBall,
            ),
            mapOf(),
            player.luck
        )
    }
}