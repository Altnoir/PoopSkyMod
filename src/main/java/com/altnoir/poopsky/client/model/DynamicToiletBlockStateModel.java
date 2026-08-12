package com.altnoir.poopsky.client.model;

import com.altnoir.poopsky.content.ToiletType;
import com.altnoir.poopsky.content.block.entity.ToiletBlockEntity;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.DynamicBlockStateModel;

import java.util.List;
import java.util.Map;

public class DynamicToiletBlockStateModel implements DynamicBlockStateModel {
    private final BlockStateModel fallbackModel;
    private final Map<ToiletType, BlockStateModel> variantModels;

    public DynamicToiletBlockStateModel(BlockStateModel fallbackModel, Map<ToiletType, BlockStateModel> variantModels) {
        this.fallbackModel = fallbackModel;
        this.variantModels = variantModels;
    }

    private BlockStateModel selectModel(BlockAndTintGetter level, BlockPos pos) {
        if (level.getBlockEntity(pos) instanceof ToiletBlockEntity blockEntity) {
            BlockStateModel variant = variantModels.get(blockEntity.getToiletType());
            if (variant != null) {
                return variant;
            }
        }
        return fallbackModel;
    }

    @Override
    public Object createGeometryKey(BlockAndTintGetter level, BlockPos pos, BlockState state, RandomSource random) {
        return selectModel(level, pos).createGeometryKey(level, pos, state, random);
    }

    @Override
    public void collectParts(BlockAndTintGetter level, BlockPos pos, BlockState state, RandomSource random, List<BlockStateModelPart> parts) {
        selectModel(level, pos).collectParts(level, pos, state, random, parts);
    }

    @Override
    public Material.Baked particleMaterial(BlockAndTintGetter level, BlockPos pos, BlockState state) {
        return selectModel(level, pos).particleMaterial(level, pos, state);
    }

    @Override
    public int materialFlags(BlockAndTintGetter level, BlockPos pos, BlockState state) {
        return selectModel(level, pos).materialFlags(level, pos, state);
    }

    @Override
    public void collectParts(RandomSource random, List<BlockStateModelPart> parts) {
        fallbackModel.collectParts(random, parts);
    }

    @Override
    public Material.Baked particleMaterial() {
        return fallbackModel.particleMaterial();
    }

    @Override
    public int materialFlags() {
        return fallbackModel.materialFlags();
    }
}
