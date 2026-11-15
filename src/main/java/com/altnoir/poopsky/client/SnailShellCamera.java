package com.altnoir.poopsky.client;

import com.altnoir.poopsky.content.item.p.SnailShellItem;
import com.altnoir.poopsky.init.PoKeyBoardInput;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.neoforged.neoforge.client.event.ViewportEvent;

public final class SnailShellCamera {
    private static boolean observing;
    private static boolean wasWearing;
    private static CameraType previousCamera;
    private static float yaw;
    private static float pitch;

    private SnailShellCamera() {
    }

    private static boolean isWearing() {
        Minecraft mc = Minecraft.getInstance();
        return mc.player != null && mc.level != null
                && mc.player.getItemBySlot(EquipmentSlot.CHEST).getItem() instanceof SnailShellItem;
    }

    private static boolean canObserve() {
        Minecraft mc = Minecraft.getInstance();
        return isWearing() && mc.screen == null && mc.isWindowActive()
                && mc.getCameraEntity() == mc.player && PoKeyBoardInput.OBSERVE_SHELL_KEY.isDown();
    }

    private static boolean updateObservation() {
        Minecraft mc = Minecraft.getInstance();
        if (!canObserve()) {
            if (observing) {
                mc.options.setCameraType(previousCamera);
                observing = false;
            }
            return false;
        }
        if (!observing) {
            previousCamera = mc.options.getCameraType();
            mc.options.setCameraType(CameraType.THIRD_PERSON_BACK);
            yaw = mc.player.getYRot();
            pitch = mc.player.getXRot();
            observing = true;
        }
        return true;
    }

    public static boolean turn(double dx, double dy) {
        if (!updateObservation()) return false;
        yaw += (float) (dx * 0.15D);
        pitch = Mth.clamp(pitch + (float) (dy * 0.15D), -90.0F, 90.0F);
        return true;
    }

    public static void tick() {
        updateObservation();
        Minecraft mc = Minecraft.getInstance();
        boolean wearing = isWearing();
        if (wearing && !wasWearing) {
            mc.player.displayClientMessage(Component.translatable("message.poopsky.snail_shell.observe",
                    PoKeyBoardInput.getLocalizedKeyMessage(PoKeyBoardInput.OBSERVE_SHELL_KEY)), true);
        }
        wasWearing = wearing;
    }

    public static void onCameraAngles(ViewportEvent.ComputeCameraAngles event) {
        if (updateObservation()) {
            event.setYaw(yaw);
            event.setPitch(pitch);
        }
    }
}
