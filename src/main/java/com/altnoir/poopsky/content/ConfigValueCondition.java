package com.altnoir.poopsky.content;

import com.altnoir.poopsky.Config;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public record ConfigValueCondition(String key, boolean value) implements LootItemCondition {
    public static final MapCodec<ConfigValueCondition> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.STRING.fieldOf("key").forGetter(ConfigValueCondition::key),
            Codec.BOOL.optionalFieldOf("value", true).forGetter(ConfigValueCondition::value)
    ).apply(instance, ConfigValueCondition::new));

    @Override
    public boolean test(LootContext context) {
        return switch (key) {
            case "plug_trades" -> Config.plugTrades == value;
            case "upgrade_template" -> Config.upgradeTemplate == value;
            default -> false;
        };
    }

    @Override
    public MapCodec<ConfigValueCondition> codec() {
        return MAP_CODEC;
    }
}
