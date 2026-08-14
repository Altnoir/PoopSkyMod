package com.altnoir.poopsky.impl.network;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.game.controls.Button;
import com.altnoir.poopsky.content.block.entity.ArcadeBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record ArcadeInputPacket(BlockPos machinePos, Button button, boolean pressed) implements CustomPacketPayload {
    public static final Type<ArcadeInputPacket> TYPE = new Type<>(PoopSky.loc("arcade_input"));

    public static final StreamCodec<FriendlyByteBuf, ArcadeInputPacket> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, ArcadeInputPacket::machinePos,
            ByteBufCodecs.STRING_UTF8.map(Button::valueOf, Button::name), ArcadeInputPacket::button,
            ByteBufCodecs.BOOL, ArcadeInputPacket::pressed,
            ArcadeInputPacket::new
    );

    public static void handle(ArcadeInputPacket payload, @NotNull IPayloadContext context) {
        context.enqueueWork(() -> {
            ServerPlayer player = (ServerPlayer) context.player();
            Level level = player.level();
            if (!level.isLoaded(payload.machinePos())) {
                return;
            }

            double distance = player.position().distanceToSqr(Vec3.atCenterOf(payload.machinePos()));
            if (distance > 8.0D * 8.0D) {
                return;
            }

            if (level.getBlockEntity(payload.machinePos()) instanceof ArcadeBlockEntity arcade) {
                arcade.handleInput(player, payload.button(), payload.pressed());
            }
        });
    }

    @Override
    public @NotNull Type<ArcadeInputPacket> type() {
        return TYPE;
    }
}
