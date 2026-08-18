package com.altnoir.poopsky.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public enum Button {
    UP, DOWN, LEFT, RIGHT, BUTTON1, BUTTON2;

    public static final StreamCodec<FriendlyByteBuf, Button> STREAM_CODEC =
            StreamCodec.of(
                    FriendlyByteBuf::writeEnum,
                    buffer -> buffer.readEnum(Button.class)
            );
}
