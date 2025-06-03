package com.altnoir.poopsky.event;

import com.altnoir.poopsky.item.PSItems;
import com.altnoir.poopsky.network.Networking;
import com.altnoir.poopsky.network.RidePack;
import com.altnoir.poopsky.network.SummonBroomPack;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.settings.KeyConflictContext;
import net.neoforged.neoforge.client.settings.KeyModifier;
import org.lwjgl.glfw.GLFW;

@EventBusSubscriber(value = Dist.CLIENT)
public class KeyBoardInput {
    public static final KeyMapping UP_KEY = new KeyMapping("key.up",
            KeyConflictContext.IN_GAME,
            KeyModifier.NONE,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_SPACE,
            "key.category.majobroom");
    public static final KeyMapping DOWN_KEY = new KeyMapping("key.down",
            KeyConflictContext.IN_GAME,
            KeyModifier.NONE,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_LEFT_CONTROL,
            "key.category.majobroom");
    public static final KeyMapping SUMMON_KEY = new KeyMapping("key.summon",
            KeyConflictContext.IN_GAME,
            KeyModifier.NONE,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_R,
            "key.category.majobroom");

    public static boolean up = false;
    public static boolean down = false;
    @SubscribeEvent
    public static void onKeyboardInput(InputEvent.Key event) {
        if (UP_KEY.getKey().getValue() == event.getKey()) {
            if (event.getAction() == GLFW.GLFW_PRESS){
                up = true;
            }else if (event.getAction() == GLFW.GLFW_RELEASE){
                up = false;
            }
        }
        if (DOWN_KEY.getKey().getValue() == event.getKey()) {
            if (event.getAction() == GLFW.GLFW_PRESS){
                down = true;
            }else if (event.getAction() == GLFW.GLFW_RELEASE){
                down = false;
            }
        }
        if (SUMMON_KEY.isDown() && event.getAction() == GLFW.GLFW_PRESS){

            Player playerEntity = Minecraft.getInstance().player;
            if (playerEntity!=null){
                if (playerEntity.isPassenger()){
                    Networking.INSTANCE.sendToServer(new RidePack(playerEntity.getVehicle().getId(),false));
                }else {
                    Networking.INSTANCE.sendToServer(new SummonBroomPack());
                }
                for (ItemStack item:playerEntity.getInventory().items) {
                    if (item.is(PSItems.Toilet_Plug.get()) || playerEntity.isCreative()){
                        playerEntity.level().playSound(playerEntity,playerEntity.blockPosition(), SoundEvents.ENDER_EYE_LAUNCH, SoundSource.NEUTRAL, 10F,1f);
                        //BroomItem.addParticle(playerEntity.level(),playerEntity.getX(), playerEntity.getY(), playerEntity.getZ(), 30,2,1);
                        break;
                    }
                }
            }
        }
    }
}
