package com.altnoir.poopsky.worldgen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

import java.util.Optional;

public record PoHugeFungusConfiguration(
        TagKey<Block> validBaseTag,
        BlockState stemState,
        BlockState hatState,
        Optional<BlockState> decorState,
        BlockPredicate replaceableBlocks
) implements FeatureConfiguration {
    public static final Codec<PoHugeFungusConfiguration> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            TagKey.hashedCodec(Registries.BLOCK).fieldOf("valid_base_block").forGetter(PoHugeFungusConfiguration::validBaseTag),
            BlockState.CODEC.fieldOf("stem_state").forGetter(PoHugeFungusConfiguration::stemState),
            BlockState.CODEC.fieldOf("hat_state").forGetter(PoHugeFungusConfiguration::hatState),
            BlockState.CODEC.optionalFieldOf("decor_state").forGetter(PoHugeFungusConfiguration::decorState),
            BlockPredicate.CODEC.fieldOf("replaceable_blocks").forGetter(PoHugeFungusConfiguration::replaceableBlocks)
    ).apply(instance, PoHugeFungusConfiguration::new));
}