package us.timinc.mc.cobblemon.droploottables.mixin;

import us.timinc.mc.cobblemon.droploottables.compat.counter.DropLootTablesCounter;

@org.spongepowered.asm.mixin.Mixin(us.timinc.mc.cobblemon.counter.CounterMod.class)
public class CounterPresent {
    @org.spongepowered.asm.mixin.injection.Inject(method = "<init>", at = @org.spongepowered.asm.mixin.injection.At("HEAD"), remap = false)
    private static void initializeMixin(org.spongepowered.asm.mixin.injection.callback.CallbackInfo ci) {
        DropLootTablesCounter.INSTANCE.toString();
        System.out.println("Loaded DropLootTables compat for Counter.");
    }
}