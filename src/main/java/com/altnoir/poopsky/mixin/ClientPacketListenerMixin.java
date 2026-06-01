package com.altnoir.poopsky.mixin;

import com.altnoir.poopsky.entity.p.ToiletPlugEntity;
import com.altnoir.poopsky.event.PSKeyBoardInput;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = ClientPacketListener.class)
public class ClientPacketListenerMixin {
    @Shadow
    @Final
    private Minecraft minecraft;

    @Redirect(
            method = "handleSetEntityPassengersPacket",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;setOverlayMessage(Lnet/minecraft/network/chat/Component;Z)V")
    )
    private void poopsky$replaceToiletPlugDismountMessage(Gui gui, Component message, boolean animate) {
        if (this.minecraft.player != null && this.minecraft.player.getVehicle() instanceof ToiletPlugEntity) {
            gui.setOverlayMessage(Component.translatable(
                    "message.poopsky.toilet_plug.dismount",
                    PSKeyBoardInput.DISMOUNT_PLUG_KEY.getTranslatedKeyMessage()
            ), animate);
            return;
        }

        gui.setOverlayMessage(message, animate);
    }
}
