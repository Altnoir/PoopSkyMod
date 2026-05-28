package com.altnoir.poopsky.datagen;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.block.AbstractToiletBlock;
import com.altnoir.poopsky.block.PSBlocks;
import com.altnoir.poopsky.block.ToiletBlocks;
import com.altnoir.poopsky.block.p.PoopPieceBlock;
import com.altnoir.poopsky.block.p.ToiletLavaBlock;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredBlock;

import java.util.function.Function;

public class PSBlockStateProvider extends BlockStateProvider {
    public static final String PARTICLE = "particle";
    public static final String TEXTURE = "texture";
    public static final String SIDE = "side";
    public static final String UP = "up";
    public static final String THE_TEXTURE = "#texture";
    public static final String THE_SIDE = "#side";
    public static final String THE_UP = "#up";

    public PSBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, PoopSky.MOD_ID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        //block models
        models().cubeAll("poop_block1", modLoc("block/poop_block"));
        models().cubeAll("poop_block2", modLoc("block/poop_block_maggots")).texture(PARTICLE, modLoc("block/poop_block"));
        models().withExistingParent("poop_block3", mcLoc("block/block"))
                .texture(SIDE, modLoc("block/poop_block"))
                .texture(UP, modLoc("block/poop_block_liquids"))
                .texture(PARTICLE, modLoc("block/poop_block"))
                .element().from(0, 0, 0).to(16, 16, 16)
                .allFaces((face, faceBuilder) -> faceBuilder.texture(THE_SIDE).uvs(0, 0, 16, 16))
                .face(Direction.UP).texture(THE_UP).end();

        for (int layers = 1; layers < 8; layers++) {
            int height = layers * 2;
            int uvHeight = 16 - (layers * 2);
            String modelName = "poop_height" + height;

            models().withExistingParent(modelName, mcLoc("block/thin_block"))
                    .texture(TEXTURE, modLoc("block/poop_block"))
                    .texture(PARTICLE, modLoc("block/poop_block"))
                    .element().from(0, 0, 0).to(16, height, 16)
                    .allFaces((face, faceBuilder) -> {
                        faceBuilder.texture(THE_TEXTURE);
                        if (face != Direction.UP) faceBuilder.cullface(face);
                    });
        }

        //block states
        getVariantBuilder(PSBlocks.POOP_BLOCK.get())
                .partialState().addModels(
                        new ConfiguredModel(models().getExistingFile(modLoc("block/poop_block1")), 0, 0, false, 9),
                        new ConfiguredModel(models().getExistingFile(modLoc("block/poop_block2")), 0, 0, false, 1),
                        new ConfiguredModel(models().getExistingFile(modLoc("block/poop_block3")), 0, 0, false, 2)
                );

        getVariantBuilder(PSBlocks.POOP_PIECE.get())
                .forAllStates(state -> {
                    int layers = state.getValue(PoopPieceBlock.LAYERS);
                    if (layers == 8) {
                        return ConfiguredModel.builder()
                                .modelFile(models().getExistingFile(modLoc("block/poop_block1")))
                                .build();
                    }
                    String modelName = "poop_height" + (layers * 2);
                    return ConfiguredModel.builder()
                            .modelFile(models().getExistingFile(modLoc("block/" + modelName)))
                            .build();
                });

        //item models
        simpleBlockItem(PSBlocks.POOP_BLOCK.get(), models().getExistingFile(modLoc("block/poop_block1")));
        simpleBlockItem(PSBlocks.POOP_PIECE.get(), models().getExistingFile(modLoc("block/poop_height2")));
        //
        blockWithTranslucentRenderType(PSBlocks.POOLIME_BLOCK.get());
        blockWithItem(PSBlocks.POOLIME_POOP_BLOCK.get());
        blockWithItem(PSBlocks.CHILI_POOP_BLOCK.get());
        stairsBlock((StairBlock) PSBlocks.CHILI_POOP_STAIRS.get(), blockTexture(PSBlocks.CHILI_POOP_BLOCK.get()));
        slabBlock((SlabBlock) PSBlocks.CHILI_POOP_SLAB.get(), blockTexture(PSBlocks.CHILI_POOP_BLOCK.get()), blockTexture(PSBlocks.CHILI_POOP_BLOCK.get()));
        wallBlock((WallBlock) PSBlocks.CHILI_POOP_WALL.get(), blockTexture(PSBlocks.CHILI_POOP_BLOCK.get()));

        blockWithItem(PSBlocks.POOP_BRICKS.get());
        blockWithItem(PSBlocks.CRACKED_POOP_BRICKS.get());
        stairsBlock((StairBlock) PSBlocks.POOP_BRICK_STAIRS.get(), blockTexture(PSBlocks.POOP_BRICKS.get()));
        slabBlock((SlabBlock) PSBlocks.POOP_BRICK_SLAB.get(), blockTexture(PSBlocks.POOP_BRICKS.get()), blockTexture(PSBlocks.POOP_BRICKS.get()));
        wallBlock((WallBlock) PSBlocks.POOP_BRICK_WALL.get(), blockTexture(PSBlocks.POOP_BRICKS.get()));

        blockWithItem(PSBlocks.MOSSY_POOP_BRICKS.get());
        stairsBlock((StairBlock) PSBlocks.MOSSY_POOP_BRICK_STAIRS.get(), blockTexture(PSBlocks.MOSSY_POOP_BRICKS.get()));
        slabBlock((SlabBlock) PSBlocks.MOSSY_POOP_BRICK_SLAB.get(), blockTexture(PSBlocks.MOSSY_POOP_BRICKS.get()), blockTexture(PSBlocks.MOSSY_POOP_BRICKS.get()));
        wallBlock((WallBlock) PSBlocks.MOSSY_POOP_BRICK_WALL.get(), blockTexture(PSBlocks.MOSSY_POOP_BRICKS.get()));

        blockWithItem(PSBlocks.DRIED_POOP_BLOCK.get());
        stairsBlock((StairBlock) PSBlocks.DRIED_POOP_BLOCK_STAIRS.get(), blockTexture(PSBlocks.DRIED_POOP_BLOCK.get()));
        slabBlock((SlabBlock) PSBlocks.DRIED_POOP_BLOCK_SLAB.get(), blockTexture(PSBlocks.DRIED_POOP_BLOCK.get()), blockTexture(PSBlocks.DRIED_POOP_BLOCK.get()));
        wallBlock((WallBlock) PSBlocks.DRIED_POOP_BLOCK_WALL.get(), blockTexture(PSBlocks.DRIED_POOP_BLOCK.get()));

        blockWithItem(PSBlocks.SMOOTH_POOP_BLOCK.get());
        stairsBlock((StairBlock) PSBlocks.SMOOTH_POOP_BLOCK_STAIRS.get(), blockTexture(PSBlocks.SMOOTH_POOP_BLOCK.get()));
        slabBlock((SlabBlock) PSBlocks.SMOOTH_POOP_BLOCK_SLAB.get(), blockTexture(PSBlocks.SMOOTH_POOP_BLOCK.get()), blockTexture(PSBlocks.SMOOTH_POOP_BLOCK.get()));
        wallBlock((WallBlock) PSBlocks.SMOOTH_POOP_BLOCK_WALL.get(), blockTexture(PSBlocks.SMOOTH_POOP_BLOCK.get()));

        blockWithItem(PSBlocks.CUT_POOP_BLOCK.get());
        stairsBlock((StairBlock) PSBlocks.CUT_POOP_BLOCK_STAIRS.get(), blockTexture(PSBlocks.CUT_POOP_BLOCK.get()));
        slabBlock((SlabBlock) PSBlocks.CUT_POOP_BLOCK_SLAB.get(), blockTexture(PSBlocks.CUT_POOP_BLOCK.get()), blockTexture(PSBlocks.CUT_POOP_BLOCK.get()));
        wallBlock((WallBlock) PSBlocks.CUT_POOP_BLOCK_WALL.get(), blockTexture(PSBlocks.CUT_POOP_BLOCK.get()));

        blockWithItem(PSBlocks.TILE_BLOCK.get());
        stairsBlock((StairBlock) PSBlocks.TILE_BLOCK_STAIRS.get(), blockTexture(PSBlocks.TILE_BLOCK.get()));
        slabBlock((SlabBlock) PSBlocks.TILE_BLOCK_SLAB.get(), blockTexture(PSBlocks.TILE_BLOCK.get()), blockTexture(PSBlocks.TILE_BLOCK.get()));
        wallBlock((WallBlock) PSBlocks.TILE_BLOCK_WALL.get(), blockTexture(PSBlocks.TILE_BLOCK.get()));

        blockItem(PSBlocks.CHILI_POOP_STAIRS);
        blockItem(PSBlocks.CHILI_POOP_SLAB);
        blockItem(PSBlocks.POOP_BRICK_STAIRS);
        blockItem(PSBlocks.POOP_BRICK_SLAB);
        blockItem(PSBlocks.MOSSY_POOP_BRICK_STAIRS);
        blockItem(PSBlocks.MOSSY_POOP_BRICK_SLAB);
        blockItem(PSBlocks.DRIED_POOP_BLOCK_STAIRS);
        blockItem(PSBlocks.DRIED_POOP_BLOCK_SLAB);
        blockItem(PSBlocks.SMOOTH_POOP_BLOCK_STAIRS);
        blockItem(PSBlocks.SMOOTH_POOP_BLOCK_SLAB);
        blockItem(PSBlocks.CUT_POOP_BLOCK_STAIRS);
        blockItem(PSBlocks.CUT_POOP_BLOCK_SLAB);
        blockItem(PSBlocks.TILE_BLOCK_STAIRS);
        blockItem(PSBlocks.TILE_BLOCK_SLAB);

        blockWithItem(PSBlocks.POOP_LEAVES.get());
        blockWithItem(PSBlocks.POOP_LEAVES_GOLD.get());
        blockWithItem(PSBlocks.POOP_LEAVES_IRON.get());

        registerToilet(ToiletBlocks.OAK_TOILET.get(), Blocks.OAK_PLANKS);
        registerToilet(ToiletBlocks.SPRUCE_TOILET.get(), Blocks.SPRUCE_PLANKS);
        registerToilet(ToiletBlocks.BIRCH_TOILET.get(), Blocks.BIRCH_PLANKS);
        registerToilet(ToiletBlocks.JUNGLE_TOILET.get(), Blocks.JUNGLE_PLANKS);
        registerToilet(ToiletBlocks.ACACIA_TOILET.get(), Blocks.ACACIA_PLANKS);
        registerToilet(ToiletBlocks.DARK_OAK_TOILET.get(), Blocks.DARK_OAK_PLANKS);
        registerToilet(ToiletBlocks.MANGROVE_TOILET.get(), Blocks.MANGROVE_PLANKS);
        registerToilet(ToiletBlocks.CHERRY_TOILET.get(), Blocks.CHERRY_PLANKS);
        registerToilet(ToiletBlocks.BAMBOO_TOILET.get(), Blocks.BAMBOO_PLANKS);
        registerToilet(ToiletBlocks.CRIMSON_TOILET.get(), Blocks.CRIMSON_PLANKS);
        registerToilet(ToiletBlocks.WARPED_TOILET.get(), Blocks.WARPED_PLANKS);

        registerToiletLava(ToiletBlocks.STONE_TOILET.get(), Blocks.STONE);
        registerToiletLava(ToiletBlocks.COBBLESTONE_TOILET.get(), Blocks.COBBLESTONE);
        registerToiletLava(ToiletBlocks.MOSSY_COBBLESTONE_TOILET.get(), Blocks.MOSSY_COBBLESTONE);
        registerToiletLava(ToiletBlocks.SMOOTH_STONE_TOILET.get(), Blocks.SMOOTH_STONE);
        registerToiletLava(ToiletBlocks.STONE_BRICK_TOILET.get(), Blocks.STONE_BRICKS);
        registerToiletLava(ToiletBlocks.MOSSY_STONE_BRICK_TOILET.get(), Blocks.MOSSY_STONE_BRICKS);
        registerToiletLava(ToiletBlocks.TILE_TOILET.get(), "tile_block");

        registerToiletLava(ToiletBlocks.WHITE_CONCRETE_TOILET.get(), Blocks.WHITE_CONCRETE);
        registerToiletLava(ToiletBlocks.LIGHT_GRAY_CONCRETE_TOILET.get(), Blocks.LIGHT_GRAY_CONCRETE);
        registerToiletLava(ToiletBlocks.GRAY_CONCRETE_TOILET.get(), Blocks.GRAY_CONCRETE);
        registerToiletLava(ToiletBlocks.BLACK_CONCRETE_TOILET.get(), Blocks.BLACK_CONCRETE);
        registerToiletLava(ToiletBlocks.BROWN_CONCRETE_TOILET.get(), Blocks.BROWN_CONCRETE);
        registerToiletLava(ToiletBlocks.RED_CONCRETE_TOILET.get(), Blocks.RED_CONCRETE);
        registerToiletLava(ToiletBlocks.ORANGE_CONCRETE_TOILET.get(), Blocks.ORANGE_CONCRETE);
        registerToiletLava(ToiletBlocks.YELLOW_CONCRETE_TOILET.get(), Blocks.YELLOW_CONCRETE);
        registerToiletLava(ToiletBlocks.LIME_CONCRETE_TOILET.get(), Blocks.LIME_CONCRETE);
        registerToiletLava(ToiletBlocks.GREEN_CONCRETE_TOILET.get(), Blocks.GREEN_CONCRETE);
        registerToiletLava(ToiletBlocks.CYAN_CONCRETE_TOILET.get(), Blocks.CYAN_CONCRETE);
        registerToiletLava(ToiletBlocks.LIGHT_BLUE_CONCRETE_TOILET.get(), Blocks.LIGHT_BLUE_CONCRETE);
        registerToiletLava(ToiletBlocks.BLUE_CONCRETE_TOILET.get(), Blocks.BLUE_CONCRETE);
        registerToiletLava(ToiletBlocks.PURPLE_CONCRETE_TOILET.get(), Blocks.PURPLE_CONCRETE);
        registerToiletLava(ToiletBlocks.MAGENTA_CONCRETE_TOILET.get(), Blocks.MAGENTA_CONCRETE);
        registerToiletLava(ToiletBlocks.PINK_CONCRETE_TOILET.get(), Blocks.PINK_CONCRETE);
        registerToiletLava(ToiletBlocks.RAINBOW_TOILET.get(), "rainbow_concrete");

        fluidBlockWithItem(PSBlocks.POOP_LIQUID.get(), "block/poop_liquid");
        makeCropBlock((CropBlock) PSBlocks.MAGGOTS.get(), "maggots_stage", "maggots_stage");
    }

    protected void makeCropBlock(CropBlock cropBlock, String model, String texture) {
        Function<BlockState, ConfiguredModel[]> function = (state -> states(state, cropBlock, model, texture));

        getVariantBuilder(cropBlock).forAllStates(function);
    }

    private ConfiguredModel[] states(BlockState state, CropBlock cropBlock, String model, String texture) {
        ConfiguredModel[] models = new ConfiguredModel[1];
        models[0] = new ConfiguredModel(models().crop(model + state.getValue(CropBlock.AGE),
                ResourceLocation.fromNamespaceAndPath(PoopSky.MOD_ID, "block/" + texture + state.getValue(CropBlock.AGE))).renderType("cutout"));
        return models;
    }

    private void fluidBlockWithItem(Block block, String texture) {
        var blockModel = models()
                .withExistingParent(BuiltInRegistries.BLOCK.getKey(block).getPath(), mcLoc("block/block"))
                .texture("particle", modLoc(texture))
                .texture("still", modLoc(texture))
                .texture("flow", modLoc(texture))
                .renderType("translucent");

        getVariantBuilder(block).partialState().addModels(new ConfiguredModel(blockModel));

        itemModels()
                .withExistingParent(BuiltInRegistries.BLOCK.getKey(block).getPath(), mcLoc("item/generated"))
                .texture("layer0", modLoc(texture));
    }

    private void blockWithTranslucentRenderType(Block block) {
        var model = models().cubeAll(
                BuiltInRegistries.BLOCK.getKey(block).getPath(), modLoc("block/" + BuiltInRegistries.BLOCK.getKey(block).getPath())
        ).renderType("translucent");

        getVariantBuilder(block).partialState().addModels(new ConfiguredModel(model));

        simpleBlockItem(block, model);
    }

    private void registerToilet(Block toilet, Block textureBlock) {
        var texture = BuiltInRegistries.BLOCK.getKey(textureBlock);

        var textureRL = ResourceLocation.fromNamespaceAndPath(texture.getNamespace(), "block/" + texture.getPath());

        var baseModel = models().withExistingParent(
                BuiltInRegistries.BLOCK.getKey(toilet).toString(),
                modLoc("block/toilet")).texture("toilet", textureRL);
        var modelN = models().withExistingParent(
                BuiltInRegistries.BLOCK.getKey(toilet) + "_n",
                modLoc("block/toilet_n")).texture("toilet", textureRL);
        var modelS = models().withExistingParent(
                BuiltInRegistries.BLOCK.getKey(toilet) + "_s",
                modLoc("block/toilet_s")).texture("toilet", textureRL);
        var modelNS = models().withExistingParent(
                BuiltInRegistries.BLOCK.getKey(toilet) + "_ns",
                modLoc("block/toilet_ns")).texture("toilet", textureRL);


        getVariantBuilder(toilet).forAllStates(state -> {
            var facing = state.getValue(BlockStateProperties.HORIZONTAL_FACING);
            var connection = state.getValue(AbstractToiletBlock.CONNECTION);
            var yRot = switch (facing) {
                case EAST -> 90;
                case SOUTH -> 180;
                case WEST -> 270;
                default -> 0;
            };

            ModelFile chosenModel = switch (connection) {
                case DEFAULT -> baseModel;
                case FRONT -> modelN;
                case BACK -> modelS;
                case BOTH -> modelNS;
            };

            return ConfiguredModel.builder()
                    .modelFile(chosenModel)
                    .rotationY(yRot)
                    .uvLock(true)
                    .build();
        });
        itemModels().getBuilder(BuiltInRegistries.BLOCK.getKey(toilet).getPath())
                .parent(baseModel);
    }

    private void registerToiletLava(Block toilet, Object texture) {
        var texturePath = texture instanceof Block ?
                BuiltInRegistries.BLOCK.getKey((Block) texture).getPath() : texture.toString();

        var baseTexture = texture instanceof Block ?
                mcLoc("block/" + texturePath) : modLoc("block/" + texturePath);

        // Base models
        var baseModel = models().withExistingParent(
                BuiltInRegistries.BLOCK.getKey(toilet).toString(),
                modLoc("block/toilet")).texture("toilet", baseTexture);
        var modelN = models().withExistingParent(
                BuiltInRegistries.BLOCK.getKey(toilet) + "_n",
                modLoc("block/toilet_n")).texture("toilet", baseTexture);
        var modelS = models().withExistingParent(
                BuiltInRegistries.BLOCK.getKey(toilet) + "_s",
                modLoc("block/toilet_s")).texture("toilet", baseTexture);
        var modelNS = models().withExistingParent(
                BuiltInRegistries.BLOCK.getKey(toilet) + "_ns",
                modLoc("block/toilet_ns")).texture("toilet", baseTexture);

        // Lava models
        var modelLava = models().withExistingParent(
                BuiltInRegistries.BLOCK.getKey(toilet) + "_lava",
                modLoc("block/toilet_lava")).texture("toilet", baseTexture);
        var modelLavaN = models().withExistingParent(
                BuiltInRegistries.BLOCK.getKey(toilet) + "_lava_n",
                modLoc("block/toilet_lava_n")).texture("toilet", baseTexture);
        var modelLavaS = models().withExistingParent(
                BuiltInRegistries.BLOCK.getKey(toilet) + "_lava_s",
                modLoc("block/toilet_lava_s")).texture("toilet", baseTexture);
        var modelLavaNS = models().withExistingParent(
                BuiltInRegistries.BLOCK.getKey(toilet) + "_lava_ns",
                modLoc("block/toilet_lava_ns")).texture("toilet", baseTexture);

        getVariantBuilder(toilet).forAllStates(state -> {
            var lava = state.getValue(ToiletLavaBlock.LAVA);
            var facing = state.getValue(BlockStateProperties.HORIZONTAL_FACING);
            var connection = state.getValue(AbstractToiletBlock.CONNECTION);

            var yRot = switch (facing) {
                case EAST -> 90;
                case SOUTH -> 180;
                case WEST -> 270;
                default -> 0;
            };

            ModelFile chosenModel;
            if (!lava) {
                chosenModel = switch (connection) {
                    case DEFAULT -> baseModel;
                    case FRONT -> modelN;
                    case BACK -> modelS;
                    case BOTH -> modelNS;
                };
            } else {
                chosenModel = switch (connection) {
                    case DEFAULT -> modelLava;
                    case FRONT -> modelLavaN;
                    case BACK -> modelLavaS;
                    case BOTH -> modelLavaNS;
                };
            }

            return ConfiguredModel.builder()
                    .modelFile(chosenModel)
                    .rotationY(yRot)
                    .uvLock(true)
                    .build();
        });

        itemModels().getBuilder(BuiltInRegistries.BLOCK.getKey(toilet).getPath())
                .parent(baseModel);
    }

    private void blockWithItem(Block block) {
        simpleBlockWithItem(block, cubeAll(block));
    }

    private void blockItem(DeferredBlock<?> block) {
        simpleBlockItem(block.get(), new ModelFile.UncheckedModelFile(PoopSky.MOD_ID + ":block/" + block.getId().getPath()));
    }

    private void blockItem(DeferredBlock<?> block, String path) {
        simpleBlockItem(block.get(), new ModelFile.UncheckedModelFile(PoopSky.MOD_ID + ":block/" + block.getId().getPath() + path));
    }

}