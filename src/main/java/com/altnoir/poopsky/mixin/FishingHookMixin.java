package com.altnoir.poopsky.mixin;

import com.altnoir.poopsky.datagen.PSFishingLootProvider;
import com.altnoir.poopsky.init.PFluids;
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
    private void onCalculateOpenWater(BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        FluidState fluidstate = poopSky$fishingHook.level().getFluidState(pos);

        if (fluidstate.is(PFluids.POOP.get()) || fluidstate.is(PFluids.FLOWING_POOP.get())) {
            cir.setReturnValue(true);
        }
    }

    @Redirect(method = "retrieve", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/ReloadableServerRegistries$Holder;getLootTable(Lnet/minecraft/resources/ResourceKey;)Lnet/minecraft/world/level/storage/loot/LootTable;"))
    private LootTable redirectGetLootTable(ReloadableServerRegistries.Holder lookup, ResourceKey<LootTable> key) {
        if ((poopSky$fishingHook.level().getFluidState(poopSky$fishingHook.blockPosition()).is(PFluids.POOP.get())
                || poopSky$fishingHook.level().getFluidState(poopSky$fishingHook.blockPosition()).is(PFluids.FLOWING_POOP.get()))
        ) {
            return lookup.getLootTable(PSFishingLootProvider.FISHING_URINE);
        }
        return lookup.getLootTable(key);
    }
}
