package com.altnoir.poopsky.impl.command;

import com.altnoir.poopsky.impl.network.PlayAnimationPayload;
import com.altnoir.poopsky.impl.network.PoAnimation;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraftforge.event.RegisterCommandsEvent;
import com.altnoir.poopsky.impl.network.PoNetworking;

public final class PoCommands {
    private PoCommands() {
    }

    public static void register(RegisterCommandsEvent event) {
        event.getDispatcher().register(
                Commands.literal("poopsky")
                        .then(Commands.literal("animation")
                                .then(animation(PoAnimation.INTRO))
                                .then(animation(PoAnimation.POEM)))
        );
    }

    private static LiteralArgumentBuilder<CommandSourceStack> animation(PoAnimation animation) {
        return Commands.literal(animation.serializedName())
                .executes(context -> {
                    PoNetworking.sendToPlayer(
                            context.getSource().getPlayerOrException(),
                            new PlayAnimationPayload(animation)
                    );
                    return 1;
                });
    }
}
