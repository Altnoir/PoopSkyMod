package com.altnoir.poopsky.mixin;

import com.altnoir.poopsky.entity.p.ToiletPlugEntity;
import com.altnoir.poopsky.event.PSKeyBoardInput;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(value = Gui.class)
public class GuiMixin {
    @ModifyVariable(method = "setOverlayMessage", at = @At("HEAD"), argsOnly = true, ordinal = 0)
    private Component poopsky$replaceToiletPlugDismountMessage(Component message) {
        if (message.getContents() instanceof TranslatableContents contents
                && contents.getKey().equals("mount.onboard")
                && Minecraft.getInstance().player != null
                && Minecraft.getInstance().player.getVehicle() instanceof ToiletPlugEntity) {
            return Component.translatable(
                    "message.poopsky.toilet_plug.dismount",
                    PSKeyBoardInput.DISMOUNT_PLUG_KEY.getTranslatedKeyMessage()
            );
        }
        return message;
    }
}
