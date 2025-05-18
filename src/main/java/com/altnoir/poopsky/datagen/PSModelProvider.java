package com.altnoir.poopsky.datagen;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.block.PSBlocks;
import com.altnoir.poopsky.block.ToiletBlocks;
import com.altnoir.poopsky.block.custom.ToiletBlock;
import com.altnoir.poopsky.block.custom.ToiletLavaBlock;
import com.altnoir.poopsky.item.PSItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.CropBlock;
import net.minecraft.data.client.*;
import net.minecraft.registry.Registries;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;

import java.util.Optional;

public class PSModelProvider extends FabricModelProvider {
    public static final TextureKey TOILET_TEXTURE = TextureKey.of("toilet");
    public PSModelProvider(FabricDataOutput output) {
        super(output);
    }

    public static final Model VERTICAL_SLAB = new Model(
            Optional.of(Identifier.of(PoopSky.MOD_ID, "block/vertical_slab")),
            Optional.empty(),
            TextureKey.TOP, TextureKey.SIDE
    );
    public static final Model TOILET = new Model(
            Optional.of(Identifier.of(PoopSky.MOD_ID, "block/toilet")),
            Optional.empty(),
            TOILET_TEXTURE
    );
    public static final Model TOILET_LAVA = new Model(
            Optional.of(Identifier.of(PoopSky.MOD_ID, "block/toilet_lava")),
            Optional.empty(),
            TOILET_TEXTURE
    );

    @Override
    public void generateBlockStateModels(BlockStateModelGenerator g) {
        g.registerSimpleCubeAll(PSBlocks.POOP_LEAVES);
        g.registerSimpleCubeAll(PSBlocks.POOP_LEAVES_IRON);
        g.registerSimpleCubeAll(PSBlocks.POOP_LEAVES_GOLD);
        g.registerSimpleCubeAll(PSBlocks.CRACKED_POOP_BRICKS);

        g.registerDoor(PSBlocks.POOP_DOOR);
        g.registerTrapdoor(PSBlocks.POOP_TRAPDOOR);

        registerToilet(g, ToiletBlocks.OAK_TOILET, Blocks.OAK_PLANKS);
        registerToilet(g, ToiletBlocks.SPRUCE_TOILET, Blocks.SPRUCE_PLANKS);
        registerToilet(g, ToiletBlocks.BIRCH_TOILET, Blocks.BIRCH_PLANKS);
        registerToilet(g, ToiletBlocks.JUNGLE_TOILET, Blocks.JUNGLE_PLANKS);
        registerToilet(g, ToiletBlocks.ACACIA_TOILET, Blocks.ACACIA_PLANKS);
        registerToilet(g, ToiletBlocks.DARK_OAK_TOILET, Blocks.DARK_OAK_PLANKS);
        registerToilet(g, ToiletBlocks.MANGROVE_TOILET, Blocks.MANGROVE_PLANKS);
        registerToilet(g, ToiletBlocks.CHERRY_TOILET, Blocks.CHERRY_PLANKS);
        registerToilet(g, ToiletBlocks.BAMBOO_TOILET, Blocks.BAMBOO_PLANKS);
        registerToilet(g, ToiletBlocks.CRIMSON_TOILET, Blocks.CRIMSON_PLANKS);
        registerToilet(g, ToiletBlocks.WARPED_TOILET, Blocks.WARPED_PLANKS);

        registerToiletLava(g, ToiletBlocks.STONE_TOILET, Blocks.STONE);
        registerToiletLava(g, ToiletBlocks.COBBLESTONE_TOILET, Blocks.COBBLESTONE);
        registerToiletLava(g, ToiletBlocks.MOSSY_COBBLESTONE_TOILET, Blocks.MOSSY_COBBLESTONE);
        registerToiletLava(g, ToiletBlocks.SMOOTH_STONE_TOILET, Blocks.SMOOTH_STONE);
        registerToiletLava(g, ToiletBlocks.STONE_BRICK_TOILET, Blocks.STONE_BRICKS);
        registerToiletLava(g, ToiletBlocks.MOSSY_STONE_BRICK_TOILET, Blocks.MOSSY_STONE_BRICKS);

        registerToiletLava(g, ToiletBlocks.WHITE_CONCRETE_TOILET, Blocks.WHITE_CONCRETE);
        registerToiletLava(g, ToiletBlocks.LIGHT_GRAY_CONCRETE_TOILET, Blocks.LIGHT_GRAY_CONCRETE);
        registerToiletLava(g, ToiletBlocks.GRAY_CONCRETE_TOILET, Blocks.GRAY_CONCRETE);
        registerToiletLava(g, ToiletBlocks.BLACK_CONCRETE_TOILET, Blocks.BLACK_CONCRETE);
        registerToiletLava(g, ToiletBlocks.BROWN_CONCRETE_TOILET, Blocks.BROWN_CONCRETE);
        registerToiletLava(g, ToiletBlocks.RED_CONCRETE_TOILET, Blocks.RED_CONCRETE);
        registerToiletLava(g, ToiletBlocks.ORANGE_CONCRETE_TOILET, Blocks.ORANGE_CONCRETE);
        registerToiletLava(g, ToiletBlocks.YELLOW_CONCRETE_TOILET, Blocks.YELLOW_CONCRETE);
        registerToiletLava(g, ToiletBlocks.LIME_CONCRETE_TOILET, Blocks.LIME_CONCRETE);
        registerToiletLava(g, ToiletBlocks.GREEN_CONCRETE_TOILET, Blocks.GREEN_CONCRETE);
        registerToiletLava(g, ToiletBlocks.CYAN_CONCRETE_TOILET, Blocks.CYAN_CONCRETE);
        registerToiletLava(g, ToiletBlocks.LIGHT_BLUE_CONCRETE_TOILET, Blocks.LIGHT_BLUE_CONCRETE);
        registerToiletLava(g, ToiletBlocks.BLUE_CONCRETE_TOILET, Blocks.BLUE_CONCRETE);
        registerToiletLava(g, ToiletBlocks.PURPLE_CONCRETE_TOILET, Blocks.PURPLE_CONCRETE);
        registerToiletLava(g, ToiletBlocks.MAGENTA_CONCRETE_TOILET, Blocks.MAGENTA_CONCRETE);
        registerToiletLava(g, ToiletBlocks.PINK_CONCRETE_TOILET, Blocks.PINK_CONCRETE);
        registerToiletLava(g, ToiletBlocks.RAINBOW_TOILET, "rainbow_concrete");

        BlockStateModelGenerator.BlockTexturePool poopBricksPool = g.registerCubeAllModelTexturePool(PSBlocks.POOP_BRICKS);
        poopBricksPool.stairs(PSBlocks.POOP_BRICK_STAIRS);
        poopBricksPool.slab(PSBlocks.POOP_BRICK_SLAB);
        registerVerticalSlab(g, PSBlocks.POOP_BRICK_VERTICAL_SLAB, PSBlocks.POOP_BRICKS);
        poopBricksPool.wall(PSBlocks.POOP_BRICK_WALL);

        BlockStateModelGenerator.BlockTexturePool mossyPoopBricksPool = g.registerCubeAllModelTexturePool(PSBlocks.MOSSY_POOP_BRICKS);
        mossyPoopBricksPool.stairs(PSBlocks.MOSSY_POOP_BRICK_STAIRS);
        mossyPoopBricksPool.slab(PSBlocks.MOSSY_POOP_BRICK_SLAB);
        registerVerticalSlab(g, PSBlocks.MOSSY_POOP_BRICK_VERTICAL_SLAB, PSBlocks.MOSSY_POOP_BRICKS);
        mossyPoopBricksPool.wall(PSBlocks.MOSSY_POOP_BRICK_WALL);

        BlockStateModelGenerator.BlockTexturePool driedPoopBlockPool = g.registerCubeAllModelTexturePool(PSBlocks.DRIED_POOP_BLOCK);
        driedPoopBlockPool.stairs(PSBlocks.DRIED_POOP_BLOCK_STAIRS);
        driedPoopBlockPool.slab(PSBlocks.DRIED_POOP_BLOCK_SLAB);
        registerVerticalSlab(g, PSBlocks.DRIED_POOP_BLOCK_VERTICAL_SLAB, PSBlocks.DRIED_POOP_BLOCK);
        driedPoopBlockPool.wall(PSBlocks.DRIED_POOP_BLOCK_WALL);

        BlockStateModelGenerator.BlockTexturePool smoothPoopBlockPool = g.registerCubeAllModelTexturePool(PSBlocks.SMOOTH_POOP_BLOCK);
        smoothPoopBlockPool.stairs(PSBlocks.SMOOTH_POOP_BLOCK_STAIRS);
        smoothPoopBlockPool.slab(PSBlocks.SMOOTH_POOP_BLOCK_SLAB);
        registerVerticalSlab(g, PSBlocks.SMOOTH_POOP_BLOCK_VERTICAL_SLAB, PSBlocks.SMOOTH_POOP_BLOCK);
        smoothPoopBlockPool.wall(PSBlocks.SMOOTH_POOP_BLOCK_WALL);

        BlockStateModelGenerator.BlockTexturePool cutPoopBlockPool = g.registerCubeAllModelTexturePool(PSBlocks.CUT_POOP_BLOCK);
        cutPoopBlockPool.stairs(PSBlocks.CUT_POOP_BLOCK_STAIRS);
        cutPoopBlockPool.slab(PSBlocks.CUT_POOP_BLOCK_SLAB);
        registerVerticalSlab(g, PSBlocks.CUT_POOP_BLOCK_VERTICAL_SLAB, PSBlocks.CUT_POOP_BLOCK);
        cutPoopBlockPool.wall(PSBlocks.CUT_POOP_BLOCK_WALL);

        g.registerCrop(PSBlocks.MAGGOTS, CropBlock.AGE, 0,1,2,3,4,5,6,7);
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
        itemModelGenerator.register(PSItems.BAKED_MAGGOTS, Models.GENERATED);
        itemModelGenerator.register(PSItems.POOP_BREAD, Models.GENERATED);
        itemModelGenerator.register(PSItems.POOP_SOUP, Models.GENERATED);
        itemModelGenerator.register(PSItems.DRAGON_BREATH_CHILI, Models.GENERATED);
    }



    private void registerVerticalSlab(BlockStateModelGenerator generator, Block verticalSlab, Block baseBlock) {
        String texturePath = Registries.BLOCK.getId(baseBlock).getPath();
        Identifier baseTexture = Identifier.of(PoopSky.MOD_ID, "block/" + texturePath);
        TextureMap textureMap = new TextureMap()
                .put(TextureKey.TOP, baseTexture)
                .put(TextureKey.SIDE, baseTexture);

        Identifier modelId = VERTICAL_SLAB.upload(verticalSlab, textureMap, generator.modelCollector);

        generator.blockStateCollector.accept(
                VariantsBlockStateSupplier.create(verticalSlab)
                        .coordinate(BlockStateVariantMap.create(Properties.HORIZONTAL_FACING)
                                .register(Direction.NORTH,
                                        BlockStateVariant.create()
                                                .put(VariantSettings.MODEL, modelId)
                                )
                                .register(Direction.EAST,
                                        BlockStateVariant.create()
                                                .put(VariantSettings.MODEL, modelId)
                                                .put(VariantSettings.Y, VariantSettings.Rotation.R90)
                                )
                                .register(Direction.SOUTH,
                                        BlockStateVariant.create()
                                                .put(VariantSettings.MODEL, modelId)
                                                .put(VariantSettings.Y, VariantSettings.Rotation.R180)
                                )
                                .register(Direction.WEST,
                                        BlockStateVariant.create()
                                                .put(VariantSettings.MODEL, modelId)
                                                .put(VariantSettings.Y, VariantSettings.Rotation.R270)
                                )
                        )
        );
        generator.registerParentedItemModel(verticalSlab, modelId);
    }

    private void registerToilet(BlockStateModelGenerator generator, Block toilet, Block texture) {
        String texturePath = Registries.BLOCK.getId(texture).getPath();
        Identifier baseTexture = Identifier.ofVanilla("block/" + texturePath);
        TextureMap textureMap = new TextureMap()
                .put(TOILET_TEXTURE, baseTexture);

        Identifier baseModelId = TOILET.upload(
            toilet, 
            textureMap, 
            generator.modelCollector
        );

        Identifier modelN = new Model(
            Optional.of(Identifier.of(PoopSky.MOD_ID, "block/toilet_n")),
            Optional.empty(),
            TOILET_TEXTURE
        ).upload(toilet, "_n", textureMap, generator.modelCollector);
        
        Identifier modelS = new Model(
            Optional.of(Identifier.of(PoopSky.MOD_ID, "block/toilet_s")),
            Optional.empty(),
            TOILET_TEXTURE
        ).upload(toilet, "_s", textureMap, generator.modelCollector);
        
        Identifier modelNS = new Model(
            Optional.of(Identifier.of(PoopSky.MOD_ID, "block/toilet_ns")),
            Optional.empty(),
            TOILET_TEXTURE
        ).upload(toilet, "_ns", textureMap, generator.modelCollector);

        MultipartBlockStateSupplier supplier = MultipartBlockStateSupplier.create(toilet);

        for (Direction direction : Properties.HORIZONTAL_FACING.getValues()) {
            supplier.with(
                When.create()
                        .set(Properties.HORIZONTAL_FACING, direction)
                        .set(ToiletBlock.FORWARD, false)
                        .set(ToiletBlock.BACKWARD, false),
                BlockStateVariant.create()
                        .put(VariantSettings.MODEL, baseModelId)
                        .put(VariantSettings.Y, getRotationForDirection(direction))
                        .put(VariantSettings.UVLOCK, true)
            );
            supplier.with(
                When.create()
                        .set(Properties.HORIZONTAL_FACING, direction)
                        .set(ToiletBlock.FORWARD, true)
                        .set(ToiletBlock.BACKWARD, false),
                BlockStateVariant.create()
                        .put(VariantSettings.MODEL, modelN)
                        .put(VariantSettings.Y, getRotationForDirection(direction))
                        .put(VariantSettings.UVLOCK, true)
            );
            supplier.with(
                When.create()
                        .set(Properties.HORIZONTAL_FACING, direction)
                        .set(ToiletBlock.FORWARD, false)
                        .set(ToiletBlock.BACKWARD, true),
                BlockStateVariant.create()
                        .put(VariantSettings.MODEL, modelS)
                        .put(VariantSettings.Y, getRotationForDirection(direction))
                        .put(VariantSettings.UVLOCK, true)
            );
            supplier.with(
                When.create()
                        .set(Properties.HORIZONTAL_FACING, direction)
                        .set(ToiletBlock.FORWARD, true)
                        .set(ToiletBlock.BACKWARD, true),
                BlockStateVariant.create()
                        .put(VariantSettings.MODEL, modelNS)
                        .put(VariantSettings.Y, getRotationForDirection(direction))
                        .put(VariantSettings.UVLOCK, true)
            );
        }

        generator.blockStateCollector.accept(supplier);
        generator.registerParentedItemModel(toilet, baseModelId);
    }
    private void registerToiletLava(BlockStateModelGenerator generator, Block toilet, Object texture) {
        String texturePath = texture instanceof Block ? Registries.BLOCK.getId((Block) texture).getPath() : texture.toString();
        String toiletPath = Registries.BLOCK.getId(toilet).getPath();
        
        Identifier baseTexture = (texture instanceof Block) ? 
            Identifier.ofVanilla("block/" + texturePath) : 
            Identifier.of(PoopSky.MOD_ID, "block/" + texturePath);

        TextureMap textureMap = new TextureMap()
                .put(TOILET_TEXTURE, baseTexture);

        Identifier baseModelId = Identifier.of(PoopSky.MOD_ID, "block/" + toiletPath);
        Identifier lavaModelId = Identifier.of(PoopSky.MOD_ID, "block/" + toiletPath + "_lava");

        Identifier modelId1 = TOILET.upload(baseModelId, textureMap, generator.modelCollector);
        Identifier modelId2 = TOILET_LAVA.upload(lavaModelId, textureMap, generator.modelCollector);

        MultipartBlockStateSupplier supplier = MultipartBlockStateSupplier.create(toilet);

        for (Direction direction : Properties.HORIZONTAL_FACING.getValues()) {
            VariantSettings.Rotation rotation = getRotationForDirection(direction);
            supplier.with(
                    When.create()
                            .set(Properties.HORIZONTAL_FACING, direction)
                            .set(ToiletLavaBlock.LAVA, false),
                    BlockStateVariant.create()
                            .put(VariantSettings.MODEL, modelId1)
                            .put(VariantSettings.Y, rotation)
                            .put(VariantSettings.UVLOCK, true)
            );
            supplier.with(
                    When.create()
                            .set(Properties.HORIZONTAL_FACING, direction)
                            .set(ToiletLavaBlock.LAVA, true),
                    BlockStateVariant.create()
                            .put(VariantSettings.MODEL, modelId2)
                            .put(VariantSettings.Y, rotation)
                            .put(VariantSettings.UVLOCK, true)
            );
        }

        generator.blockStateCollector.accept(supplier);
        generator.registerParentedItemModel(toilet, modelId1);
    }

    private static VariantSettings.Rotation getRotationForDirection(Direction direction) {
        return switch (direction) {
            case EAST -> VariantSettings.Rotation.R90;
            case SOUTH -> VariantSettings.Rotation.R180;
            case WEST -> VariantSettings.Rotation.R270;
            default -> VariantSettings.Rotation.R0;
        };
    }
}
