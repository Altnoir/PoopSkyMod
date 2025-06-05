package com.altnoir.poopsky.network;

import com.altnoir.poopsky.component.PPlug;
import com.altnoir.poopsky.component.PSComponents;
import com.altnoir.poopsky.entity.p.PlugEntity;
import com.altnoir.poopsky.item.PSItems;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class RidePacketHandler {
    public static void handleDataOnMain(final RidePacket data, final IPayloadContext context) {
        context.enqueueWork(() -> {
            var player = context.player();
            var plugEntity = (PlugEntity) player.level().getEntity(data.eid());

            if (plugEntity != null) {
                if (data.ride()){
                    player.startRiding(player);
                }
                else {
                    plugEntity.remove(Entity.RemovalReason.KILLED);
                    var itemStack = new ItemStack(PSItems.Toilet_Plug.get());
                    itemStack.set(PSComponents.PPLUG.get(), new PPlug(plugEntity.getControlMode()));
                    if (!player.isCreative()) {
                        if (!player.getInventory().add(itemStack)){
                            plugEntity.spawnAtLocation(plugEntity.getDropItem());
                        }
                    }
                }
            }
        });
    }
}
