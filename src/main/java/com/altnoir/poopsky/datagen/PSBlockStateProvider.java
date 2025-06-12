package com.altnoir.poopsky.datagen;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.block.PSBlocks;
import com.altnoir.poopsky.block.ToiletBlocks;
import com.altnoir.poopsky.block.p.PoopPiece;
import com.altnoir.poopsky.block.p.ToiletBlock;
import com.altnoir.poopsky.block.p.ToiletLavaBlock;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.AbstractBannerBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.client.model.generators.MultiPartBlockStateBuilder;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

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

        for(int layers = 1; layers < 8; layers++) {
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
                    int layers = state.getValue(PoopPiece.LAYERS);
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

        simpleBlockWithItem(PSBlocks.POOP_LEAVES.get(), this.cubeAll(PSBlocks.POOP_LEAVES.get()));
        simpleBlockWithItem(PSBlocks.POOP_LEAVES_GOLD.get(), this.cubeAll(PSBlocks.POOP_LEAVES_GOLD.get()));
        simpleBlockWithItem(PSBlocks.POOP_LEAVES_IRON.get(), this.cubeAll(PSBlocks.POOP_LEAVES_GOLD.get()));

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
    }

    private void registerToilet(Block toilet, Block textureBlock) {
        var texture = BuiltInRegistries.BLOCK.getKey(textureBlock);
        if (texture == null) throw new IllegalArgumentException("textureBlock not registered yet!");

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
            var forward = state.getValue(ToiletBlock.FORWARD);
            var backward = state.getValue(ToiletBlock.BACKWARD);
            var yRot = switch (facing) {
                case EAST -> 90;
                case SOUTH -> 180;
                case WEST -> 270;
                default -> 0;
            };

            ModelFile chosenModel;
            if (!forward && !backward) chosenModel = baseModel;
            else if (forward && !backward) chosenModel = modelN;
            else if (!forward && backward) chosenModel = modelS;
            else chosenModel = modelNS;

            return ConfiguredModel.builder()
                    .modelFile(chosenModel)
                    .rotationY(yRot)
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

        var baseModel = models().withExistingParent(
                BuiltInRegistries.BLOCK.getKey(toilet).toString(),
                modLoc("block/toilet")).texture("toilet", baseTexture);
        var modelLava = models().withExistingParent(
                BuiltInRegistries.BLOCK.getKey(toilet) + "_lava",
                modLoc("block/toilet_lava")).texture("toilet", baseTexture);


        getVariantBuilder(toilet).forAllStates(state -> {
            var lava = state.getValue(ToiletLavaBlock.LAVA);
            var facing = state.getValue(BlockStateProperties.HORIZONTAL_FACING);
            var chosenModel = lava ? modelLava : baseModel;
            var yRot = switch (facing) {
                case EAST -> 90;
                case SOUTH -> 180;
                case WEST -> 270;
                default -> 0;
            };

            return ConfiguredModel.builder()
                    .modelFile(chosenModel)
                    .rotationY(yRot)
                    .build();
        });
        itemModels().getBuilder(BuiltInRegistries.BLOCK.getKey(toilet).getPath())
                .parent(baseModel);
    }
}
