package com.altnoir.poopsky.mixin;

import com.altnoir.poopsky.init.PoVanillaBackport;
import com.altnoir.poopsky.impl.event.PoCopperEvents;
import net.minecraft.world.item.HoneycombItem;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import java.util.Optional;

@Mixin(HoneycombItem.class)
public abstract class PoCopperWaxMixin {
    @Inject(method = "getWaxed", at = @At("HEAD"), cancellable = true)
    private static void poopsky$wax(BlockState state, CallbackInfoReturnable<Optional<BlockState>> cir) {
        if (!PoCopperEvents.ready) return;
        int i = PoVanillaBackport.copperStages().indexOf(state.getBlock());
        if (i >= 0) cir.setReturnValue(Optional.of(PoVanillaBackport.waxedCopperStages().get(i).withPropertiesOf(state)));
    }
}
