package com.altnoir.poopsky.network;

import net.minecraft.resources.ResourceLocation;

public class Networking {
    public static SimpleChannel INSTANCE;
    public static final String VERSION = "1.0";
    private static int ID = 0;

    public static int nextID() {
        return ID++;
    }

    public static void registerMessage() {
        INSTANCE = NetworkRegistry.newSimpleChannel(
                ResourceLocation.fromNamespaceAndPath("majobroom", "first_networking"),
                () -> VERSION,
                (version) -> version.equals(VERSION),
                (version) -> version.equals(VERSION)
        );

        INSTANCE.registerMessage(
                nextID(),
                RidePack.class,
                RidePack::toBytes,
                RidePack::new,
                RidePack::handler
        );
        INSTANCE.registerMessage(
                nextID(),
                SummonBroomPack.class,
                SummonBroomPack::toBytes,
                SummonBroomPack::new,
                SummonBroomPack::handler
        );

    }
}
