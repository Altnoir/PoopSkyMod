package com.altnoir.poopsky.keybinding;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.entity.PSEntities;
import com.altnoir.poopsky.entity.ToiletPlugEntity;
import com.altnoir.poopsky.item.PSItems;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

public class KeyInputHandler {
    private static final String CATEGORY = "key.poopsky.category";
    private static KeyBinding plugKey;
    private static final Identifier USE_PLUG_PACKET_ID = Identifier.of(PoopSky.MOD_ID, "use_plug");

    public static void register() {
        plugKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.poopsky.use_plug",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_V,
                CATEGORY
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (plugKey.wasPressed()) {
                client.player.sendMessage(Text.literal("Key 1 was pressed!"), false);
                //PacketByteBuf buf = PacketByteBufs.create();
                //ClientPlayNetworking.send(USE_PLUG_PACKET_ID, buf);
            }

        });
    }
    public static void registerServerReceiver() {
/*        ServerPlayNetworking.registerGlobalReceiver(USE_PLUG_PACKET_ID, (server, player, handler, buf, sender) -> {
            server.execute(() -> {
                if (player.getVehicle() instanceof ToiletPlugEntity plug) {
                    plug.kill();
                    if (!player.isCreative()) {
                        giveOrDropItem(player, PSItems.TOILET_PLUG.getDefaultStack());
                    }
                } else {
                    if (hasItemInInventory(player)) {
                        ToiletPlugEntity entity = PSEntities.TOILET_PLUG_ENTITY_TYPE.create(player.getWorld());
                        entity.setPosition(player.getPos());
                        player.getWorld().spawnEntity(entity);
                        player.startRiding(entity);

                        if (!player.isCreative()) {
                            removeOneItem(player);
                        }
                    }
                }
            });
        });*/
    }
    // 给予或掉落物品
    private static void giveOrDropItem(PlayerEntity player, ItemStack stack) {
        if (!player.getInventory().insertStack(stack)) {
            player.dropItem(stack, false);
        }
    }

    // 检查背包是否有马桶塞
    private static boolean hasItemInInventory(PlayerEntity player) {
        return player.getInventory().contains(PSItems.TOILET_PLUG.getDefaultStack());
    }

    // 移除一个马桶塞
    private static void removeOneItem(PlayerEntity player) {
        for(int i = 0; i < player.getInventory().size(); i++) {
            ItemStack stack = player.getInventory().getStack(i);
            if (stack.getItem() == PSItems.TOILET_PLUG) {
                stack.decrement(1);
                return;
            }
        }
    }
}
