package com.altnoir.poopsky.mixin;

import com.altnoir.poopsky.content.item.p.SnailShellItem;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public class PlayerMixin {
    @Inject(method = "updatePlayerPose", at = @At("TAIL"))
    private void poopsky$crawlWithShell(CallbackInfo ci) {
        Player player = (Player) (Object) this;
        if (player.isAlive() && !player.isPassenger() && !player.isSleeping()
                && !player.isFallFlying() && !player.getAbilities().flying
                && player.getForcedPose() == null
                && player.getItemBySlot(EquipmentSlot.CHEST).getItem() instanceof SnailShellItem) {
            player.setPose(Pose.SWIMMING);
        }
    }
}
