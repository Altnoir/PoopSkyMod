package com.altnoir.poopsky.client.model;

import com.altnoir.poopsky.content.ToiletType;
import com.altnoir.poopsky.content.block.abs.AbstractToiletBlock;
import com.altnoir.poopsky.content.block.entity.ToiletBlockEntity;
import com.altnoir.poopsky.content.block.p.BaseToiletLavaBlock;
import net.fabricmc.fabric.api.blockview.v2.FabricBlockView;
import net.fabricmc.fabric.api.renderer.v1.model.FabricBakedModel;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class ToiletBakedModel implements BakedModel, FabricBakedModel {
    private final BakedModel defaultModel;
    private final BakedModel[] templateModels;
    private final boolean hasLava;
    private final Map<ToiletType, ResourceLocation> variantTextures;
    private final Map<ToiletType, TypedModel> typedModels = new HashMap<>();
    private final TypedModel fallbackModel = new TypedModel(null);

    public ToiletBakedModel(
            BakedModel defaultModel,
            BakedModel[] templateModels,
            Map<ToiletType, ResourceLocation> variantTextures,
            boolean hasLava
    ) {
        this.defaultModel = defaultModel;
        this.templateModels = templateModels;
        this.variantTextures = variantTextures;
        this.hasLava = hasLava;
        for (ToiletType type : variantTextures.keySet()) {
            typedModels.put(type, new TypedModel(type));
        }
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

    private BakedModel selectModel(@Nullable BlockState state, @Nullable ToiletType type) {
        if (state == null) return defaultModel;
        int index = getStateIndex(state);

        if (templateModels != null && index < templateModels.length && templateModels[index] != null) {
            return templateModels[index];
        }
        return defaultModel;
    }

    private TextureAtlasSprite getVariantSprite(ToiletType type) {
        ResourceLocation texture = variantTextures.get(type);
        if (texture == null) return null;
        return Minecraft.getInstance()
                .getTextureAtlas(InventoryMenu.BLOCK_ATLAS)
                .apply(texture);
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

        TextureAtlasSprite sourceSprite = selected.getParticleIcon();
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
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction face, RandomSource random) {
        return getQuads(state, face, random, null);
    }

    private List<BakedQuad> getQuads(
            @Nullable BlockState state,
            @Nullable Direction face,
            RandomSource random,
            @Nullable ToiletType type
    ) {
        BakedModel selected = selectModel(state, type);
        int yRot = getYRotation(state);
        Direction sourceFace = face == null ? null : unrotateDirection(face, yRot);
        List<BakedQuad> quads = selected.getQuads(state, sourceFace, random);
        return rotateQuads(replaceToiletSprite(quads, selected, type), yRot);
    }

    @Override
    public boolean isVanillaAdapter() {
        return false;
    }

    @Override
    public void emitBlockQuads(
            BlockAndTintGetter blockView,
            BlockState state,
            net.minecraft.core.BlockPos pos,
            Supplier<RandomSource> randomSupplier,
            RenderContext context
    ) {
        Object renderData = blockView instanceof FabricBlockView fabricBlockView
                ? fabricBlockView.getBlockEntityRenderData(pos)
                : null;
        ToiletType type = renderData instanceof ToiletType toiletType ? toiletType : null;
        if (type == null && blockView.getBlockEntity(pos) instanceof ToiletBlockEntity blockEntity) {
            type = blockEntity.getToiletType();
        }
        TypedModel model = type == null ? fallbackModel : typedModels.getOrDefault(type, fallbackModel);
        model.emitBlockQuads(blockView, state, pos, randomSupplier, context);
    }

    @Override
    public boolean useAmbientOcclusion() {
        return defaultModel.useAmbientOcclusion();
    }

    @Override
    public boolean isGui3d() {
        return defaultModel.isGui3d();
    }

    @Override
    public boolean usesBlockLight() {
        return defaultModel.usesBlockLight();
    }

    @Override
    public boolean isCustomRenderer() {
        return defaultModel.isCustomRenderer();
    }

    @Override
    public TextureAtlasSprite getParticleIcon() {
        return defaultModel.getParticleIcon();
    }

    @Override
    public ItemOverrides getOverrides() {
        return defaultModel.getOverrides();
    }

    @Override
    public ItemTransforms getTransforms() {
        return defaultModel.getTransforms();
    }

    private final class TypedModel implements BakedModel, FabricBakedModel {
        private final @Nullable ToiletType type;

        private TypedModel(@Nullable ToiletType type) {
            this.type = type;
        }

        @Override
        public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction face, RandomSource random) {
            return ToiletBakedModel.this.getQuads(state, face, random, type);
        }

        @Override
        public boolean useAmbientOcclusion() {
            return ToiletBakedModel.this.useAmbientOcclusion();
        }

        @Override
        public boolean isGui3d() {
            return ToiletBakedModel.this.isGui3d();
        }

        @Override
        public boolean usesBlockLight() {
            return ToiletBakedModel.this.usesBlockLight();
        }

        @Override
        public boolean isCustomRenderer() {
            return ToiletBakedModel.this.isCustomRenderer();
        }

        @Override
        public TextureAtlasSprite getParticleIcon() {
            if (type == null) return ToiletBakedModel.this.getParticleIcon();
            TextureAtlasSprite sprite = getVariantSprite(type);
            return sprite != null ? sprite : ToiletBakedModel.this.getParticleIcon();
        }

        @Override
        public ItemOverrides getOverrides() {
            return ToiletBakedModel.this.getOverrides();
        }

        @Override
        public ItemTransforms getTransforms() {
            return ToiletBakedModel.this.getTransforms();
        }
    }
}
