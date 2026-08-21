package com.altnoir.poopsky.content.block;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.worldgen.PoConfigureFeatures;
import net.minecraft.world.level.block.grower.TreeGrower;

import java.util.Optional;

public class PoTreeGrower {
    public static final TreeGrower GINKGO = new TreeGrower(PoopSky.MOD_ID + ":ginkgo",
            Optional.of(PoConfigureFeatures.MEGA_GINKGO_TREE),
            Optional.of(PoConfigureFeatures.GINKGO_TREE),
            Optional.of(PoConfigureFeatures.GINKGO_BEE_TREE));

    public static final TreeGrower PRIMO = new TreeGrower(PoopSky.MOD_ID + ":primo",
            Optional.empty(),
            Optional.of(PoConfigureFeatures.PRIMO_FUNGUS),
            Optional.empty());

    public static final TreeGrower GLOW_PRIMO = new TreeGrower(PoopSky.MOD_ID + ":glow_primo",
            Optional.empty(),
            Optional.of(PoConfigureFeatures.GLOW_PRIMO_FUNGUS),
            Optional.empty());
}