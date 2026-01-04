package us.timinc.mc.cobblemon.droploottables.dropper

import com.cobblemon.mod.common.pokemon.Pokemon
import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.level.storage.loot.LootParams
import net.minecraft.world.level.storage.loot.parameters.LootContextParam
import net.minecraft.world.level.storage.loot.parameters.LootContextParams
import us.timinc.mc.cobblemon.droploottables.DropLootTables
import us.timinc.mc.cobblemon.droploottables.api.DropContext
import us.timinc.mc.cobblemon.droploottables.api.Dropper
import us.timinc.mc.cobblemon.droploottables.api.Dropper.Companion.CodecPieces
import us.timinc.mc.cobblemon.droploottables.api.DropperType
import us.timinc.mc.cobblemon.timcore.PokemonMatcher

class KilledDropper(
    override val trigger: ResourceLocation,
    override val matcher: List<PokemonMatcher>,
    override val antiMatcher: List<PokemonMatcher>,
    override val lootTables: List<ResourceLocation>,
    val preserveBaseDrops: Boolean = false,
) : Dropper<KilledDropper.Context>() {
    companion object {
        val CODEC: MapCodec<KilledDropper> = RecordCodecBuilder.mapCodec { instance ->
            instance.group(
                CodecPieces.getTrigger(KilledDropper::trigger),
                CodecPieces.getMatcher(KilledDropper::matcher),
                CodecPieces.getAntiMatcher(KilledDropper::antiMatcher),
                CodecPieces.getTables(KilledDropper::lootTables),
                Codec.BOOL.optionalFieldOf("preserveBaseDrops", false).forGetter(KilledDropper::preserveBaseDrops),
            ).apply(instance, ::KilledDropper)
        }

        val DROPPER_TYPE = DropperType(CODEC)
    }

    override fun getType(): DropperType<*, *> = DropLootTables.DropperTypes.KILLED

    class Context(
        override val level: ServerLevel,
        override val pokemon: Pokemon,
        val killer: LivingEntity?,
    ) : DropContext {
        override fun toLootParams(): LootParams {
            val params = mutableMapOf<LootContextParam<*>, Any>()
            val origin = pokemon.entity?.position()
            origin?.let { params[LootContextParams.ORIGIN] = it }
            pokemon.entity?.let { params[LootContextParams.THIS_ENTITY] = it }
            params[DropLootTables.LootParams.POKEMON_DETAILS] = pokemon
            (killer as? ServerPlayer)?.let {
                params[DropLootTables.LootParams.RELEVANT_PLAYER] = it
            }
            return LootParams(level, params, mapOf(), (killer as? ServerPlayer)?.luck ?: 0F)
        }
    }
}
