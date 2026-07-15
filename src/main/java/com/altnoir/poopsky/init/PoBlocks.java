package com.altnoir.poopsky.init;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.content.SetToiletTypeFunction;
import com.altnoir.poopsky.content.block.p.*;
import com.altnoir.poopsky.content.item.p.CompooperBlockItem;
import com.altnoir.poopsky.content.item.p.ToiletBlockItem;
import com.altnoir.poopsky.impl.registrate.PoRegistrate;
import com.altnoir.poopsky.impl.type.PoBlockSetType;
import com.altnoir.poopsky.impl.type.PoWoodType;
import com.tterrag.registrate.providers.loot.RegistrateBlockLootTables;
import com.tterrag.registrate.util.entry.BlockEntry;
import com.tterrag.registrate.util.nullness.NonNullBiConsumer;
import com.tterrag.registrate.util.nullness.NonNullFunction;
import net.minecraft.advancements.critereon.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.AlternativesEntry;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer;
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.ExplosionCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.MatchTool;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.ToIntFunction;
import java.util.stream.IntStream;

public class PoBlocks {
    private static final float POOP = 0.5F;
    private static final float HARDEN = 1.5F;
    private static final float LOG = 2.0F;
    private static final float WOODEN_STRENGTH = 4.0F;
    private static final float HARD_STRENGTH = 10.0F;
    private static final float TOILET_RESISTANCE = 1200.0F;
    private static final int LAVA_LIGHT_LEVEL = 15;
    private static final PoRegistrate REGISTRATE = PoopSky.registrate();

    public static final BlockEntry<PoopCakeBlock> POOP_CAKE = registerBlock("poop_cake",
            props -> new PoopCakeBlock(poopCakeProperties()),
            (loot, block) -> loot.add(block, BlockLootSubProvider.noDrop()));

    private static final Block[] POOP_CAKE_CANDLES = {
            Blocks.CANDLE,
            Blocks.WHITE_CANDLE,
            Blocks.ORANGE_CANDLE,
            Blocks.MAGENTA_CANDLE,
            Blocks.LIGHT_BLUE_CANDLE,
            Blocks.YELLOW_CANDLE,
            Blocks.LIME_CANDLE,
            Blocks.PINK_CANDLE,
            Blocks.GRAY_CANDLE,
            Blocks.LIGHT_GRAY_CANDLE,
            Blocks.CYAN_CANDLE,
            Blocks.PURPLE_CANDLE,
            Blocks.BLUE_CANDLE,
            Blocks.BROWN_CANDLE,
            Blocks.GREEN_CANDLE,
            Blocks.RED_CANDLE,
            Blocks.BLACK_CANDLE
    };

    private static final Map<Block, BlockEntry<PoopCandleCakeBlock>> POOP_CANDLE_CAKES = registerPoopCandleCakes();

    public static final BlockEntry<PoopPieceBlock> POOP_PIECE = registerBlock("poop_piece",
            props -> new PoopPieceBlock(poopProperties(0.1F)
                    .replaceable()
                    .randomTicks()
                    .requiresCorrectToolForDrops()
                    .isViewBlocking((state, getter, pos) -> state.getValue(PoopPieceBlock.LAYERS) >= 8)
                    .pushReaction(PushReaction.DESTROY)),
            (loot, block) -> loot.add(block, createPoopPieceDrop(loot, block, PoItems.POOP_BALL.get())));
    public static final BlockEntry<PoopBlock> POOP_BLOCK = registerBlock("poop_block",
            props -> new PoopBlock(poopProperties()
                    .randomTicks()
                    .speedFactor(0.4F)
                    .isValidSpawn(Blocks::always)
                    .isRedstoneConductor(PoBlocks::always)
                    .isSuffocating(PoBlocks::always)
                    .instrument(NoteBlockInstrument.COW_BELL)));
    public static final BlockEntry<PoopFarmlandBlock> POOP_FARMLAND = registerBlock("poop_farmland",
            props -> new PoopFarmlandBlock(BlockBehaviour.Properties.ofFullCopy(POOP_BLOCK.get())
                    .randomTicks()),
            (loot, block) -> loot.add(block, LootTable.lootTable()
                    .withPool(LootPool.lootPool()
                            .setRolls(ConstantValue.exactly(1.0F))
                            .when(ExplosionCondition.survivesExplosion())
                            .add(LootItem.lootTableItem(POOP_BLOCK.get())))));
    public static final BlockEntry<PoolimeMaggotsBlock> POOLIME_MAGGOTS_BLOCK = registerBlock("poolime_maggots_block",
            props -> new PoolimeMaggotsBlock(poopProperties(1.0F)
                    .randomTicks()
                    .speedFactor(0.4F)
                    .isValidSpawn(Blocks::always)
                    .instrument(NoteBlockInstrument.COW_BELL)));
    public static final BlockEntry<StairBlock> POOP_STAIRS = registerBlock("poop_stairs",
            props -> new StairBlock(POOP_BLOCK.get().defaultBlockState(), poopProperties()));

    public static final BlockEntry<SlabBlock> POOP_SLAB = registerBlock("poop_slab",
            props -> new SlabBlock(poopProperties()));
    public static final BlockEntry<VerticalSlabBlock> POOP_VERTICAL_SLAB = registerBlock("poop_vertical_slab",
            props -> new VerticalSlabBlock(poopProperties()));
    public static final BlockEntry<ButtonBlock> POOP_BUTTON = registerBlock("poop_button",
            props -> new ButtonBlock(PoBlockSetType.POOP, 200, poopProperties().noCollission()));
    public static final BlockEntry<PressurePlateBlock> POOP_PRESSURE_PLATE = registerBlock("poop_pressure_plate",
            props -> new PressurePlateBlock(PoBlockSetType.POOP, poopProperties().noCollission()));
    public static final BlockEntry<FenceBlock> POOP_FENCE = registerBlock("poop_fence",
            props -> new FenceBlock(poopProperties()));
    public static final BlockEntry<FenceGateBlock> POOP_FENCE_GATE = registerBlock("poop_fence_gate",
            props -> new FenceGateBlock(PoWoodType.POOP, poopProperties()));
    public static final BlockEntry<WallBlock> POOP_WALL = registerBlock("poop_wall",
            props -> new WallBlock(poopProperties()));
    public static final BlockFamily POOP_FAMILY = new BlockFamily(POOP_BLOCK, POOP_STAIRS, POOP_SLAB, POOP_VERTICAL_SLAB, POOP_WALL);

    public static final BlockEntry<DoorBlock> POOP_DOOR = registerBlock("poop_door",
            props -> new DoorBlock(PoBlockSetType.POOP, poopProperties()
                    .noOcclusion()
                    .pushReaction(PushReaction.DESTROY)),
            (loot, block) -> loot.add(block, loot.createDoorTable(block)));
    public static final BlockEntry<TrapDoorBlock> POOP_TRAPDOOR = registerBlock("poop_trapdoor",
            props -> new TrapDoorBlock(PoBlockSetType.POOP, poopProperties()
                    .noOcclusion()
                    .isValidSpawn(Blocks::never)));

    public static final BlockEntry<ChairBlock> STOOL = registerBlock("stool",
            props -> new ChairBlock(poopProperties()
                    .pushReaction(PushReaction.DESTROY)
                    .noOcclusion()));
    public static final BlockEntry<PoolimeBlock> POOLIME_BLOCK = registerBlock("poolime_block",
            props -> new PoolimeBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BROWN)
                    .friction(0.8F)
                    .sound(SoundType.SLIME_BLOCK)
                    .noOcclusion()));

    public static final BlockEntry<Block> POOP_BRICKS = registerBlock("poop_bricks",
            props -> new Block(hardenedProperties(MapColor.COLOR_BROWN, SoundType.FROGLIGHT)));
    public static final BlockEntry<Block> CRACKED_POOP_BRICKS = registerBlock("cracked_poop_bricks",
            props -> new Block(BlockBehaviour.Properties.ofFullCopy(POOP_BRICKS.get())));
    public static final BlockFamily POOP_BRICK_FAMILY = registerBlockFamily("poop_brick", POOP_BRICKS, false);
    public static final BlockEntry<StairBlock> POOP_BRICK_STAIRS = POOP_BRICK_FAMILY.stairs();
    public static final BlockEntry<SlabBlock> POOP_BRICK_SLAB = POOP_BRICK_FAMILY.slab();
    public static final BlockEntry<VerticalSlabBlock> POOP_BRICK_VERTICAL_SLAB = POOP_BRICK_FAMILY.verticalSlab();
    public static final BlockEntry<WallBlock> POOP_BRICK_WALL = POOP_BRICK_FAMILY.wall();

    public static final BlockEntry<Block> MOSSY_POOP_BRICKS = registerBlock("mossy_poop_bricks",
            props -> new Block(hardenedProperties(MapColor.COLOR_GREEN, SoundType.FROGLIGHT)));
    public static final BlockFamily MOSSY_POOP_BRICK_FAMILY = registerBlockFamily("mossy_poop_brick", MOSSY_POOP_BRICKS, false);
    public static final BlockEntry<StairBlock> MOSSY_POOP_BRICK_STAIRS = MOSSY_POOP_BRICK_FAMILY.stairs();
    public static final BlockEntry<SlabBlock> MOSSY_POOP_BRICK_SLAB = MOSSY_POOP_BRICK_FAMILY.slab();
    public static final BlockEntry<VerticalSlabBlock> MOSSY_POOP_BRICK_VERTICAL_SLAB = MOSSY_POOP_BRICK_FAMILY.verticalSlab();
    public static final BlockEntry<WallBlock> MOSSY_POOP_BRICK_WALL = MOSSY_POOP_BRICK_FAMILY.wall();

    public static final BlockEntry<ChiliPoopBlock> CHILI_POOP_BLOCK = registerBlock("chili_poop_block",
            props -> new ChiliPoopBlock(poopProperties()
                    .requiresCorrectToolForDrops()
                    .speedFactor(0.4F)
                    .isValidSpawn(Blocks::always)
                    .isRedstoneConductor(PoBlocks::always)
                    .isSuffocating(PoBlocks::always)
                    .instrument(NoteBlockInstrument.COW_BELL)));
    public static final BlockFamily CHILI_POOP_FAMILY = registerBlockFamily("chili_poop", CHILI_POOP_BLOCK, false);
    public static final BlockEntry<StairBlock> CHILI_POOP_STAIRS = CHILI_POOP_FAMILY.stairs();
    public static final BlockEntry<SlabBlock> CHILI_POOP_SLAB = CHILI_POOP_FAMILY.slab();
    public static final BlockEntry<VerticalSlabBlock> CHILI_POOP_VERTICAL_SLAB = CHILI_POOP_FAMILY.verticalSlab();
    public static final BlockEntry<WallBlock> CHILI_POOP_WALL = CHILI_POOP_FAMILY.wall();
    public static final BlockEntry<GoldenPoopBlock> GOLDEN_POOP_BLOCK = registerBlock("golden_poop_block",
            props -> new GoldenPoopBlock(simpleProperties(MapColor.GOLD, 0.65F, SoundType.MUD)
                    .requiresCorrectToolForDrops()
                    .speedFactor(0.4F)
                    .isValidSpawn(Blocks::always)
                    .isRedstoneConductor(PoBlocks::always)
                    .isSuffocating(PoBlocks::always)
                    .instrument(NoteBlockInstrument.BELL)));
    public static final BlockFamily GOLDEN_POOP_FAMILY = registerBlockFamily("golden_poop", GOLDEN_POOP_BLOCK, false);
    public static final BlockEntry<StairBlock> GOLDEN_POOP_STAIRS = GOLDEN_POOP_FAMILY.stairs();
    public static final BlockEntry<SlabBlock> GOLDEN_POOP_SLAB = GOLDEN_POOP_FAMILY.slab();
    public static final BlockEntry<VerticalSlabBlock> GOLDEN_POOP_VERTICAL_SLAB = GOLDEN_POOP_FAMILY.verticalSlab();
    public static final BlockEntry<WallBlock> GOLDEN_POOP_WALL = GOLDEN_POOP_FAMILY.wall();

    public static final BlockEntry<DriedPoopBlock> DRIED_POOP_BLOCK = registerBlock("dried_poop_block",
            props -> new DriedPoopBlock(hardenedProperties(MapColor.COLOR_ORANGE, SoundType.TUFF)
                    .instrument(NoteBlockInstrument.COW_BELL)));
    public static final BlockFamily DRIED_POOP_BLOCK_FAMILY = registerBlockFamily("dried_poop_block", DRIED_POOP_BLOCK, false);
    public static final BlockEntry<StairBlock> DRIED_POOP_BLOCK_STAIRS = DRIED_POOP_BLOCK_FAMILY.stairs();
    public static final BlockEntry<SlabBlock> DRIED_POOP_BLOCK_SLAB = DRIED_POOP_BLOCK_FAMILY.slab();
    public static final BlockEntry<VerticalSlabBlock> DRIED_POOP_BLOCK_VERTICAL_SLAB = DRIED_POOP_BLOCK_FAMILY.verticalSlab();
    public static final BlockEntry<WallBlock> DRIED_POOP_BLOCK_WALL = DRIED_POOP_BLOCK_FAMILY.wall();

    public static final BlockEntry<Block> SMOOTH_POOP_BLOCK = registerBlock("smooth_poop_block",
            props -> new Block(hardenedProperties(MapColor.COLOR_ORANGE, SoundType.CALCITE)));
    public static final BlockFamily SMOOTH_POOP_BLOCK_FAMILY = registerBlockFamily("smooth_poop_block", SMOOTH_POOP_BLOCK, false);
    public static final BlockEntry<StairBlock> SMOOTH_POOP_BLOCK_STAIRS = SMOOTH_POOP_BLOCK_FAMILY.stairs();
    public static final BlockEntry<SlabBlock> SMOOTH_POOP_BLOCK_SLAB = SMOOTH_POOP_BLOCK_FAMILY.slab();
    public static final BlockEntry<VerticalSlabBlock> SMOOTH_POOP_BLOCK_VERTICAL_SLAB = SMOOTH_POOP_BLOCK_FAMILY.verticalSlab();
    public static final BlockEntry<WallBlock> SMOOTH_POOP_BLOCK_WALL = SMOOTH_POOP_BLOCK_FAMILY.wall();

    public static final BlockEntry<Block> CUT_POOP_BLOCK = registerBlock("cut_poop_block",
            props -> new Block(hardenedProperties(MapColor.COLOR_ORANGE, SoundType.POLISHED_TUFF)));
    public static final BlockFamily CUT_POOP_BLOCK_FAMILY = registerBlockFamily("cut_poop_block", CUT_POOP_BLOCK, false);
    public static final BlockEntry<StairBlock> CUT_POOP_BLOCK_STAIRS = CUT_POOP_BLOCK_FAMILY.stairs();
    public static final BlockEntry<SlabBlock> CUT_POOP_BLOCK_SLAB = CUT_POOP_BLOCK_FAMILY.slab();
    public static final BlockEntry<VerticalSlabBlock> CUT_POOP_BLOCK_VERTICAL_SLAB = CUT_POOP_BLOCK_FAMILY.verticalSlab();
    public static final BlockEntry<WallBlock> CUT_POOP_BLOCK_WALL = CUT_POOP_BLOCK_FAMILY.wall();

    public static final BlockEntry<Block> TILE_BLOCK = registerDefaultBlock("tile_block",
            props -> new Block(hardenedProperties(MapColor.COLOR_LIGHT_BLUE, SoundType.STONE)));
    public static final BlockFamily TILE_BLOCK_FAMILY = registerBlockFamily("tile_block", TILE_BLOCK, true);
    public static final BlockEntry<StairBlock> TILE_BLOCK_STAIRS = TILE_BLOCK_FAMILY.stairs();
    public static final BlockEntry<SlabBlock> TILE_BLOCK_SLAB = TILE_BLOCK_FAMILY.slab();
    public static final BlockEntry<VerticalSlabBlock> TILE_BLOCK_VERTICAL_SLAB = TILE_BLOCK_FAMILY.verticalSlab();
    public static final BlockEntry<WallBlock> TILE_BLOCK_WALL = TILE_BLOCK_FAMILY.wall();

    public static final BlockEntry<CompooperBlock> COMPOOPER = registerCompooperBlock("compooper",
            props -> new CompooperBlock(simpleProperties(MapColor.COLOR_BROWN, 0.6F, SoundType.METAL)
                    .noOcclusion()
                    .instrument(NoteBlockInstrument.BASS)
                    .requiresCorrectToolForDrops()),
            (loot, block) -> loot.add(block, LootTable.lootTable()
                    .withPool(LootPool.lootPool()
                            .add(loot.applyExplosionDecay(block, LootItem.lootTableItem(block))))
                    .withPool(LootPool.lootPool()
                            .add(LootItem.lootTableItem(PoItems.SAPLING_POOP_BALL.get()))
                            .when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(block)
                                    .setProperties(StatePropertiesPredicate.Builder.properties()
                                            .hasProperty(CompooperBlock.POOP_LEVEL, CompooperBlock.READY))))));
    public static final BlockEntry<WaterCompooperBlock> WATER_COMPOOPER = registerDefaultBlock("water_compooper",
            props -> new WaterCompooperBlock(BlockBehaviour.Properties.ofFullCopy(COMPOOPER.get())),
            (loot, block) -> loot.dropOther(block, COMPOOPER.get()));
    public static final BlockEntry<LavaCompooperBlock> LAVA_COMPOOPER = registerDefaultBlock("lava_compooper",
            props -> new LavaCompooperBlock(BlockBehaviour.Properties.ofFullCopy(COMPOOPER.get()).lightLevel(state -> 15)),
            (loot, block) -> loot.dropOther(block, COMPOOPER.get()));
    public static final BlockEntry<PowderSnowCompooperBlock> POWDER_SNOW_COMPOOPER = registerDefaultBlock("powder_snow_compooper",
            props -> new PowderSnowCompooperBlock(BlockBehaviour.Properties.ofFullCopy(COMPOOPER.get())),
            (loot, block) -> loot.dropOther(block, COMPOOPER.get()));
    public static final BlockEntry<UrineCompooperBlock> URINE_COMPOOPER = registerDefaultBlock("urine_compooper",
            props -> new UrineCompooperBlock(BlockBehaviour.Properties.ofFullCopy(COMPOOPER.get()).randomTicks()),
            (loot, block) -> loot.add(block, LootTable.lootTable()
                    .withPool(LootPool.lootPool()
                            .add(loot.applyExplosionDecay(block, LootItem.lootTableItem(COMPOOPER.get()))))
                    .withPool(LootPool.lootPool()
                            .add(LootItem.lootTableItem(PoItems.MAGGOTS_SEEDS.get()))
                            .when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(block)
                                    .setProperties(StatePropertiesPredicate.Builder.properties()
                                            .hasProperty(UrineCompooperBlock.MAGGOTS, true))))));
    public static final BlockEntry<PlacerBlock> PLACER = registerDefaultBlock("placer",
            props -> new PlacerBlock(BlockBehaviour.Properties.of().mapColor(MapColor.STONE)
                    .instrument(NoteBlockInstrument.BASEDRUM)
                    .requiresCorrectToolForDrops().strength(3.5F)));
    public static final BlockEntry<SieveBlock> SIEVE = registerDefaultBlock("sieve_stable",
            props -> new SieveBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.STONE)
                    .strength(1.5f, 3.0f)
                    .requiresCorrectToolForDrops()
                    .isValidSpawn(Blocks::never)
                    .noOcclusion()));

    public static final BlockEntry<PoopTntBlock> POOP_TNT = registerBlock("poop_tnt",
            props -> new PoopTntBlock(simpleProperties(MapColor.FIRE, 0.0F, SoundType.GRASS)
                    .ignitedByLava()
                    .pushReaction(PushReaction.DESTROY)));
    public static final BlockEntry<FlyBarrelBlock> FLY_BARREL = registerDefaultBlock("fly_barrel",
            props -> new FlyBarrelBlock(simpleProperties(MapColor.COLOR_BROWN, 0.5F, SoundType.WOOD)
                    .noOcclusion()));
    public static final BlockEntry<BreedingChestBlock> BREEDING_CHEST = registerDefaultBlock("breeding_chest",
            props -> new BreedingChestBlock(simpleProperties(MapColor.COLOR_BROWN, 1.0F, SoundType.POLISHED_TUFF)
                    .requiresCorrectToolForDrops()
                    .noOcclusion()));

    public static final BlockEntry<Block> RAW_POOP_BLOCK = registerBlock("raw_poop_block",
            props -> new Block(simpleProperties(MapColor.COLOR_BROWN, 0.65F, SoundType.MUD)
                    .randomTicks()
                    .isValidSpawn(Blocks::always)
                    .instrument(NoteBlockInstrument.COW_BELL)));
    public static final BlockEntry<RawSaplingBlock> RAW_SAPLING_POOP_BLOCK = registerBlock("raw_sapling_poop_block",
            props -> new RawSaplingBlock(BlockBehaviour.Properties.ofFullCopy(RAW_POOP_BLOCK.get()).sound(SoundType.ROOTED_DIRT)));
    public static final BlockEntry<RawSeaBlock> RAW_SEA_POOP_BLOCK = registerBlock("raw_sea_poop_block",
            props -> new RawSeaBlock(BlockBehaviour.Properties.ofFullCopy(RAW_POOP_BLOCK.get()).sound(SoundType.ROOTED_DIRT)));
    public static final BlockEntry<RawWitherBlock> RAW_WITHER_POOP_BLOCK = registerBlock("raw_wither_poop_block",
            props -> new RawWitherBlock(BlockBehaviour.Properties.ofFullCopy(RAW_POOP_BLOCK.get()).sound(SoundType.ROOTED_DIRT)));

    public static final BlockEntry<PoopLogBlock> POOP_LOG = registerBlock("poop_log",
            props -> new PoopLogBlock(logProperties(SoundType.STEM).randomTicks()),
            (loot, block) -> loot.add(block, createSpallOreDrops(loot, block)));
    public static final BlockEntry<PoopEmptyLogBlock> POOP_EMPTY_LOG = registerBlock("poop_empty_log",
            props -> new PoopEmptyLogBlock(logProperties(SoundType.BAMBOO_WOOD).noOcclusion()));
    public static final BlockEntry<PoopLogBlock> STRIPPED_POOP_LOG = registerBlock("stripped_poop_log",
            props -> new PoopLogBlock(logProperties(SoundType.STEM).randomTicks()),
            (loot, block) -> loot.add(block, createSpallOreDrops(loot, block)));
    public static final BlockEntry<PoopEmptyLogBlock> STRIPPED_POOP_EMPTY_LOG = registerBlock("stripped_poop_empty_log",
            props -> new PoopEmptyLogBlock(logProperties(SoundType.BAMBOO_WOOD).noOcclusion()));

    public static final BlockEntry<PoopLeavesBlock> POOP_LEAVES = registerBlock("poop_leaves",
            props -> new PoopLeavesBlock(0x5E4228, leavesProperties(MapColor.COLOR_BROWN)),
            (loot, block) -> loot.add(block, createLeavesDrops(loot, block, PoItems.POOP.get())));
    public static final BlockEntry<PoopLeavesBlock> POOP_LEAVES_IRON = registerBlock("poop_leaves_iron",
            props -> new PoopLeavesBlock(0xFFFFFF, leavesProperties(MapColor.TERRACOTTA_WHITE)),
            (loot, block) -> loot.add(block, createIronLeavesDrops(loot, block)));
    public static final BlockEntry<PoopLeavesBlock> POOP_LEAVES_GOLD = registerBlock("poop_leaves_gold",
            props -> new PoopLeavesBlock(0xFFD700, leavesProperties(MapColor.COLOR_YELLOW)),
            (loot, block) -> loot.add(block, createGoldLeavesDrops(loot, block)));
    public static final BlockEntry<PoopTreeBlock> POOP_SAPLING = registerBlock("poop_sapling",
            props -> new PoopTreeBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BROWN)
                    .noCollission()
                    .noOcclusion()
                    .instabreak()
                    .sound(SoundType.MUD)
                    .offsetType(BlockBehaviour.OffsetType.XZ)
                    .pushReaction(PushReaction.DESTROY)));

    public static final BlockEntry<AmethystBlock> SALTPETER_BLOCK = registerBlock("saltpeter_block",
            props -> new AmethystBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_LIGHT_GRAY)
                    .strength(1.5F)
                    .sound(SoundType.AMETHYST)
                    .requiresCorrectToolForDrops()));
    public static final BlockEntry<SaltpeterClusterBlock> SALTPETER_CLUSTER = registerBlock("saltpeter_cluster",
            props -> new SaltpeterClusterBlock(7.0F, 3.0F, BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_LIGHT_GRAY)
                    .forceSolidOn()
                    .noOcclusion()
                    .sound(SoundType.AMETHYST_CLUSTER)
                    .strength(1.5F)
                    .lightLevel(p_152632_ -> 5)
                    .pushReaction(PushReaction.DESTROY)),
            (loot, block) -> loot.add(block, createSaltpeterClusterDrop(loot, block)));
    public static final BlockEntry<SaltpeterClusterBlock> LARGE_SALTPETER_BUD = registerBlock("large_saltpeter_bud",
            props -> new SaltpeterClusterBlock(5.0F, 3.0F, BlockBehaviour.Properties.ofFullCopy(SALTPETER_CLUSTER.get())
                    .sound(SoundType.MEDIUM_AMETHYST_BUD)
                    .lightLevel(p_152629_ -> 4)),
            RegistrateBlockLootTables::dropWhenSilkTouch);
    public static final BlockEntry<SaltpeterClusterBlock> MEDIUM_SALTPETER_BUD = registerBlock("medium_saltpeter_bud",
            props -> new SaltpeterClusterBlock(4.0F, 3.0F, BlockBehaviour.Properties.ofFullCopy(SALTPETER_CLUSTER.get())
                    .sound(SoundType.LARGE_AMETHYST_BUD)
                    .lightLevel(p_152617_ -> 2)),
            RegistrateBlockLootTables::dropWhenSilkTouch);
    public static final BlockEntry<SaltpeterClusterBlock> SMALL_SALTPETER_BUD = registerBlock("small_saltpeter_bud",
            props -> new SaltpeterClusterBlock(3.0F, 4.0F, BlockBehaviour.Properties.ofFullCopy(SALTPETER_CLUSTER.get())
                    .sound(SoundType.SMALL_AMETHYST_BUD)
                    .lightLevel(p_187409_ -> 1)),
            RegistrateBlockLootTables::dropWhenSilkTouch);

    public static final BlockEntry<? extends LiquidBlock> URINE_LIQUID = PoFluids.URINE_LIQUID;

    public static final BlockEntry<Block> MAGGOTS_BLOCK = registerBlock("maggots_block",
            props -> new Block(simpleProperties(MapColor.TERRACOTTA_WHITE, POOP, SoundType.WEEPING_VINES)));
    public static final BlockEntry<Block> ROUNDWORM_BLOCK = registerBlock("roundworm_block",
            props -> new Block(simpleProperties(MapColor.TERRACOTTA_WHITE, POOP, SoundType.TWISTING_VINES)));
    public static final BlockEntry<MaggotsBlock> MAGGOTS = registerBlockNoItem("maggots",
            props -> new MaggotsBlock(plantProperties(MapColor.COLOR_YELLOW, SoundType.CROP)
                    .noCollission()
                    .randomTicks()),
            (loot, block) -> {
                LootItemCondition.Builder grownCondition = LootItemBlockStatePropertyCondition.hasBlockStateProperties(block)
                        .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(CropBlock.AGE, CropBlock.MAX_AGE));
                var registrylookup = loot.getRegistries().lookupOrThrow(Registries.ENCHANTMENT);
                loot.add(block, loot.applyExplosionDecay(block,
                        LootTable.lootTable()
                                .withPool(LootPool.lootPool()
                                        .add(LootItem.lootTableItem(Items.WHEAT_SEEDS).when(grownCondition).otherwise(LootItem.lootTableItem(PoItems.MAGGOTS_SEEDS.get())))
                                        .add(LootItem.lootTableItem(Items.SWEET_BERRIES).when(grownCondition).otherwise(LootItem.lootTableItem(PoItems.MAGGOTS_SEEDS.get())))
                                        .add(LootItem.lootTableItem(Items.CARROT).when(grownCondition).otherwise(LootItem.lootTableItem(PoItems.MAGGOTS_SEEDS.get())))
                                        .add(LootItem.lootTableItem(Items.POTATO).when(grownCondition).otherwise(LootItem.lootTableItem(PoItems.MAGGOTS_SEEDS.get())))
                                )
                                .withPool(LootPool.lootPool()
                                        .when(grownCondition)
                                        .add(LootItem.lootTableItem(PoItems.MAGGOTS_SEEDS.get())
                                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 3.0F)))
                                                .apply(ApplyBonusCount.addBonusBinomialDistributionCount(registrylookup.getOrThrow(Enchantments.FORTUNE), 0.5714286F, 3)))
                                )
                ));
            });
    public static final BlockEntry<RoundwormVinesBlock> ROUNDWORM_VINES = registerBlockNoItem("roundworm_vines",
            props -> new RoundwormVinesBlock(
                    plantProperties(MapColor.TERRACOTTA_WHITE, SoundType.TWISTING_VINES)
                            .randomTicks()
                            .noCollission()),
            (loot, block) -> loot.dropOther(block, PoItems.ROUNDWORM.get()));
    public static final BlockEntry<RoundwormVinesPlantBlock> ROUNDWORM_VINES_PLANT = registerBlockNoItem("roundworm_vines_plant",
            props -> new RoundwormVinesPlantBlock(
                    plantProperties(MapColor.TERRACOTTA_WHITE, SoundType.TWISTING_VINES)
                            .noCollission()),
            (loot, block) -> {
                LootItemCondition.Builder seedsCondition = LootItemBlockStatePropertyCondition.hasBlockStateProperties(block)
                        .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(RoundwormVinesPlantBlock.SEEDS, true));
                var registrylookup = loot.getRegistries().lookupOrThrow(Registries.ENCHANTMENT);
                loot.add(block, LootTable.lootTable()
                        .withPool(LootPool.lootPool()
                                .add(LootItem.lootTableItem(Items.PUMPKIN_SEEDS).when(seedsCondition).otherwise(LootItem.lootTableItem(PoItems.ROUNDWORM.get())))
                                .add(LootItem.lootTableItem(Items.MELON_SEEDS).when(seedsCondition).otherwise(LootItem.lootTableItem(PoItems.ROUNDWORM.get())))
                                .add(LootItem.lootTableItem(Items.FROGSPAWN).when(seedsCondition).otherwise(LootItem.lootTableItem(PoItems.ROUNDWORM.get())))
                                .add(LootItem.lootTableItem(Items.BEETROOT_SEEDS).when(seedsCondition).otherwise(LootItem.lootTableItem(PoItems.ROUNDWORM.get())))
                        )
                        .withPool(LootPool.lootPool()
                                .when(seedsCondition)
                                .add(LootItem.lootTableItem(PoItems.ROUNDWORM.get())
                                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 3.0F)))
                                        .apply(ApplyBonusCount.addBonusBinomialDistributionCount(registrylookup.getOrThrow(Enchantments.FORTUNE), 0.5714286F, 3)))
                        )
                );
            });

    // Toilet
    public static final BlockEntry<ToiletBlock> WOODEN_TOILET = registerToiletBlock(
            "wooden_toilet",
            props -> new ToiletBlock(toiletProperties(MapColor.WOOD, WOODEN_STRENGTH, SoundType.WOOD, NoteBlockInstrument.BASS)
                    .randomTicks()
                    .ignitedByLava()),
            (loot, block) -> loot.add(block, createToiletDrop(block)));

    public static final BlockEntry<LavaToiletBlock> HARD_TOILET = registerToiletBlock(
            "hard_toilet",
            props -> new LavaToiletBlock(toiletProperties(MapColor.STONE, HARD_STRENGTH, SoundType.STONE, NoteBlockInstrument.BASEDRUM)
                    .lightLevel(lavaLightLevel())
                    .requiresCorrectToolForDrops()
                    .randomTicks()
                    .ignitedByLava()),
            (loot, block) -> loot.add(block, createToiletDrop(block)));

    public record BlockFamily(
            BlockEntry<? extends Block> block,
            BlockEntry<StairBlock> stairs,
            BlockEntry<SlabBlock> slab,
            BlockEntry<VerticalSlabBlock> verticalSlab,
            BlockEntry<WallBlock> wall
    ) {
        public List<BlockEntry<? extends Block>> blocks() {
            return List.of(block, stairs, slab, verticalSlab, wall);
        }
    }

    public static final List<BlockFamily> POOP_BUILDING_FAMILIES = List.of(POOP_FAMILY, CHILI_POOP_FAMILY, GOLDEN_POOP_FAMILY);
    public static final List<BlockFamily> HARDENED_POOP_FAMILIES = List.of(POOP_BRICK_FAMILY, MOSSY_POOP_BRICK_FAMILY, DRIED_POOP_BLOCK_FAMILY, SMOOTH_POOP_BLOCK_FAMILY, CUT_POOP_BLOCK_FAMILY);
    public static final List<BlockFamily> SIMPLE_MODEL_FAMILIES = List.of(CHILI_POOP_FAMILY, GOLDEN_POOP_FAMILY, POOP_BRICK_FAMILY, MOSSY_POOP_BRICK_FAMILY, DRIED_POOP_BLOCK_FAMILY, SMOOTH_POOP_BLOCK_FAMILY, CUT_POOP_BLOCK_FAMILY, TILE_BLOCK_FAMILY);
    public static final List<BlockFamily> WALL_TAG_FAMILIES = List.of(POOP_FAMILY, CHILI_POOP_FAMILY, GOLDEN_POOP_FAMILY, POOP_BRICK_FAMILY, MOSSY_POOP_BRICK_FAMILY, DRIED_POOP_BLOCK_FAMILY, SMOOTH_POOP_BLOCK_FAMILY, CUT_POOP_BLOCK_FAMILY, TILE_BLOCK_FAMILY);

    private static BlockBehaviour.Properties poopCakeProperties() {
        return BlockBehaviour.Properties.of()
                .forceSolidOn()
                .strength(0.5F)
                .sound(SoundType.WOOL)
                .pushReaction(PushReaction.DESTROY);
    }

    private static BlockBehaviour.Properties poopProperties() {
        return poopProperties(POOP);
    }

    private static BlockBehaviour.Properties poopProperties(float strength) {
        return simpleProperties(MapColor.COLOR_BROWN, strength, SoundType.MUD);
    }

    private static BlockBehaviour.Properties hardenedProperties(MapColor color, SoundType sound) {
        return simpleProperties(color, HARDEN, sound)
                .requiresCorrectToolForDrops();
    }

    private static BlockBehaviour.Properties logProperties(SoundType sound) {
        return simpleProperties(MapColor.COLOR_BROWN, LOG, sound)
                .instrument(NoteBlockInstrument.BASS);
    }

    private static BlockBehaviour.Properties leavesProperties(MapColor color) {
        return simpleProperties(color, 0.2F, SoundType.SCULK_SENSOR)
                .randomTicks()
                .noOcclusion()
                .isValidSpawn(Blocks::ocelotOrParrot)
                .isSuffocating(PoBlocks::neverSuffocate)
                .isViewBlocking(PoBlocks::neverBlockVision)
                .ignitedByLava()
                .pushReaction(PushReaction.DESTROY)
                .isRedstoneConductor(PoBlocks::never);
    }

    private static BlockBehaviour.Properties plantProperties(MapColor color, SoundType sound) {
        return BlockBehaviour.Properties.of()
                .mapColor(color)
                .instabreak()
                .sound(sound)
                .pushReaction(PushReaction.DESTROY);
    }

    private static BlockBehaviour.Properties toiletProperties(MapColor color, float strength, SoundType sound, NoteBlockInstrument instrument) {
        return BlockBehaviour.Properties.of()
                .mapColor(color)
                .instrument(instrument)
                .strength(strength, TOILET_RESISTANCE)
                .isRedstoneConductor(PoBlocks::always)
                .isSuffocating(PoBlocks::always)
                .sound(sound);
    }

    private static BlockBehaviour.Properties simpleProperties(MapColor color, float strength, SoundType sound) {
        return BlockBehaviour.Properties.of()
                .mapColor(color)
                .strength(strength)
                .sound(sound);
    }

    private static Map<Block, BlockEntry<PoopCandleCakeBlock>> registerPoopCandleCakes() {
        Map<Block, BlockEntry<PoopCandleCakeBlock>> candleCakes = new LinkedHashMap<>();
        for (Block candle : POOP_CAKE_CANDLES) {
            candleCakes.put(candle, registerPoopCandleCake(candle));
        }
        return Collections.unmodifiableMap(candleCakes);
    }

    private static BlockEntry<PoopCandleCakeBlock> registerPoopCandleCake(Block candle) {
        if (!(candle instanceof CandleBlock)) {
            throw new IllegalArgumentException("Expected candle block: " + candle);
        }

        String candleName = BuiltInRegistries.BLOCK.getKey(candle).getPath();
        String name = candle == Blocks.CANDLE ? "poop_candle_cake" : candleName.replace("_candle", "_poop_candle_cake");

        return REGISTRATE.block(name,
                        props -> new PoopCandleCakeBlock(candle, poopCakeProperties()
                                .lightLevel(state -> state.getValue(PoopCandleCakeBlock.LIT) ? 3 : 0)))
                .blockstate((ctx, prov) -> {
                })
                .loot((loot, block) -> loot.add(block, RegistrateBlockLootTables.createCandleCakeDrops(candle)))
                .register();
    }

    public static Map<Block, BlockEntry<PoopCandleCakeBlock>> getPoopCandleCakes() {
        return POOP_CANDLE_CAKES;
    }

    public static BlockState getPoopCandleCake(CandleBlock candle) {
        BlockEntry<PoopCandleCakeBlock> candleCake = POOP_CANDLE_CAKES.get(candle);
        return candleCake == null ? null : candleCake.get().defaultBlockState();
    }

    public static boolean neverSuffocate(BlockState state, BlockGetter world, BlockPos pos) {
        return false;
    }

    public static boolean never(BlockState state, BlockGetter world, BlockPos pos) {
        return false;
    }

    public static boolean always(BlockState state, BlockGetter blockGetter, BlockPos pos) {
        return true;
    }

    public static boolean neverBlockVision(BlockState state, BlockGetter world, BlockPos pos) {
        return false;
    }

    public static <T extends Block> BlockEntry<T> registerBlock(String name, NonNullFunction<BlockBehaviour.Properties, T> factory) {
        return registerBlock(name, factory, RegistrateBlockLootTables::dropSelf);
    }

    public static <T extends Block> BlockEntry<T> registerBlock(String name, NonNullFunction<BlockBehaviour.Properties, T> factory, NonNullBiConsumer<RegistrateBlockLootTables, T> loot) {
        return REGISTRATE.block(name, factory)
                .blockstate((ctx, prov) -> {
                })
                .loot(loot)
                .item((b, p) -> new BlockItem(b, p.stacksTo(88)))
                .model((ctx, prov) -> {
                })
                .build()
                .register();
    }

    public static <T extends Block> BlockEntry<T> registerDefaultBlock(String name, NonNullFunction<BlockBehaviour.Properties, T> factory) {
        return registerDefaultBlock(name, factory, RegistrateBlockLootTables::dropSelf);
    }

    public static <T extends Block> BlockEntry<T> registerDefaultBlock(String name, NonNullFunction<BlockBehaviour.Properties, T> factory, NonNullBiConsumer<RegistrateBlockLootTables, T> loot) {
        return REGISTRATE.block(name, factory)
                .blockstate((ctx, prov) -> {
                })
                .loot(loot)
                .item()
                .model((ctx, prov) -> {
                })
                .build()
                .register();
    }

    public static <T extends Block> BlockEntry<T> registerBlockNoItem(String name, NonNullFunction<BlockBehaviour.Properties, T> factory, NonNullBiConsumer<RegistrateBlockLootTables, T> loot) {
        return REGISTRATE.block(name, factory)
                .blockstate((ctx, prov) -> {
                })
                .loot(loot)
                .register();
    }

    public static <T extends Block> BlockEntry<T> registerCompooperBlock(String name, NonNullFunction<BlockBehaviour.Properties, T> factory, NonNullBiConsumer<RegistrateBlockLootTables, T> loot) {
        return REGISTRATE.block(name, factory)
                .blockstate((ctx, prov) -> {
                })
                .loot(loot)
                .item((b, p) -> new CompooperBlockItem(b, new Item.Properties()))
                .model((ctx, prov) -> {
                })
                .build()
                .register();
    }

    public static <T extends Block> BlockEntry<T> registerToiletBlock(String name, NonNullFunction<BlockBehaviour.Properties, T> factory, NonNullBiConsumer<RegistrateBlockLootTables, T> loot) {
        return REGISTRATE.block(name, factory)
                .blockstate((ctx, prov) -> {
                })
                .loot(loot)
                .item((b, p) -> new ToiletBlockItem(b, p.stacksTo(88)))
                .model((ctx, prov) -> {
                })
                .build()
                .register();
    }

    private static BlockFamily registerBlockFamily(String name, BlockEntry<? extends Block> base, boolean defaultBlockItem) {
        return new BlockFamily(
                base,
                registerFamilyBlock(name + "_stairs", props -> new StairBlock(base.get().defaultBlockState(), BlockBehaviour.Properties.ofFullCopy(base.get())), defaultBlockItem),
                registerFamilyBlock(name + "_slab", props -> new SlabBlock(BlockBehaviour.Properties.ofFullCopy(base.get())), defaultBlockItem, (loot, block) -> loot.add(block, loot.createSlabItemTable(block))),
                registerFamilyBlock(name + "_vertical_slab", props -> new VerticalSlabBlock(BlockBehaviour.Properties.ofFullCopy(base.get())), defaultBlockItem),
                registerFamilyBlock(name + "_wall", props -> new WallBlock(BlockBehaviour.Properties.ofFullCopy(base.get())), defaultBlockItem)
        );
    }

    private static <T extends Block> BlockEntry<T> registerFamilyBlock(String name, NonNullFunction<BlockBehaviour.Properties, T> factory, boolean defaultBlockItem) {
        return registerFamilyBlock(name, factory, defaultBlockItem, RegistrateBlockLootTables::dropSelf);
    }

    private static <T extends Block> BlockEntry<T> registerFamilyBlock(String name, NonNullFunction<BlockBehaviour.Properties, T> factory, boolean defaultBlockItem, NonNullBiConsumer<RegistrateBlockLootTables, T> loot) {
        return defaultBlockItem ? registerDefaultBlock(name, factory, loot) : registerBlock(name, factory, loot);
    }

    private static ToIntFunction<BlockState> lavaLightLevel() {
        return state -> state.getValue(BaseToiletLavaBlock.LAVA) ? LAVA_LIGHT_LEVEL : 0;
    }

    public static void register() {
    }

    // Loot
    public static LootTable.Builder createToiletDrop(Block block) {
        return LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1.0F))
                        .when(ExplosionCondition.survivesExplosion())
                        .add(LootItem.lootTableItem(block)
                                .apply(SetToiletTypeFunction.setType())));
    }

    private static LootTable.Builder createPoopPieceDrop(RegistrateBlockLootTables loot, Block block, Item item) {
        var registrylookup = loot.getRegistries().lookupOrThrow(Registries.ENCHANTMENT);
        LootItemCondition.Builder hasSilkTouch = MatchTool.toolMatches(
                ItemPredicate.Builder.item()
                        .withSubPredicate(
                                ItemSubPredicates.ENCHANTMENTS,
                                ItemEnchantmentsPredicate.enchantments(
                                        List.of(new EnchantmentPredicate(registrylookup.getOrThrow(Enchantments.SILK_TOUCH), MinMaxBounds.Ints.atLeast(1)))
                                )
                        ));

        LootPoolEntryContainer.Builder<?> nonSilkTouch = AlternativesEntry.alternatives(
                IntStream.rangeClosed(1, 8)
                        .mapToObj(i -> LootItem.lootTableItem(item)
                                .when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(block)
                                        .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(PoopPieceBlock.LAYERS, i)))
                                .apply(SetItemCountFunction.setCount(ConstantValue.exactly(i)))
                                .apply(ApplyBonusCount.addOreBonusCount(registrylookup.getOrThrow(Enchantments.FORTUNE)))
                        ).toArray(LootPoolEntryContainer.Builder[]::new)
        ).when(hasSilkTouch.invert());

        LootPoolEntryContainer.Builder<?> silkTouch = AlternativesEntry.alternatives(
                IntStream.rangeClosed(1, 8)
                        .mapToObj(i -> {
                            if (i == 8) {
                                return LootItem.lootTableItem(POOP_BLOCK.get());
                            }
                            return LootItem.lootTableItem(block)
                                    .when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(block)
                                            .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(PoopPieceBlock.LAYERS, i)))
                                    .apply(SetItemCountFunction.setCount(ConstantValue.exactly(i)));
                        }).toArray(LootPoolEntryContainer.Builder[]::new)
        ).when(hasSilkTouch);

        return LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(AlternativesEntry.alternatives(nonSilkTouch, silkTouch))
                );
    }

    private static LootTable.Builder createSpallOreDrops(RegistrateBlockLootTables loot, Block block) {
        var registrylookup = loot.getRegistries().lookupOrThrow(Registries.ENCHANTMENT);
        return loot.createSilkTouchDispatchTable(block,
                loot.applyExplosionDecay(block,
                        LootItem.lootTableItem(PoItems.SPALL)
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(4.0F, 5.0F)))
                                .apply(ApplyBonusCount.addUniformBonusCount(registrylookup.getOrThrow(Enchantments.FORTUNE))))
        );
    }

    private static LootTable.Builder createIronLeavesDrops(RegistrateBlockLootTables loot, Block block) {
        var registrylookup = loot.getRegistries().lookupOrThrow(Registries.ENCHANTMENT);
        return createShearsOrSilkTouchDispatchTable(loot, block,
                LootItem.lootTableItem(Items.IRON_NUGGET)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(0.0F, 3.0F)))
                        .apply(ApplyBonusCount.addUniformBonusCount(registrylookup.getOrThrow(Enchantments.FORTUNE))));
    }

    private static LootTable.Builder createGoldLeavesDrops(RegistrateBlockLootTables loot, Block block) {
        var registrylookup = loot.getRegistries().lookupOrThrow(Registries.ENCHANTMENT);
        return createShearsOrSilkTouchDispatchTable(loot, block,
                LootItem.lootTableItem(Items.GOLD_NUGGET)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(0.0F, 2.0F)))
                        .apply(ApplyBonusCount.addUniformBonusCount(registrylookup.getOrThrow(Enchantments.FORTUNE))));
    }

    private static LootTable.Builder createLeavesDrops(RegistrateBlockLootTables loot, Block block, Item dropItem) {
        var registrylookup = loot.getRegistries().lookupOrThrow(Registries.ENCHANTMENT);
        LootItemCondition.Builder hasShearsOrSilkTouch = hasShearsOrSilkTouch(loot);
        return createShearsOrSilkTouchDispatchTable(loot, block,
                LootItem.lootTableItem(dropItem)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(0.0F, 2.0F)))
                        .apply(ApplyBonusCount.addUniformBonusCount(registrylookup.getOrThrow(Enchantments.FORTUNE)))
        ).withPool(
                LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1.0F))
                        .when(hasShearsOrSilkTouch.invert())
                        .add(((LootPoolSingletonContainer.Builder<?>)
                                loot.applyExplosionCondition(block, LootItem.lootTableItem(PoItems.ROUNDWORM.get())))
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(0.0F, 1.0F)))
                                .apply(ApplyBonusCount.addUniformBonusCount(registrylookup.getOrThrow(Enchantments.FORTUNE))))
        );
    }

    private static LootTable.Builder createShearsOrSilkTouchDispatchTable(RegistrateBlockLootTables loot, Block block, LootPoolSingletonContainer.Builder<?> fallback) {
        LootItemCondition.Builder hasShearsOrSilkTouch = hasShearsOrSilkTouch(loot);
        return LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(AlternativesEntry.alternatives(
                                loot.applyExplosionCondition(block, LootItem.lootTableItem(block))
                                        .when(hasShearsOrSilkTouch),
                                loot.applyExplosionDecay(block, fallback)
                        )));
    }

    private static LootItemCondition.Builder hasShearsOrSilkTouch(RegistrateBlockLootTables loot) {
        var registrylookup = loot.getRegistries().lookupOrThrow(Registries.ENCHANTMENT);
        LootItemCondition.Builder hasSilkTouch = MatchTool.toolMatches(
                ItemPredicate.Builder.item()
                        .withSubPredicate(
                                ItemSubPredicates.ENCHANTMENTS,
                                ItemEnchantmentsPredicate.enchantments(
                                        List.of(new EnchantmentPredicate(registrylookup.getOrThrow(Enchantments.SILK_TOUCH), MinMaxBounds.Ints.atLeast(1)))
                                )
                        ));
        return MatchTool.toolMatches(ItemPredicate.Builder.item().of(Items.SHEARS))
                .or(hasSilkTouch);
    }

    private static LootTable.Builder createSaltpeterClusterDrop(RegistrateBlockLootTables loot, Block block) {
        var registrylookup = loot.getRegistries().lookupOrThrow(Registries.ENCHANTMENT);
        return loot.createSilkTouchDispatchTable(block,
                LootItem.lootTableItem(PoItems.SALTPETER_SHARD.get())
                        .apply(SetItemCountFunction.setCount(ConstantValue.exactly(4.0F)))
                        .apply(ApplyBonusCount.addOreBonusCount(registrylookup.getOrThrow(Enchantments.FORTUNE)))
                        .when(MatchTool.toolMatches(ItemPredicate.Builder.item().of(ItemTags.CLUSTER_MAX_HARVESTABLES)))
                        .otherwise(loot.applyExplosionDecay(block,
                                LootItem.lootTableItem(PoItems.SALTPETER_SHARD.get()).apply(SetItemCountFunction.setCount(ConstantValue.exactly(2.0F))))));
    }
}