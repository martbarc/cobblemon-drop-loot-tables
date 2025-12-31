package us.timinc.mc.cobblemon.droploottables.api

import com.cobblemon.mod.common.pokemon.Pokemon
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.storage.loot.LootParams

interface DropContext {
    val pokemon: Pokemon
    val level: ServerLevel

    fun toLootParams(): LootParams
}