package com.altnoir.poopsky;

import com.altnoir.poopsky.block.AllToiletBlocks;
import com.altnoir.poopsky.block.PSBlockEntities;
import com.altnoir.poopsky.block.PSBlocks;
import com.altnoir.poopsky.block.abs.AbstractCompooperBlock;
import com.altnoir.poopsky.block.p.CompooperBlock;
import com.altnoir.poopsky.component.PSComponents;
import com.altnoir.poopsky.effect.PSEffects;
import com.altnoir.poopsky.effect.PSPotions;
import com.altnoir.poopsky.entity.PSEntityType;
import com.altnoir.poopsky.entity.renderer.*;
import com.altnoir.poopsky.fluid.PSFluidTypes;
import com.altnoir.poopsky.fluid.PSFluids;
import com.altnoir.poopsky.item.PSItems;
import com.altnoir.poopsky.network.PSNetworking;
import com.altnoir.poopsky.particle.PSParticles;
import com.altnoir.poopsky.particle.PoopParticle;
import com.altnoir.poopsky.recipe.PSRecipes;
import com.altnoir.poopsky.sound.PSSoundEvents;
import com.altnoir.poopsky.villager.PSVillagers;
import com.altnoir.poopsky.worldgen.PSChunkGenerators;
import com.altnoir.poopsky.worldgen.foliage.PSFoliagePlacerTypes;
import com.mojang.logging.LogUtils;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.OptionalDispenseItemBehavior;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BoneMealItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DispenserBlock;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.event.RegisterNamedRenderTypesEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.fluids.FluidInteractionRegistry;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

@Mod(PoopSky.MOD_ID)
public class PoopSky {
    public static final String MOD_ID = "poopsky";
    public static final Logger LOGGER = LogUtils.getLogger();


    public PoopSky(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(PSNetworking::register);

        PSEffects.register(modEventBus);
        PSPotions.register(modEventBus);
        PSParticles.register(modEventBus);

        PSBlocks.register(modEventBus);
        AllToiletBlocks.register(modEventBus);
        PSBlockEntities.register(modEventBus);
        PSItems.register(modEventBus);
        PSEntityType.register(modEventBus);
        PSFoliagePlacerTypes.register(modEventBus);
        PSChunkGenerators.register(modEventBus);

        PSItemGroups.register(modEventBus);
        PSSoundEvents.register(modEventBus);

        PSComponents.register(modEventBus);
        PSVillagers.register(modEventBus);
        PSRecipes.register(modEventBus);

        PSFluids.FLUIDS.register(modEventBus);
        PSFluidTypes.FLUID_TYPES.register(modEventBus);

        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
        modContainer.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            CompooperBlock.bootStrap();

            DispenserBlock.registerProjectileBehavior(PSItems.POOP_BALL);
            DispenserBlock.registerProjectileBehavior(PSItems.SEA_POOP_BALL);
            DispenserBlock.registerProjectileBehavior(PSItems.WITHER_POOP_BALL);
            DispenserBlock.registerBehavior(PSItems.POOP.get(), new OptionalDispenseItemBehavior() {
                @Override
                protected ItemStack execute(BlockSource blockSource, ItemStack itemStack) {
                    this.setSuccess(true);
                    Level level = blockSource.level();
                    BlockPos blockpos = blockSource.pos().relative(blockSource.state().getValue(DispenserBlock.FACING));
                    if (!BoneMealItem.growCrop(itemStack, level, blockpos) && !BoneMealItem.growWaterPlant(itemStack, level, blockpos, null)) {
                        this.setSuccess(false);
                    } else if (!level.isClientSide) {
                        level.levelEvent(1505, blockpos, 15);
                    }
                    return itemStack;
                }
            });

            FluidInteractionRegistry.addInteraction(PSFluidTypes.POOP_FLUID_TYPE.get(), new FluidInteractionRegistry.InteractionInformation(
                    NeoForgeMod.WATER_TYPE.value(), (fluidState) -> fluidState.isSource() ? Blocks.COARSE_DIRT.defaultBlockState() : Blocks.CLAY.defaultBlockState()));
            FluidInteractionRegistry.addInteraction(NeoForgeMod.LAVA_TYPE.value(), new FluidInteractionRegistry.InteractionInformation(
                    (level, currentPos, relativePos, currentState) -> level.getBlockState(currentPos.below()).is(PSBlocks.POOP_BLOCK.get()) && level.getBlockState(relativePos).is(Blocks.BLUE_ICE),
                    Blocks.DEEPSLATE.defaultBlockState()));
        });
    }

    @EventBusSubscriber(modid = MOD_ID, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void registerRenderTypes(RegisterNamedRenderTypesEvent event) {
            event.register(PoopSky.loc("poop_sapling"), RenderType.cutout(), RenderType.entityCutout(PSBlocks.POOP_SAPLING.getId()));
            event.register(PoopSky.loc("poop_empty_log"), RenderType.cutout(), RenderType.entityCutout(PSBlocks.POOP_EMPTY_LOG.getId()));
        }

        @SubscribeEvent
        public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
            event.registerEntityRenderer(PSEntityType.TOILET_PLUG.get(), ToiletPlugRenderer::new);
            event.registerEntityRenderer(PSEntityType.POOLIME.get(), PoolimeRenderer::new);
            event.registerEntityRenderer(PSEntityType.FLY.get(), FlyRenderer::new);
            event.registerEntityRenderer(PSEntityType.STOOL.get(), ChairRenderer::new);
            event.registerEntityRenderer(PSEntityType.TOILET.get(), ToiletRenderer::new);
        }

        @SubscribeEvent
        public static void registerParticleProviders(RegisterParticleProvidersEvent event) {
            event.registerSpriteSet(PSParticles.POOP_PARTICLE.get(), PoopParticle.Provider::new);
        }

        @SubscribeEvent
        public static void onRegisterBlockColors(RegisterColorHandlersEvent.Block event) {
            event.register((state, world, pos, tintIndex) -> {
                if (tintIndex == 1) {
                    if (state.getValue(AbstractCompooperBlock.LEVEL) != AbstractCompooperBlock.MIN_LEVEL) {
                        return world != null && pos != null
                                ? BiomeColors.getAverageWaterColor(world, pos)
                                : 0x3F76E4;
                    }
                    return 0x47311A;
                }
                return -1;
            }, PSBlocks.WATER_COMPOOPER.get());
        }

        @SubscribeEvent
        public static void onRegisterFluidRenderTypes(net.neoforged.neoforge.client.event.RegisterRenderBuffersEvent event) {
            event.registerRenderBuffer(RenderType.translucent());
        }

        @SubscribeEvent
        public static void registerClientExtensions(RegisterClientExtensionsEvent event) {
            event.registerFluidType(new IClientFluidTypeExtensions() {
                @Override
                public @NotNull ResourceLocation getStillTexture() {
                    return PSFluidTypes.POOP_STILL_TEXTURE;
                }

                @Override
                public @NotNull ResourceLocation getFlowingTexture() {
                    return PSFluidTypes.POOP_FLOWING_TEXTURE;
                }

                @Override
                public ResourceLocation getOverlayTexture() {
                    return null;
                }
            }, PSFluidTypes.POOP_FLUID_TYPE.get());
        }
    }

    public static ResourceLocation loc(String path) {
        return ResourceLocation.fromNamespaceAndPath(PoopSky.MOD_ID, path);
    }
}