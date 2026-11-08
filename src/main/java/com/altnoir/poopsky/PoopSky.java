package com.altnoir.poopsky;

import com.altnoir.poopsky.compat.PoMods;
import com.altnoir.poopsky.compat.create.CreatePlugin;
import com.altnoir.poopsky.compat.maid.MaidPlugin;
import com.altnoir.poopsky.content.FlyTypeManager;
import com.altnoir.poopsky.content.ToiletTypeManager;
import com.altnoir.poopsky.content.block.abs.AbstractToiletBlock;
import com.altnoir.poopsky.content.block.p.CompooperBlock;
import com.altnoir.poopsky.content.entity.p.PoopTntEntity;
import com.altnoir.poopsky.content.item.p.JinKeLaItem;
import com.altnoir.poopsky.content.villager.PoVillagers;
import com.altnoir.poopsky.impl.event.PoGameEvents;
import com.altnoir.poopsky.impl.event.PoModEvents;
import com.altnoir.poopsky.impl.registrate.PoRegistrate;
import com.altnoir.poopsky.init.*;
import com.altnoir.poopsky.worldgen.PoChunkGenerators;
import com.altnoir.poopsky.worldgen.PoFeatures;
import com.altnoir.poopsky.worldgen.PoStructures;
import com.altnoir.poopsky.worldgen.foliage.PoFoliagePlacerTypes;
import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockSource;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.core.dispenser.OptionalDispenseItemBehavior;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.FlowerPotBlock;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.fluids.FluidInteractionRegistry;
import org.slf4j.Logger;

@Mod(PoopSky.MOD_ID)
public class PoopSky {
    public static final String MOD_ID = "poopsky";
    public static final Logger LOGGER = LogUtils.getLogger();
    private static final PoRegistrate REGISTRATE = PoRegistrate.create(MOD_ID);

    static {
        REGISTRATE.defaultCreativeTab((ResourceKey<CreativeModeTab>) null);
    }

    public PoopSky() {
        IEventBus modEventBus = net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::commonSetup);
        net.minecraftforge.fml.DistExecutor.safeRunWhenOn(net.minecraftforge.api.distmarker.Dist.CLIENT,
                () -> PoopSkyClient::initialize);

        PoVanillaBackport.register(modEventBus);
        PoAttributes.register(modEventBus);
        PoItems.register();
        PoBlocks.register();
        PoFluids.register();
        PoItemGroups.register();
        PoMenuTypes.register();

        PoEffects.register();
        PoPotions.register();
        PoRecipes.register(modEventBus);
        PoComponents.register(modEventBus);
        PoAttachments.register(modEventBus);

        PoEntityType.register();
        PoBlockEntityType.register();
        PoLootFunctions.register();
        com.altnoir.poopsky.impl.loot.PoLootModifiers.register(modEventBus);
        PoVillagers.register();

        PoParticles.register();
        PoSoundEvents.register();
        PoPainting.register(modEventBus);
        PoStats.register();


        PoFoliagePlacerTypes.register();
        PoFeatures.register();
        PoStructures.register();
        PoChunkGenerators.register();

        var gameEventBus = MinecraftForge.EVENT_BUS;
        PoModEvents.registerMod(modEventBus);
        PoGameEvents.registerGame(gameEventBus);
        gameEventBus.addListener(this::reload);
        net.minecraftforge.fml.ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC);

        if (ModList.get().isLoaded(PoMods.TOUHOU_LITTLE_MAID.id())) {
            MaidPlugin.registry(modEventBus);
        }
        if (ModList.get().isLoaded(PoMods.CREATE.id())) {
            CreatePlugin.register(modEventBus);
        }
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            com.altnoir.poopsky.impl.event.PoCopperEvents.ready = true;
            ToiletTypes.init();
            PoEffects.attachAttributes();
            CompooperBlock.bootStrap();
            PoGameEvents.registerBrewingRecipes();
            com.altnoir.poopsky.impl.event.PoDataMaps.bootstrap();
            ((FlowerPotBlock) Blocks.FLOWER_POT).addPlant(
                    PoBlocks.GINKGO_SAPLING.getId(), PoBlocks.POTTED_GINKGO_SAPLING);
            com.altnoir.poopsky.content.item.PoProjectileItem.registerDispense(PoItems.POOP_BALL);
            com.altnoir.poopsky.content.item.PoProjectileItem.registerDispense(PoItems.SEA_POOP_BALL);
            com.altnoir.poopsky.content.item.PoProjectileItem.registerDispense(PoItems.WITHER_POOP_BALL);
            com.altnoir.poopsky.content.item.PoProjectileItem.registerDispense(PoItems.GACHAPON);
            DispenserBlock.registerBehavior(PoBlocks.SHIT.asItem(), ArmorItem.DISPENSE_ITEM_BEHAVIOR);
            DispenserBlock.registerBehavior(PoBlocks.CHILI_SHIT.asItem(), ArmorItem.DISPENSE_ITEM_BEHAVIOR);
            DispenserBlock.registerBehavior(PoBlocks.GOLDEN_SHIT.asItem(), ArmorItem.DISPENSE_ITEM_BEHAVIOR);
            DispenserBlock.registerBehavior(PoItems.POOP.get(), new OptionalDispenseItemBehavior() {
                @Override
                protected ItemStack execute(BlockSource blockSource, ItemStack item) {
                    this.setSuccess(true);
                    Level level = blockSource.getLevel();
                    BlockPos blockpos = blockSource.getPos().relative(blockSource.getBlockState().getValue(DispenserBlock.FACING));

                    if (!BoneMealItem.applyBonemeal(item, level, blockpos, null) && !BoneMealItem.growWaterPlant(item, level, blockpos, null)) {
                        this.setSuccess(false);
                    } else if (!level.isClientSide) {
                        level.levelEvent(1505, blockpos, 15);
                    }
                    return item;
                }
            });
            DispenserBlock.registerBehavior(PoItems.JINKELA.get(), new OptionalDispenseItemBehavior() {
                @Override
                protected ItemStack execute(BlockSource blockSource, ItemStack item) {
                    boolean success = false;
                    {
                        ServerLevel serverLevel = blockSource.getLevel();
                        BlockPos blockpos = blockSource.getPos().relative(blockSource.getBlockState().getValue(DispenserBlock.FACING));
                        success = JinKeLaItem.tryApplyToBlock(serverLevel, blockpos, serverLevel.getBlockState(blockpos));
                    }
                    this.setSuccess(success);
                    if (success) {
                        item.shrink(1);
                    }
                    return item;
                }
            });
            DispenserBlock.registerBehavior(PoBlocks.POP.asItem(), new DefaultDispenseItemBehavior() {
                @Override
                protected ItemStack execute(BlockSource blockSource, ItemStack item) {
                    Level level = blockSource.getLevel();
                    Direction facing = blockSource.getBlockState().getValue(DispenserBlock.FACING);
                    BlockPos pos = blockSource.getPos().relative(facing);

                    PoopTntEntity tnt = new PoopTntEntity(level, pos.getX() + 0.5, pos.getY() + 0.125, pos.getZ() + 0.5, null);
                    level.addFreshEntity(tnt);
                    level.playSound(null, tnt.getX(), tnt.getY(), tnt.getZ(), SoundEvents.TNT_PRIMED, SoundSource.BLOCKS, 1.0F, 1.0F);
                    level.gameEvent(null, GameEvent.ENTITY_PLACE, pos);
                    item.shrink(1);
                    return item;
                }
            });
            DispenseItemBehavior dispenseitembehavior1 = new DefaultDispenseItemBehavior() {
                private final DefaultDispenseItemBehavior defaultDispenseItemBehavior = new DefaultDispenseItemBehavior();

                @Override
                public ItemStack execute(BlockSource p_338850_, ItemStack p_338251_) {
                    DispensibleContainerItem dispensiblecontaineritem = (DispensibleContainerItem) p_338251_.getItem();
                    BlockPos blockpos = p_338850_.getPos().relative(p_338850_.getBlockState().getValue(DispenserBlock.FACING));
                    Level level = p_338850_.getLevel();
                    if (dispensiblecontaineritem.emptyContents(null, level, blockpos, null, p_338251_)) {
                        dispensiblecontaineritem.checkExtraContent(null, level, p_338251_, blockpos);
                        p_338251_.shrink(1);
                        if (p_338251_.isEmpty()) return new ItemStack(Items.BUCKET);
                        var dispenser = (net.minecraft.world.level.block.entity.DispenserBlockEntity) p_338850_.getEntity();
                        if (dispenser.addItem(new ItemStack(Items.BUCKET)) < 0) this.defaultDispenseItemBehavior.dispense(p_338850_, new ItemStack(Items.BUCKET));
                        return p_338251_;
                    } else {
                        return this.defaultDispenseItemBehavior.dispense(p_338850_, p_338251_);
                    }
                }
            };
            DispenserBlock.registerBehavior(PoItems.URINE_BUCKET.get(), dispenseitembehavior1);

            AbstractToiletBlock.dispenserToiletExplosion(Items.FLINT_AND_STEEL, (toilet, level, pos, stack) -> {
                if (stack.hurt(1, level.getRandom(), null)) {
                    stack.shrink(1);
                    stack.setDamageValue(0);
                }
            });
            AbstractToiletBlock.dispenserToiletExplosion(Items.FIRE_CHARGE, (toilet, level, pos, stack) -> {
                stack.shrink(1);
            });

            FluidInteractionRegistry.addInteraction(ForgeMod.WATER_TYPE.get(), new FluidInteractionRegistry.InteractionInformation(
                    PFluidTypes.URINE_FLUID_TYPE.get(), (fluidState) -> fluidState.isSource() ? PoBlocks.POOLIME_BLOCK.get().defaultBlockState() : Blocks.CLAY.defaultBlockState()));
            FluidInteractionRegistry.addInteraction(PFluidTypes.URINE_FLUID_TYPE.get(), new FluidInteractionRegistry.InteractionInformation(
                    ForgeMod.WATER_TYPE.get(), (fluidState) -> fluidState.isSource() ? Blocks.COARSE_DIRT.defaultBlockState() : Blocks.CLAY.defaultBlockState()));

            FluidInteractionRegistry.addInteraction(PFluidTypes.URINE_FLUID_TYPE.get(), new FluidInteractionRegistry.InteractionInformation(
                    ForgeMod.LAVA_TYPE.get(), (fluidState) -> fluidState.isSource() ? Blocks.MAGMA_BLOCK.defaultBlockState() : Blocks.NETHERRACK.defaultBlockState()));
            FluidInteractionRegistry.addInteraction(ForgeMod.LAVA_TYPE.get(), new FluidInteractionRegistry.InteractionInformation(
                    PFluidTypes.URINE_FLUID_TYPE.get(), (fluidState) -> fluidState.isSource() ? Blocks.OBSIDIAN.defaultBlockState() : Blocks.NETHERRACK.defaultBlockState()));

            FluidInteractionRegistry.addInteraction(ForgeMod.LAVA_TYPE.get(), new FluidInteractionRegistry.InteractionInformation(
                    (level, currentPos, relativePos, currentState) -> level.getBlockState(currentPos.below()).is(PoBlocks.POOP_BLOCK.get()) && level.getBlockState(relativePos).is(Blocks.BLUE_ICE),
                    Blocks.DRIPSTONE_BLOCK.defaultBlockState()));
            FluidInteractionRegistry.addInteraction(ForgeMod.LAVA_TYPE.get(), new FluidInteractionRegistry.InteractionInformation(
                    (level, currentPos, relativePos, currentState) -> level.getBlockState(currentPos.below()).is(PoBlocks.DRIED_POOP_BLOCK.get()) && level.getBlockState(relativePos).is(Blocks.BLUE_ICE),
                    Blocks.DEEPSLATE.defaultBlockState()));
            FluidInteractionRegistry.addInteraction(ForgeMod.LAVA_TYPE.get(), new FluidInteractionRegistry.InteractionInformation(
                    (level, currentPos, relativePos, currentState) -> level.getBlockState(currentPos.below()).is(PoBlocks.DRIED_CHILI_POOP_BLOCK.get()) && level.getBlockState(relativePos).is(Blocks.BLUE_ICE),
                    Blocks.NETHERRACK.defaultBlockState()));
            FluidInteractionRegistry.addInteraction(ForgeMod.LAVA_TYPE.get(), new FluidInteractionRegistry.InteractionInformation(
                    (level, currentPos, relativePos, currentState) -> level.getBlockState(currentPos.below()).is(PoBlocks.DRIED_GOLDEN_POOP_BLOCK.get()) && level.getBlockState(relativePos).is(Blocks.BLUE_ICE),
                    Blocks.END_STONE.defaultBlockState()));

            FluidInteractionRegistry.addInteraction(ForgeMod.LAVA_TYPE.get(), new FluidInteractionRegistry.InteractionInformation(
                    (level, currentPos, relativePos, currentState) -> level.getBlockState(currentPos.below()).is(PoBlocks.RAW_POOP_BLOCK.get()) && level.getBlockState(relativePos).is(Blocks.BLUE_ICE),
                    Blocks.ANDESITE.defaultBlockState()));
            FluidInteractionRegistry.addInteraction(ForgeMod.LAVA_TYPE.get(), new FluidInteractionRegistry.InteractionInformation(
                    (level, currentPos, relativePos, currentState) -> level.getBlockState(currentPos.below()).is(PoBlocks.RAW_SAPLING_POOP_BLOCK.get()) && level.getBlockState(relativePos).is(Blocks.BLUE_ICE),
                    Blocks.GRANITE.defaultBlockState()));
            FluidInteractionRegistry.addInteraction(ForgeMod.LAVA_TYPE.get(), new FluidInteractionRegistry.InteractionInformation(
                    (level, currentPos, relativePos, currentState) -> level.getBlockState(currentPos.below()).is(PoBlocks.RAW_SEA_POOP_BLOCK.get()) && level.getBlockState(relativePos).is(Blocks.BLUE_ICE),
                    Blocks.PRISMARINE.defaultBlockState()));
            FluidInteractionRegistry.addInteraction(ForgeMod.LAVA_TYPE.get(), new FluidInteractionRegistry.InteractionInformation(
                    (level, currentPos, relativePos, currentState) -> level.getBlockState(currentPos.below()).is(PoBlocks.RAW_WITHER_POOP_BLOCK.get()) && level.getBlockState(relativePos).is(Blocks.BLUE_ICE),
                    Blocks.BLACKSTONE.defaultBlockState()));
        });
        PoStats.init();
    }

    private void reload(final AddReloadListenerEvent event) {
        event.addListener(FlyTypeManager.INSTANCE);
        event.addListener(ToiletTypeManager.INSTANCE);
    }

    public static ResourceLocation loc(String path) {
        return modloc(PoopSky.MOD_ID, path);
    }

    public static ResourceLocation modloc(String namespace, String path) {
        return new ResourceLocation(namespace, path);
    }

    public static ResourceLocation mcloc(String path) {
        return new ResourceLocation("minecraft", path);
    }

    public static ResourceLocation parse(String location) {
        return new ResourceLocation(location);
    }

    public static ResourceLocation tryParse(String location) {
        return ResourceLocation.tryParse(location);
    }

    public static String getItemPath(Item item) {
        return BuiltInRegistries.ITEM.getKey(item).getPath();
    }

    public static String getBlockPath(Block block) {
        return getBlockKey(block).getPath();
    }

    public static ResourceLocation getBlockKey(Block block) {
        return BuiltInRegistries.BLOCK.getKey(block);
    }

    public static PoRegistrate registrate() {
        return REGISTRATE;
    }
}
