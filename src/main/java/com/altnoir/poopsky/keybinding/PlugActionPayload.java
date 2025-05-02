package com.altnoir.poopsky.keybinding;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.entity.PSEntities;
import com.altnoir.poopsky.entity.ToiletPlugEntity;
import com.altnoir.poopsky.item.PSItems;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

public record PlugActionPayload() implements CustomPayload {
    public static final CustomPayload.Id<PlugActionPayload> ID = new CustomPayload.Id<>(Identifier.of(PoopSky.MOD_ID, "use_plug"));
    public static final PacketCodec<RegistryByteBuf, PlugActionPayload> CODEC = PacketCodec.unit(new PlugActionPayload());

    private static final String CATEGORY = "key.poopsky.category";
    private static KeyBinding plugKey;

    public static void register() {
        plugKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.poopsky.use_plug",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_V,
                CATEGORY
        ));
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (plugKey.wasPressed()) {
                ClientPlayNetworking.send(new PlugActionPayload());
            }
        });
    }
    public static void registerServerReceiver() {
        PayloadTypeRegistry.playC2S().register(PlugActionPayload.ID, PlugActionPayload.CODEC);

        ServerPlayNetworking.registerGlobalReceiver(PlugActionPayload.ID, (payload, context) -> {
            context.player().server.execute(() -> {
                PlayerEntity player = context.player();
                if (player.getVehicle() instanceof ToiletPlugEntity plug) {
                    removePlug(player, plug);
                } else if (player.isCreative() || hasItemInInventory(player)) {
                    spawnAndRidePlug(player);
                    if (!player.isCreative()) removeItem(player);
                }
            });
        });
    }
    // 生成并骑乘
    private static void spawnAndRidePlug(PlayerEntity player) {
        ToiletPlugEntity entity = PSEntities.TOILET_PLUG_ENTITY.create(player.getWorld());
        entity.setPosition(player.getPos());
        entity.setYaw(player.getYaw());

        player.getWorld().spawnEntity(entity);
        player.startRiding(entity);

        player.getWorld().playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.ENTITY_ITEM_FRAME_REMOVE_ITEM, entity.getSoundCategory(), 0.5F, 1.0F);
    }
    // 移除坐骑
    private static void removePlug(PlayerEntity player, ToiletPlugEntity plug) {
        plug.kill();
        player.getWorld().playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENTITY_ITEM_PICKUP, player.getSoundCategory(), 0.5F, 1.0F);
        if (!player.isCreative()) {
            giveOrDropItem(player, PSItems.TOILET_PLUG.getDefaultStack());
        }
    }
    // 给予物品
    private static void giveOrDropItem(PlayerEntity player, ItemStack stack) {
        if (!player.getInventory().insertStack(stack)) {
            player.dropItem(stack, false);
        }
    }
    // 检查背包
    private static boolean hasItemInInventory(PlayerEntity player) {
        return player.getInventory().contains(PSItems.TOILET_PLUG.getDefaultStack());
    }
    // 移除物品
    private static void removeItem(PlayerEntity player) {
        for(int i = 0; i < player.getInventory().size(); i++) {
            ItemStack stack = player.getInventory().getStack(i);
            if (stack.getItem() == PSItems.TOILET_PLUG) {
                stack.decrement(1);
                return;
            }
        }
    }
    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
