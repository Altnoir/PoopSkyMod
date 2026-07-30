package com.altnoir.poopsky.impl.registrate;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.content.ToiletType;
import com.altnoir.poopsky.content.block.ChiliVines;
import com.altnoir.poopsky.content.block.abs.AbstractToiletBlock;
import com.altnoir.poopsky.content.block.p.*;
import com.altnoir.poopsky.init.PoBlocks;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import com.tterrag.registrate.util.entry.BlockEntry;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.client.model.generators.ItemModelBuilder;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.*;

public class BlockStateGen extends RegistrateBlockstateProvider {
    public static final String PARTICLE = "particle";

    public BlockStateGen(PackOutput packOutput, ExistingFileHelper exFileHelper) {
        super(PoopSky.registrate(), packOutput, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        poopBlock();
        poopPiece();
        poopFarmland();
        poolimeMaggotsBlock();
        registerPoopCake();
        blockWithTranslucentRenderType(PoBlocks.POOLIME_BLOCK.get());
        PoBlocks.SIMPLE_MODEL_FAMILIES.forEach(this::blockFamily);
        blockWithItem(PoBlocks.CRACKED_POOP_BRICKS.get());

        randomBlockWithItem(PoBlocks.RAW_POOP_BLOCK.get(), 3, 1);
        blockWithItem(PoBlocks.RAW_SAPLING_POOP_BLOCK.get());
        blockWithItem(PoBlocks.RAW_SEA_POOP_BLOCK.get());
        blockWithItem(PoBlocks.RAW_WITHER_POOP_BLOCK.get());
        blockWithItem(PoBlocks.POOP_LEAVES.get());
        blockWithItem(PoBlocks.POOP_LEAVES_GOLD.get());
        blockWithItem(PoBlocks.POOP_LEAVES_IRON.get());
        ginkgoWoodSet();
        blockWithItem(PoBlocks.SALTPETER_BLOCK.get());
        clusterBlock(PoBlocks.SALTPETER_CLUSTER.get());
        clusterBlock(PoBlocks.LARGE_SALTPETER_BUD.get());
        clusterBlock(PoBlocks.MEDIUM_SALTPETER_BUD.get());
        clusterBlock(PoBlocks.SMALL_SALTPETER_BUD.get());
        cubeBottomTop(PoBlocks.POOP_TNT.get());
        cubeBottomTopFace(PoBlocks.FLY_BARREL.get());
        cubeBottomTop(PoBlocks.BREEDING_CHEST.get(), PoBlocks.CUT_POOP_BLOCK.get());
        orientable(PoBlocks.PLACER.get());
        cubeBottomTop(PoBlocks.MAGGOTS_BLOCK.get());
        blockWithItem(PoBlocks.ROUNDWORM_BLOCK.get());
        chiliVines(PoBlocks.CHILI_VINES.get());
        chiliVines(PoBlocks.CHILI_VINES_PLANT.get());

        registerToilet(PoBlocks.WOODEN_TOILET, ToiletType.Category.WOOD, false);
        registerToilet(PoBlocks.HARD_TOILET, ToiletType.Category.HARD, true);
        flushToilet(PoBlocks.FLUSH_TOILET.get());
        flushToilet(PoBlocks.GOLDEN_FLUSH_TOILET.get());
        shitBlock(PoBlocks.SHIT.get());
        shitBlock(PoBlocks.CHILI_SHIT.get());
        shitBlock(PoBlocks.GOLDEN_SHIT.get());

        fluidBlockWithItem(PoBlocks.URINE_LIQUID.get());
        makeCropBlock(PoBlocks.MAGGOTS.get(), "maggots_stage", "maggots_stage");
    }

    protected void makeCropBlock(CropBlock cropBlock, String model, String texture) {
        getVariantBuilder(cropBlock).forAllStates(state -> new ConfiguredModel[]{
                new ConfiguredModel(models().crop(model + state.getValue(CropBlock.AGE),
                        PoopSky.loc("block/" + texture + state.getValue(CropBlock.AGE))).renderType("cutout"))
        });
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

        getVariantBuilder(PoBlocks.POOP_BLOCK.get())
                .partialState().addModels(
                        new ConfiguredModel(models().getExistingFile(modLoc("block/poop_block1")), 0, 0, false, 9),
                        new ConfiguredModel(models().getExistingFile(modLoc("block/poop_block2")), 0, 0, false, 1),
                        new ConfiguredModel(models().getExistingFile(modLoc("block/poop_block3")), 0, 0, false, 2)
                );
        verticalSlabBlock(PoBlocks.POOP_VERTICAL_SLAB.get(), blockTexture(PoBlocks.POOP_BLOCK.get()));
        simpleBlockItem(PoBlocks.POOP_VERTICAL_SLAB.get(), blockModel(PoBlocks.POOP_VERTICAL_SLAB.get()));
        simpleBlockItem(PoBlocks.POOP_BLOCK.get(), models().getExistingFile(modLoc("block/poop_block1")));
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
        getVariantBuilder(PoBlocks.POOP_PIECE.get())
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
        simpleBlockItem(PoBlocks.POOP_PIECE.get(), models().getExistingFile(modLoc("block/poop_height2")));
    }

    private void poolimeMaggotsBlock() {
        ModelFile model = models().withExistingParent(getBlockPath(PoBlocks.POOLIME_MAGGOTS_BLOCK.get()), mcLoc("block/cube"))
                .texture("south", modLoc("block/poop_block"))
                .texture("west", modLoc("block/poop_block"))
                .texture("north", modLoc("block/poop_block"))
                .texture("east", modLoc("block/poop_block"))
                .texture("down", modLoc("block/poop_block"))
                .texture("up", modLoc("block/" + getBlockPath(PoBlocks.POOLIME_MAGGOTS_BLOCK.get())))
                .texture(PARTICLE, modLoc("block/poop_block"));
        simpleBlockWithItem(PoBlocks.POOLIME_MAGGOTS_BLOCK.get(), model);
    }

    private void poopFarmland() {
        String path = getBlockPath(PoBlocks.POOP_FARMLAND.get());
        ModelFile defaultModel = models().withExistingParent(path, mcLoc("block/template_farmland"))
                .texture("dirt", modLoc("block/poop_block"))
                .texture("top", modLoc("block/" + path));
        ModelFile enrichedModel = models().withExistingParent(path + "_enriched", mcLoc("block/template_farmland"))
                .texture("dirt", modLoc("block/poop_block"))
                .texture("top", modLoc("block/" + path + "_enriched"));
        ModelFile leakModel = models().withExistingParent(path + "_leak", mcLoc("block/template_farmland"))
                .texture("dirt", modLoc("block/poop_block"))
                .texture("top", modLoc("block/" + path + "_leak"));
        ModelFile enrichedLeakModel = models().withExistingParent(path + "_enriched_leak", mcLoc("block/template_farmland"))
                .texture("dirt", modLoc("block/poop_block"))
                .texture("top", modLoc("block/" + path + "_enriched_leak"));

        getVariantBuilder(PoBlocks.POOP_FARMLAND.get())
                .partialState().with(PoopFarmlandBlock.MODE, PoopFarmlandBlock.FarmMode.DEFAULT).addModels(new ConfiguredModel(defaultModel))
                .partialState().with(PoopFarmlandBlock.MODE, PoopFarmlandBlock.FarmMode.ENRICHED).addModels(new ConfiguredModel(enrichedModel))
                .partialState().with(PoopFarmlandBlock.MODE, PoopFarmlandBlock.FarmMode.LEAK).addModels(new ConfiguredModel(leakModel))
                .partialState().with(PoopFarmlandBlock.MODE, PoopFarmlandBlock.FarmMode.ENRICHED_LEAK).addModels(new ConfiguredModel(enrichedLeakModel));

        simpleBlockItem(PoBlocks.POOP_FARMLAND.get(), defaultModel);
    }

    private void registerPoopCake() {
        ResourceLocation bottom = modLoc("block/poop_cake_bottom");
        ResourceLocation top = modLoc("block/poop_cake_top");
        ResourceLocation side = modLoc("block/poop_cake_side");
        ResourceLocation inside = modLoc("block/poop_cake_inner");

        ModelFile[] cakeModels = new ModelFile[7];
        cakeModels[0] = cakeModel("poop_cake", "cake", bottom, top, side, null);
        for (int bites = 1; bites <= 6; bites++) {
            cakeModels[bites] = cakeModel("poop_cake_slice" + bites, "cake_slice" + bites, bottom, top, side, inside);
        }

        getVariantBuilder(PoBlocks.POOP_CAKE.get()).forAllStates(state -> ConfiguredModel.builder()
                .modelFile(cakeModels[state.getValue(CakeBlock.BITES)])
                .build());

        PoBlocks.getPoopCandleCakes().forEach((candle, candleCake) -> {
            String candleCakePath = getBlockPath(candleCake.get());
            String candlePath = getBlockPath(candle);
            var unlitModel = candleCakeModel(candleCakePath, candlePath, bottom, top, side);
            var litModel = candleCakeModel(candleCakePath + "_lit", candlePath + "_lit", bottom, top, side);

            getVariantBuilder(candleCake.get())
                    .partialState().with(PoopCandleCakeBlock.LIT, false).addModels(new ConfiguredModel(unlitModel))
                    .partialState().with(PoopCandleCakeBlock.LIT, true).addModels(new ConfiguredModel(litModel));
        });
    }

    private ModelFile cakeModel(String name, String parent, ResourceLocation bottom, ResourceLocation top, ResourceLocation side, ResourceLocation inside) {
        var model = models().withExistingParent(name, mcLoc("block/" + parent))
                .texture(PARTICLE, side)
                .texture("bottom", bottom)
                .texture("top", top)
                .texture("side", side);
        if (inside != null) {
            model.texture("inside", inside);
        }
        return model;
    }

    private ModelFile candleCakeModel(String name, String candleTexture, ResourceLocation bottom, ResourceLocation top, ResourceLocation side) {
        return models().withExistingParent(name, mcLoc("block/template_cake_with_candle"))
                .texture(PARTICLE, side)
                .texture("bottom", bottom)
                .texture("top", top)
                .texture("side", side)
                .texture("candle", mcLoc("block/" + candleTexture));
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

        simpleBlock(block, configuredModels);
        simpleBlockItem(block, models().getExistingFile(modLoc("block/" + blockPath + "0")));
    }

    private void fluidBlockWithItem(Block block) {
        var blockModel = models()
                .withExistingParent(getBlockPath(block), mcLoc("block/block"))
                .texture("particle", modLoc("block/" + getBlockPath(block)))
                .texture("still", modLoc("block/" + getBlockPath(block)))
                .texture("flow", modLoc("block/" + getBlockPath(block)))
                .renderType("translucent");

        simpleBlock(block, blockModel);

        itemModels()
                .withExistingParent(getBlockPath(block), mcLoc("item/generated"))
                .texture("layer0", modLoc("block/" + getBlockPath(block)));
    }

    private void blockWithTranslucentRenderType(Block block) {
        var model = models().cubeAll(
                getBlockPath(block), modLoc("block/" + getBlockPath(block))
        ).renderType("translucent");

        simpleBlockWithItem(block, model);
    }

    private void ginkgoWoodSet() {
        logBlock(PoBlocks.GINKGO_LOG.get());
        logBlock(PoBlocks.STRIPPED_GINKGO_LOG.get());
        axisBlock(PoBlocks.GINKGO_WOOD.get(), blockTexture(PoBlocks.GINKGO_LOG.get()), blockTexture(PoBlocks.GINKGO_LOG.get()));
        axisBlock(PoBlocks.STRIPPED_GINKGO_WOOD.get(), blockTexture(PoBlocks.STRIPPED_GINKGO_LOG.get()), blockTexture(PoBlocks.STRIPPED_GINKGO_LOG.get()));
        simpleBlockItems(
                PoBlocks.GINKGO_LOG.get(),
                PoBlocks.STRIPPED_GINKGO_LOG.get(),
                PoBlocks.GINKGO_WOOD.get(),
                PoBlocks.STRIPPED_GINKGO_WOOD.get()
        );

        ResourceLocation planks = blockTexture(PoBlocks.GINKGO_PLANKS.get());
        blockWithItem(PoBlocks.GINKGO_PLANKS.get());
        stairsBlock(PoBlocks.GINKGO_STAIRS.get(), planks);
        slabBlock(PoBlocks.GINKGO_SLAB.get(), planks, planks);
        verticalSlabBlock(PoBlocks.GINKGO_VERTICAL_SLAB.get(), planks);
        buttonBlock(PoBlocks.GINKGO_BUTTON.get(), planks);
        pressurePlateBlock(PoBlocks.GINKGO_PRESSURE_PLATE.get(), planks);
        fenceBlock(PoBlocks.GINKGO_FENCE.get(), planks);
        fenceGateBlock(PoBlocks.GINKGO_FENCE_GATE.get(), planks);
        doorBlockWithRenderType(PoBlocks.GINKGO_DOOR.get(), ResourceLocation.parse(blockTexture(PoBlocks.GINKGO_DOOR.get()) + "_bottom"), ResourceLocation.parse(blockTexture(PoBlocks.GINKGO_DOOR.get()) + "_top"), "cutout");
        trapdoorBlockWithRenderType(PoBlocks.GINKGO_TRAPDOOR.get(), blockTexture(PoBlocks.GINKGO_TRAPDOOR.get()), true, "cutout");
        blockWithItem(PoBlocks.GINKGO_LEAVES.get());
        saplingBlock(PoBlocks.GINKGO_SAPLING.get());

        simpleBlockItems(
                PoBlocks.GINKGO_STAIRS.get(),
                PoBlocks.GINKGO_SLAB.get(),
                PoBlocks.GINKGO_PRESSURE_PLATE.get(),
                PoBlocks.GINKGO_FENCE_GATE.get()
        );
        ModelFile buttonInventory = models().withExistingParent(getBlockPath(PoBlocks.GINKGO_BUTTON.get()) + "_inventory", mcLoc("block/button_inventory"))
                .texture("texture", planks);
        simpleBlockItem(PoBlocks.GINKGO_BUTTON.get(), buttonInventory);
        simpleBlockItem(PoBlocks.GINKGO_FENCE.get(), models().withExistingParent(getBlockPath(PoBlocks.GINKGO_FENCE.get()) + "_inventory", mcLoc("block/fence_inventory"))
                .texture("texture", planks));
        itemModels().withExistingParent(getItemPath(PoBlocks.GINKGO_DOOR.get()), mcLoc("item/generated"))
                .texture("layer0", modLoc("item/" + getBlockPath(PoBlocks.GINKGO_DOOR.get())));
        simpleBlockItem(PoBlocks.GINKGO_TRAPDOOR.get(), blockModel(PoBlocks.GINKGO_TRAPDOOR.get(), "_bottom"));
    }

    private void verticalSlabBlock(VerticalSlabBlock block, ResourceLocation texture) {
        String path = getBlockPath(block);
        ModelFile model = models().withExistingParent(path, mcLoc("block/block"))
                .texture("particle", texture)
                .texture("texture", texture)
                .element().from(0, 0, 0).to(16, 16, 8)
                .allFaces((face, builder) -> builder.texture("#texture"))
                .end();
        ModelFile doubleModel = models().cubeAll(path + "_double", texture);

        getVariantBuilder(block).forAllStates(state -> {
            boolean isDouble = state.getValue(VerticalSlabBlock.DOUBLE);
            return ConfiguredModel.builder()
                    .modelFile(isDouble ? doubleModel : model)
                    .rotationY(isDouble ? 0 : horizontalRotation(state.getValue(VerticalSlabBlock.FACING)))
                    .uvLock(true)
                    .build();
        });

        itemModels().withExistingParent(getItemPath(block), mcLoc("block/block"))
                .texture("particle", texture)
                .texture("texture", texture)
                .element().from(0, 0, 4).to(16, 16, 12)
                .allFaces((face, builder) -> builder.texture("#texture"))
                .end();
    }

    private void saplingBlock(SaplingBlock block) {
        String path = getBlockPath(block);
        ModelFile model = models().cross(path, blockTexture(block)).renderType("cutout");
        simpleBlock(block, model);
        bushItem(block);
    }

    private void clusterBlock(Block block) {
        ModelFile model = models().withExistingParent(getBlockPath(block), mcLoc("block/cross")).renderType("cutout")
                .texture("cross", modLoc("block/" + getBlockPath(block)));
        getVariantBuilder(block)
                .partialState().with(BlockStateProperties.FACING, Direction.DOWN)
                .modelForState().modelFile(model).rotationX(180).addModel()
                .partialState().with(BlockStateProperties.FACING, Direction.UP)
                .modelForState().modelFile(model).addModel()
                .partialState().with(BlockStateProperties.FACING, Direction.NORTH)
                .modelForState().modelFile(model).rotationX(90).addModel()
                .partialState().with(BlockStateProperties.FACING, Direction.SOUTH)
                .modelForState().modelFile(model).rotationX(90).rotationY(180).addModel()
                .partialState().with(BlockStateProperties.FACING, Direction.WEST)
                .modelForState().modelFile(model).rotationX(90).rotationY(270).addModel()
                .partialState().with(BlockStateProperties.FACING, Direction.EAST)
                .modelForState().modelFile(model).rotationX(90).rotationY(90).addModel();
        bushItem(block);
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

        simpleBlockWithItem(block, model);
    }

    private void cubeBottomTopFace(Block block) {
        var model = models().withExistingParent(getBlockPath(block), mcLoc("block/cube_bottom_top"))
                .texture("top", modLoc("block/" + getBlockPath(block) + "_top"))
                .texture("side", modLoc("block/" + getBlockPath(block) + "_side"))
                .texture("bottom", modLoc("block/" + getBlockPath(block) + "_bottom"));

        getVariantBuilder(block).forAllStates(state -> {
            var facing = state.getValue(BlockStateProperties.FACING);
            int xRot = switch (facing) {
                case DOWN -> 180;
                case UP -> 0;
                default -> 90;
            };
            int yRot = horizontalRotation(facing);
            return ConfiguredModel.builder().modelFile(model).rotationX(xRot).rotationY(yRot).build();
        });

        simpleBlockItem(block, model);
    }

    private void orientable(Block block) {
        String path = getBlockPath(block);
        String texture = "block/" + path;

        var horizontal = models()
                .withExistingParent(path, mcLoc("block/orientable"))
                .texture("top", modLoc(texture + "_top"))
                .texture("side", modLoc(texture + "_side"))
                .texture("front", modLoc(texture + "_front"))
                .texture(PARTICLE, modLoc(texture + "_side"));

        var vertical = models()
                .withExistingParent(path + "_vertical", mcLoc("block/orientable_vertical"))
                .texture("side", modLoc(texture + "_top"))
                .texture("front", modLoc(texture + "_front_vertical"))
                .texture(PARTICLE, modLoc(texture + "_side"));

        getVariantBuilder(block).forAllStates(state -> {
            Direction facing = state.getValue(BlockStateProperties.FACING);

            return ConfiguredModel.builder()
                    .modelFile(facing.getAxis().isVertical() ? vertical : horizontal)
                    .rotationX(facing == Direction.DOWN ? 180 : 0)
                    .rotationY(horizontalRotation(facing))
                    .build();
        });

        simpleBlockItem(block, horizontal);
    }

    private void chiliVines(Block block) {
        ModelFile chiliVinesPlantModel = models().withExistingParent(getBlockPath(block), mcLoc("block/cross"))
                .texture("cross", modLoc("block/" + getBlockPath(block))).renderType("cutout");
        ModelFile chiliVinesPlantChiliModel = models().withExistingParent(getBlockPath(block) + "_chili", mcLoc("block/cross"))
                .texture("cross", modLoc("block/" + getBlockPath(block) + "_chili")).renderType("cutout");

        getVariantBuilder(block)
                .partialState().with(ChiliVines.CHILI, false)
                .modelForState().modelFile(chiliVinesPlantModel).addModel()
                .partialState().with(ChiliVines.CHILI, true)
                .modelForState().modelFile(chiliVinesPlantChiliModel).addModel();
    }

    private void registerToilet(BlockEntry<? extends Block> block, ToiletType.Category category, boolean hasLava) {
        Map<ToiletType, ResourceLocation> textures = new LinkedHashMap<>();
        for (ToiletType type : ToiletType.getByCategory(category).values()) {
            textures.put(type, toiletTexture(type));
        }
        registerVariantToilet(block.get(), textures, hasLava);
    }

    private void registerVariantToilet(Block toilet, Map<ToiletType, ResourceLocation> textures, boolean hasLava) {
        ToiletType firstType = textures.keySet().iterator().next();
        ResourceLocation firstTex = textures.get(firstType);
        String blockPath = getBlockPath(toilet);

        List<ModelFile> templateModelList = new ArrayList<>();
        templateModelList.add(models().withExistingParent(blockPath, modLoc("block/toilet")).texture("toilet", firstTex));
        templateModelList.add(models().withExistingParent(blockPath + "_n", modLoc("block/toilet_n")).texture("toilet", firstTex));
        templateModelList.add(models().withExistingParent(blockPath + "_ns", modLoc("block/toilet_ns")).texture("toilet", firstTex));
        if (hasLava) {
            templateModelList.add(models().withExistingParent(blockPath + "_lava", modLoc("block/toilet_lava")).texture("toilet", firstTex));
            templateModelList.add(models().withExistingParent(blockPath + "_lava_n", modLoc("block/toilet_lava_n")).texture("toilet", firstTex));
            templateModelList.add(models().withExistingParent(blockPath + "_lava_ns", modLoc("block/toilet_lava_ns")).texture("toilet", firstTex));
        }
        ModelFile[] templateModels = templateModelList.toArray(new ModelFile[0]);

        Map<ToiletType, ModelFile> itemModels = new LinkedHashMap<>();
        for (var entry : textures.entrySet()) {
            ToiletType type = entry.getKey();
            ResourceLocation tex = entry.getValue();
            String suffix = "_" + type.id();
            itemModels.put(type, models().withExistingParent(blockPath + suffix, modLoc("block/toilet")).texture("toilet", tex));
            // 生成 _n (FRONT/BACK) 和 _ns (BOTH) 变体模型，供 ToiletBakedModel 直接加载
            models().withExistingParent(blockPath + suffix + "_n", modLoc("block/toilet_n")).texture("toilet", tex);
            models().withExistingParent(blockPath + suffix + "_ns", modLoc("block/toilet_ns")).texture("toilet", tex);
            if (hasLava) {
                models().withExistingParent(blockPath + suffix + "_lava", modLoc("block/toilet_lava")).texture("toilet", tex);
                models().withExistingParent(blockPath + suffix + "_lava_n", modLoc("block/toilet_lava_n")).texture("toilet", tex);
                models().withExistingParent(blockPath + suffix + "_lava_ns", modLoc("block/toilet_lava_ns")).texture("toilet", tex);
            }
        }

        getVariantBuilder(toilet).forAllStates(state -> {
            var facing = state.getValue(BlockStateProperties.HORIZONTAL_FACING);
            var connection = state.getValue(AbstractToiletBlock.CONNECTION);

            var yRot = horizontalRotation(facing);

            int offset = hasLava && state.getValue(BaseToiletLavaBlock.LAVA) ? 3 : 0;
            int extraYRot = connection == AbstractToiletBlock.ToiletState.BACK ? 180 : 0;
            ModelFile chosenModel = switch (connection) {
                case DEFAULT -> templateModels[offset];
                case FRONT, BACK -> templateModels[offset + 1];
                case BOTH -> templateModels[offset + 2];
            };

            return ConfiguredModel.builder()
                    .modelFile(chosenModel)
                    .rotationY((yRot + extraYRot) % 360)
                    .uvLock(true)
                    .build();
        });

        var itemBuilder = itemModels().getBuilder(getBlockPath(toilet)).parent(templateModels[0]);
        var sortedEntries = new ArrayList<>(textures.entrySet());
        sortedEntries.sort(Comparator.comparingInt(entry -> ToiletType.getIndex(entry.getKey())));
        for (int i = 0; i < sortedEntries.size(); i++) {
            var entry = sortedEntries.get(i);
            String suffix = "_" + entry.getKey().id();
            var overrideModel = new ModelFile.UncheckedModelFile(modLoc("block/" + blockPath + suffix));
            itemBuilder.override()
                    .predicate(PoopSky.loc("toilet_type"), (float) i)
                    .model(overrideModel)
                    .end();
        }
    }

    private void flushToilet(Block block) {
        String path = getBlockPath(block);
        ModelFile openModel;
        ModelFile closeModel;

        if (path.equals("flush_toilet")) {
            openModel = models().getExistingFile(modLoc("block/" + path));
            closeModel = models().getExistingFile(modLoc("block/" + path + "_close"));
        } else {
            var texture = modLoc("block/" + path);
            openModel = models().withExistingParent(path, modLoc("block/flush_toilet")).texture("toilet", texture);
            closeModel = models().withExistingParent(path + "_close", modLoc("block/flush_toilet_close")).texture("toilet", texture);
        }

        getVariantBuilder(block).forAllStates(state -> {
            Direction facing = state.getValue(BlockStateProperties.HORIZONTAL_FACING);
            boolean isClosed = state.getValue(FlushToiletBlock.CLOSED);
            ModelFile model = isClosed ? closeModel : openModel;
            return ConfiguredModel.builder()
                    .modelFile(model)
                    .rotationY(horizontalRotation(facing))
                    .build();
        });

        simpleBlockItem(block, openModel);
    }

    private void shitBlock(Block block) {
        String path = getBlockPath(block);
        ModelFile model;

        if (path.equals("shit")) {
            model = models().getExistingFile(modLoc("block/" + path));
        } else {
            var texture = modLoc("block/" + path);
            model = models().withExistingParent(path, modLoc("block/shit"))
                    .texture(PARTICLE, texture)
                    .texture("shit", texture);
        }

        getVariantBuilder(block).forAllStates(state -> {
            Direction facing = state.getValue(BlockStateProperties.HORIZONTAL_FACING);
            return ConfiguredModel.builder()
                    .modelFile(model)
                    .rotationY(horizontalRotation(facing))
                    .build();
        });

        simpleBlockItem(block, model);
    }

    private void blockWithItem(Block block) {
        simpleBlockWithItem(block, cubeAll(block));
    }

    private void simpleBlockItems(Block... blocks) {
        Arrays.stream(blocks).forEach(block -> simpleBlockItem(block, blockModel(block)));
    }

    private int horizontalRotation(Direction direction) {
        return switch (direction) {
            case EAST -> 90;
            case SOUTH -> 180;
            case WEST -> 270;
            default -> 0;
        };
    }

    private void blockFamily(PoBlocks.BlockFamily family) {
        blockFamily(family.block().get(), family.stairs().get(), family.slab().get(), family.verticalSlab().get(), family.wall().get());
    }

    private void blockFamily(Block block, Block stairs, Block slab, Block verticalSlab, Block wall) {
        var texture = blockTexture(block);
        blockWithItem(block);
        stairsBlock((StairBlock) stairs, texture);
        slabBlock((SlabBlock) slab, texture, texture);
        verticalSlabBlock((VerticalSlabBlock) verticalSlab, texture);
        wallBlock((WallBlock) wall, texture);

        simpleBlockItem(stairs, blockModel(stairs));
        simpleBlockItem(slab, blockModel(slab));
        wallItemModel(wall, block);
    }

    private ItemModelBuilder wallItemModel(Block wall, Block baseBlock) {
        return itemModels().withExistingParent(getItemPath(wall), mcLoc("block/wall_inventory")).texture("wall", modLoc("block/" + getBlockPath(baseBlock)));
    }

    protected ItemModelBuilder bushItem(Block block) {
        return itemModels().withExistingParent(getItemPath(block), mcLoc("item/generated"))
                .texture("layer0", modLoc("block/" + getBlockPath(block)));
    }

    private ModelFile blockModel(Block block) {
        return new ModelFile.UncheckedModelFile(modLoc("block/" + getBlockPath(block)));
    }

    private ModelFile blockModel(Block block, String suffix) {
        return new ModelFile.UncheckedModelFile(modLoc("block/" + getBlockPath(block) + suffix));
    }

    private String getItemPath(Block block) {
        return BuiltInRegistries.ITEM.getKey(block.asItem()).getPath();
    }

    private String getBlockPath(Block block) {
        return getBlockKey(block).getPath();
    }

    private String getBlockNameSpace(Block block) {
        return getBlockKey(block).getNamespace();
    }

    private ResourceLocation getBlockKey(Block block) {
        return BuiltInRegistries.BLOCK.getKey(block);
    }

    private ResourceLocation toiletTexture(ToiletType toiletType) {
        String tex = toiletType.texture();
        if (tex != null) {
            String namespace = toiletType.sourceBlock() != null
                    ? getBlockNameSpace(Objects.requireNonNull(toiletType.sourceBlock()))
                    : PoopSky.MOD_ID;
            return ResourceLocation.fromNamespaceAndPath(namespace, "block/" + tex);
        }
        Block sourceBlock = toiletType.sourceBlock();
        String key = getBlockNameSpace(sourceBlock);
        String path = getBlockPath(sourceBlock);
        return ResourceLocation.fromNamespaceAndPath(key, "block/" + path);
    }
}
