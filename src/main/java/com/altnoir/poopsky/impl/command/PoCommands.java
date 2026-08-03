package com.altnoir.poopsky.impl.command;

import com.altnoir.poopsky.impl.network.PlayAnimationPayload;
import com.altnoir.poopsky.impl.network.PoAnimation;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public final class PoCommands {
    private PoCommands() {
    }

    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
                dispatcher.register(
                        Commands.literal("poopsky")
                                .then(Commands.literal("animation")
                                        .then(animation(PoAnimation.INTRO))
                                        .then(animation(PoAnimation.POEM)))
                ));
    }

    private static LiteralArgumentBuilder<CommandSourceStack> animation(PoAnimation animation) {
        return Commands.literal(animation.serializedName())
                .executes(context -> {
                    ServerPlayNetworking.send(
                            context.getSource().getPlayerOrException(),
                            new PlayAnimationPayload(animation)
                    );
                    return 1;
                });
    }
}
