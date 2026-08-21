package com.altnoir.poopsky.client.model;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.content.ToiletType;
import com.altnoir.poopsky.content.block.abs.AbstractToiletBlock;
import com.altnoir.poopsky.content.block.p.BaseToiletLavaBlock;
import com.altnoir.poopsky.init.PoBlocks;
import com.mojang.math.Quadrant;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.renderer.block.dispatch.SingleVariant;
import net.minecraft.client.renderer.block.dispatch.Variant;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelDebugName;
import net.minecraft.client.resources.model.ResolvableModel;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.event.RegisterSelectItemModelPropertyEvent;
import net.neoforged.neoforge.client.model.standalone.StandaloneModelKey;
import net.neoforged.neoforge.client.model.standalone.StandaloneModelLoader;
import net.neoforged.neoforge.client.model.standalone.UnbakedStandaloneModel;

import java.util.HashMap;
import java.util.Map;

public class BakedModelEventHandler {
    private static final Map<ToiletModelDescriptor, StandaloneModelKey<BlockStateModelPart>> TOILET_MODEL_PARTS = new HashMap<>();

    public static void onRegisterStandalone(ModelEvent.RegisterStandalone event) {
        TOILET_MODEL_PARTS.clear();
        registerToiletModelParts(event, "wooden_toilet", ToiletType.Category.WOOD, false);
        registerToiletModelParts(event, "hard_toilet", ToiletType.Category.HARD, true);
    }

    private static void registerToiletModelParts(ModelEvent.RegisterStandalone event, String blockPath, ToiletType.Category category, boolean hasLava) {
        for (ToiletType type : ToiletType.getByCategory(category).values()) {
            for (String suffix : hasLava ? new String[]{"", "_n", "_ns", "_lava", "_lava_n", "_lava_ns"} : new String[]{"", "_n", "_ns"}) {
                for (Quadrant rotation : Quadrant.values()) {
                    ToiletModelDescriptor descriptor = new ToiletModelDescriptor(blockPath, type, suffix, rotation);
                    Identifier modelId = PoopSky.loc("block/" + blockPath + "_" + type.id() + suffix);
                    Variant variant = new Variant(modelId)
                            .withYRot(rotation)
                            .withUvLock(true);
                    StandaloneModelKey<BlockStateModelPart> key = new StandaloneModelKey<>(
                            () -> modelId + "#" + rotation.name());
                    TOILET_MODEL_PARTS.put(descriptor, key);
                    event.register(key, new ModelPartBaker(variant));
                }
            }
        }
    }

    public static void onModifyBakingResult(ModelEvent.ModifyBakingResult event) {
        Map<BlockState, BlockStateModel> models = event.getBakingResult().blockStateModels();
        StandaloneModelLoader.BakedModels standaloneModels = event.getBakingResult().standaloneModels();
        wrapToiletModels(models, standaloneModels, "wooden_toilet", ToiletType.Category.WOOD, false, PoBlocks.WOODEN_TOILET.get());
        wrapToiletModels(models, standaloneModels, "hard_toilet", ToiletType.Category.HARD, true, PoBlocks.HARD_TOILET.get());
    }

    private static void wrapToiletModels(Map<BlockState, BlockStateModel> models, StandaloneModelLoader.BakedModels standaloneModels, String blockPath, ToiletType.Category category, boolean hasLava, Block block) {
        for (var entry : models.entrySet()) {
            BlockState state = entry.getKey();
            if (!state.is(block)) {
                continue;
            }

            Map<ToiletType, BlockStateModel> variants = new HashMap<>();
            for (ToiletType type : ToiletType.getByCategory(category).values()) {
                StandaloneModelKey<BlockStateModelPart> key = TOILET_MODEL_PARTS.get(
                        new ToiletModelDescriptor(blockPath, type, stateSuffix(state, hasLava), stateRotation(state)));
                BlockStateModelPart part = key == null ? null : standaloneModels.get(key);
                if (part != null) {
                    variants.put(type, new SingleVariant(part));
                }
            }
            entry.setValue(new DynamicToiletBlockStateModel(entry.getValue(), variants));
        }
    }

    private static String stateSuffix(BlockState state, boolean hasLava) {
        String lavaSuffix = hasLava && state.getValue(BaseToiletLavaBlock.LAVA) ? "_lava" : "";
        return lavaSuffix + switch (state.getValue(AbstractToiletBlock.CONNECTION)) {
            case DEFAULT -> "";
            case FRONT, BACK -> "_n";
            case BOTH -> "_ns";
        };
    }

    private static Quadrant stateRotation(BlockState state) {
        int rotation = switch (state.getValue(AbstractToiletBlock.FACING)) {
            case EAST -> 90;
            case SOUTH -> 180;
            case WEST -> 270;
            default -> 0;
        };
        if (state.getValue(AbstractToiletBlock.CONNECTION) == AbstractToiletBlock.ToiletState.BACK) {
            rotation += 180;
        }
        return switch (rotation % 360) {
            case 90 -> Quadrant.R90;
            case 180 -> Quadrant.R180;
            case 270 -> Quadrant.R270;
            default -> Quadrant.R0;
        };
    }

    public static void onRegisterItemModelProperties(RegisterSelectItemModelPropertyEvent event) {
        event.register(PoopSky.loc("fly_type"), FlyTypeItemModelProperty.TYPE);
        event.register(PoopSky.loc("toilet_type"), ToiletTypeItemModelProperty.TYPE);
        event.register(PoopSky.loc("gashapon_color"), GashaponColorItemModelProperty.TYPE);
    }

    public static void register(IEventBus modEventBus) {
        modEventBus.addListener(BakedModelEventHandler::onRegisterStandalone);
        modEventBus.addListener(BakedModelEventHandler::onModifyBakingResult);
        modEventBus.addListener(BakedModelEventHandler::onRegisterItemModelProperties);
    }

    private record ToiletModelDescriptor(String blockPath, ToiletType type, String suffix, Quadrant rotation) {
    }

    private record ModelPartBaker(Variant variant) implements UnbakedStandaloneModel<BlockStateModelPart> {
        @Override
        public BlockStateModelPart bake(ModelBaker baker, ModelDebugName debugName) {
            return variant.bake(baker);
        }

        @Override
        public void resolveDependencies(ResolvableModel.Resolver resolver) {
            variant.resolveDependencies(resolver);
        }
    }
}
