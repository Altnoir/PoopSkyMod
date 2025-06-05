package com.altnoir.poopsky.network;

import com.altnoir.poopsky.component.PPlug;
import com.altnoir.poopsky.component.PSComponents;
import com.altnoir.poopsky.entity.PSEntities;
import com.altnoir.poopsky.entity.p.PlugEntity;
import com.altnoir.poopsky.item.PSItems;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class SummonBroomPacketHandler {
    public static void handleDataOnMain(final SummonBroomPacket data, final IPayloadContext context) {
        context.enqueueWork(() -> {
            var player = context.player();

            if (!player.isPassenger()){
                for (ItemStack item:player.getInventory().items) {
                    if (item.is(PSItems.Toilet_Plug.get()) || player.isCreative()){
                        summonBroom(player, item);
                        break;
                    }
                }
            }
        });
    }

    private static void summonBroom(Player playerEntity, ItemStack itemStack){
        var plug = new PlugEntity(PSEntities.PLUG.get(), playerEntity.level());
//        plug.setYHeadRot(playerEntity.getYHeadRot());
        plug.setYRot(playerEntity.getYRot());
        plug.setPos(playerEntity.getX(), playerEntity.getY(), playerEntity.getZ());
        var pPlug = itemStack.get(PSComponents.PPLUG);

        if (pPlug == null)
            return;

        plug.setControlMode(pPlug.controlMode());
        playerEntity.level().addFreshEntity(plug);
        if (!playerEntity.isCreative()) {
            itemStack.shrink(1);
        }
        playerEntity.startRiding(plug);

    }
}
