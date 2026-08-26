package com.altnoir.poopsky.compat.kubejs;

import com.altnoir.poopsky.game.danmaku.modifier.BossModifierTemplate;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.function.Consumer;

public record CustomBossDefinition(
        String id,
        int weight,
        int minWave,
        List<ItemStack> loot,
        BossModifierTemplate modifiers,
        Consumer<CustomBossTickContext> tick,
        RawCustomBossTickHandler rawTick
) {
    public CustomBossDefinition {
        loot = loot.stream()
                .map(ItemStack::copy)
                .toList();
    }
}
