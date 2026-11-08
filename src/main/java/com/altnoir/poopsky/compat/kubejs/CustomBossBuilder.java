package com.altnoir.poopsky.compat.kubejs;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.game.danmaku.CircularRotation;
import com.altnoir.poopsky.game.danmaku.modifier.BossModifierTemplate;
import com.altnoir.poopsky.game.danmaku.modifier.FloatProvider;
import com.altnoir.poopsky.game.danmaku.modifier.IntProvider;
import com.altnoir.poopsky.game.danmaku.modifier.UniformFloat;
import com.altnoir.poopsky.game.danmaku.modifier.UniformInt;
import com.altnoir.poopsky.game.danmaku.movement.BossMovement;
import com.altnoir.poopsky.game.danmaku.movement.LeftRightBossMovement;
import com.altnoir.poopsky.game.danmaku.movement.OrbitBossMovement;
import com.altnoir.poopsky.game.danmaku.movement.RandomBossMovement;
import com.altnoir.poopsky.game.model.TouhouGameState;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.function.Consumer;

public final class CustomBossBuilder {
    private static final int MAX_WEIGHT = 1000;
    private static final Consumer<CustomBossTickContext> NO_OP_TICK = context -> {
    };

    private final String id;
    private IntProvider baseHp = random -> TouhouGameState.START_BOSS_HP;
    private IntProvider bulletCount = random -> 20;
    private IntProvider maxBounces = random -> 0;
    private IntProvider attackInterval = random -> 8;
    private FloatProvider bulletSpeed = random -> 2.0F;
    private IntProvider rotation = random -> 0;
    private IntProvider angleStep = random -> 5;
    private CircularRotation circularRotation;
    private BossMovement movement;
    private int movementWave = 1;
    private boolean randomMovement;
    private boolean rotationSet;
    private int weight = 1;
    private int minWave = 1;
    private final List<ItemStack> loot = new ArrayList<>();
    private Consumer<CustomBossTickContext> tick = NO_OP_TICK;
    private RawCustomBossTickHandler rawTick;

    CustomBossBuilder(String id) {
        this.id = id;
    }

    public CustomBossBuilder baseHp(int baseHp) {
        return baseHp(baseHp, baseHp);
    }

    public CustomBossBuilder baseHp(int min, int max) {
        this.baseHp = positiveRange("baseHp", min, max);
        return this;
    }

    public CustomBossBuilder bulletCount(int bulletCount) {
        return bulletCount(bulletCount, bulletCount);
    }

    public CustomBossBuilder bulletCount(int min, int max) {
        this.bulletCount = nonNegativeRange("bulletCount", min, max);
        return this;
    }

    public CustomBossBuilder maxBounces(int maxBounces) {
        return maxBounces(maxBounces, maxBounces);
    }

    public CustomBossBuilder maxBounces(int min, int max) {
        this.maxBounces = nonNegativeRange("maxBounces", min, max);
        return this;
    }

    public CustomBossBuilder attackInterval(int attackInterval) {
        return attackInterval(attackInterval, attackInterval);
    }

    public CustomBossBuilder attackInterval(int min, int max) {
        this.attackInterval = nonNegativeRange("attackInterval", min, max);
        return this;
    }

    public CustomBossBuilder bulletSpeed(float bulletSpeed) {
        return bulletSpeed(bulletSpeed, bulletSpeed);
    }

    public CustomBossBuilder bulletSpeed(float min, float max) {
        this.bulletSpeed = nonNegativeRange("bulletSpeed", min, max);
        return this;
    }

    public CustomBossBuilder rotation(int rotation) {
        return rotation(rotation, rotation);
    }

    public CustomBossBuilder rotation(int min, int max) {
        this.rotation = nonNegativeRange("rotation", min, max);
        this.rotationSet = true;
        return this;
    }

    public CustomBossBuilder angleStep(int angleStep) {
        return angleStep(angleStep, angleStep);
    }

    public CustomBossBuilder angleStep(int min, int max) {
        this.angleStep = nonNegativeRange("angleStep", min, max);
        return this;
    }

    public CustomBossBuilder circularRotation() {
        return circularRotation(0, 0);
    }

    public CustomBossBuilder circularRotation(int startDelay, int duration) {
        if (startDelay < 0 || duration < 0) {
            throw new IllegalArgumentException("circularRotation values must be non-negative");
        }
        this.circularRotation = new CircularRotation(startDelay, duration);
        return this;
    }

    public CustomBossBuilder movement(String type, float amplitude, float speed) {
        if (amplitude < 0.0F || speed < 0.0F) {
            throw new IllegalArgumentException("movement amplitude and speed must be non-negative");
        }
        this.movement = switch (type.toLowerCase(Locale.ROOT)) {
            case "left_right" -> new LeftRightBossMovement(amplitude, speed);
            case "orbit" -> new OrbitBossMovement(amplitude, speed);
            case "random" -> new RandomBossMovement(amplitude, speed);
            default -> throw new IllegalArgumentException("Unknown custom boss movement: " + type);
        };
        return this;
    }

    public CustomBossBuilder movementWave(int movementWave) {
        return movementWave(movementWave, false);
    }

    public CustomBossBuilder movementWave(int movementWave, boolean randomMovement) {
        if (movementWave < 1) {
            throw new IllegalArgumentException("movementWave must be at least 1");
        }
        this.movementWave = movementWave;
        this.randomMovement = randomMovement;
        return this;
    }

    public CustomBossBuilder weight(int weight) {
        if (weight < 1 || weight > MAX_WEIGHT) {
            throw new IllegalArgumentException("weight must be between 1 and " + MAX_WEIGHT);
        }
        this.weight = weight;
        return this;
    }

    public CustomBossBuilder minWave(int minWave) {
        if (minWave < 1) {
            throw new IllegalArgumentException("minWave must be at least 1");
        }
        this.minWave = minWave;
        return this;
    }

    public CustomBossBuilder loot(String itemId) {
        ResourceLocation item = PoopSky.tryParse(itemId);
        if (item == null || !BuiltInRegistries.ITEM.containsKey(item)) {
            throw new IllegalArgumentException("Invalid item id: " + itemId);
        }

        ItemStack stack = new ItemStack(BuiltInRegistries.ITEM.get(item));
        if (stack.isEmpty()) {
            throw new IllegalArgumentException("Boss loot cannot be empty: " + itemId);
        }
        loot.add(stack);
        return this;
    }

    public CustomBossBuilder tick(Consumer<CustomBossTickContext> tick) {
        if (rawTick != null) {
            throw new IllegalStateException("tick and rawTick cannot both be configured");
        }
        this.tick = Objects.requireNonNull(tick, "tick");
        return this;
    }

    public CustomBossBuilder rawTick(RawCustomBossTickHandler rawTick) {
        if (tick != null && tick != NO_OP_TICK) {
            throw new IllegalStateException("tick and rawTick cannot both be configured");
        }
        this.rawTick = Objects.requireNonNull(rawTick, "rawTick");
        return this;
    }

    CustomBossDefinition build() {
        BossModifierTemplate.Builder modifiers = BossModifierTemplate.builder()
                .baseHp(baseHp)
                .bulletCount(bulletCount)
                .maxBounces(maxBounces)
                .attackInterval(attackInterval)
                .bulletSpeed(bulletSpeed)
                .angleStep(angleStep);

        if (rotationSet) {
            modifiers.rotation(rotation);
        }
        if (circularRotation != null) {
            modifiers.circularRotation(circularRotation.startDelay(), circularRotation.duration());
        }
        if (movement != null) {
            modifiers.movement(movement)
                    .movementWave(movementWave, randomMovement);
        }

        return new CustomBossDefinition(id, weight, minWave, loot, modifiers.build(), tick, rawTick);
    }

    private static IntProvider positiveRange(String name, int min, int max) {
        if (min < 1 || max < 1) {
            throw new IllegalArgumentException(name + " must be at least 1");
        }
        return UniformInt.of(min, max);
    }

    private static IntProvider nonNegativeRange(String name, int min, int max) {
        if (min < 0 || max < 0) {
            throw new IllegalArgumentException(name + " must be non-negative");
        }
        return UniformInt.of(min, max);
    }

    private static FloatProvider nonNegativeRange(String name, float min, float max) {
        if (min < 0.0F || max < 0.0F) {
            throw new IllegalArgumentException(name + " must be non-negative");
        }
        return UniformFloat.of(min, max);
    }
}
