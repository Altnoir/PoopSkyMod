package com.altnoir.poopsky.client.model;

import com.altnoir.poopsky.content.ToiletType;
import com.altnoir.poopsky.content.block.abs.AbstractToiletBlock;
import com.altnoir.poopsky.content.block.entity.ToiletBlockEntity;
import com.altnoir.poopsky.content.block.p.BaseToiletLavaBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.neoforge.client.model.BakedModelWrapper;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.client.model.data.ModelProperty;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ToiletBakedModel extends BakedModelWrapper<BakedModel> {
    public static final ModelProperty<ToiletType> TOILET_TYPE_PROPERTY = new ModelProperty<>();

    private final BakedModel[] templateModels;
    private final boolean hasLava;
    private final Map<ToiletType, BakedModel[]> variantModels;
    private final Map<ToiletType, ResourceLocation> variantTextures;

    public ToiletBakedModel(
            BakedModel defaultModel,
            BakedModel[] templateModels,
            Map<ToiletType, BakedModel[]> variantModels,
            Map<ToiletType, ResourceLocation> variantTextures,
            boolean hasLava
    ) {
        super(defaultModel);
        this.templateModels = templateModels;
        this.variantModels = variantModels;
        this.variantTextures = variantTextures;
        this.hasLava = hasLava;
    }

    private int getStateIndex(BlockState state) {
        var connection = state.getValue(AbstractToiletBlock.CONNECTION);
        boolean lava = hasLava && state.hasProperty(BaseToiletLavaBlock.LAVA) && state.getValue(BaseToiletLavaBlock.LAVA);
        int offset = lava ? 3 : 0;
        return offset + switch (connection) {
            case DEFAULT -> 0;
            case FRONT, BACK -> 1;
            case BOTH -> 2;
        };
    }

    private BakedModel selectModel(@Nullable BlockState state, ModelData modelData) {
        if (state == null) return originalModel;
        int index = getStateIndex(state);

        ToiletType type = modelData.get(TOILET_TYPE_PROPERTY);
        if (type != null && variantModels.containsKey(type)) {
            BakedModel[] models = variantModels.get(type);
            if (models != null && index < models.length && models[index] != null) {
                return models[index];
            }
        }

        if (templateModels != null && index < templateModels.length && templateModels[index] != null) {
            return templateModels[index];
        }
        return originalModel;
    }

    private TextureAtlasSprite getVariantSprite(ToiletType type) {
        ResourceLocation texture = variantTextures.get(type);
        if (texture == null) return null;
        return Minecraft.getInstance()
                .getTextureAtlas(InventoryMenu.BLOCK_ATLAS)
                .apply(texture);
    }

    private BakedModel selectParticleModel(ModelData modelData) {
        ToiletType type = modelData.get(TOILET_TYPE_PROPERTY);
        if (type == null || !variantModels.containsKey(type)) return originalModel;
        BakedModel[] models = variantModels.get(type);
        if (models != null && models.length > 0 && models[0] != null) {
            return models[0];
        }
        return originalModel;
    }

    private int getYRotation(BlockState state) {
        if (state == null || !state.hasProperty(BlockStateProperties.HORIZONTAL_FACING)) return 0;
        var facing = state.getValue(BlockStateProperties.HORIZONTAL_FACING);
        var connection = state.getValue(AbstractToiletBlock.CONNECTION);
        int baseRot = switch (facing) {
            case EAST -> 90;
            case SOUTH -> 180;
            case WEST -> 270;
            default -> 0;
        };
        int extraRot = connection == AbstractToiletBlock.ToiletState.BACK ? 180 : 0;
        return (baseRot + extraRot) % 360;
    }

    private List<BakedQuad> rotateQuads(List<BakedQuad> quads, int yRot) {
        if (yRot == 0 || quads.isEmpty()) return quads;
        float angle = (float) Math.toRadians(-yRot);
        Matrix4f rotMat = new Matrix4f().rotationY(angle);
        List<BakedQuad> rotated = new ArrayList<>(quads.size());
        for (BakedQuad quad : quads) {
            rotated.add(rotateQuad(quad, rotMat, yRot));
        }
        return rotated;
    }

    private BakedQuad rotateQuad(BakedQuad quad, Matrix4f rotMat, int yRot) {
        int[] vertexData = quad.getVertices().clone();
        int stride = vertexData.length / 4;
        for (int i = 0; i < 4; i++) {
            int offset = i * stride;
            float x = Float.intBitsToFloat(vertexData[offset]);
            float y = Float.intBitsToFloat(vertexData[offset + 1]);
            float z = Float.intBitsToFloat(vertexData[offset + 2]);

            Vector4f pos = new Vector4f(x - 0.5f, y, z - 0.5f, 1.0f);
            rotMat.transform(pos);

            vertexData[offset] = Float.floatToRawIntBits(pos.x + 0.5f);
            vertexData[offset + 1] = Float.floatToRawIntBits(pos.y);
            vertexData[offset + 2] = Float.floatToRawIntBits(pos.z + 0.5f);
        }

        if (quad.getDirection().getAxis() == Direction.Axis.Y && yRot != 0) {
            int uvRot = quad.getDirection() == Direction.DOWN ? (360 - yRot) % 360 : yRot;
            rotateUVForYAxis(vertexData, stride, uvRot, quad.getSprite());
        }

        Direction newDirection = rotateDirection(quad.getDirection(), yRot);
        return new BakedQuad(vertexData, quad.getTintIndex(), newDirection, quad.getSprite(), quad.isShade());
    }

    private void rotateUVForYAxis(int[] vertexData, int stride, int yRot, TextureAtlasSprite sprite) {
        float angle = (float) Math.toRadians(yRot);
        float cos = (float) Math.cos(angle);
        float sin = (float) Math.sin(angle);
        for (int i = 0; i < 4; i++) {
            int offset = i * stride;
            float u = Float.intBitsToFloat(vertexData[offset + 4]);
            float v = Float.intBitsToFloat(vertexData[offset + 5]);

            float unInterpU = sprite.getUOffset(u);
            float unInterpV = sprite.getVOffset(v);

            float cu = unInterpU - 0.5f;
            float cv = unInterpV - 0.5f;
            float newUnInterpU = cu * cos - cv * sin + 0.5f;
            float newUnInterpV = cu * sin + cv * cos + 0.5f;

            vertexData[offset + 4] = Float.floatToRawIntBits(sprite.getU(newUnInterpU));
            vertexData[offset + 5] = Float.floatToRawIntBits(sprite.getV(newUnInterpV));
        }
    }

    private List<BakedQuad> replaceToiletSprite(List<BakedQuad> quads, BakedModel selected, @Nullable ToiletType type) {
        if (type == null || quads.isEmpty()) return quads;

        TextureAtlasSprite sourceSprite = selected.getParticleIcon(ModelData.EMPTY);
        TextureAtlasSprite targetSprite = getVariantSprite(type);
        if (targetSprite == null || targetSprite == sourceSprite) return quads;

        List<BakedQuad> replaced = new ArrayList<>(quads.size());
        for (BakedQuad quad : quads) {
            if (quad.getSprite() == sourceSprite) {
                replaced.add(replaceQuadSprite(quad, sourceSprite, targetSprite));
            } else {
                replaced.add(quad);
            }
        }
        return replaced;
    }

    private BakedQuad replaceQuadSprite(BakedQuad quad, TextureAtlasSprite sourceSprite, TextureAtlasSprite targetSprite) {
        int[] vertexData = quad.getVertices().clone();
        int stride = vertexData.length / 4;
        for (int i = 0; i < 4; i++) {
            int offset = i * stride;
            float u = Float.intBitsToFloat(vertexData[offset + 4]);
            float v = Float.intBitsToFloat(vertexData[offset + 5]);
            float unInterpolatedU = sourceSprite.getUOffset(u);
            float unInterpolatedV = sourceSprite.getVOffset(v);

            vertexData[offset + 4] = Float.floatToRawIntBits(targetSprite.getU(unInterpolatedU));
            vertexData[offset + 5] = Float.floatToRawIntBits(targetSprite.getV(unInterpolatedV));
        }

        return new BakedQuad(vertexData, quad.getTintIndex(), quad.getDirection(), targetSprite, quad.isShade());
    }

    private Direction rotateDirection(Direction dir, int yRot) {
        if (dir.getAxis() == Direction.Axis.Y) return dir;
        int steps = (yRot / 90) % 4;
        Direction result = dir;
        for (int i = 0; i < steps; i++) {
            result = result.getClockWise(Direction.Axis.Y);
        }
        return result;
    }

    private Direction unrotateDirection(Direction dir, int yRot) {
        if (dir.getAxis() == Direction.Axis.Y) return dir;
        int steps = (yRot / 90) % 4;
        Direction result = dir;
        for (int i = 0; i < steps; i++) {
            result = result.getCounterClockWise(Direction.Axis.Y);
        }
        return result;
    }

    @Override
    public ModelData getModelData(BlockAndTintGetter level, BlockPos pos, BlockState state, ModelData modelData) {
        if (modelData.has(TOILET_TYPE_PROPERTY)) {
            return modelData;
        }
        if (level.getBlockEntity(pos) instanceof ToiletBlockEntity be) {
            ToiletType type = be.getToiletType();
            return type != null ? modelData.derive().with(TOILET_TYPE_PROPERTY, type).build() : modelData;
        }
        return modelData;
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction face, RandomSource random, ModelData modelData, @Nullable RenderType renderType) {
        BakedModel selected = selectModel(state, modelData);
        int yRot = getYRotation(state);
        Direction sourceFace = face == null ? null : unrotateDirection(face, yRot);
        List<BakedQuad> quads = selected.getQuads(state, sourceFace, random, modelData, renderType);
        ToiletType type = modelData.get(TOILET_TYPE_PROPERTY);
        return rotateQuads(replaceToiletSprite(quads, selected, type), yRot);
    }

    @Override
    public TextureAtlasSprite getParticleIcon(ModelData modelData) {
        ToiletType type = modelData.get(TOILET_TYPE_PROPERTY);
        TextureAtlasSprite sprite = type != null ? getVariantSprite(type) : null;
        return sprite != null ? sprite : selectParticleModel(modelData).getParticleIcon(modelData);
    }

}
