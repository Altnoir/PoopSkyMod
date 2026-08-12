package com.altnoir.poopsky;

import com.altnoir.poopsky.compat.PoMods;
import com.altnoir.poopsky.compat.create.CreatePlugin;
import com.altnoir.poopsky.content.block.abs.AbstractToiletBlock;
import com.altnoir.poopsky.content.block.p.CompooperBlock;
import com.altnoir.poopsky.content.entity.p.PoopTntEntity;
import com.altnoir.poopsky.content.item.p.JinKeLaItem;
import com.altnoir.poopsky.content.villager.PoVillagers;
import com.altnoir.poopsky.data.*;
import com.altnoir.poopsky.data.lang.LangGen;
import com.altnoir.poopsky.impl.event.PoGameEvents;
import com.altnoir.poopsky.impl.event.PoModEvents;
import com.altnoir.poopsky.impl.registrate.PoRegistrate;
import com.altnoir.poopsky.init.*;
import com.altnoir.poopsky.worldgen.PoChunkGenerators;
import com.altnoir.poopsky.worldgen.PoStructures;
import com.altnoir.poopsky.worldgen.foliage.PoFoliagePlacerTypes;
import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.core.dispenser.OptionalDispenseItemBehavior;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
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

        PoItems.register();
        PoBlocks.register();
        PoFluids.register();
        PoItemGroups.register();
        PoMenuTypes.register();

        PoEffects.register();
        PoPotions.register();
        PoRecipes.register(modEventBus);
        PoComponents.register(modEventBus);

        PoEntityType.register();
        PoBlockEntityType.register();
        EntityLootTableGen.register();
        FishingLootGen.register();
        GlobalLootModifierGen.register();
        PoLootFunctions.register(modEventBus);
        PoLootConditions.register(modEventBus);
        PoVillagers.register();

        PoParticles.register();
        ParticleGen.register();
        AdvancementGen.register();
        PoSoundEvents.register();
        PoStats.register();

        ItemTagGen.register();
        BlockTagGen.register();
        EntityTypeTagsGen.register();
        FluidTagsGen.register();
        DataMapGen.register();
        LangGen.register();

        PoFoliagePlacerTypes.register();
        PoStructures.register();
        PoChunkGenerators.register();

        var gameEventBus = NeoForge.EVENT_BUS;
        PoModEvents.registerMod(modEventBus);
        PoGameEvents.registerGame(gameEventBus);
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);

        if (ModList.get().isLoaded(PoMods.CREATE.id())) {
            CreatePlugin.register(modEventBus);
        }
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            CompooperBlock.bootStrap();
            DispenserBlock.registerProjectileBehavior(PoItems.POOP_BALL);
            DispenserBlock.registerProjectileBehavior(PoItems.SEA_POOP_BALL);
            DispenserBlock.registerProjectileBehavior(PoItems.WITHER_POOP_BALL);
            DispenserBlock.registerBehavior(PoBlocks.SHIT.asItem(), ArmorItem.DISPENSE_ITEM_BEHAVIOR);
            DispenserBlock.registerBehavior(PoBlocks.CHILI_SHIT.asItem(), ArmorItem.DISPENSE_ITEM_BEHAVIOR);
            DispenserBlock.registerBehavior(PoBlocks.GOLDEN_SHIT.asItem(), ArmorItem.DISPENSE_ITEM_BEHAVIOR);
            DispenserBlock.registerBehavior(PoItems.POOP.get(), new OptionalDispenseItemBehavior() {
                @Override
                protected ItemStack execute(BlockSource blockSource, ItemStack item) {
                    this.setSuccess(true);
                    Level level = blockSource.level();
                    BlockPos blockpos = blockSource.pos().relative(blockSource.state().getValue(DispenserBlock.FACING));

                    if (!BoneMealItem.applyBonemeal(item, level, blockpos, null) && !BoneMealItem.growWaterPlant(item, level, blockpos, null)) {
                        this.setSuccess(false);
                    } else if (!level.isClientSide()) {
                        level.levelEvent(1505, blockpos, 15);
                    }
                    return item;
                }
            });
            DispenserBlock.registerBehavior(PoItems.JINKELA.get(), new OptionalDispenseItemBehavior() {
                @Override
                protected ItemStack execute(BlockSource blockSource, ItemStack item) {
                    boolean success = false;
                    if (blockSource.level() instanceof ServerLevel serverLevel) {
                        BlockPos blockpos = blockSource.pos().relative(blockSource.state().getValue(DispenserBlock.FACING));
                        success = JinKeLaItem.tryApplyToBlock(serverLevel, blockpos, serverLevel.getBlockState(blockpos));
                    }
                    this.setSuccess(success);
                    if (success) {
                        item.shrink(1);
                    }
                    return item;
                }
            });
            DispenserBlock.registerBehavior(PoBlocks.POOP_TNT.asItem(), new DefaultDispenseItemBehavior() {
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
            DispenseItemBehavior dispenseitembehavior1 = new DefaultDispenseItemBehavior() {
                private final DefaultDispenseItemBehavior defaultDispenseItemBehavior = new DefaultDispenseItemBehavior();

                @Override
                public ItemStack execute(BlockSource p_338850_, ItemStack p_338251_) {
                    DispensibleContainerItem dispensiblecontaineritem = (DispensibleContainerItem) p_338251_.getItem();
                    BlockPos blockpos = p_338850_.pos().relative(p_338850_.state().getValue(DispenserBlock.FACING));
                    Level level = p_338850_.level();
                    if (dispensiblecontaineritem.emptyContents(null, level, blockpos, null, p_338251_)) {
                        dispensiblecontaineritem.checkExtraContent(null, level, p_338251_, blockpos);
                        return this.consumeWithRemainder(p_338850_, p_338251_, new ItemStack(Items.BUCKET));
                    } else {
                        return this.defaultDispenseItemBehavior.dispense(p_338850_, p_338251_);
                    }
                }
            };
            DispenserBlock.registerBehavior(PoItems.URINE_BUCKET.get(), dispenseitembehavior1);

            AbstractToiletBlock.dispenserToiletExplosion(Items.FLINT_AND_STEEL, (toilet, level, pos, stack) -> {
                stack.hurtAndBreak(1, level, null, p -> {
                });
            });
            AbstractToiletBlock.dispenserToiletExplosion(Items.FIRE_CHARGE, (toilet, level, pos, stack) -> {
                stack.shrink(1);
            });

            FluidInteractionRegistry.addInteraction(NeoForgeMod.WATER_TYPE.value(), new FluidInteractionRegistry.InteractionInformation(
                    PFluidTypes.URINE_FLUID_TYPE.get(), (fluidState) -> fluidState.isSource() ? PoBlocks.POOLIME_BLOCK.get().defaultBlockState() : Blocks.CLAY.defaultBlockState()));
            FluidInteractionRegistry.addInteraction(PFluidTypes.URINE_FLUID_TYPE.get(), new FluidInteractionRegistry.InteractionInformation(
                    NeoForgeMod.WATER_TYPE.value(), (fluidState) -> fluidState.isSource() ? Blocks.COARSE_DIRT.defaultBlockState() : Blocks.CLAY.defaultBlockState()));

            FluidInteractionRegistry.addInteraction(PFluidTypes.URINE_FLUID_TYPE.get(), new FluidInteractionRegistry.InteractionInformation(
                    NeoForgeMod.LAVA_TYPE.value(), (fluidState) -> fluidState.isSource() ? Blocks.MAGMA_BLOCK.defaultBlockState() : Blocks.NETHERRACK.defaultBlockState()));
            FluidInteractionRegistry.addInteraction(NeoForgeMod.LAVA_TYPE.value(), new FluidInteractionRegistry.InteractionInformation(
                    PFluidTypes.URINE_FLUID_TYPE.get(), (fluidState) -> fluidState.isSource() ? Blocks.OBSIDIAN.defaultBlockState() : Blocks.NETHERRACK.defaultBlockState()));

            FluidInteractionRegistry.addInteraction(NeoForgeMod.LAVA_TYPE.value(), new FluidInteractionRegistry.InteractionInformation(
                    (level, currentPos, relativePos, currentState) -> level.getBlockState(currentPos.below()).is(PoBlocks.POOP_BLOCK.get()) && level.getBlockState(relativePos).is(Blocks.BLUE_ICE),
                    Blocks.DRIPSTONE_BLOCK.defaultBlockState()));
            FluidInteractionRegistry.addInteraction(NeoForgeMod.LAVA_TYPE.value(), new FluidInteractionRegistry.InteractionInformation(
                    (level, currentPos, relativePos, currentState) -> level.getBlockState(currentPos.below()).is(PoBlocks.DRIED_POOP_BLOCK.get()) && level.getBlockState(relativePos).is(Blocks.BLUE_ICE),
                    Blocks.DEEPSLATE.defaultBlockState()));
            FluidInteractionRegistry.addInteraction(NeoForgeMod.LAVA_TYPE.value(), new FluidInteractionRegistry.InteractionInformation(
                    (level, currentPos, relativePos, currentState) -> level.getBlockState(currentPos.below()).is(PoBlocks.DRIED_CHILI_POOP_BLOCK.get()) && level.getBlockState(relativePos).is(Blocks.BLUE_ICE),
                    Blocks.NETHERRACK.defaultBlockState()));
            FluidInteractionRegistry.addInteraction(NeoForgeMod.LAVA_TYPE.value(), new FluidInteractionRegistry.InteractionInformation(
                    (level, currentPos, relativePos, currentState) -> level.getBlockState(currentPos.below()).is(PoBlocks.DRIED_GOLDEN_POOP_BLOCK.get()) && level.getBlockState(relativePos).is(Blocks.BLUE_ICE),
                    Blocks.END_STONE.defaultBlockState()));

            FluidInteractionRegistry.addInteraction(NeoForgeMod.LAVA_TYPE.value(), new FluidInteractionRegistry.InteractionInformation(
                    (level, currentPos, relativePos, currentState) -> level.getBlockState(currentPos.below()).is(PoBlocks.RAW_POOP_BLOCK.get()) && level.getBlockState(relativePos).is(Blocks.BLUE_ICE),
                    Blocks.ANDESITE.defaultBlockState()));
            FluidInteractionRegistry.addInteraction(NeoForgeMod.LAVA_TYPE.value(), new FluidInteractionRegistry.InteractionInformation(
                    (level, currentPos, relativePos, currentState) -> level.getBlockState(currentPos.below()).is(PoBlocks.RAW_SAPLING_POOP_BLOCK.get()) && level.getBlockState(relativePos).is(Blocks.BLUE_ICE),
                    Blocks.GRANITE.defaultBlockState()));
            FluidInteractionRegistry.addInteraction(NeoForgeMod.LAVA_TYPE.value(), new FluidInteractionRegistry.InteractionInformation(
                    (level, currentPos, relativePos, currentState) -> level.getBlockState(currentPos.below()).is(PoBlocks.RAW_SEA_POOP_BLOCK.get()) && level.getBlockState(relativePos).is(Blocks.BLUE_ICE),
                    Blocks.PRISMARINE.defaultBlockState()));
            FluidInteractionRegistry.addInteraction(NeoForgeMod.LAVA_TYPE.value(), new FluidInteractionRegistry.InteractionInformation(
                    (level, currentPos, relativePos, currentState) -> level.getBlockState(currentPos.below()).is(PoBlocks.RAW_WITHER_POOP_BLOCK.get()) && level.getBlockState(relativePos).is(Blocks.BLUE_ICE),
                    Blocks.BLACKSTONE.defaultBlockState()));
        });
        PoStats.init();
    }

    private void reload(final AddReloadListenerEvent event) {
        // 用于/Reload命令, 比如fly
    }

    public static Identifier loc(String path) {
        return modloc(PoopSky.MOD_ID, path);
    }

    public static Identifier modloc(String namespace, String path) {
        return Identifier.fromNamespaceAndPath(namespace, path);
    }

    public static Identifier mcloc(String path) {
        return Identifier.withDefaultNamespace(path);
    }

    public static String getItemPath(Item item) {
        return getItemKey(item).getPath();
    }

    public static String getItemNameSpace(Item item) {
        return getItemKey(item).getNamespace();
    }

    public static Identifier getItemKey(Item item) {
        return BuiltInRegistries.ITEM.getKey(item);
    }

    public static String getBlockPath(Block block) {
        return getBlockKey(block).getPath();
    }

    public static String getBlockNameSpace(Block block) {
        return getBlockKey(block).getNamespace();
    }

    public static Identifier getBlockKey(Block block) {
        return BuiltInRegistries.BLOCK.getKey(block);
    }

    public static PoRegistrate registrate() {
        return REGISTRATE;
    }
}
