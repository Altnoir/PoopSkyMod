package com.altnoir.poopsky.game.danmaku;

import com.altnoir.poopsky.game.model.TouhouGameState;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.Random;

public interface BossScript {
    BossModifiers createModifiers(Random random, int wave);

    void tick(Boss boss, TouhouGameState state, Random random);

    default List<ItemStack> getLootDrops() {
        return List.of();
    }
}
