package com.altnoir.poopsky;

import com.altnoir.poopsky.compat.PSMods;
import com.altnoir.poopsky.compat.create.CreatePlugin;
import com.altnoir.poopsky.compat.maid.MaidPlugin;
import com.altnoir.poopsky.content.FlyTypeManager;
import com.altnoir.poopsky.content.block.ToiletTypeManager;
import com.altnoir.poopsky.content.block.p.CompooperBlock;
import com.altnoir.poopsky.content.entity.p.PoopTntEntity;
import com.altnoir.poopsky.content.villager.PVillagers;
import com.altnoir.poopsky.datagen.PSAdvancementProvider;
import com.altnoir.poopsky.datagen.PSEntityLootTableProvider;
import com.altnoir.poopsky.impl.registrate.PoRegistrate;
import com.altnoir.poopsky.init.*;
import com.altnoir.poopsky.worldgen.PSChunkGenerators;
import com.altnoir.poopsky.worldgen.PSStructures;
import com.altnoir.poopsky.worldgen.foliage.PSFoliagePlacerTypes;
import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.dispenser.OptionalDispenseItemBehavior;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.BoneMealItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.gameevent.GameEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.fluids.FluidInteractionRegistry;
import org.slf4j.Logger;

@Mod(PoopSky.MOD_ID)
public class PoopSky {
    public static final String MOD_ID = "poopsky";
    public static final Logger LOGGER = LogUtils.getLogger();
    private static final PoRegistrate REGISTRATE = PoRegistrate.create(MOD_ID);

    static {
        REGISTRATE.defaultCreativeTab((ResourceKey<CreativeModeTab>) null);
    }

    public PoopSky(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(PSNetworking::register);

        PItems.register();
        PBlocks.register();
        PFluids.register();
        PParticles.register();
        PSAdvancementProvider.register();
        PSEntityLootTableProvider.register();
        PItemGroups.register();

        PEffects.register();
        PPotions.register();
        PBlockEntityType.register();
        PEntityType.register();
        PSFoliagePlacerTypes.register(modEventBus);
        PSStructures.register(modEventBus);
        PSChunkGenerators.register(modEventBus);
        PSoundEvents.register(modEventBus);
        PStats.register(modEventBus);
        PComponents.register(modEventBus);
        PLootFunctions.register(modEventBus);
        PVillagers.register(modEventBus);
        PRecipes.register(modEventBus);
        PMenuTypes.register(modEventBus);

        NeoForge.EVENT_BUS.addListener(this::onAddReloadListener);

        if (ModList.get().isLoaded(PSMods.TOUHOU_LITTLE_MAID.id())) {
            MaidPlugin.registry(modEventBus);
        }
        if (ModList.get().isLoaded(PSMods.CREATE.id())) {
            CreatePlugin.register(modEventBus);
        }
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void onAddReloadListener(AddReloadListenerEvent event) {
        event.addListener(FlyTypeManager.INSTANCE);
        event.addListener(ToiletTypeManager.INSTANCE);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            CompooperBlock.bootStrap();
            DispenserBlock.registerProjectileBehavior(PItems.POOP_BALL);
            DispenserBlock.registerProjectileBehavior(PItems.SEA_POOP_BALL);
            DispenserBlock.registerProjectileBehavior(PItems.WITHER_POOP_BALL);
            DispenserBlock.registerBehavior(PItems.POOP.get(), new OptionalDispenseItemBehavior() {
                @Override
                protected ItemStack execute(BlockSource blockSource, ItemStack item) {
                    this.setSuccess(true);
                    Level level = blockSource.level();
                    BlockPos blockpos = blockSource.pos().relative(blockSource.state().getValue(DispenserBlock.FACING));

                    if (!BoneMealItem.applyBonemeal(item, level, blockpos, null) && !BoneMealItem.growWaterPlant(item, level, blockpos, null)) {
                        this.setSuccess(false);
                    } else if (!level.isClientSide) {
                        level.levelEvent(1505, blockpos, 15);
                    }
                    return item;
                }
            });
            DispenserBlock.registerBehavior(PBlocks.POOP_TNT.asItem(), new DefaultDispenseItemBehavior() {
                @Override
                protected ItemStack execute(BlockSource blockSource, ItemStack item) {
                    Level level = blockSource.level();
                    Direction facing = blockSource.state().getValue(DispenserBlock.FACING);
                    BlockPos pos = blockSource.pos().relative(facing);

                    PoopTntEntity tnt = new PoopTntEntity(level, pos.getX() + 0.5, pos.getY() + 0.125, pos.getZ() + 0.5, null);
                    level.addFreshEntity(tnt);
                    level.playSound(null, tnt.getX(), tnt.getY(), tnt.getZ(), SoundEvents.TNT_PRIMED, SoundSource.BLOCKS, 1.0F, 1.0F);
                    level.gameEvent(null, GameEvent.ENTITY_PLACE, pos);
                    item.shrink(1);
                    return item;
                }
            });

            FluidInteractionRegistry.addInteraction(NeoForgeMod.WATER_TYPE.value(), new FluidInteractionRegistry.InteractionInformation(
                    PFluidTypes.URINE_FLUID_TYPE.get(), (fluidState) -> fluidState.isSource() ? PBlocks.POOLIME_BLOCK.get().defaultBlockState() : Blocks.CLAY.defaultBlockState()));
            FluidInteractionRegistry.addInteraction(PFluidTypes.URINE_FLUID_TYPE.get(), new FluidInteractionRegistry.InteractionInformation(
                    NeoForgeMod.WATER_TYPE.value(), (fluidState) -> fluidState.isSource() ? Blocks.COARSE_DIRT.defaultBlockState() : Blocks.CLAY.defaultBlockState()));

            FluidInteractionRegistry.addInteraction(PFluidTypes.URINE_FLUID_TYPE.get(), new FluidInteractionRegistry.InteractionInformation(
                    NeoForgeMod.LAVA_TYPE.value(), (fluidState) -> fluidState.isSource() ? Blocks.MAGMA_BLOCK.defaultBlockState() : Blocks.NETHERRACK.defaultBlockState()));
            FluidInteractionRegistry.addInteraction(NeoForgeMod.LAVA_TYPE.value(), new FluidInteractionRegistry.InteractionInformation(
                    PFluidTypes.URINE_FLUID_TYPE.get(), (fluidState) -> fluidState.isSource() ? Blocks.OBSIDIAN.defaultBlockState() : Blocks.MAGMA_BLOCK.defaultBlockState()));

            FluidInteractionRegistry.addInteraction(NeoForgeMod.LAVA_TYPE.value(), new FluidInteractionRegistry.InteractionInformation(
                    (level, currentPos, relativePos, currentState) -> level.getBlockState(currentPos.below()).is(PBlocks.POOP_BLOCK.get()) && level.getBlockState(relativePos).is(Blocks.BLUE_ICE),
                    Blocks.DEEPSLATE.defaultBlockState()));
        });
        PStats.init();
    }

    public static ResourceLocation loc(String path) {
        return ResourceLocation.fromNamespaceAndPath(PoopSky.MOD_ID, path);
    }

    public static PoRegistrate registrate() {
        return REGISTRATE;
    }
}