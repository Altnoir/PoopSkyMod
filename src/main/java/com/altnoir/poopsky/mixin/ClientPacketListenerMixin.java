package com.altnoir.poopsky.mixin;

import com.altnoir.poopsky.content.entity.p.ToiletPlugEntity;
import com.altnoir.poopsky.impl.event.PSKeyBoardInput;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetPassengersPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ClientPacketListener.class)
public class ClientPacketListenerMixin {
    @Inject(method = "handleSetEntityPassengersPacket", at = @At("TAIL"))
    private void poopsky$replaceToiletPlugDismountMessage(ClientboundSetPassengersPacket packet, CallbackInfo ci) {
        var minecraft = Minecraft.getInstance();
        if (minecraft.player != null && minecraft.player.getVehicle() instanceof ToiletPlugEntity) {
            minecraft.gui.setOverlayMessage(Component.translatable(
                    "message.poopsky.toilet_plug.dismount",
                    PSKeyBoardInput.getLocalizedKeyMessage(PSKeyBoardInput.DISMOUNT_PLUG_KEY)
            ), false);
        }
    }
}
