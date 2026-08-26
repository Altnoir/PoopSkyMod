package com.altnoir.poopsky.mixin;

import com.altnoir.poopsky.data.FishingLootGen;
import com.altnoir.poopsky.init.PoFluids;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.ReloadableServerRegistries;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.storage.loot.LootTable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = FishingHook.class)
public class FishingHookMixin {
    @Unique
    private final FishingHook poopSky$fishingHook = (FishingHook) (Object) this;

    @ModifyReturnValue(method = "calculateOpenWater", at = @At("RETURN"))
    private boolean poopsky$calculateOpenWater(boolean original, BlockPos pos) {
        FluidState fluidstate = poopSky$fishingHook.level().getFluidState(pos);
        return original || fluidstate.is(PoFluids.URINE.get()) || fluidstate.is(PoFluids.FLOWING_URINE.get());
    }

    @WrapOperation(method = "retrieve", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/ReloadableServerRegistries$Holder;getLootTable(Lnet/minecraft/resources/ResourceKey;)Lnet/minecraft/world/level/storage/loot/LootTable;"))
    private LootTable poopsky$getLootTable(ReloadableServerRegistries.Holder lookup, ResourceKey<LootTable> key,
                                           Operation<LootTable> original) {
        if ((poopSky$fishingHook.level().getFluidState(poopSky$fishingHook.blockPosition()).is(PoFluids.URINE.get())
                || poopSky$fishingHook.level().getFluidState(poopSky$fishingHook.blockPosition()).is(PoFluids.FLOWING_URINE.get()))
        ) {
            return original.call(lookup, FishingLootGen.FISHING_URINE);
        }
        return original.call(lookup, key);
    }
}
