package com.altnoir.poopsky.impl.registrate;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.content.ToiletType;
import com.altnoir.poopsky.content.block.abs.AbstractToiletBlock;
import com.altnoir.poopsky.content.block.p.BaseToiletLavaBlock;
import com.altnoir.poopsky.content.block.p.PoopCandleCakeBlock;
import com.altnoir.poopsky.content.block.p.PoopFarmlandBlock;
import com.altnoir.poopsky.content.block.p.PoopPieceBlock;
import com.altnoir.poopsky.init.PoBlocks;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import com.tterrag.registrate.util.entry.BlockEntry;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.client.model.generators.ItemModelBuilder;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.*;
import java.util.function.Function;

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
        blockWithItem(PoBlocks.SALTPETER_BLOCK.get());
        clusterBlock(PoBlocks.SALTPETER_CLUSTER.get());
        clusterBlock(PoBlocks.LARGE_SALTPETER_BUD.get());
        clusterBlock(PoBlocks.MEDIUM_SALTPETER_BUD.get());
        clusterBlock(PoBlocks.SMALL_SALTPETER_BUD.get());
        cubeBottomTop(PoBlocks.POOP_TNT.get());
        cubeBottomTopFace(PoBlocks.FLY_BARREL.get());
        cubeBottomTop(PoBlocks.BREEDING_CHEST.get(), PoBlocks.CUT_POOP_BLOCK.get());
        orientable(PoBlocks.PLACER.get());
        registerPoopCake();
        cubeBottomTop(PoBlocks.MAGGOTS_BLOCK.get());
        blockWithItem(PoBlocks.ROUNDWORM_BLOCK.get());

        registerToilet(PoBlocks.WOODEN_TOILET, ToiletType.Category.WOOD, false);
        registerToilet(PoBlocks.HARD_TOILET, ToiletType.Category.HARD, true);

        fluidBlockWithItem(PoBlocks.URINE_LIQUID.get());
        makeCropBlock(PoBlocks.MAGGOTS.get(), "maggots_stage", "maggots_stage");
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

        getVariantBuilder(PoBlocks.POOP_BLOCK.get())
                .partialState().addModels(
                        new ConfiguredModel(models().getExistingFile(modLoc("block/poop_block1")), 0, 0, false, 9),
                        new ConfiguredModel(models().getExistingFile(modLoc("block/poop_block2")), 0, 0, false, 1),
                        new ConfiguredModel(models().getExistingFile(modLoc("block/poop_block3")), 0, 0, false, 2)
                );
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
            int yRot = switch (facing) {
                case SOUTH -> 180;
                case WEST -> 270;
                case EAST -> 90;
                default -> 0;
            };
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
                    .rotationY(switch (facing) {
                        case EAST -> 90;
                        case SOUTH -> 180;
                        case WEST -> 270;
                        default -> 0;
                    })
                    .build();
        });

        simpleBlockItem(block, horizontal);
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
            // 生成 _n (FRONT/BACK) 和 _ns (BOTH) 变种模型，供 ToiletBakedModel 直接加载
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

            var yRot = switch (facing) {
                case EAST -> 90;
                case SOUTH -> 180;
                case WEST -> 270;
                default -> 0;
            };

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
        sortedEntries.sort((a, b) -> {
            int ia = ToiletType.getIndex(a.getKey());
            int ib = ToiletType.getIndex(b.getKey());
            return Integer.compare(ia, ib);
        });
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

    private void blockWithItem(Block block) {
        simpleBlockWithItem(block, cubeAll(block));
    }

    private void blockFamily(PoBlocks.BlockFamily family) {
        blockFamily(family.block().get(), family.stairs().get(), family.slab().get(), family.wall().get());
    }

    private void blockFamily(Block block, Block stairs, Block slab, Block wall) {
        var texture = blockTexture(block);
        blockWithItem(block);
        stairsBlock((StairBlock) stairs, texture);
        slabBlock((SlabBlock) slab, texture, texture);
        wallBlock((WallBlock) wall, texture);

        simpleBlockItem(stairs, blockModel(stairs));
        simpleBlockItem(slab, blockModel(slab));
        wallItemModel(wall, block);
    }

    private ItemModelBuilder wallItemModel(Block wall, Block baseBlock) {
        return itemModels().withExistingParent(getItemPath(wall), mcLoc("block/wall_inventory"))
                .texture("wall", modLoc("block/" + getBlockPath(baseBlock)));
    }

    protected ItemModelBuilder bushItem(Block block) {
        return itemModels().withExistingParent(getItemPath(block), mcLoc("item/generated"))
                .texture("layer0", modLoc("block/" + getBlockPath(block)));
    }

    private ModelFile blockModel(Block block) {
        return new ModelFile.UncheckedModelFile(modLoc("block/" + getBlockPath(block)));
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