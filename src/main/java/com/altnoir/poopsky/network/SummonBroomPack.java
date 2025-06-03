package com.altnoir.poopsky.network;

import com.altnoir.poopsky.entity.PSEntities;
import com.altnoir.poopsky.entity.p.PlugEntity;
import com.altnoir.poopsky.item.PSItems;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class SummonBroomPack {
    public SummonBroomPack(FriendlyByteBuf packetBuffer){


    }
    public SummonBroomPack(){
    }

    public void toBytes(FriendlyByteBuf buf) {

    }
    public void handler(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {

            Player playerEntity = ctx.get().getSender();
            if (playerEntity != null ){
                if (!playerEntity.isPassenger()){
                    for (ItemStack item:playerEntity.getInventory().items) {
                        if (item.is(PSItems.Toilet_Plug.get()) || playerEntity.isCreative()){
                            summonBroom(playerEntity,item);
                            break;
                        }
                    }
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
    private void summonBroom(Player playerEntity,ItemStack itemStack){
        PlugEntity plug = new PlugEntity(PSEntities.PLUG.get(), playerEntity.level());
//        plug.setYHeadRot(playerEntity.getYHeadRot());
        plug.setYRot(playerEntity.getYRot());
        plug.setPos(playerEntity.getX(), playerEntity.getY(), playerEntity.getZ());
        plug.setControlMode(itemStack.getOrCreateTag().getBoolean("controlMode"));
        playerEntity.level().addFreshEntity(plug);
        if (!playerEntity.isCreative()) {
            itemStack.shrink(1);
        }
        playerEntity.startRiding(plug);

    }
}
