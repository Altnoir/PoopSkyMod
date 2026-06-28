package com.altnoir.poopsky.datagen;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.block.AllToiletBlocks;
import com.altnoir.poopsky.block.abs.AbstractToiletBlock;
import com.altnoir.poopsky.block.p.*;
import com.altnoir.poopsky.init.PBlocks;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredBlock;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class PSBlockStateProvider extends BlockStateProvider {
    public static final String PARTICLE = "particle";

    public PSBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, PoopSky.MOD_ID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        //
        poopBlock();
        poopPiece();
        poolimePoopBlock();
        blockWithTranslucentRenderType(PBlocks.POOLIME_BLOCK.get());
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

        randomBlockWithItem(PBlocks.RAW_POOP_BLOCK.get(), 3, 1);
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
        cubeBottomTop(PBlocks.BREEDING_BOX.get(), PBlocks.CUT_POOP_BLOCK.get());
        orientable(PBlocks.PLACER.get());

        registerWoodToilet();
        registerStoneToilet();
        registerMetalToilet();
        registerToiletLava(AllToiletBlocks.RAINBOW_TOILET.get(), "rainbow_concrete");

        var flyNestModel = models().singleTexture("fly_nest", mcLoc("block/cube_all"), mcLoc("block/beehive_side"));
        getVariantBuilder(PBlocks.FLY_NEST.get()).partialState().addModels(
                new ConfiguredModel(flyNestModel));
        simpleBlockItem(PBlocks.FLY_NEST.get(), flyNestModel);

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

    private void poopBlock() {
        models().cubeAll("poop_block1", modLoc("block/poop_block"));
        models().cubeAll("poop_block2", modLoc("block/poop_block_maggots")).texture(PARTICLE, modLoc("block/poop_block"));
        models().withExistingParent("poop_block3", mcLoc("block/block"))
                .texture("side", modLoc("block/poop_block"))
                .texture("up", modLoc("block/poop_block_liquids"))
                .texture(PARTICLE, modLoc("block/poop_block"))
                .element().from(0, 0, 0).to(16, 16, 16)
                .allFaces((face, faceBuilder) -> faceBuilder.texture("#side").uvs(0, 0, 16, 16))
                .face(Direction.UP).texture("#up").end();

        getVariantBuilder(PBlocks.POOP_BLOCK.get())
                .partialState().addModels(
                        new ConfiguredModel(models().getExistingFile(modLoc("block/poop_block1")), 0, 0, false, 9),
                        new ConfiguredModel(models().getExistingFile(modLoc("block/poop_block2")), 0, 0, false, 1),
                        new ConfiguredModel(models().getExistingFile(modLoc("block/poop_block3")), 0, 0, false, 2)
                );
        simpleBlockItem(PBlocks.POOP_BLOCK.get(), models().getExistingFile(modLoc("block/poop_block1")));
    }

    private void poopPiece() {
        for (int layers = 1; layers < 8; layers++) {
            int height = layers * 2;
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
        simpleBlockItem(PBlocks.POOP_PIECE.get(), models().getExistingFile(modLoc("block/poop_height2")));
    }

    private void poolimePoopBlock() {
        models().withExistingParent("poolime_poop_block", mcLoc("block/cube"))
                .texture("south", modLoc("block/poop_block"))
                .texture("west", modLoc("block/poop_block"))
                .texture("north", modLoc("block/poop_block"))
                .texture("east", modLoc("block/poop_block"))
                .texture("down", modLoc("block/poop_block"))
                .texture("up", modLoc("block/poolime_poop_block"))
                .texture(PARTICLE, modLoc("block/poop_block"));
        getVariantBuilder(PBlocks.POOLIME_POOP_BLOCK.get()).partialState().addModels(new ConfiguredModel(models().getExistingFile(modLoc("block/poolime_poop_block"))));
        simpleBlockItem(PBlocks.POOLIME_POOP_BLOCK.get(), models().getExistingFile(modLoc("block/poolime_poop_block")));
    }

    private void randomBlockWithItem(Block block, int... weight) {
        var blockPath = getBlockPath(block);
        int layers = weight.length;
        ConfiguredModel[] configuredModels = new ConfiguredModel[layers];

        for (int i = 0; i < layers; i++) {
            String modelName = blockPath + (i);
            String textureName = i == 0 ? blockPath : modelName;
            models().cubeAll(modelName, modLoc("block/" + textureName));
            configuredModels[i] = new ConfiguredModel(
                    models().getExistingFile(modLoc("block/" + modelName)), 0, 0, false, weight[i]);
        }

        getVariantBuilder(block).partialState().addModels(configuredModels);
        simpleBlockItem(block, models().getExistingFile(modLoc("block/" + blockPath + "0")));
    }

    private void fluidBlockWithItem(Block block) {
        var blockModel = models()
                .withExistingParent(getBlockPath(block), mcLoc("block/block"))
                .texture("particle", modLoc("block/" + getBlockPath(block)))
                .texture("still", modLoc("block/" + getBlockPath(block)))
                .texture("flow", modLoc("block/" + getBlockPath(block)))
                .renderType("translucent");

        getVariantBuilder(block).partialState().addModels(new ConfiguredModel(blockModel));

        itemModels()
                .withExistingParent(getBlockPath(block), mcLoc("item/generated"))
                .texture("layer0", modLoc("block/" + getBlockPath(block)));
    }

    private void blockWithTranslucentRenderType(Block block) {
        var model = models().cubeAll(
                getBlockPath(block), modLoc("block/" + getBlockPath(block))
        ).renderType("translucent");

        getVariantBuilder(block).partialState().addModels(new ConfiguredModel(model));

        simpleBlockItem(block, model);
    }

    private void cubeBottomTop(Block block) {
        cubeBottomTop(block, getBlockPath(block) + "_top", getBlockPath(block) + "_side", getBlockPath(block) + "_bottom");
    }

    private void cubeBottomTop(Block block, Block topBottom) {
        cubeBottomTop(block, getBlockPath(topBottom), getBlockPath(block) + "_side", getBlockPath(topBottom));
    }

    private void cubeBottomTop(Block block, String top, String side, String bottom) {
        var model = models().withExistingParent(getBlockPath(block), mcLoc("block/cube_bottom_top"))
                .texture("top", modLoc("block/" + top))
                .texture("side", modLoc("block/" + side))
                .texture("bottom", modLoc("block/" + bottom));

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

    private <T extends Enum<T> & StringRepresentable> void registerVariantToilet(
            Block toilet, EnumProperty<T> typeProperty, Map<T, ResourceLocation> textures, T defaultType, boolean hasLava) {
        Map<T, ModelFile[]> variantModels = new LinkedHashMap<>();
        for (var entry : textures.entrySet()) {
            T type = entry.getKey();
            ResourceLocation tex = entry.getValue();
            String suffix = "_" + type.getSerializedName();
            List<ModelFile> modelList = new ArrayList<>();
            modelList.add(models().withExistingParent(getBlockKey(toilet) + suffix, modLoc("block/toilet")).texture("toilet", tex));
            modelList.add(models().withExistingParent(getBlockKey(toilet) + suffix + "_n", modLoc("block/toilet_n")).texture("toilet", tex));
            modelList.add(models().withExistingParent(getBlockKey(toilet) + suffix + "_s", modLoc("block/toilet_s")).texture("toilet", tex));
            modelList.add(models().withExistingParent(getBlockKey(toilet) + suffix + "_ns", modLoc("block/toilet_ns")).texture("toilet", tex));
            if (hasLava) {
                modelList.add(models().withExistingParent(getBlockKey(toilet) + suffix + "_lava", modLoc("block/toilet_lava")).texture("toilet", tex));
                modelList.add(models().withExistingParent(getBlockKey(toilet) + suffix + "_lava_n", modLoc("block/toilet_lava_n")).texture("toilet", tex));
                modelList.add(models().withExistingParent(getBlockKey(toilet) + suffix + "_lava_s", modLoc("block/toilet_lava_s")).texture("toilet", tex));
                modelList.add(models().withExistingParent(getBlockKey(toilet) + suffix + "_lava_ns", modLoc("block/toilet_lava_ns")).texture("toilet", tex));
            }
            variantModels.put(type, modelList.toArray(new ModelFile[0]));
        }

        ModelFile defaultModel = variantModels.get(defaultType)[0];

        getVariantBuilder(toilet).forAllStates(state -> {
            var facing = state.getValue(BlockStateProperties.HORIZONTAL_FACING);
            var connection = state.getValue(AbstractToiletBlock.CONNECTION);
            T variantType = state.getValue(typeProperty);

            var yRot = switch (facing) {
                case EAST -> 90;
                case SOUTH -> 180;
                case WEST -> 270;
                default -> 0;
            };

            ModelFile[] models = variantModels.get(variantType);
            int offset = hasLava && state.getValue(ToiletLavaBlock.LAVA) ? 4 : 0;
            ModelFile chosenModel = switch (connection) {
                case DEFAULT -> models[offset];
                case FRONT -> models[offset + 1];
                case BACK -> models[offset + 2];
                case BOTH -> models[offset + 3];
            };

            return ConfiguredModel.builder()
                    .modelFile(chosenModel)
                    .rotationY(yRot)
                    .uvLock(true)
                    .build();
        });

        itemModels().getBuilder(getBlockPath(toilet)).parent(defaultModel);
    }

    private void registerWoodToilet() {
        Map<ToiletBlock.WoodType, ResourceLocation> textures = new LinkedHashMap<>();
        textures.put(ToiletBlock.WoodType.OAK, mcLoc("block/oak_planks"));
        textures.put(ToiletBlock.WoodType.SPRUCE, mcLoc("block/spruce_planks"));
        textures.put(ToiletBlock.WoodType.BIRCH, mcLoc("block/birch_planks"));
        textures.put(ToiletBlock.WoodType.JUNGLE, mcLoc("block/jungle_planks"));
        textures.put(ToiletBlock.WoodType.ACACIA, mcLoc("block/acacia_planks"));
        textures.put(ToiletBlock.WoodType.CHERRY, mcLoc("block/cherry_planks"));
        textures.put(ToiletBlock.WoodType.DARK_OAK, mcLoc("block/dark_oak_planks"));
        textures.put(ToiletBlock.WoodType.MANGROVE, mcLoc("block/mangrove_planks"));
        textures.put(ToiletBlock.WoodType.BAMBOO, mcLoc("block/bamboo_planks"));
        textures.put(ToiletBlock.WoodType.CRIMSON, mcLoc("block/crimson_planks"));
        textures.put(ToiletBlock.WoodType.WARPED, mcLoc("block/warped_planks"));

        registerVariantToilet(AllToiletBlocks.WOOD_TOILET.get(), ToiletBlock.WOOD_TYPE, textures, ToiletBlock.WoodType.OAK, false);
    }

    private void registerStoneToilet() {
        Map<StoneToiletBlock.StoneType, ResourceLocation> textures = new LinkedHashMap<>();
        textures.put(StoneToiletBlock.StoneType.STONE, mcLoc("block/stone"));
        textures.put(StoneToiletBlock.StoneType.COBBLESTONE, mcLoc("block/cobblestone"));
        textures.put(StoneToiletBlock.StoneType.MOSSY_COBBLESTONE, mcLoc("block/mossy_cobblestone"));
        textures.put(StoneToiletBlock.StoneType.SMOOTH_STONE, mcLoc("block/smooth_stone"));
        textures.put(StoneToiletBlock.StoneType.STONE_BRICK, mcLoc("block/stone_bricks"));
        textures.put(StoneToiletBlock.StoneType.MOSSY_STONE_BRICK, mcLoc("block/mossy_stone_bricks"));
        textures.put(StoneToiletBlock.StoneType.TILE, modLoc("block/tile_block"));
        textures.put(StoneToiletBlock.StoneType.WHITE_CONCRETE, mcLoc("block/white_concrete"));
        textures.put(StoneToiletBlock.StoneType.ORANGE_CONCRETE, mcLoc("block/orange_concrete"));
        textures.put(StoneToiletBlock.StoneType.MAGENTA_CONCRETE, mcLoc("block/magenta_concrete"));
        textures.put(StoneToiletBlock.StoneType.LIGHT_BLUE_CONCRETE, mcLoc("block/light_blue_concrete"));
        textures.put(StoneToiletBlock.StoneType.YELLOW_CONCRETE, mcLoc("block/yellow_concrete"));
        textures.put(StoneToiletBlock.StoneType.LIME_CONCRETE, mcLoc("block/lime_concrete"));
        textures.put(StoneToiletBlock.StoneType.PINK_CONCRETE, mcLoc("block/pink_concrete"));
        textures.put(StoneToiletBlock.StoneType.GRAY_CONCRETE, mcLoc("block/gray_concrete"));
        textures.put(StoneToiletBlock.StoneType.LIGHT_GRAY_CONCRETE, mcLoc("block/light_gray_concrete"));
        textures.put(StoneToiletBlock.StoneType.CYAN_CONCRETE, mcLoc("block/cyan_concrete"));
        textures.put(StoneToiletBlock.StoneType.PURPLE_CONCRETE, mcLoc("block/purple_concrete"));
        textures.put(StoneToiletBlock.StoneType.BLUE_CONCRETE, mcLoc("block/blue_concrete"));
        textures.put(StoneToiletBlock.StoneType.BROWN_CONCRETE, mcLoc("block/brown_concrete"));
        textures.put(StoneToiletBlock.StoneType.GREEN_CONCRETE, mcLoc("block/green_concrete"));
        textures.put(StoneToiletBlock.StoneType.RED_CONCRETE, mcLoc("block/red_concrete"));
        textures.put(StoneToiletBlock.StoneType.BLACK_CONCRETE, mcLoc("block/black_concrete"));

        registerVariantToilet(AllToiletBlocks.STONE_TOILET.get(), StoneToiletBlock.STONE_TYPE, textures, StoneToiletBlock.StoneType.STONE, true);
    }

    private void registerMetalToilet() {
        Map<MetalToiletBlock.MetalType, ResourceLocation> textures = new LinkedHashMap<>();
        textures.put(MetalToiletBlock.MetalType.IRON, mcLoc("block/iron_block"));
        textures.put(MetalToiletBlock.MetalType.GOLD, mcLoc("block/gold_block"));
        textures.put(MetalToiletBlock.MetalType.COPPER, mcLoc("block/copper_block"));
        textures.put(MetalToiletBlock.MetalType.NETHERITE, mcLoc("block/netherite_block"));
        textures.put(MetalToiletBlock.MetalType.RAW_IRON, mcLoc("block/raw_iron_block"));
        textures.put(MetalToiletBlock.MetalType.RAW_GOLD, mcLoc("block/raw_gold_block"));
        textures.put(MetalToiletBlock.MetalType.RAW_COPPER, mcLoc("block/raw_copper_block"));
        textures.put(MetalToiletBlock.MetalType.DIAMOND, mcLoc("block/diamond_block"));
        textures.put(MetalToiletBlock.MetalType.EMERALD, mcLoc("block/emerald_block"));
        textures.put(MetalToiletBlock.MetalType.LAPIS, mcLoc("block/lapis_block"));
        textures.put(MetalToiletBlock.MetalType.REDSTONE, mcLoc("block/redstone_block"));
        textures.put(MetalToiletBlock.MetalType.QUARTZ, mcLoc("block/quartz_block_bottom"));

        registerVariantToilet(AllToiletBlocks.METAL_TOILET.get(), MetalToiletBlock.METAL_TYPE, textures, MetalToiletBlock.MetalType.IRON, true);
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