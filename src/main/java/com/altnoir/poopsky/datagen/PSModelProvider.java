package com.altnoir.poopsky.datagen;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.block.PSBlocks;
import com.altnoir.poopsky.block.VerticalSlabBlock;
import com.altnoir.poopsky.item.PSItems;
import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.CropBlock;
import net.minecraft.block.StairsBlock;
import net.minecraft.block.enums.SlabType;
import net.minecraft.data.client.*;
import net.minecraft.data.family.BlockFamily;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class PSModelProvider extends FabricModelProvider {
    public PSModelProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {
        blockStateModelGenerator.registerSimpleCubeAll(PSBlocks.POOP_LEAVES);
        blockStateModelGenerator.registerSimpleCubeAll(PSBlocks.CRACKED_POOP_BRICKS);

        blockStateModelGenerator.registerDoor(PSBlocks.POOP_DOOR);
        blockStateModelGenerator.registerTrapdoor(PSBlocks.POOP_TRAPDOOR);

        BlockStateModelGenerator.BlockTexturePool poopBricksPool = blockStateModelGenerator.registerCubeAllModelTexturePool(PSBlocks.POOP_BRICKS);
        poopBricksPool.stairs(PSBlocks.POOP_BRICK_STAIRS);
        poopBricksPool.slab(PSBlocks.POOP_BRICK_SLAB);
        //PSBlocks.POOP_BRICK_VERTICAL_SLAB
        poopBricksPool.wall(PSBlocks.POOP_BRICK_WALL);

        BlockStateModelGenerator.BlockTexturePool mossyPoopBricksPool = blockStateModelGenerator.registerCubeAllModelTexturePool(PSBlocks.MOSSY_POOP_BRICKS);
        mossyPoopBricksPool.stairs(PSBlocks.MOSSY_POOP_BRICK_STAIRS);
        mossyPoopBricksPool.slab(PSBlocks.MOSSY_POOP_BRICK_SLAB);
        //PSBlocks.MOSSY_POOP_BRICK_VERTICAL_SLAB
        mossyPoopBricksPool.wall(PSBlocks.MOSSY_POOP_BRICK_WALL);

        BlockStateModelGenerator.BlockTexturePool driedPoopBlockPool = blockStateModelGenerator.registerCubeAllModelTexturePool(PSBlocks.DRIED_POOP_BLOCK);
        driedPoopBlockPool.stairs(PSBlocks.DRIED_POOP_BLOCK_STAIRS);
        driedPoopBlockPool.slab(PSBlocks.DRIED_POOP_BLOCK_SLAB);
        //PSBlocks.DRIED_POOP_BLOCK_VERTICAL_SLAB
        driedPoopBlockPool.wall(PSBlocks.DRIED_POOP_BLOCK_WALL);

        BlockStateModelGenerator.BlockTexturePool smoothPoopBlockPool = blockStateModelGenerator.registerCubeAllModelTexturePool(PSBlocks.SMOOTH_POOP_BLOCK);
        smoothPoopBlockPool.stairs(PSBlocks.SMOOTH_POOP_BLOCK_STAIRS);
        smoothPoopBlockPool.slab(PSBlocks.SMOOTH_POOP_BLOCK_SLAB);
        //PSBlocks.SMOOTH_POOP_BLOCK_VERTICAL_SLAB
        smoothPoopBlockPool.wall(PSBlocks.SMOOTH_POOP_BLOCK_WALL);

        BlockStateModelGenerator.BlockTexturePool cutPoopBlockPool = blockStateModelGenerator.registerCubeAllModelTexturePool(PSBlocks.CUT_POOP_BLOCK);
        cutPoopBlockPool.stairs(PSBlocks.CUT_POOP_BLOCK_STAIRS);
        cutPoopBlockPool.slab(PSBlocks.CUT_POOP_BLOCK_SLAB);
        //PSBlocks.CUT_POOP_BLOCK_VERTICAL_SLAB
        cutPoopBlockPool.wall(PSBlocks.CUT_POOP_BLOCK_WALL);
    }

    @Override
    public void generateItemModels(ItemModelGenerator itemModelGenerator) {
        itemModelGenerator.register(PSItems.POOP, Models.GENERATED);
        itemModelGenerator.register(PSItems.POOP_BALL, Models.GENERATED);
        itemModelGenerator.register(PSItems.SPALL, Models.GENERATED);
        itemModelGenerator.register(PSItems.TOILET_LINKER, Models.GENERATED);
        itemModelGenerator.register(PSItems.TOILET_PLUG, Models.GENERATED);
        itemModelGenerator.register(PSItems.LAWRENCE_MUSIC_DISC, Models.GENERATED);
        itemModelGenerator.register(PSItems.URINE_BOTTLE, Models.GENERATED);
        itemModelGenerator.register(PSItems.MAGGOTS_SEEDS, Models.GENERATED);
    }


}
