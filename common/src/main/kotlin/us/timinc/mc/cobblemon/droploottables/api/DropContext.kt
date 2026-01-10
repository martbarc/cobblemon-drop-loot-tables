package us.timinc.mc.cobblemon.droploottables.api

import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.storage.loot.LootParams

interface DropContext {
    val level: ServerLevel

    fun toLootParams(): LootParams
}