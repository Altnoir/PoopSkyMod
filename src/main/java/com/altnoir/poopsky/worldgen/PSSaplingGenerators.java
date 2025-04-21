package com.altnoir.poopsky.worldgen;

import com.altnoir.poopsky.PoopSky;
import net.minecraft.block.SaplingGenerator;

import java.util.Optional;

public class PSSaplingGenerators {
    public static final SaplingGenerator POOP_TREE = new SaplingGenerator(PoopSky.MOD_ID + ":poop_tree",
            Optional.of(PSConfigureFeatures.MEGA_POOP_TREE), Optional.of(PSConfigureFeatures.POOP_TREE), Optional.empty());
}
