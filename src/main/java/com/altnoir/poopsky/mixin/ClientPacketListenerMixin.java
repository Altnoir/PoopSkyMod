package com.altnoir.poopsky.mixin;

import com.altnoir.poopsky.entity.p.FlyEntity;
import com.altnoir.poopsky.entity.p.ToiletPlugEntity;
import com.altnoir.poopsky.event.PSKeyBoardInput;
import com.altnoir.poopsky.sound.FlyAggressiveSoundInstance;
import com.altnoir.poopsky.sound.FlyFlyingSoundInstance;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetPassengersPacket;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ClientPacketListener.class)
public class ClientPacketListenerMixin {
    @Inject(method = "postAddEntitySoundInstance", at = @At("HEAD"), cancellable = true)
    private void queueFlySoundInstance(Entity entity, CallbackInfo ci) {
        if (entity instanceof FlyEntity fly) {
            var soundManager = Minecraft.getInstance().getSoundManager();
            soundManager.queueTickingSound(fly.isAngry() ? new FlyAggressiveSoundInstance(fly) : new FlyFlyingSoundInstance(fly));
            ci.cancel();
        }
    }

    @Inject(method = "handleSetEntityPassengersPacket", at = @At("TAIL"))
    private void replaceToiletPlugDismountMessage(ClientboundSetPassengersPacket packet, CallbackInfo ci) {
        var minecraft = Minecraft.getInstance();
        if (minecraft.player != null && minecraft.player.getVehicle() instanceof ToiletPlugEntity) {
            minecraft.gui.setOverlayMessage(Component.translatable(
                    "message.poopsky.toilet_plug.dismount",
                    PSKeyBoardInput.getLocalizedKeyMessage(PSKeyBoardInput.DISMOUNT_PLUG_KEY)
            ), false);
        }
    }
}
