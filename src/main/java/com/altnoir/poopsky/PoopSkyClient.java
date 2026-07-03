package com.altnoir.poopsky;

import com.altnoir.poopsky.client.model.ToiletModelEventHandler;
import com.altnoir.poopsky.common.block.ToiletType;
import com.altnoir.poopsky.common.block.abs.AbstractCompooperBlock;
import com.altnoir.poopsky.client.ToiletClientBlockExtensions;
import com.altnoir.poopsky.client.inventory.BreedingBoxScreen;
import com.altnoir.poopsky.client.inventory.FlyNestScreen;
import com.altnoir.poopsky.client.particle.LeavesParticle;
import com.altnoir.poopsky.client.particle.PoopParticle;
import com.altnoir.poopsky.client.renderer.TimeBellOverlay;
import com.altnoir.poopsky.client.renderer.ToiletHighlightRenderer;
import com.altnoir.poopsky.client.renderer.ToiletPlugItemRenderer;
import com.altnoir.poopsky.common.FlyType;
import com.altnoir.poopsky.common.event.PSClientGameEvents;
import com.altnoir.poopsky.common.event.PSClientModEvents;
import com.altnoir.poopsky.common.event.PSKeyBoardInput;
import com.altnoir.poopsky.common.entity.renderer.*;
import com.altnoir.poopsky.init.*;
import com.altnoir.poopsky.common.item.PFlyTypes;
import com.altnoir.poopsky.common.item.p.ToiletBlockItem;
import net.minecraft.client.RecipeBookCategories;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import net.neoforged.neoforge.common.NeoForge;
import org.jetbrains.annotations.NotNull;

@Mod(value = PoopSky.MOD_ID, dist = Dist.CLIENT)
public class PoopSkyClient {
    public PoopSkyClient(IEventBus modEventBus, ModContainer modContainer) {
        modContainer.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);

        var gameEventBus = NeoForge.EVENT_BUS;
        PoopSkyClient.registerMod(modEventBus);
        PoopSkyClient.registerGame(gameEventBus);
    }

    public static void registerMod(IEventBus modEventBus) {
        ToiletModelEventHandler.register(modEventBus);
        modEventBus.addListener(PSClientModEvents::registerLayers);
        modEventBus.addListener(ClientModEvents::registerItemProperties);
        modEventBus.addListener(PSClientModEvents::registerBlockEntityRenderers);
        modEventBus.addListener(PSKeyBoardInput::onRegisterKeyMappings);
        modEventBus.addListener(ClientModEvents::registerRenderTypes);
        modEventBus.addListener(ClientModEvents::registerEntityRenderers);
        modEventBus.addListener(ClientModEvents::registerParticleProviders);
        modEventBus.addListener(ClientModEvents::registerRecipeBookCategories);
        modEventBus.addListener(ClientModEvents::onRegisterBlockColors);
        modEventBus.addListener(ClientModEvents::onRegisterItemColors);
        modEventBus.addListener(ClientModEvents::onRegisterBlockRenderBuffers);
        modEventBus.addListener(ClientModEvents::registerGuiOverlays);
        modEventBus.addListener(ClientModEvents::registerClientExtensions);
        modEventBus.addListener(ClientModEvents::registerMenuScreens);
    }

    public static void registerGame(IEventBus modEventBus) {
        modEventBus.addListener(PSClientGameEvents::onScreenOpen);
        modEventBus.addListener(PSClientGameEvents::onClientTick);
        modEventBus.addListener(ToiletHighlightRenderer::onRenderLevel);
    }

    public static class ClientModEvents {
        public static void registerGuiOverlays(RegisterGuiLayersEvent event) {
            event.registerBelow(VanillaGuiLayers.CAMERA_OVERLAYS, PoopSky.loc("time_bell_overlay"), TimeBellOverlay::render);
        }

        public static void registerRenderTypes(RegisterNamedRenderTypesEvent event) {
            event.register(PoopSky.loc("poop_empty_log"), RenderType.cutout(), RenderType.entityCutout(PBlocks.POOP_EMPTY_LOG.getId()));
        }

        public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
            event.registerEntityRenderer(PEntityType.TOILET_PLUG.get(), ToiletPlugRenderer::new);
            event.registerEntityRenderer(PEntityType.POOLIME.get(), PoolimeRenderer::new);
            event.registerEntityRenderer(PEntityType.FLY.get(), FlyRenderer::new);
            event.registerEntityRenderer(PEntityType.STOOL.get(), ChairRenderer::new);
            event.registerEntityRenderer(PEntityType.TOILET.get(), ToiletRenderer::new);
            event.registerEntityRenderer(PEntityType.POOP_TNT.get(), PoopTntRenderer::new);
        }

        public static void registerMenuScreens(RegisterMenuScreensEvent event) {
            event.register(PMenuTypes.FLY_NEST.get(), FlyNestScreen::new);
            event.register(PMenuTypes.BREEDING_BOX.get(), BreedingBoxScreen::new);
        }

        public static void registerRecipeBookCategories(RegisterRecipeBookCategoriesEvent event) {
            event.registerRecipeCategoryFinder(PRecipes.SIEVE_TYPE.get(), recipe -> RecipeBookCategories.UNKNOWN);
            event.registerRecipeCategoryFinder(PRecipes.FLY_NEST_TYPE.get(), recipe -> RecipeBookCategories.UNKNOWN);
            event.registerRecipeCategoryFinder(PRecipes.BREEDING_BOX_TYPE.get(), recipe -> RecipeBookCategories.UNKNOWN);
        }

        public static void registerItemProperties(FMLClientSetupEvent event) {
            event.enqueueWork(() -> {
                ItemProperties.register(PItems.FLY.get(), PoopSky.loc("fly_type"),
                        (stack, level, entity, seed) -> {
                            String id = stack.get(PComponents.FLY_TYPE.get());
                            return (float) FlyType.getIndex(id != null ? id : PFlyTypes.NORMAL.id()) / FlyType.size();
                        });

                for (var block : PBlocks.BLOCKS.getEntries()) {
                    Item item = block.get().asItem();
                    if (item instanceof ToiletBlockItem && item != Items.AIR) {
                        ItemProperties.register(item, PoopSky.loc("toilet_type"),
                                (stack, level, entity, seed) -> {
                                    ToiletType type = stack.get(PComponents.TOILET_TYPE.get());
                                    return type != null ? (float) ToiletType.getIndex(type) : 0;
                                });
                    }
                }
            });
        }

        public static void registerParticleProviders(RegisterParticleProvidersEvent event) {
            event.registerSpriteSet(PParticles.POOP_PARTICLE.get(), PoopParticle.Provider::new);
            event.registerSpriteSet(PParticles.LEAVES_PARTICLE.get(), LeavesParticle.provider());
        }

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
            }, PBlocks.WATER_COMPOOPER.get());
        }

        public static void onRegisterItemColors(RegisterColorHandlersEvent.Item event) {
            event.register((stack, tintIndex) -> tintIndex == 1 ? 0x3F76E4 : -1, PBlocks.WATER_COMPOOPER.get());
        }

        public static void onRegisterBlockRenderBuffers(net.neoforged.neoforge.client.event.RegisterRenderBuffersEvent event) {
            event.registerRenderBuffer(RenderType.translucent());
        }

        public static void registerClientExtensions(RegisterClientExtensionsEvent event) {
            event.registerBlock(ToiletClientBlockExtensions.INSTANCE, PBlocks.WOODEN_TOILET.get(), PBlocks.HARD_TOILET.get());

            var toiletPlugRenderer = new ToiletPlugItemRenderer();
            event.registerItem(new IClientItemExtensions() {
                @Override
                public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                    return toiletPlugRenderer;
                }
            }, PItems.TOILET_PLUG.get());

            event.registerFluidType(new IClientFluidTypeExtensions() {
                @Override
                public @NotNull ResourceLocation getStillTexture() {
                    return PFluidTypes.URINE_STILL_TEXTURE;
                }

                @Override
                public @NotNull ResourceLocation getFlowingTexture() {
                    return PFluidTypes.URINE_FLOWING_TEXTURE;
                }

                @Override
                public ResourceLocation getOverlayTexture() {
                    return null;
                }
            }, PFluidTypes.URINE_FLUID_TYPE.get());
        }
    }
}