package com.altnoir.poopsky.event;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.entity.p.ToiletPlugEntity;
import com.altnoir.poopsky.network.PlugActionPayload;
import com.altnoir.poopsky.network.PlugDismountPayload;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

@EventBusSubscriber(modid = PoopSky.MOD_ID, value = Dist.CLIENT)
public class PSKeyBoardEvent {
    private static boolean wasRidingPlug;

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Pre event) {
        var mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) {
            wasRidingPlug = false;
            return;
        }

        boolean isRidingPlug = mc.player.getVehicle() instanceof ToiletPlugEntity;

        if (isRidingPlug && !wasRidingPlug) {
            mc.gui.setOverlayMessage(Component.translatable(
                    "message.poopsky.toilet_plug.dismount",
                    PSKeyBoardInput.DISMOUNT_PLUG_KEY.getTranslatedKeyMessage()
            ), false);
        }
        wasRidingPlug = isRidingPlug;

        while (PSKeyBoardInput.USE_PLUG_KEY.consumeClick()) {
            PacketDistributor.sendToServer(new PlugActionPayload());
        }
        if (isRidingPlug && PSKeyBoardInput.DISMOUNT_PLUG_KEY.consumeClick()) {
            PacketDistributor.sendToServer(new PlugDismountPayload());
        }
    }
}
