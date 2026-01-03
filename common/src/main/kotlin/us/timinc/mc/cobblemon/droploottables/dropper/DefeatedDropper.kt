package us.timinc.mc.cobblemon.droploottables.dropper

import com.cobblemon.mod.common.pokemon.Pokemon
import com.mojang.serialization.Codec
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
import us.timinc.mc.cobblemon.timcore.LimitedList
import us.timinc.mc.cobblemon.timcore.PokemonMatcher

class DefeatedDropper(
    override val trigger: ResourceLocation,
    override val matcher: List<PokemonMatcher>,
    override val antiMatcher: List<PokemonMatcher>,
    override val lootTables: List<ResourceLocation>,
    val byMatcher: List<PokemonMatcher> = emptyList(),
    val byAntiMatcher: List<PokemonMatcher> = emptyList(),
    val preserveBaseDrops: Boolean = false,
) : Dropper<DefeatedDropper.Context>() {
    companion object {
        val CODEC: MapCodec<DefeatedDropper> = RecordCodecBuilder.mapCodec { instance ->
            instance.group(
                CodecPieces.getTrigger(DefeatedDropper::trigger),
                CodecPieces.getMatcher(DefeatedDropper::matcher),
                CodecPieces.getAntiMatcher(DefeatedDropper::antiMatcher),
                CodecPieces.getTables(DefeatedDropper::lootTables),
                PokemonMatcher.STRING_CODEC.listOf().optionalFieldOf("byMatcher", emptyList())
                    .forGetter(DefeatedDropper::byMatcher),
                PokemonMatcher.STRING_CODEC.listOf().optionalFieldOf("byAntiMatcher", emptyList())
                    .forGetter(DefeatedDropper::byAntiMatcher),
                Codec.BOOL.optionalFieldOf("preserveBaseDrops", false).forGetter(DefeatedDropper::preserveBaseDrops)
            ).apply(instance, ::DefeatedDropper)
        }

        val DROPPER_TYPE = DropperType(CODEC)
    }

    override fun getType(): DropperType<*, *> = DropLootTables.DropperTypes.DEFEATED

    class Context(
        override val pokemon: Pokemon,
        override val level: ServerLevel,
        val player: ServerPlayer,
        val defeatedBy: Pokemon,
    ) : DropContext {
        override fun toLootParams(): LootParams = LootParams(
            level, mapOf(
                LootContextParams.ORIGIN to player.position(),
                LootContextParams.THIS_ENTITY to pokemon.entity,
                DropLootTables.LootParams.POKEMON_DETAILS to pokemon,
                DropLootTables.LootParams.RELEVANT_PLAYER to player
            ), mapOf(), player.luck
        )
    }

    override fun canDrop(context: Context): Boolean =
        super.canDrop(context) && (LimitedList.PokemonMatcherList.matchesList(
            context.defeatedBy, byMatcher.toSet(), byAntiMatcher.toSet()
        ))
}