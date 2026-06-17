package com.altnoir.poopsky.datagen;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.block.AllToiletBlocks;
import com.altnoir.poopsky.block.PBlocks;
import com.altnoir.poopsky.block.abs.AbstractToiletBlock;
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

    public PSBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, PoopSky.MOD_ID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        //block models
        models().cubeAll("poop_block1", modLoc("block/poop_block"));
        models().cubeAll("poop_block2", modLoc("block/poop_block_maggots")).texture(PARTICLE, modLoc("block/poop_block"));
        models().withExistingParent("poop_block3", mcLoc("block/block"))
                .texture("side", modLoc("block/poop_block"))
                .texture("up", modLoc("block/poop_block_liquids"))
                .texture(PARTICLE, modLoc("block/poop_block"))
                .element().from(0, 0, 0).to(16, 16, 16)
                .allFaces((face, faceBuilder) -> faceBuilder.texture("#side").uvs(0, 0, 16, 16))
                .face(Direction.UP).texture("#up").end();

        models().withExistingParent("poolime_poop_block", mcLoc("block/cube"))
                .texture("south", modLoc("block/poop_block"))
                .texture("west", modLoc("block/poop_block"))
                .texture("north", modLoc("block/poop_block"))
                .texture("east", modLoc("block/poop_block"))
                .texture("down", modLoc("block/poop_block"))
                .texture("up", modLoc("block/poolime_poop_block"))
                .texture(PARTICLE, modLoc("block/poop_block"));

        for (int layers = 1; layers < 8; layers++) {
            int height = layers * 2;
            int uvHeight = 16 - (layers * 2);
            String modelName = "poop_height" + height;

            models().withExistingParent(modelName, mcLoc("block/thin_block"))
                    .texture("texture", modLoc("block/poop_block"))
                    .texture(PARTICLE, modLoc("block/poop_block"))
                    .element().from(0, 0, 0).to(16, height, 16)
                    .allFaces((face, faceBuilder) -> {
                        faceBuilder.texture("#texture");
                        if (face != Direction.UP) faceBuilder.cullface(face);
                    });
        }

        //block states
        getVariantBuilder(PBlocks.POOP_BLOCK.get())
                .partialState().addModels(
                        new ConfiguredModel(models().getExistingFile(modLoc("block/poop_block1")), 0, 0, false, 9),
                        new ConfiguredModel(models().getExistingFile(modLoc("block/poop_block2")), 0, 0, false, 1),
                        new ConfiguredModel(models().getExistingFile(modLoc("block/poop_block3")), 0, 0, false, 2)
                );

        getVariantBuilder(PBlocks.POOP_PIECE.get())
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
        simpleBlockItem(PBlocks.POOP_BLOCK.get(), models().getExistingFile(modLoc("block/poop_block1")));
        simpleBlockItem(PBlocks.POOP_PIECE.get(), models().getExistingFile(modLoc("block/poop_height2")));
        //
        blockWithTranslucentRenderType(PBlocks.POOLIME_BLOCK.get());
        getVariantBuilder(PBlocks.POOLIME_POOP_BLOCK.get()).partialState().addModels(new ConfiguredModel(models().getExistingFile(modLoc("block/poolime_poop_block"))));
        simpleBlockItem(PBlocks.POOLIME_POOP_BLOCK.get(), models().getExistingFile(modLoc("block/poolime_poop_block")));

        blockWithItem(PBlocks.CHILI_POOP_BLOCK.get());
        stairsBlock((StairBlock) PBlocks.CHILI_POOP_STAIRS.get(), blockTexture(PBlocks.CHILI_POOP_BLOCK.get()));
        slabBlock((SlabBlock) PBlocks.CHILI_POOP_SLAB.get(), blockTexture(PBlocks.CHILI_POOP_BLOCK.get()), blockTexture(PBlocks.CHILI_POOP_BLOCK.get()));
        wallBlock((WallBlock) PBlocks.CHILI_POOP_WALL.get(), blockTexture(PBlocks.CHILI_POOP_BLOCK.get()));
        blockWithItem(PBlocks.GOLDEN_POOP_BLOCK.get());
        stairsBlock((StairBlock) PBlocks.GOLDEN_POOP_STAIRS.get(), blockTexture(PBlocks.GOLDEN_POOP_BLOCK.get()));
        slabBlock((SlabBlock) PBlocks.GOLDEN_POOP_SLAB.get(), blockTexture(PBlocks.GOLDEN_POOP_BLOCK.get()), blockTexture(PBlocks.GOLDEN_POOP_BLOCK.get()));
        wallBlock((WallBlock) PBlocks.GOLDEN_POOP_WALL.get(), blockTexture(PBlocks.GOLDEN_POOP_BLOCK.get()));

        blockWithItem(PBlocks.POOP_BRICKS.get());
        blockWithItem(PBlocks.CRACKED_POOP_BRICKS.get());
        stairsBlock((StairBlock) PBlocks.POOP_BRICK_STAIRS.get(), blockTexture(PBlocks.POOP_BRICKS.get()));
        slabBlock((SlabBlock) PBlocks.POOP_BRICK_SLAB.get(), blockTexture(PBlocks.POOP_BRICKS.get()), blockTexture(PBlocks.POOP_BRICKS.get()));
        wallBlock((WallBlock) PBlocks.POOP_BRICK_WALL.get(), blockTexture(PBlocks.POOP_BRICKS.get()));

        blockWithItem(PBlocks.MOSSY_POOP_BRICKS.get());
        stairsBlock((StairBlock) PBlocks.MOSSY_POOP_BRICK_STAIRS.get(), blockTexture(PBlocks.MOSSY_POOP_BRICKS.get()));
        slabBlock((SlabBlock) PBlocks.MOSSY_POOP_BRICK_SLAB.get(), blockTexture(PBlocks.MOSSY_POOP_BRICKS.get()), blockTexture(PBlocks.MOSSY_POOP_BRICKS.get()));
        wallBlock((WallBlock) PBlocks.MOSSY_POOP_BRICK_WALL.get(), blockTexture(PBlocks.MOSSY_POOP_BRICKS.get()));

        blockWithItem(PBlocks.DRIED_POOP_BLOCK.get());
        stairsBlock((StairBlock) PBlocks.DRIED_POOP_BLOCK_STAIRS.get(), blockTexture(PBlocks.DRIED_POOP_BLOCK.get()));
        slabBlock((SlabBlock) PBlocks.DRIED_POOP_BLOCK_SLAB.get(), blockTexture(PBlocks.DRIED_POOP_BLOCK.get()), blockTexture(PBlocks.DRIED_POOP_BLOCK.get()));
        wallBlock((WallBlock) PBlocks.DRIED_POOP_BLOCK_WALL.get(), blockTexture(PBlocks.DRIED_POOP_BLOCK.get()));

        blockWithItem(PBlocks.SMOOTH_POOP_BLOCK.get());
        stairsBlock((StairBlock) PBlocks.SMOOTH_POOP_BLOCK_STAIRS.get(), blockTexture(PBlocks.SMOOTH_POOP_BLOCK.get()));
        slabBlock((SlabBlock) PBlocks.SMOOTH_POOP_BLOCK_SLAB.get(), blockTexture(PBlocks.SMOOTH_POOP_BLOCK.get()), blockTexture(PBlocks.SMOOTH_POOP_BLOCK.get()));
        wallBlock((WallBlock) PBlocks.SMOOTH_POOP_BLOCK_WALL.get(), blockTexture(PBlocks.SMOOTH_POOP_BLOCK.get()));

        blockWithItem(PBlocks.CUT_POOP_BLOCK.get());
        stairsBlock((StairBlock) PBlocks.CUT_POOP_BLOCK_STAIRS.get(), blockTexture(PBlocks.CUT_POOP_BLOCK.get()));
        slabBlock((SlabBlock) PBlocks.CUT_POOP_BLOCK_SLAB.get(), blockTexture(PBlocks.CUT_POOP_BLOCK.get()), blockTexture(PBlocks.CUT_POOP_BLOCK.get()));
        wallBlock((WallBlock) PBlocks.CUT_POOP_BLOCK_WALL.get(), blockTexture(PBlocks.CUT_POOP_BLOCK.get()));

        blockWithItem(PBlocks.TILE_BLOCK.get());
        stairsBlock((StairBlock) PBlocks.TILE_BLOCK_STAIRS.get(), blockTexture(PBlocks.TILE_BLOCK.get()));
        slabBlock((SlabBlock) PBlocks.TILE_BLOCK_SLAB.get(), blockTexture(PBlocks.TILE_BLOCK.get()), blockTexture(PBlocks.TILE_BLOCK.get()));
        wallBlock((WallBlock) PBlocks.TILE_BLOCK_WALL.get(), blockTexture(PBlocks.TILE_BLOCK.get()));

        blockWithItem(PBlocks.RAW_POOP_BLOCK.get());
        blockWithItem(PBlocks.RAW_SAPLING_POOP_BLOCK.get());
        blockWithItem(PBlocks.RAW_SEA_POOP_BLOCK.get());
        blockWithItem(PBlocks.RAW_WITHER_POOP_BLOCK.get());

        blockItem(PBlocks.CHILI_POOP_STAIRS);
        blockItem(PBlocks.CHILI_POOP_SLAB);
        blockItem(PBlocks.GOLDEN_POOP_STAIRS);
        blockItem(PBlocks.GOLDEN_POOP_SLAB);
        blockItem(PBlocks.POOP_BRICK_STAIRS);
        blockItem(PBlocks.POOP_BRICK_SLAB);
        blockItem(PBlocks.MOSSY_POOP_BRICK_STAIRS);
        blockItem(PBlocks.MOSSY_POOP_BRICK_SLAB);
        blockItem(PBlocks.DRIED_POOP_BLOCK_STAIRS);
        blockItem(PBlocks.DRIED_POOP_BLOCK_SLAB);
        blockItem(PBlocks.SMOOTH_POOP_BLOCK_STAIRS);
        blockItem(PBlocks.SMOOTH_POOP_BLOCK_SLAB);
        blockItem(PBlocks.CUT_POOP_BLOCK_STAIRS);
        blockItem(PBlocks.CUT_POOP_BLOCK_SLAB);
        blockItem(PBlocks.TILE_BLOCK_STAIRS);
        blockItem(PBlocks.TILE_BLOCK_SLAB);

        blockWithItem(PBlocks.POOP_LEAVES.get());
        blockWithItem(PBlocks.POOP_LEAVES_GOLD.get());
        blockWithItem(PBlocks.POOP_LEAVES_IRON.get());
        cubeBottomTop(PBlocks.POOP_TNT.get());
        orientable(PBlocks.PLACER.get());

        registerToilet(AllToiletBlocks.OAK_TOILET.get(), Blocks.OAK_PLANKS);
        registerToilet(AllToiletBlocks.SPRUCE_TOILET.get(), Blocks.SPRUCE_PLANKS);
        registerToilet(AllToiletBlocks.BIRCH_TOILET.get(), Blocks.BIRCH_PLANKS);
        registerToilet(AllToiletBlocks.JUNGLE_TOILET.get(), Blocks.JUNGLE_PLANKS);
        registerToilet(AllToiletBlocks.ACACIA_TOILET.get(), Blocks.ACACIA_PLANKS);
        registerToilet(AllToiletBlocks.DARK_OAK_TOILET.get(), Blocks.DARK_OAK_PLANKS);
        registerToilet(AllToiletBlocks.MANGROVE_TOILET.get(), Blocks.MANGROVE_PLANKS);
        registerToilet(AllToiletBlocks.CHERRY_TOILET.get(), Blocks.CHERRY_PLANKS);
        registerToilet(AllToiletBlocks.BAMBOO_TOILET.get(), Blocks.BAMBOO_PLANKS);
        registerToilet(AllToiletBlocks.CRIMSON_TOILET.get(), Blocks.CRIMSON_PLANKS);
        registerToilet(AllToiletBlocks.WARPED_TOILET.get(), Blocks.WARPED_PLANKS);

        registerToiletLava(AllToiletBlocks.STONE_TOILET.get(), Blocks.STONE);
        registerToiletLava(AllToiletBlocks.COBBLESTONE_TOILET.get(), Blocks.COBBLESTONE);
        registerToiletLava(AllToiletBlocks.MOSSY_COBBLESTONE_TOILET.get(), Blocks.MOSSY_COBBLESTONE);
        registerToiletLava(AllToiletBlocks.SMOOTH_STONE_TOILET.get(), Blocks.SMOOTH_STONE);
        registerToiletLava(AllToiletBlocks.STONE_BRICK_TOILET.get(), Blocks.STONE_BRICKS);
        registerToiletLava(AllToiletBlocks.MOSSY_STONE_BRICK_TOILET.get(), Blocks.MOSSY_STONE_BRICKS);
        registerToiletLava(AllToiletBlocks.TILE_TOILET.get(), "tile_block");

        registerToiletLava(AllToiletBlocks.WHITE_CONCRETE_TOILET.get(), Blocks.WHITE_CONCRETE);
        registerToiletLava(AllToiletBlocks.LIGHT_GRAY_CONCRETE_TOILET.get(), Blocks.LIGHT_GRAY_CONCRETE);
        registerToiletLava(AllToiletBlocks.GRAY_CONCRETE_TOILET.get(), Blocks.GRAY_CONCRETE);
        registerToiletLava(AllToiletBlocks.BLACK_CONCRETE_TOILET.get(), Blocks.BLACK_CONCRETE);
        registerToiletLava(AllToiletBlocks.BROWN_CONCRETE_TOILET.get(), Blocks.BROWN_CONCRETE);
        registerToiletLava(AllToiletBlocks.RED_CONCRETE_TOILET.get(), Blocks.RED_CONCRETE);
        registerToiletLava(AllToiletBlocks.ORANGE_CONCRETE_TOILET.get(), Blocks.ORANGE_CONCRETE);
        registerToiletLava(AllToiletBlocks.YELLOW_CONCRETE_TOILET.get(), Blocks.YELLOW_CONCRETE);
        registerToiletLava(AllToiletBlocks.LIME_CONCRETE_TOILET.get(), Blocks.LIME_CONCRETE);
        registerToiletLava(AllToiletBlocks.GREEN_CONCRETE_TOILET.get(), Blocks.GREEN_CONCRETE);
        registerToiletLava(AllToiletBlocks.CYAN_CONCRETE_TOILET.get(), Blocks.CYAN_CONCRETE);
        registerToiletLava(AllToiletBlocks.LIGHT_BLUE_CONCRETE_TOILET.get(), Blocks.LIGHT_BLUE_CONCRETE);
        registerToiletLava(AllToiletBlocks.BLUE_CONCRETE_TOILET.get(), Blocks.BLUE_CONCRETE);
        registerToiletLava(AllToiletBlocks.PURPLE_CONCRETE_TOILET.get(), Blocks.PURPLE_CONCRETE);
        registerToiletLava(AllToiletBlocks.MAGENTA_CONCRETE_TOILET.get(), Blocks.MAGENTA_CONCRETE);
        registerToiletLava(AllToiletBlocks.PINK_CONCRETE_TOILET.get(), Blocks.PINK_CONCRETE);
        registerToiletLava(AllToiletBlocks.RAINBOW_TOILET.get(), "rainbow_concrete");

        fluidBlockWithItem(PBlocks.URINE_LIQUID.get());
        makeCropBlock((CropBlock) PBlocks.MAGGOTS.get(), "maggots_stage", "maggots_stage");
    }

    protected void makeCropBlock(CropBlock cropBlock, String model, String texture) {
        Function<BlockState, ConfiguredModel[]> function = (state -> states(state, cropBlock, model, texture));

        getVariantBuilder(cropBlock).forAllStates(function);
    }

    private ConfiguredModel[] states(BlockState state, CropBlock cropBlock, String model, String texture) {
        ConfiguredModel[] models = new ConfiguredModel[1];
        models[0] = new ConfiguredModel(models().crop(model + state.getValue(CropBlock.AGE),
                PoopSky.loc("block/" + texture + state.getValue(CropBlock.AGE))).renderType("cutout"));
        return models;
    }

    private void fluidBlockWithItem(Block block) {
        var blockModel = models()
                .withExistingParent(getBlockPath(block), mcLoc("block/block"))
                .texture("particle", modLoc(getBlockPath(block)))
                .texture("still", modLoc(getBlockPath(block)))
                .texture("flow", modLoc(getBlockPath(block)))
                .renderType("translucent");

        getVariantBuilder(block).partialState().addModels(new ConfiguredModel(blockModel));

        itemModels()
                .withExistingParent(getBlockPath(block), mcLoc("item/generated"))
                .texture("layer0", modLoc(getBlockPath(block)));
    }

    private void blockWithTranslucentRenderType(Block block) {
        var model = models().cubeAll(
                getBlockPath(block), modLoc("block/" + getBlockPath(block))
        ).renderType("translucent");

        getVariantBuilder(block).partialState().addModels(new ConfiguredModel(model));

        simpleBlockItem(block, model);
    }

    private void cubeBottomTop(Block block) {
        var model = models().withExistingParent(getBlockPath(block), mcLoc("block/cube_bottom_top"))
                .texture("top", modLoc("block/" + getBlockPath(block) + "_top"))
                .texture("side", modLoc("block/" + getBlockPath(block) + "_side"))
                .texture("bottom", modLoc("block/" + getBlockPath(block) + "_bottom"));

        getVariantBuilder(block).partialState().addModels(new ConfiguredModel(model));

        simpleBlockItem(block, model);
    }

    private void orientable(Block block) {
        var model = models().withExistingParent(getBlockPath(block), mcLoc("block/orientable"))
                .texture("top", modLoc("block/" + getBlockPath(block) + "_top"))
                .texture("side", modLoc("block/" + getBlockPath(block) + "_side"))
                .texture("front", modLoc("block/" + getBlockPath(block) + "_front"))
                .texture(PARTICLE, modLoc("block/" + getBlockPath(block) + "_side"));
        var modelV = models().withExistingParent(getBlockPath(block) + "_vertical", mcLoc("block/orientable_vertical"))
                .texture("side", modLoc("block/" + getBlockPath(block) + "_top"))
                .texture("front", modLoc("block/" + getBlockPath(block) + "_front_vertical"))
                .texture(PARTICLE, modLoc("block/" + getBlockPath(block) + "_side"));

        getVariantBuilder(block).forAllStates(state -> {
            var facing = state.getValue(BlockStateProperties.FACING);

            if (facing == Direction.UP || facing == Direction.DOWN) {
                int xRot = switch (facing) {
                    case DOWN -> 90;
                    default -> 0;
                };
                return ConfiguredModel.builder().modelFile(modelV).rotationX(xRot).build();
            }
            int yRot = switch (facing) {
                case EAST -> 90;
                case SOUTH -> 180;
                case WEST -> 270;
                default -> 0;
            };
            return ConfiguredModel.builder().modelFile(model).rotationY(yRot).build();
        });

        simpleBlockItem(block, model);
    }

    private void registerToilet(Block toilet, Block textureBlock) {
        var texture = getBlockKey(textureBlock);

        var textureRL = ResourceLocation.fromNamespaceAndPath(texture.getNamespace(), "block/" + texture.getPath());

        var baseModel = models().withExistingParent(getBlockKey(toilet).toString(),
                modLoc("block/toilet")).texture("toilet", textureRL);
        var modelN = models().withExistingParent(getBlockKey(toilet) + "_n",
                modLoc("block/toilet_n")).texture("toilet", textureRL);
        var modelS = models().withExistingParent(getBlockKey(toilet) + "_s",
                modLoc("block/toilet_s")).texture("toilet", textureRL);
        var modelNS = models().withExistingParent(getBlockKey(toilet) + "_ns",
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
        itemModels().getBuilder(getBlockPath(toilet))
                .parent(baseModel);
    }

    private void registerToiletLava(Block toilet, Object texture) {
        var texturePath = texture instanceof Block ?
                getBlockKey((Block) texture).getPath() : texture.toString();

        var baseTexture = texture instanceof Block ?
                mcLoc("block/" + texturePath) : modLoc("block/" + texturePath);

        // Base models
        var baseModel = models().withExistingParent(getBlockKey(toilet).toString(),
                modLoc("block/toilet")).texture("toilet", baseTexture);
        var modelN = models().withExistingParent(getBlockKey(toilet) + "_n",
                modLoc("block/toilet_n")).texture("toilet", baseTexture);
        var modelS = models().withExistingParent(getBlockKey(toilet) + "_s",
                modLoc("block/toilet_s")).texture("toilet", baseTexture);
        var modelNS = models().withExistingParent(getBlockKey(toilet) + "_ns",
                modLoc("block/toilet_ns")).texture("toilet", baseTexture);

        // Lava models
        var modelLava = models().withExistingParent(getBlockKey(toilet) + "_lava",
                modLoc("block/toilet_lava")).texture("toilet", baseTexture);
        var modelLavaN = models().withExistingParent(getBlockKey(toilet) + "_lava_n",
                modLoc("block/toilet_lava_n")).texture("toilet", baseTexture);
        var modelLavaS = models().withExistingParent(getBlockKey(toilet) + "_lava_s",
                modLoc("block/toilet_lava_s")).texture("toilet", baseTexture);
        var modelLavaNS = models().withExistingParent(getBlockKey(toilet) + "_lava_ns",
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

        itemModels().getBuilder(getBlockPath(toilet)).parent(baseModel);
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


    private String getBlockPath(Block block) {
        return getBlockKey(block).getPath();
    }

    private ResourceLocation getBlockKey(Block block) {
        return BuiltInRegistries.BLOCK.getKey(block);
    }
}