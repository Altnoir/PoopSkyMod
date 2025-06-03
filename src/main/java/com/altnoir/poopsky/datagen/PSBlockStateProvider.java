package com.altnoir.poopsky.datagen;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.block.PSBlocks;
import com.altnoir.poopsky.block.p.PoopPiece;
import net.minecraft.core.Direction;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
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
    }
}
