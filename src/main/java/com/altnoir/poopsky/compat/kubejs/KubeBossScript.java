package com.altnoir.poopsky.compat.kubejs;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.game.danmaku.Boss;
import com.altnoir.poopsky.game.danmaku.BossModifiers;
import com.altnoir.poopsky.game.danmaku.BossScript;
import com.altnoir.poopsky.game.model.TouhouGameState;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.Random;

public final class KubeBossScript implements BossScript {
    private final CustomBossDefinition definition;
    private int age;
    private float rotation;
    private boolean disabled;

    public KubeBossScript(CustomBossDefinition definition) {
        this.definition = definition;
    }

    @Override
    public BossModifiers createModifiers(Random random, int wave) {
        BossModifiers modifiers = definition.modifiers().roll(random);
        rotation = modifiers.rotation();
        return modifiers;
    }

    @Override
    public void tick(Boss boss, TouhouGameState state, Random random) {
        if (disabled) {
            return;
        }

        CustomBossTickContext context = new CustomBossTickContext(state, boss.modifiers(), age, rotation);
        try {
            if (definition.rawTick() != null) {
                definition.rawTick().tick(boss, state, random);
            } else {
                definition.tick().accept(context);
            }
            rotation = context.getRotation();
            age++;
        } catch (Exception exception) {
            disabled = true;
            PoopSky.LOGGER.error("Disabled KubeJS custom boss '{}' after a tick error", definition.id(), exception);
        }
    }

    @Override
    public List<ItemStack> getLootDrops() {
        return definition.loot().stream()
                .map(ItemStack::copy)
                .toList();
    }
}
