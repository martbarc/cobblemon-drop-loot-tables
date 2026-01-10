package us.timinc.mc.cobblemon.droploottables.dropper

import com.cobblemon.mod.common.pokemon.Pokemon
import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.TamableAnimal
import net.minecraft.world.level.storage.loot.LootParams
import net.minecraft.world.level.storage.loot.parameters.LootContextParam
import net.minecraft.world.level.storage.loot.parameters.LootContextParams
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition
import us.timinc.mc.cobblemon.droploottables.DropLootTables
import us.timinc.mc.cobblemon.droploottables.api.DropContext
import us.timinc.mc.cobblemon.droploottables.api.Dropper
import us.timinc.mc.cobblemon.droploottables.api.Dropper.Companion.CodecPieces
import us.timinc.mc.cobblemon.droploottables.api.DropperType

class KilledDropper(
    override val trigger: ResourceLocation,
    override val lootTables: List<ResourceLocation>,
    override val conditions: List<LootItemCondition>,
    val preserveBaseDrops: Boolean = false,
) : Dropper<KilledDropper.Context>() {
    companion object {
        val CODEC: MapCodec<KilledDropper> = RecordCodecBuilder.mapCodec { instance ->
            instance.group(
                CodecPieces.getTrigger(KilledDropper::trigger),
                CodecPieces.getTables(KilledDropper::lootTables),
                CodecPieces.getConditions(KilledDropper::conditions),
                Codec.BOOL.optionalFieldOf("preserveBaseDrops", false)
                    .forGetter(KilledDropper::preserveBaseDrops),
            ).apply(instance, ::KilledDropper)
        }

        val DROPPER_TYPE = DropperType(CODEC)
    }

    override fun getType(): DropperType<*, *> = DropLootTables.DropperTypes.KILLED

    class Context(
        override val level: ServerLevel,
        val focusPokemon: Pokemon,
        val focusKiller: LivingEntity?,
    ) : DropContext {
        override fun toLootParams(): LootParams {
            val params = mutableMapOf<LootContextParam<*>, Any>(
                DropLootTables.LootParams.FOCUS_POKEMON to focusPokemon
            )
            focusPokemon.entity?.let { entity ->
                params[LootContextParams.ORIGIN] = entity.position()
                params[LootContextParams.THIS_ENTITY] = entity
                entity.lastAttacker?.let { params[LootContextParams.ATTACKING_ENTITY] = it }
                entity.lastDamageSource?.entity?.let { params[LootContextParams.DIRECT_ATTACKING_ENTITY] = it }
            }
            (focusKiller as? ServerPlayer)?.let {
                params[DropLootTables.LootParams.FOCUS_PLAYER] = it
            }
            val attackingPlayerOrPet =
                focusKiller as? ServerPlayer ?: focusKiller as? TamableAnimal
            attackingPlayerOrPet?.let { params[LootContextParams.LAST_DAMAGE_PLAYER] = it }
            return LootParams(
                level,
                params,
                mapOf(),
                (attackingPlayerOrPet as? ServerPlayer
                    ?: ((attackingPlayerOrPet as? TamableAnimal)?.owner) as? ServerPlayer)?.luck ?: 0F
            )
        }
    }
}
