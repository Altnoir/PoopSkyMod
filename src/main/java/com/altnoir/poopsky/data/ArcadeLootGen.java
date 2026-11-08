package com.altnoir.poopsky.data;

import com.altnoir.poopsky.PoopSky;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;

/** 生成后的街机奖池位于 data/poopsky/loot_tables/gameplay/arcade。 */
public final class ArcadeLootGen {
    private ArcadeLootGen() {}
    public static ResourceLocation lootTableKey(Block block) {
        return PoopSky.loc("gameplay/arcade/" + PoopSky.getBlockPath(block));
    }
}
