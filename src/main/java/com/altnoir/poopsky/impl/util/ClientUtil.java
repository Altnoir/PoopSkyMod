package com.altnoir.poopsky.impl.util;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.worldselection.WorldCreationUiState;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.ColorResolver;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.lighting.LevelLightEngine;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4fStack;
import org.joml.Vector3f;

import java.util.Optional;

public class ClientUtil {
    private static final Minecraft mc = Minecraft.getInstance();
    private static final FluidState EMPTY = Fluids.EMPTY.defaultFluidState();
    private static final BlockState AIR = Blocks.AIR.defaultBlockState();

    private static final Vector3f L1 = new Vector3f(0.4F, 0.0F, 1.0F).normalize();
    private static final Vector3f L2 = new Vector3f(-0.4F, 1.0F, -0.2F).normalize();

    public static void renderBlock(GuiGraphics guiGraphics, BlockState state, float x, float y, float z, float scale) {
        PoseStack poseStack = guiGraphics.pose();

        poseStack.pushPose();

        poseStack.translate(x, y, z);
        poseStack.scale(-scale, -scale, -scale);
        poseStack.translate(-0.5F, -0.5F, 0);
        poseStack.mulPose(Axis.XP.rotationDegrees(-30F));
        poseStack.translate(0.5F, 0, -0.5F);
        poseStack.mulPose(Axis.YP.rotationDegrees(45f));
        poseStack.translate(-0.5F, 0, 0.5F);

        RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
        poseStack.translate(0, 0, -1);

        FluidState fluidState = state.getFluidState();

        if (fluidState.isEmpty()) {
            MultiBufferSource.BufferSource buffers = mc.renderBuffers().bufferSource();
            BakedModel model = mc.getBlockRenderer().getBlockModel(state);
            ModelData modelData = model.getModelData(Dummy.INSTANCE, BlockPos.ZERO, state, ModelData.EMPTY);
            RenderSystem.setupGui3DDiffuseLighting(L1, L2);
            Dummy.tempState = state;
            for (RenderType renderType : model.getRenderTypes(state, RandomSource.create(), modelData)) {
                mc.getBlockRenderer().renderBatched(state, BlockPos.ZERO, Dummy.INSTANCE, poseStack, buffers.getBuffer(renderType), false, RandomSource.create());
            }
            Dummy.tempState = AIR;
            buffers.endBatch();
        } else {
            RenderType renderType = ItemBlockRenderTypes.getRenderLayer(fluidState);
            Matrix4fStack modelView = RenderSystem.getModelViewStack();
            Tesselator tesselator = Tesselator.getInstance();
            renderType.setupRenderState();
            modelView.pushMatrix();
            modelView.mul(poseStack.last().pose());
            RenderSystem.applyModelViewMatrix();

            BufferBuilder builder = tesselator.begin(renderType.mode(), renderType.format());

            Dummy.tempState = state;
            Dummy.tempFluid = fluidState;
            mc.getBlockRenderer().renderLiquid(BlockPos.ZERO, Dummy.INSTANCE, builder, state, state.getFluidState());
            Dummy.tempFluid = EMPTY;
            Dummy.tempState = AIR;

            MeshData build = builder.build();
            if (build != null) {
                BufferUploader.drawWithShader(build);
            }

            renderType.clearRenderState();
            modelView.popMatrix();
            RenderSystem.applyModelViewMatrix();
        }

        poseStack.popPose();
    }

    public enum Dummy implements BlockAndTintGetter {
        INSTANCE;

        private static BlockState tempState = AIR;
        private static FluidState tempFluid = EMPTY;

        @Override
        public float getShade(Direction pDirection, boolean pShade) {
            return 1;
        }

        @SuppressWarnings("DataFlowIssue")
        @Override
        public LevelLightEngine getLightEngine() {
            return Minecraft.getInstance().level.getLightEngine();
        }

        @Override
        public int getBlockTint(BlockPos pBlockPos, ColorResolver pColorResolver) {
            return 0x3F76E4;
        }

        @Override
        public int getBrightness(LightLayer pLightType, BlockPos pBlockPos) {
            return 15;
        }

        @Override
        public int getRawBrightness(BlockPos pBlockPos, int pAmount) {
            return 15;
        }

        @Nullable
        @Override
        public BlockEntity getBlockEntity(BlockPos pPos) {
            return null;
        }

        @Override
        public BlockState getBlockState(BlockPos pos) {
            return pos.equals(BlockPos.ZERO) ? tempState : AIR;
        }

        @Override
        public FluidState getFluidState(BlockPos pos) {
            return pos.equals(BlockPos.ZERO) ? tempFluid : EMPTY;
        }

        @Override
        public int getHeight() {
            return 0;
        }

        @Override
        public int getMinBuildHeight() {
            return 0;
        }
    }

    public static boolean isPoopSkyWorldType(WorldCreationUiState uiState) {
        return isPoopSkyWorldType(uiState.getWorldType());
    }

    public static boolean isPoopSkyWorldType(WorldCreationUiState.WorldTypeEntry worldType) {
        return Optional.ofNullable(worldType.preset())
                .flatMap(Holder::unwrapKey)
                .filter(PHooks.POOPSKY::equals)
                .isPresent();
    }
}
