package com.altnoir.poopsky.network;

import com.altnoir.poopsky.entity.p.PlugEntity;
import com.altnoir.poopsky.item.PSItems;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.function.Supplier;

public class RidePack {
    private final int eid;
    private final boolean ride;
    public RidePack(FriendlyByteBuf packetBuffer){
        ride = packetBuffer.readBoolean();
        eid = packetBuffer.readInt();

    }
    public RidePack(int eid, boolean ride){
        this.eid = eid;
        this.ride = ride;
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeBoolean(this.ride);
        buf.writeInt(this.eid);
    }
    public void handler(Supplier<CustomPacketPayload> ctx) {
        ctx.get().enqueueWork(() -> {

            Player playerEntity = ctx.get().getSender();
            if (playerEntity != null){

                PlugEntity plugEntity = (PlugEntity) playerEntity.level().getEntity(this.eid);
                if(plugEntity != null) {
                    if (ride) {
                        playerEntity.startRiding(plugEntity);

                    } else {
                        plugEntity.remove(Entity.RemovalReason.KILLED);
                        ItemStack itemStack = new ItemStack(PSItems.Toilet_Plug.get());
                        itemStack.getOrCreateTag().putBoolean("controlMode",plugEntity.getControlMode());
                        if (!playerEntity.isCreative()) {
                            if (!playerEntity.getInventory().add(itemStack)){
                                plugEntity.spawnAtLocation(plugEntity.getDropItem());
                            }
                        }
                    }
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
