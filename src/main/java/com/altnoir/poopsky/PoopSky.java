package com.altnoir.poopsky;

import com.altnoir.poopsky.compat.PoMods;
import com.altnoir.poopsky.compat.maid.MaidPlugin;
import com.altnoir.poopsky.content.block.abs.AbstractToiletBlock;
import com.altnoir.poopsky.content.block.p.CompooperBlock;
import com.altnoir.poopsky.content.entity.p.PoopTntEntity;
import com.altnoir.poopsky.content.villager.PoVillagers;
import com.altnoir.poopsky.data.*;
import com.altnoir.poopsky.data.entity.EntityLootTableGen;
import com.altnoir.poopsky.data.entity.EntityTypeTagsGen;
import com.altnoir.poopsky.data.lang.LangGen;
import com.altnoir.poopsky.data.sound.PoSoundEvents;
import com.altnoir.poopsky.fabric.PoFabricated;
import com.altnoir.poopsky.fabric.port.fluidhandler.FluidInteractionRegistry;
import com.altnoir.poopsky.impl.command.PoCommands;
import com.altnoir.poopsky.impl.event.PoGameEvents;
import com.altnoir.poopsky.impl.network.PoNetworking;
import com.altnoir.poopsky.impl.registrate.PoRegistrate;
import com.altnoir.poopsky.init.*;
import com.altnoir.poopsky.worldgen.PoChunkGenerators;
import com.altnoir.poopsky.worldgen.PoStructures;
import com.altnoir.poopsky.worldgen.foliage.PoFoliagePlacerTypes;
import com.mojang.logging.LogUtils;
import fuzs.forgeconfigapiport.fabric.api.neoforge.v4.NeoForgeConfigRegistry;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.dispenser.OptionalDispenseItemBehavior;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.fml.config.ModConfig;
import org.slf4j.Logger;

public class PoopSky implements ModInitializer {
    public static final String MOD_ID = "poopsky";
    public static final Logger LOGGER = LogUtils.getLogger();
    private static final PoRegistrate REGISTRATE = PoRegistrate.create(MOD_ID);

    static {
        REGISTRATE.defaultCreativeTab((ResourceKey<CreativeModeTab>) null);
    }

    @Override
    public void onInitialize() {
        PoItems.register();
        PoBlocks.register();
        PoFluids.register();
        PoItemGroups.register();
        PoMenuTypes.register();

        PoEffects.register();
        PoPotions.register();
        PoRecipes.register();
        PoComponents.register();

        PoEntityType.register();
        PoBlockEntityType.register();
        EntityLootTableGen.register();
        FishingLootGen.register();
        GlobalLootModifierGen.register();
        PoLootFunctions.register();
        PoVillagers.register();

        PoParticles.register();
        AdvancementGen.register();
        PoSoundEvents.register();
        PoStats.register();

        ItemTagGen.register();
        BlockTagGen.register();
        EntityTypeTagsGen.register();
        FluidTagsGen.register();
        LangGen.register();

        PoFoliagePlacerTypes.register();
        PoStructures.register();
        PoChunkGenerators.register();

        REGISTRATE.register();
        DataMapGen.register();

        commonSetup();

        PoNetworking.registerNetworking();
        PoCommands.register();

        PoFabricated.init();
        PoGameEvents.registerGame();

        if (FabricLoader.getInstance().isModLoaded(PoMods.TOUHOU_LITTLE_MAID.id())) {
            MaidPlugin.registry();
        }
        FabricLoader.getInstance().getModContainer(MOD_ID).ifPresent(container ->
                ResourceManagerHelper.registerBuiltinResourcePack(
                        loc("poopsky_pack"), container, Component.translatable("pack.poopsky.name"),
                        ResourcePackActivationType.NORMAL));
        Config.onLoad();
        NeoForgeConfigRegistry.INSTANCE.register(MOD_ID, ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup() {
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

                if (!BoneMealItem.growCrop(item, level, blockpos) && !BoneMealItem.growWaterPlant(item, level, blockpos, null)) {
                    this.setSuccess(false);
                } else if (!level.isClientSide) {
                    level.levelEvent(1505, blockpos, 15);
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
        DispenserBlock.registerBehavior(PoItems.URINE_BUCKET.get(), new DefaultDispenseItemBehavior() {
            private final DefaultDispenseItemBehavior fallback = new DefaultDispenseItemBehavior();

            @Override
            public ItemStack execute(BlockSource source, ItemStack stack) {
                DispensibleContainerItem container = (DispensibleContainerItem) stack.getItem();
                BlockPos pos = source.pos().relative(source.state().getValue(DispenserBlock.FACING));
                Level level = source.level();
                if (container.emptyContents(null, level, pos, null)) {
                    container.checkExtraContent(null, level, stack, pos);
                    return this.consumeWithRemainder(source, stack, new ItemStack(Items.BUCKET));
                }
                return this.fallback.dispense(source, stack);
            }
        });

        AbstractToiletBlock.dispenserToiletExplosion(Items.FLINT_AND_STEEL, (toilet, level, pos, stack) -> {
            stack.hurtAndBreak(1, level, null, p -> {
            });
        });
        AbstractToiletBlock.dispenserToiletExplosion(Items.FIRE_CHARGE, (toilet, level, pos, stack) -> {
            stack.shrink(1);
        });

        FluidInteractionRegistry.addInteraction(Fluids.WATER, new FluidInteractionRegistry.InteractionInformation(
                PoFluids.URINE.get(),
                fluidState -> fluidState.isSource()
                        ? PoBlocks.POOLIME_BLOCK.get().defaultBlockState()
                        : Blocks.CLAY.defaultBlockState()));
        FluidInteractionRegistry.addInteraction(PoFluids.URINE.get(), new FluidInteractionRegistry.InteractionInformation(
                Fluids.WATER,
                fluidState -> fluidState.isSource()
                        ? Blocks.COARSE_DIRT.defaultBlockState()
                        : Blocks.CLAY.defaultBlockState()));
        FluidInteractionRegistry.addInteraction(PoFluids.URINE.get(), new FluidInteractionRegistry.InteractionInformation(
                Fluids.LAVA,
                fluidState -> fluidState.isSource()
                        ? Blocks.MAGMA_BLOCK.defaultBlockState()
                        : Blocks.NETHERRACK.defaultBlockState()));
        FluidInteractionRegistry.addInteraction(Fluids.LAVA, new FluidInteractionRegistry.InteractionInformation(
                PoFluids.URINE.get(),
                fluidState -> fluidState.isSource()
                        ? Blocks.OBSIDIAN.defaultBlockState()
                        : Blocks.MAGMA_BLOCK.defaultBlockState()));
        FluidInteractionRegistry.addInteraction(Fluids.LAVA, new FluidInteractionRegistry.InteractionInformation(
                (level, currentPos, relativePos, currentState) ->
                        level.getBlockState(currentPos.below()).is(PoBlocks.POOP_BLOCK.get())
                                && level.getBlockState(relativePos).is(Blocks.BLUE_ICE),
                Blocks.DEEPSLATE.defaultBlockState()));

        PoStats.init();
    }

    public static ResourceLocation loc(String path) {
        return ResourceLocation.fromNamespaceAndPath(PoopSky.MOD_ID, path);
    }

    public static ResourceLocation locMc(String path) {
        return ResourceLocation.withDefaultNamespace(path);
    }

    public static PoRegistrate registrate() {
        return REGISTRATE;
    }

}
