package com.altnoir.poopsky.game.danmaku;

import com.altnoir.poopsky.game.danmaku.movement.BossMovement;
import com.altnoir.poopsky.game.model.TouhouGameState;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.Random;

public final class Boss {
    private final BossScript script;
    private final BossModifiers modifiers;
    private boolean movementDecided;
    private boolean movementEnabled;

    public Boss(BossScript script, BossModifiers modifiers) {
        this.script = script;
        this.modifiers = modifiers;
    }

    public void tick(TouhouGameState state, Random random) {
        BossMovement movement = modifiers.movement();
        if (movement != null && state.getWave() + 1 >= modifiers.movementWave()) {
            if (modifiers.randomMovement() && !movementDecided) {
                movementEnabled = random.nextBoolean();
                movementDecided = true;
            }

            if (!modifiers.randomMovement() || movementEnabled) {
                movement.tick(state, random);
            }
        }

        script.tick(this, state, random);
    }

    public BossScript script() {
        return script;
    }

    public BossModifiers modifiers() {
        return modifiers;
    }

    public List<ItemStack> getLootDrops() {
        return script.getLootDrops();
    }
}
