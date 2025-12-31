package us.timinc.mc.cobblemon.droploottables.dropper

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

class DefeatedDropper(
    override val trigger: ResourceLocation,
    override val matcher: List<PokemonMatcher>,
    override val antiMatcher: List<PokemonMatcher>,
    override val lootTables: List<ResourceLocation>,
) : Dropper<DefeatedDropper.Context>() {
    companion object {
        val CODEC: MapCodec<DefeatedDropper> = RecordCodecBuilder.mapCodec { instance ->
            instance.group(
                CodecPieces.getTrigger(DefeatedDropper::trigger),
                CodecPieces.getMatcher(DefeatedDropper::matcher),
                CodecPieces.getAntiMatcher(DefeatedDropper::antiMatcher),
                CodecPieces.getTables(DefeatedDropper::lootTables),
            ).apply(instance, ::DefeatedDropper)
        }

        val DROPPER_TYPE = DropperType(CODEC)
    }

    class Context(
        override val pokemon: Pokemon,
        override val level: ServerLevel,
        val player: ServerPlayer,
        val participatedInBattle: Boolean,
    ) : DropContext {
        override fun toLootParams(): LootParams = LootParams(
            level,
            mapOf(
                LootContextParams.ORIGIN to player.position(),
                LootContextParams.THIS_ENTITY to player,
                DropLootTables.LootParams.POKEMON_DETAILS to pokemon,
                DropLootTables.LootParams.PARTICIPATED_IN_BATTLE to participatedInBattle,
            ),
            mapOf(),
            player.luck
        )
    }

    override fun getType(): DropperType<*, *> = DropLootTables.DropperTypes.DEFEATED
}