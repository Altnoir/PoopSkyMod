package com.altnoir.poopsky.mixin;

import com.altnoir.poopsky.init.PoVanillaBackport;
import com.altnoir.poopsky.impl.event.PoCopperEvents;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import java.util.Optional;

@Mixin(LightningBolt.class)
public abstract class PoCopperWeatheringMixin {
    @Redirect(method = "clearCopperOnLightningStrike", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/WeatheringCopper;getFirst(Lnet/minecraft/world/level/block/state/BlockState;)Lnet/minecraft/world/level/block/state/BlockState;"))
    private static BlockState poopsky$first(BlockState state) {
        if (PoCopperEvents.ready && PoVanillaBackport.copperStages().contains(state.getBlock())) return PoVanillaBackport.CHISELED_COPPER.get().withPropertiesOf(state);
        return WeatheringCopper.getFirst(state);
    }
    @Redirect(method = "randomStepCleaningCopper", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/WeatheringCopper;getPrevious(Lnet/minecraft/world/level/block/state/BlockState;)Ljava/util/Optional;"))
    private static Optional<BlockState> poopsky$previous(BlockState state) {
        if (PoCopperEvents.ready) {
            int i = PoVanillaBackport.copperStages().indexOf(state.getBlock());
            if (i >= 0) return i > 0 ? Optional.of(PoVanillaBackport.copperStages().get(i - 1).withPropertiesOf(state)) : Optional.empty();
        }
        return WeatheringCopper.getPrevious(state);
    }
}
