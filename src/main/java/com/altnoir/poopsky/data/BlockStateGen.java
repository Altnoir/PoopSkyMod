package com.altnoir.poopsky.data;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.content.ToiletType;
import com.altnoir.poopsky.content.block.ChiliVines;
import com.altnoir.poopsky.content.block.abs.AbstractToiletBlock;
import com.altnoir.poopsky.content.block.p.*;
import com.altnoir.poopsky.init.PoBlocks;
import com.tterrag.registrate.providers.ProviderType;
import com.tterrag.registrate.providers.generators.RegistrateBlockModelGenerator;
import com.tterrag.registrate.util.entry.BlockEntry;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.SlabType;

import java.util.*;
import java.util.function.Consumer;

public class BlockStateGen extends LegacyBlockStateGenerator {
    public static final String PARTICLE = "particle";

    private BlockStateGen(RegistrateBlockModelGenerator prov) {
        super(prov);
    }

    public static void register() {
        PoopSky.registrate().addDataGenerator(ProviderType.BLOCKSTATE, prov -> {
            BlockStateGen gen = new BlockStateGen(prov);
            gen.registerStatesAndModels();
            gen.flushVariantBuilders();
        });
    }

    private void registerStatesAndModels() {
        poopBlock();
        poopWoodSet();
        poopDecoSet();
        poopPiece();
        poopFarmland();
        poopCraftingTable();
        poolimeMaggotsBlock();
        registerPoopCake();
        blockWithTranslucentRenderType(PoBlocks.POOLIME_BLOCK.get());
        PoBlocks.SIMPLE_MODEL_FAMILIES.forEach(this::blockFamily);
        blockWithItem(PoBlocks.CRACKED_POOP_BRICKS.get());
        blockWithItem(PoBlocks.POOP_SAND.get());
        blockWithItem(PoBlocks.DRIED_CHILI_POOP_BLOCK.get());
        blockWithItem(PoBlocks.DRIED_GOLDEN_POOP_BLOCK.get());

        randomBlockWithItem(PoBlocks.RAW_POOP_BLOCK.get(), 3, 1);
        blockWithItem(PoBlocks.RAW_SAPLING_POOP_BLOCK.get());
        blockWithItem(PoBlocks.RAW_SEA_POOP_BLOCK.get());
        blockWithItem(PoBlocks.RAW_WITHER_POOP_BLOCK.get());
        rotationYblockWithItem(PoBlocks.MYCELIUM_BLOCK.get());
        multifaceBlock(PoBlocks.MYCELIUM_MAT.get());
        flowerbedModels(PoBlocks.MUSHROOM_BED.get());
        blockWithTranslucentRenderType(PoBlocks.POOPSKY_BLOCK.get());
        blockWithItem(PoBlocks.POOP_LEAVES.get());
        blockWithItem(PoBlocks.POOP_LEAVES_GOLD.get());
        blockWithItem(PoBlocks.POOP_LEAVES_IRON.get());
        ginkgoWoodSet();
        primoFungusSet();
        blockWithItem(PoBlocks.SALTPETER_BLOCK.get());
        clusterBlock(PoBlocks.SALTPETER_CLUSTER.get());
        clusterBlock(PoBlocks.LARGE_SALTPETER_BUD.get());
        clusterBlock(PoBlocks.MEDIUM_SALTPETER_BUD.get());
        clusterBlock(PoBlocks.SMALL_SALTPETER_BUD.get());
        cubeBottomTop(PoBlocks.POP.get());
        cubeBottomTopFace(PoBlocks.FLY_BARREL.get());
        cubeBottomTop(PoBlocks.BREEDING_CHEST.get(), PoBlocks.CUT_POOP_BLOCK.get());
        orientable(PoBlocks.PLACER.get());
        maggotsChunkLoader();
        cubeBottomTop(PoBlocks.MAGGOTS_BLOCK.get());
        existingBlockstate(PoBlocks.STOOL.get());
        existingBlockstate(PoBlocks.COMPOOPER.get());
        existingBlockstate(PoBlocks.WATER_COMPOOPER.get());
        existingBlockstate(PoBlocks.LAVA_COMPOOPER.get());
        existingBlockstate(PoBlocks.POWDER_SNOW_COMPOOPER.get());
        existingBlockstate(PoBlocks.URINE_COMPOOPER.get());
        waterCompooperItemModel();
        existingBlockstate(PoBlocks.SIEVE.get());
        existingBlockstate(PoBlocks.POOP_EMPTY_LOG.get());
        existingBlockstate(PoBlocks.STRIPPED_POOP_EMPTY_LOG.get());
        existingBlockstate(PoBlocks.POOP_SAPLING.get());
        blockWithItem(PoBlocks.ROUNDWORM_BLOCK.get());
        foliumSennae(PoBlocks.FOLIUM_SENNAE_PLANT.get());
        simpleBlock(PoBlocks.POTTED_FOLIUM_SENNAE_PLANT.get(), models()
                .withExistingParent(getBlockPath(PoBlocks.POTTED_FOLIUM_SENNAE_PLANT.get()), mcLoc("block/flower_pot_cross"))
                .texture("plant", blockTexture(PoBlocks.FOLIUM_SENNAE_PLANT.get()))
                .renderType("cutout"));
        chiliVines(PoBlocks.CHILI_VINES.get());
        chiliVines(PoBlocks.CHILI_VINES_PLANT.get());

        registerToilet(PoBlocks.WOODEN_TOILET, ToiletType.Category.WOOD, false);
        registerToilet(PoBlocks.HARD_TOILET, ToiletType.Category.HARD, true);
        flushToilet(PoBlocks.FLUSH_TOILET.get());
        flushToilet(PoBlocks.GOLDEN_FLUSH_TOILET.get());
        portableToilet(PoBlocks.GINKGO_TOILET.get());
        portableToilet(PoBlocks.PORTABLE_TOILET.get());
        arcade(PoBlocks.BROWN_ARCADE.get());
        arcade(PoBlocks.RED_ARCADE.get());
        arcade(PoBlocks.BLUE_ARCADE.get());
        gachaMachine(PoBlocks.GACHA_MACHINE.get());
        shitBlock(PoBlocks.SHIT.get(), PoBlocks.POOP_BLOCK.get());
        shitBlock(PoBlocks.CHILI_SHIT.get(), PoBlocks.CHILI_POOP_BLOCK.get());
        shitBlock(PoBlocks.GOLDEN_SHIT.get(), PoBlocks.GOLDEN_POOP_BLOCK.get());

        fluidBlockWithItem(PoBlocks.URINE_LIQUID.get());
        makeCropBlock(PoBlocks.MAGGOTS.get(), "maggots_stage", "maggots_stage");
        roundwormVines();
    }

    private void waterCompooperItemModel() {
        models().withExistingParent("water_compooper_item", modLoc("block/compooper_item"))
                .texture("inside", mcLoc("block/water_still"))
                .renderType("translucent");
    }

    private void rotationYblockWithItem(Block block) {
        ModelFile model = models().cubeAll(getBlockPath(block), blockTexture(block));
        getVariantBuilder(block).forAllStates(state -> new ConfiguredModel[]{
                new ConfiguredModel(model, 0, 0, false, 1),
                new ConfiguredModel(model, 0, 90, false, 1),
                new ConfiguredModel(model, 0, 180, false, 1),
                new ConfiguredModel(model, 0, 270, false, 1)
        });
        simpleBlockItem(block, model);
    }

    private void multifaceBlock(Block block) {
        String path = getBlockPath(block);
        Identifier texture = modLoc("block/" + path);
        models().withExistingParent(path, mcLoc("block/block")).renderType("cutout")
                .texture(PARTICLE, texture).texture("multiface", texture)
                .element().from(0, 0, 0.1F).to(16, 16, 0.1F)
                .allFaces((face, builder) -> builder.texture("#multiface")).end();
        existingBlockstate(block);
        generatedItem(block);
    }

    private void flowerbedModels(Block block) {
        String path = getBlockPath(block);
        for (int amount = 1; amount <= 4; amount++) {
            models().withExistingParent(path + "_" + amount, mcLoc("block/flowerbed_" + amount))
                    .renderType("cutout")
                    .texture("flowerbed", modLoc("block/" + path))
                    .texture("stem", modLoc("block/" + path + "_stem"));
        }
        existingBlockstate(block);
        generatedItem(block, "item");
    }

    private void poopWoodSet() {
        Identifier log = blockTexture(PoBlocks.POOP_LOG.get());

        logBlock(PoBlocks.POOP_LOG.get());
        axisBlock(PoBlocks.POOP_WOOD.get(), log, log);
        itemModels().withExistingParent(getItemPath(PoBlocks.POOP_LOG.get()), modLoc("block/poop_log_horizontal"));
        itemModels().withExistingParent(getItemPath(PoBlocks.POOP_WOOD.get()), modLoc("block/poop_wood_horizontal"));

        weightedLogBlock(
                PoBlocks.STRIPPED_POOP_LOG.get(),
                modLoc("block/stripped_poop_log"),
                modLoc("block/stripped_poop_log2"),
                modLoc("block/stripped_poop_log_top")
        );
        weightedAxisBlock(
                PoBlocks.STRIPPED_POOP_WOOD.get(),
                modLoc("block/stripped_poop_log"),
                modLoc("block/stripped_poop_log2")
        );
        itemModels().withExistingParent(getItemPath(PoBlocks.STRIPPED_POOP_LOG.get()), modLoc("block/stripped_poop_log_horizontal"));
        itemModels().withExistingParent(getItemPath(PoBlocks.STRIPPED_POOP_WOOD.get()), modLoc("block/stripped_poop_wood_horizontal"));
    }

    private void weightedLogBlock(Block block, Identifier side, Identifier side2, Identifier end) {
        String path = getBlockPath(block);
        ModelFile vertical = models().withExistingParent(path, mcLoc("block/cube_column"))
                .texture("side", side)
                .texture("end", end);
        ModelFile vertical2 = models().withExistingParent(path + "2", mcLoc("block/cube_column"))
                .texture("side", side2)
                .texture("end", end);
        ModelFile horizontal = models().withExistingParent(path + "_horizontal", mcLoc("block/cube_column_horizontal"))
                .texture("side", side)
                .texture("end", end);
        ModelFile horizontal2 = models().withExistingParent(path + "_horizontal2", mcLoc("block/cube_column_horizontal"))
                .texture("side", side2)
                .texture("end", end);

        getVariantBuilder(block)
                .partialState().with(RotatedPillarBlock.AXIS, Direction.Axis.Y)
                .addModels(
                        new ConfiguredModel(vertical, 0, 0, false, 4),
                        new ConfiguredModel(vertical2, 0, 0, false, 1)
                )
                .partialState().with(RotatedPillarBlock.AXIS, Direction.Axis.Z)
                .addModels(
                        new ConfiguredModel(horizontal, 90, 0, false, 4),
                        new ConfiguredModel(horizontal2, 90, 0, false, 1)
                )
                .partialState().with(RotatedPillarBlock.AXIS, Direction.Axis.X)
                .addModels(
                        new ConfiguredModel(horizontal, 90, 90, false, 4),
                        new ConfiguredModel(horizontal2, 90, 90, false, 1)
                );
    }

    private void weightedAxisBlock(Block block, Identifier side, Identifier side2) {
        String path = getBlockPath(block);
        ModelFile vertical = models().withExistingParent(path, mcLoc("block/cube_column"))
                .texture("side", side)
                .texture("end", side);
        ModelFile vertical2 = models().withExistingParent(path + "2", mcLoc("block/cube_column"))
                .texture("side", side2)
                .texture("end", side2);
        ModelFile horizontal = models().withExistingParent(path + "_horizontal", mcLoc("block/cube_column_horizontal"))
                .texture("side", side)
                .texture("end", side);
        ModelFile horizontal2 = models().withExistingParent(path + "_horizontal2", mcLoc("block/cube_column_horizontal"))
                .texture("side", side2)
                .texture("end", side2);

        getVariantBuilder(block)
                .partialState().with(RotatedPillarBlock.AXIS, Direction.Axis.Y)
                .addModels(
                        new ConfiguredModel(vertical, 0, 0, false, 4),
                        new ConfiguredModel(vertical2, 0, 0, false, 1)
                )
                .partialState().with(RotatedPillarBlock.AXIS, Direction.Axis.Z)
                .addModels(
                        new ConfiguredModel(horizontal, 90, 0, false, 4),
                        new ConfiguredModel(horizontal2, 90, 0, false, 1)
                )
                .partialState().with(RotatedPillarBlock.AXIS, Direction.Axis.X)
                .addModels(
                        new ConfiguredModel(horizontal, 90, 90, false, 4),
                        new ConfiguredModel(horizontal2, 90, 90, false, 1)
                );
    }

    private void poopDecoSet() {
        Identifier texture = blockTexture(PoBlocks.POOP_BLOCK.get());

        stairsBlock(PoBlocks.POOP_STAIRS.get(), texture);
        poopSlab(PoBlocks.POOP_SLAB.get(), texture);
        wallBlock(PoBlocks.POOP_WALL.get(), texture);
        buttonBlock(PoBlocks.POOP_BUTTON.get(), texture);
        pressurePlateBlock(PoBlocks.POOP_PRESSURE_PLATE.get(), texture);
        fenceBlock(PoBlocks.POOP_FENCE.get(), texture);
        fenceGateBlock(PoBlocks.POOP_FENCE_GATE.get(), texture);
        doorBlockWithRenderType(
                PoBlocks.POOP_DOOR.get(),
                Identifier.parse(blockTexture(PoBlocks.POOP_DOOR.get()) + "_bottom"),
                Identifier.parse(blockTexture(PoBlocks.POOP_DOOR.get()) + "_top"),
                "cutout"
        );
        trapdoorBlockWithRenderType(PoBlocks.POOP_TRAPDOOR.get(), blockTexture(PoBlocks.POOP_TRAPDOOR.get()), true, "cutout");

        simpleBlockItem(PoBlocks.POOP_STAIRS.get(), blockModel(PoBlocks.POOP_STAIRS.get()));
        simpleBlockItem(PoBlocks.POOP_SLAB.get(), blockModel(PoBlocks.POOP_SLAB.get()));
        wallItemModel(PoBlocks.POOP_WALL.get(), PoBlocks.POOP_BLOCK.get());
        simpleBlockItem(PoBlocks.POOP_PRESSURE_PLATE.get(), blockModel(PoBlocks.POOP_PRESSURE_PLATE.get()));
        simpleBlockItem(PoBlocks.POOP_FENCE_GATE.get(), blockModel(PoBlocks.POOP_FENCE_GATE.get()));
        simpleBlockItem(PoBlocks.POOP_BUTTON.get(), models().withExistingParent("poop_button_inventory", mcLoc("block/button_inventory"))
                .texture("texture", texture));
        simpleBlockItem(PoBlocks.POOP_FENCE.get(), models().withExistingParent("poop_fence_inventory", mcLoc("block/fence_inventory"))
                .texture("texture", texture));
        generatedItem(PoBlocks.POOP_DOOR.get(), "item");
        simpleBlockItem(PoBlocks.POOP_TRAPDOOR.get(), blockModel(PoBlocks.POOP_TRAPDOOR.get(), "_bottom"));
    }

    private void poopSlab(Block slabBlock, Identifier base) {
        Identifier maggots = modLoc("block/poop_block_maggots");
        Identifier liquids = modLoc("block/poop_block_liquids");

        ModelFile bottom = models().withExistingParent("poop_slab", mcLoc("block/slab"))
                .texture("bottom", base)
                .texture("side", base)
                .texture("top", base);
        ModelFile top = models().withExistingParent("poop_slab_top", mcLoc("block/slab_top"))
                .texture("bottom", base)
                .texture("side", base)
                .texture("top", base);
        ModelFile doubleModel = models().withExistingParent("poop_slab_double", mcLoc("block/cube_column"))
                .texture("end", base)
                .texture("side", base);
        ModelFile bottomMaggots = models().withExistingParent("poop_slab_maggots", mcLoc("block/slab"))
                .texture("bottom", maggots)
                .texture("side", maggots)
                .texture("top", maggots);
        ModelFile topMaggots = models().withExistingParent("poop_slab_top_maggots", mcLoc("block/slab_top"))
                .texture("bottom", maggots)
                .texture("side", maggots)
                .texture("top", maggots);
        ModelFile doubleMaggots = models().withExistingParent("poop_slab_double_maggots", mcLoc("block/cube_column"))
                .texture("end", maggots)
                .texture("side", maggots);
        ModelFile bottomLiquids = models().withExistingParent("poop_slab_liquids", mcLoc("block/slab"))
                .texture("bottom", base)
                .texture("side", base)
                .texture("top", liquids);
        ModelFile topLiquids = models().withExistingParent("poop_slab_top_liquids", mcLoc("block/slab_top"))
                .texture("bottom", base)
                .texture("side", base)
                .texture("top", liquids);
        ModelFile doubleLiquids = models().withExistingParent("poop_slab_double_liquids", modLoc("block/poop_block3"));

        getVariantBuilder(slabBlock)
                .partialState().with(SlabBlock.TYPE, SlabType.BOTTOM)
                .addModels(
                        new ConfiguredModel(bottom, 0, 0, false, 9),
                        new ConfiguredModel(bottomMaggots, 0, 0, false, 1),
                        new ConfiguredModel(bottomLiquids, 0, 0, false, 2)
                )
                .partialState().with(SlabBlock.TYPE, SlabType.TOP)
                .addModels(
                        new ConfiguredModel(top, 0, 0, false, 9),
                        new ConfiguredModel(topMaggots, 0, 0, false, 1),
                        new ConfiguredModel(topLiquids, 0, 0, false, 2)
                )
                .partialState().with(SlabBlock.TYPE, SlabType.DOUBLE)
                .addModels(
                        new ConfiguredModel(doubleModel, 0, 0, false, 9),
                        new ConfiguredModel(doubleMaggots, 0, 0, false, 1),
                        new ConfiguredModel(doubleLiquids, 0, 0, false, 2)
                );
    }

    private void roundwormVines() {
        Block head = PoBlocks.ROUNDWORM_VINES.get();
        ModelFile headModel = models().cross("roundworm_vines", blockTexture(head)).renderType("cutout");
        ModelFile headModelM = models().cross("roundworm_vines_m", modLoc("block/roundworm_vines_m")).renderType("cutout");
        simpleBlock(head, new ConfiguredModel(headModel), new ConfiguredModel(headModelM));

        Block plant = PoBlocks.ROUNDWORM_VINES_PLANT.get();
        ModelFile plantModel = models().cross("roundworm_vines_plant", blockTexture(plant)).renderType("cutout");
        ModelFile seedsModel = models().cross("roundworm_vines_plant_seeds", modLoc("block/roundworm_vines_plant_seeds")).renderType("cutout");
        getVariantBuilder(plant)
                .partialState().with(RoundwormVinesPlantBlock.SEEDS, false)
                .modelForState().modelFile(plantModel).addModel()
                .partialState().with(RoundwormVinesPlantBlock.SEEDS, true)
                .modelForState().modelFile(seedsModel).addModel();
    }

    protected void makeCropBlock(CropBlock cropBlock, String model, String texture) {
        getVariantBuilder(cropBlock).forAllStates(state -> new ConfiguredModel[]{
                new ConfiguredModel(models().crop(model + state.getValue(CropBlock.AGE),
                        PoopSky.loc("block/" + texture + state.getValue(CropBlock.AGE))).renderType("cutout"))
        });
    }

    private void maggotsChunkLoader() {
        Block block = PoBlocks.MAGGOTS_CHUNK_LOADER.get();
        ModelFile model = models().getExistingFile(modLoc("block/" + getBlockPath(block)));
        getVariantBuilder(block).forAllStates(state -> ConfiguredModel.builder().modelFile(model).build());
        simpleBlockItem(block, model);
    }

    private void poopBlock() {
        models().cubeAll("poop_block1", modLoc("block/poop_block"));
        models().cubeAll("poop_block2", modLoc("block/poop_block_maggots")).texture(PARTICLE, modLoc("block/poop_block"));
        models().withExistingParent("poop_block3", mcLoc("block/block"))
                .texture("side", modLoc("block/poop_block"))
                .texture("up", modLoc("block/poop_block_liquids"))
                .texture(PARTICLE, modLoc("block/poop_block"))
                .element().from(0, 0, 0).to(16, 16, 16)
                .allFaces((face, faceBuilder) -> faceBuilder.texture("#side"))
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
            int height = layers << 1;
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
                    String modelName = "poop_height" + (layers << 1);
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

    private void poopCraftingTable() {
        String path = getBlockPath(PoBlocks.POOP_CRAFTING_TABLE.get());
        ModelFile model = models().slab(path,
                modLoc("block/poop_crafting_table_side"),
                modLoc("block/poop_block"),
                modLoc("block/poop_crafting_table_top")
        );
        simpleBlockWithItem(PoBlocks.POOP_CRAFTING_TABLE.get(), model);
    }

    private void registerPoopCake() {
        Identifier bottom = modLoc("block/poop_cake_bottom");
        Identifier top = modLoc("block/poop_cake_top");
        Identifier side = modLoc("block/poop_cake_side");
        Identifier inside = modLoc("block/poop_cake_inner");

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

    private ModelFile cakeModel(String name, String parent, Identifier bottom, Identifier top, Identifier side, Identifier inside) {
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

    private ModelFile candleCakeModel(String name, String candleTexture, Identifier bottom, Identifier top, Identifier side) {
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

        itemModels().withExistingParent(getBlockPath(block), mcLoc("item/generated"))
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

        Identifier planks = blockTexture(PoBlocks.GINKGO_PLANKS.get());
        blockWithItem(PoBlocks.GINKGO_PLANKS.get());
        stairsBlock(PoBlocks.GINKGO_STAIRS.get(), planks);
        slabBlock(PoBlocks.GINKGO_SLAB.get(), planks, planks);
        verticalSlabBlock(PoBlocks.GINKGO_VERTICAL_SLAB.get(), planks);
        buttonBlock(PoBlocks.GINKGO_BUTTON.get(), planks);
        pressurePlateBlock(PoBlocks.GINKGO_PRESSURE_PLATE.get(), planks);
        fenceBlock(PoBlocks.GINKGO_FENCE.get(), planks);
        fenceGateBlock(PoBlocks.GINKGO_FENCE_GATE.get(), planks);
        doorBlockWithRenderType(PoBlocks.GINKGO_DOOR.get(), Identifier.parse(blockTexture(PoBlocks.GINKGO_DOOR.get()) + "_bottom"), Identifier.parse(blockTexture(PoBlocks.GINKGO_DOOR.get()) + "_top"), "cutout");
        trapdoorBlockWithRenderType(PoBlocks.GINKGO_TRAPDOOR.get(), blockTexture(PoBlocks.GINKGO_TRAPDOOR.get()), true, "cutout");
        blockWithItem(PoBlocks.GINKGO_LEAVES.get());
        saplingBlock(PoBlocks.GINKGO_SAPLING.get());
        simpleBlock(PoBlocks.POTTED_GINKGO_SAPLING.get(), models()
                .withExistingParent(getBlockPath(PoBlocks.POTTED_GINKGO_SAPLING.get()), mcLoc("block/flower_pot_cross"))
                .texture("plant", blockTexture(PoBlocks.GINKGO_SAPLING.get()))
                .renderType("cutout"));

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
        generatedItem(PoBlocks.GINKGO_DOOR.get(), "item");
        simpleBlockItem(PoBlocks.GINKGO_TRAPDOOR.get(), blockModel(PoBlocks.GINKGO_TRAPDOOR.get(), "_bottom"));
    }

    private void primoFungusSet() {
        registerWoodSet(new WoodSetData(
                PoBlocks.PRIMO_STEM.get(), PoBlocks.STRIPPED_PRIMO_STEM.get(),
                PoBlocks.PRIMO_HYPHAE.get(), PoBlocks.STRIPPED_PRIMO_HYPHAE.get(),
                PoBlocks.PRIMO_PLANKS.get(), PoBlocks.PRIMO_STAIRS.get(), PoBlocks.PRIMO_SLAB.get(),
                PoBlocks.PRIMO_VERTICAL_SLAB.get(), PoBlocks.PRIMO_BUTTON.get(),
                PoBlocks.PRIMO_PRESSURE_PLATE.get(), PoBlocks.PRIMO_FENCE.get(),
                PoBlocks.PRIMO_FENCE_GATE.get(), PoBlocks.PRIMO_DOOR.get(), PoBlocks.PRIMO_TRAPDOOR.get(),
                PoBlocks.PRIMO_CAP.get(), PoBlocks.PRIMO_FUNGUS.get()), this::primoFungusModel);
        blockWithTranslucentRenderType(PoBlocks.GLOW_PRIMO_CAP.get());
        glowPrimoFungusModel(PoBlocks.GLOW_PRIMO_FUNGUS.get(), PoBlocks.GLOW_PRIMO_CAP.get());
    }

    private void registerWoodSet(WoodSetData set, Consumer<SaplingBlock> plantGenerator) {
        logBlock(set.log());
        logBlock(set.strippedLog());
        axisBlock(set.wood(), blockTexture(set.log()), blockTexture(set.log()));
        axisBlock(set.strippedWood(), blockTexture(set.strippedLog()), blockTexture(set.strippedLog()));
        simpleBlockItems(set.log(), set.strippedLog(), set.wood(), set.strippedWood());

        Identifier planks = blockTexture(set.planks());
        blockWithItem(set.planks());
        stairsBlock(set.stairs(), planks);
        slabBlock(set.slab(), planks, planks);
        verticalSlabBlock(set.verticalSlab(), planks);
        buttonBlock(set.button(), planks);
        pressurePlateBlock(set.pressurePlate(), planks);
        fenceBlock(set.fence(), planks);
        fenceGateBlock(set.fenceGate(), planks);
        doorBlockWithRenderType(set.door(), Identifier.parse(blockTexture(set.door()) + "_bottom"),
                Identifier.parse(blockTexture(set.door()) + "_top"), "cutout");
        trapdoorBlockWithRenderType(set.trapdoor(), blockTexture(set.trapdoor()), true, "cutout");
        blockWithItem(set.extraBlock());
        plantGenerator.accept(set.plant());

        simpleBlockItems(set.stairs(), set.slab(), set.pressurePlate(), set.fenceGate());
        ModelFile buttonInventory = models().withExistingParent(getBlockPath(set.button()) + "_inventory", mcLoc("block/button_inventory"))
                .texture("texture", planks);
        simpleBlockItem(set.button(), buttonInventory);
        simpleBlockItem(set.fence(), models().withExistingParent(getBlockPath(set.fence()) + "_inventory", mcLoc("block/fence_inventory"))
                .texture("texture", planks));
        generatedItem(set.door(), "item");
        simpleBlockItem(set.trapdoor(), blockModel(set.trapdoor(), "_bottom"));
    }

    private void primoFungusModel(SaplingBlock fungus) {
        ModelFile model = models().withExistingParent(getBlockPath(fungus), modLoc("block/mushroom"))
                .texture("all", modLoc("block/mushroom/" + getBlockPath(fungus)))
                .texture(PARTICLE, blockTexture(PoBlocks.PRIMO_PLANKS.get()));
        simpleBlock(fungus, model);
        generatedItem(fungus, "item");
    }

    private void glowPrimoFungusModel(SaplingBlock fungus, Block cap) {
        String path = getBlockPath(fungus);
        Identifier texture = modLoc("block/mushroom/" + path);
        Identifier particle = blockTexture(cap);
        ModelFile bottom = models().withExistingParent(path + "_bottom", mcLoc("block/block"))
                .texture("all", texture).texture(PARTICLE, particle)
                .element().from(5, 0, 5).to(11, 8, 11).allFaces((face, builder) -> builder.texture("#all")).end();
        ModelFile top = models().withExistingParent(path + "_top", mcLoc("block/block"))
                .texture("all", texture).texture(PARTICLE, particle).renderType("translucent")
                .element().from(2, 8, 2).to(14, 14, 14).allFaces((face, builder) -> builder.texture("#all")).end();
        existingBlockstate(fungus);
        generatedItem(fungus, "item");
    }

    private void verticalSlabBlock(VerticalSlabBlock block, Identifier texture) {
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
        generatedItem(block);
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
        generatedItem(block);
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

    private void foliumSennae(Block block) {
        ModelFile model = models().withExistingParent(getBlockPath(block), mcLoc("block/cross"))
                .texture("cross", modLoc("block/" + getBlockPath(block))).renderType("cutout");

        simpleBlock(block, model);
    }

    private void registerToilet(BlockEntry<? extends Block> block, ToiletType.Category category, boolean hasLava) {
        Map<ToiletType, Identifier> textures = new LinkedHashMap<>();
        for (ToiletType type : ToiletType.getByCategory(category).values()) {
            textures.put(type, toiletTexture(type));
        }
        registerVariantToilet(block.get(), textures, hasLava);
    }

    private void registerVariantToilet(Block toilet, Map<ToiletType, Identifier> textures, boolean hasLava) {
        ToiletType firstType = textures.keySet().iterator().next();
        Identifier firstTex = textures.get(firstType);
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
            Identifier tex = entry.getValue();
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

    private void portableToilet(Block block) {
        String path = getBlockPath(block);
        ModelFile bottom = models().getExistingFile(modLoc("block/" + path + "_bottom"));
        ModelFile top = models().getExistingFile(modLoc("block/" + path + "_top"));
        ModelFile bottomOpen = models().getExistingFile(modLoc("block/" + path + "_bottom_open"));
        ModelFile topOpen = models().getExistingFile(modLoc("block/" + path + "_top_open"));

        getVariantBuilder(block).forAllStates(state -> {
            boolean open = state.getValue(PortableToiletBlock.OPEN);
            boolean upper = state.getValue(PortableToiletBlock.HALF) == DoubleBlockHalf.UPPER;
            ModelFile model = upper
                    ? (open ? topOpen : top)
                    : (open ? bottomOpen : bottom);
            return ConfiguredModel.builder()
                    .modelFile(model)
                    .rotationY(horizontalRotation(state.getValue(PortableToiletBlock.FACING)))
                    .build();
        });

        generatedItem(block, "item");
    }

    private void flushToilet(Block block) {
        String path = getBlockPath(block);
        ModelFile openModel;
        ModelFile closeModel;

        if (path.equals("flush_toilet")) {
            openModel = models().getExistingFile(modLoc("block/" + path));
            closeModel = models().getExistingFile(modLoc("block/" + path + "_close"));
        } else {
            var texture = modLoc("block/toilet/" + path);
            openModel = models().withExistingParent(path, modLoc("block/flush_toilet"))
                    .texture("toilet", texture + "_cart")
                    .texture("particle", texture + "_particle");
            closeModel = models().withExistingParent(path + "_close", modLoc("block/flush_toilet_close"))
                    .texture("toilet", texture + "_cart")
                    .texture("particle", texture + "_particle");
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

        generatedItem(block, "item");
    }

    private void arcade(Block block) {
        String path = getBlockPath(block);
        ModelFile bottom = models().withExistingParent(path + "_bottom", modLoc("block/arcade_bottom"))
                .texture("arcade", modLoc("block/arcade/" + path + "_bottom"))
                .texture(PARTICLE, modLoc("block/arcade/" + path + "_particle"));
        ModelFile top = models().withExistingParent(path + "_top", modLoc("block/arcade_top"))
                .texture("arcade", modLoc("block/arcade/" + path + "_top"))
                .texture(PARTICLE, modLoc("block/arcade/" + path + "_particle"))
                .texture("screen", modLoc("block/arcade/screen"));
        getVariantBuilder(block).forAllStates(state -> ConfiguredModel.builder()
                .modelFile(state.getValue(ArcadeBlock.HALF) == DoubleBlockHalf.UPPER ? top : bottom)
                .rotationY(horizontalRotation(state.getValue(ArcadeBlock.FACING))).build());
        generatedItem(block, "item");
    }

    private void gachaMachine(Block block) {
        String path = getBlockPath(block);
        ModelFile base = models().getExistingFile(modLoc("block/" + path));
        ModelFile left = models().getExistingFile(modLoc("block/" + path + "_l"));
        ModelFile center = models().getExistingFile(modLoc("block/" + path + "_c"));
        ModelFile right = models().getExistingFile(modLoc("block/" + path + "_r"));
        getVariantBuilder(block).forAllStates(state -> {
            ModelFile model = switch (state.getValue(GachaBlock.STEP)) {
                case 1, 5 -> left;
                case 2, 6 -> center;
                case 3, 7 -> right;
                default -> base;
            };
            return ConfiguredModel.builder().modelFile(model)
                    .rotationY(horizontalRotation(state.getValue(BlockStateProperties.HORIZONTAL_FACING))).build();
        });
        simpleBlockItem(block, base);
    }

    private void shitBlock(Block block, Block particleBlock) {
        String path = getBlockPath(block);
        ModelFile model;

        if (path.equals("shit")) {
            model = models().getExistingFile(modLoc("block/" + path));
        } else {
            var texture = modLoc("block/shit/" + path);
            var particleTexture = modLoc("block/" + getBlockPath(particleBlock));
            model = models().withExistingParent(path, modLoc("block/shit"))
                    .texture(PARTICLE, particleTexture)
                    .texture("shit", texture);
        }

        getVariantBuilder(block).forAllStates(state -> {
            Direction facing = state.getValue(BlockStateProperties.HORIZONTAL_FACING);
            return ConfiguredModel.builder()
                    .modelFile(model)
                    .rotationY(horizontalRotation(facing))
                    .build();
        });
    }

    private void blockWithItem(Block block) {
        simpleBlockWithItem(block, cubeAll(block));
    }

    private void simpleBlockItems(Block... blocks) {
        Arrays.stream(blocks).forEach(block -> simpleBlockItem(block, blockModel(block)));
    }

    private static int horizontalRotation(Direction direction) {
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
        Identifier texture = modLoc("block/" + getBlockPath(baseBlock));
        models().withExistingParent(getBlockPath(wall), mcLoc("block/wall_inventory")).texture("wall", texture);
        return itemModels().withExistingParent(getItemPath(wall), mcLoc("block/wall_inventory")).texture("wall", texture);
    }

    protected ItemModelBuilder generatedItem(Block block) {
        return generatedItem(block, "block");
    }

    protected ItemModelBuilder generatedItem(Block block, String name) {
        return itemModels().withExistingParent(getItemPath(block), mcLoc("item/generated"))
                .texture("layer0", modLoc(name + "/" + getBlockPath(block)));
    }

    private ModelFile blockModel(Block block) {
        return new ModelFile.UncheckedModelFile(modLoc("block/" + getBlockPath(block)));
    }

    private ModelFile blockModel(Block block, String suffix) {
        return new ModelFile.UncheckedModelFile(modLoc("block/" + getBlockPath(block) + suffix));
    }

    private static String getItemPath(Block block) {
        return PoopSky.getItemPath(block.asItem());
    }

    private String getBlockPath(Block block) {
        return PoopSky.getBlockKey(block).getPath();
    }

    private static String getBlockNameSpace(Block block) {
        return PoopSky.getBlockKey(block).getNamespace();
    }

    private Identifier toiletTexture(ToiletType toiletType) {
        String tex = toiletType.texture();
        if (tex != null) {
            String namespace = toiletType.sourceBlock() != null
                    ? getBlockNameSpace(Objects.requireNonNull(toiletType.sourceBlock()))
                    : PoopSky.MOD_ID;
            return PoopSky.modloc(namespace, "block/" + tex);
        }
        Block sourceBlock = toiletType.sourceBlock();
        String key = getBlockNameSpace(sourceBlock);
        String path = getBlockPath(sourceBlock);
        return PoopSky.modloc(key, "block/" + path);
    }

    private record WoodSetData(
            RotatedPillarBlock log,
            RotatedPillarBlock strippedLog,
            RotatedPillarBlock wood,
            RotatedPillarBlock strippedWood,
            Block planks,
            StairBlock stairs,
            SlabBlock slab,
            VerticalSlabBlock verticalSlab,
            ButtonBlock button,
            PressurePlateBlock pressurePlate,
            FenceBlock fence,
            FenceGateBlock fenceGate,
            DoorBlock door,
            TrapDoorBlock trapdoor,
            Block extraBlock,
            SaplingBlock plant
    ) {
    }

    private record MultifaceFace(BooleanProperty property, int rotationX, int rotationY) {
    }

    private static final List<MultifaceFace> MULTIFACE_FACES = List.of(
            new MultifaceFace(BlockStateProperties.NORTH, 0, 0),
            new MultifaceFace(BlockStateProperties.EAST, 0, 90),
            new MultifaceFace(BlockStateProperties.SOUTH, 0, 180),
            new MultifaceFace(BlockStateProperties.WEST, 0, 270),
            new MultifaceFace(BlockStateProperties.UP, 270, 0),
            new MultifaceFace(BlockStateProperties.DOWN, 90, 0)
    );
}
