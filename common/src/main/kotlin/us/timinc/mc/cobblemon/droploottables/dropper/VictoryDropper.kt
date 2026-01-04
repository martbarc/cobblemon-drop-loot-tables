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

class VictoryDropper(
    override val trigger: ResourceLocation,
    override val matcher: List<PokemonMatcher>,
    override val antiMatcher: List<PokemonMatcher>,
    override val lootTables: List<ResourceLocation>,
    val facedMatcher: List<PokemonMatcher> = emptyList(),
    val facedAntiMatcher: List<PokemonMatcher> = emptyList(),
    val teamMatcher: List<PokemonMatcher> = emptyList(),
    val teamAntiMatcher: List<PokemonMatcher> = emptyList(),
) : Dropper<VictoryDropper.Context>() {
    companion object {
        val CODEC: MapCodec<VictoryDropper> = RecordCodecBuilder.mapCodec { instance ->
            instance.group(
                CodecPieces.getTrigger(VictoryDropper::trigger),
                CodecPieces.getMatcher(VictoryDropper::matcher),
                CodecPieces.getAntiMatcher(VictoryDropper::antiMatcher),
                CodecPieces.getTables(VictoryDropper::lootTables),
                PokemonMatcher.STRING_CODEC.listOf().optionalFieldOf("facedMatcher", emptyList()).forGetter(VictoryDropper::facedMatcher),
                PokemonMatcher.STRING_CODEC.listOf().optionalFieldOf("facedAntiMatcher", emptyList()).forGetter(VictoryDropper::facedAntiMatcher),
                PokemonMatcher.STRING_CODEC.listOf().optionalFieldOf("teamMatcher", emptyList()).forGetter(VictoryDropper::teamMatcher),
                PokemonMatcher.STRING_CODEC.listOf().optionalFieldOf("teamAntiMatcher", emptyList()).forGetter(VictoryDropper::teamAntiMatcher),
            ).apply(instance, ::VictoryDropper)
        }

        val DROPPER_TYPE = DropperType(CODEC)
    }

    override fun getType(): DropperType<*, *> = DropLootTables.DropperTypes.VICTORY

    class Context(
        override val pokemon: Pokemon,
        override val level: ServerLevel,
        val owner: ServerPlayer,
    ) : DropContext {
        override fun toLootParams(): LootParams = LootParams(
            level,
            mapOf(
                LootContextParams.ORIGIN to owner.position(),
                LootContextParams.THIS_ENTITY to pokemon.entity,
                DropLootTables.LootParams.RELEVANT_PLAYER to owner,
                DropLootTables.LootParams.POKEMON_DETAILS to pokemon
            ),
            mapOf(),
            owner.luck
        )
    }
}