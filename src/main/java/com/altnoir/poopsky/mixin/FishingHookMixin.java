package com.altnoir.poopsky.mixin;

import com.altnoir.poopsky.data.FishingLootGen;
import com.altnoir.poopsky.init.PoFluids;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.ReloadableServerRegistries;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.storage.loot.LootTable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = FishingHook.class)
public class FishingHookMixin {
    @Unique
    private final FishingHook poopSky$fishingHook = (FishingHook) (Object) this;

    @Inject(method = "calculateOpenWater", at = @At("HEAD"), cancellable = true)
    private void onCalculateOpenWater(BlockPos blockPos, CallbackInfoReturnable<Boolean> cir) {
        FluidState fluidstate = poopSky$fishingHook.level().getFluidState(blockPos);

        if (fluidstate.is(PoFluids.URINE.get()) || fluidstate.is(PoFluids.FLOWING_URINE.get())) {
            cir.setReturnValue(true);
        }
    }

    @Redirect(method = "retrieve", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/ReloadableServerRegistries$Holder;getLootTable(Lnet/minecraft/resources/ResourceKey;)Lnet/minecraft/world/level/storage/loot/LootTable;"))
    private LootTable redirectGetLootTable(ReloadableServerRegistries.Holder lookup, ResourceKey<LootTable> id) {
        if ((poopSky$fishingHook.level().getFluidState(poopSky$fishingHook.blockPosition()).is(PoFluids.URINE.get())
                || poopSky$fishingHook.level().getFluidState(poopSky$fishingHook.blockPosition()).is(PoFluids.FLOWING_URINE.get()))
        ) {
            return lookup.getLootTable(FishingLootGen.FISHING_URINE);
        }
        return lookup.getLootTable(id);
    }
}
