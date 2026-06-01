package com.altnoir.poopsky.network;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.entity.PSEntityType;
import com.altnoir.poopsky.entity.p.ToiletPlugEntity;
import com.altnoir.poopsky.item.PSItems;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record PlugActionPayload() implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<PlugActionPayload> TYPE = new CustomPacketPayload.Type<>(PoopSky.loc("use_plug"));
    public static final StreamCodec<FriendlyByteBuf, PlugActionPayload> CODEC = StreamCodec.unit(new PlugActionPayload());

    public static void handle(final PlugActionPayload payload, final IPayloadContext context) {
        context.enqueueWork(() -> {
            ServerPlayer player = (ServerPlayer) context.player();

            if (player.getVehicle() instanceof ToiletPlugEntity plug) {
                removePlug(player, plug);
            } else {
                ItemStack plugStack = findToiletPlug(player);
                if (player.isCreative() || !plugStack.isEmpty()) {
                    spawnAndRidePlug(player);
                    if (!player.isCreative()) {
                        plugStack.shrink(1);
                    }
                }
            }
        });
    }

    private static void spawnAndRidePlug(ServerPlayer player) {
        var level = player.level();
        var entity = PSEntityType.TOILET_PLUG.get().create(level);
        if (entity == null) return;

        entity.setPos(player.position());
        entity.setYRot(player.getYRot());
        level.addFreshEntity(entity);
        player.startRiding(entity);

        level.playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.ITEM_FRAME_REMOVE_ITEM, SoundSource.PLAYERS, 0.5F, 1.0F);
    }

    private static void removePlug(ServerPlayer player, ToiletPlugEntity plug) {
        plug.kill();
        Level level = player.level();
        level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 0.5F, 1.0F);
        if (!player.isCreative()) {
            giveOrDropItem(player, new ItemStack(PSItems.TOILET_PLUG.get()));
        }
    }

    private static void giveOrDropItem(ServerPlayer player, ItemStack stack) {
        if (!player.getInventory().add(stack)) {
            player.drop(stack, false);
        }
    }

    private static ItemStack findToiletPlug(ServerPlayer player) {
        Inventory inv = player.getInventory();

        for (ItemStack stack : inv.items) {
            if (stack.is(PSItems.TOILET_PLUG.get())) {
                return stack;
            }
        }

        for (ItemStack stack : inv.offhand) {
            if (stack.is(PSItems.TOILET_PLUG.get())) {
                return stack;
            }
        }

        return ItemStack.EMPTY;
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

