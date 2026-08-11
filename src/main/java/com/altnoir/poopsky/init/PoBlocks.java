package com.altnoir.poopsky.init;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.content.SetToiletTypeFunction;
import com.altnoir.poopsky.content.block.ChiliVines;
import com.altnoir.poopsky.content.block.PoTreeGrower;
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
import net.minecraft.advancements.criterion.EnchantmentPredicate;
import net.minecraft.advancements.criterion.ItemPredicate;
import net.minecraft.advancements.criterion.MinMaxBounds;
import net.minecraft.advancements.criterion.StatePropertiesPredicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ColorRGBA;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.block.state.properties.WoodType;
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
import net.minecraft.world.level.storage.loot.predicates.*;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.ToIntFunction;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class PoBlocks {
    private static final float POOP = 0.5F;
    private static final float HARDEN = 1.5F;
    private static final float LOG = 2.0F;
    private static final float WOODEN_STRENGTH = 4.0F;
    private static final float HARD_STRENGTH = 10.0F;
    private static final float TOILET_RESISTANCE = 1200.0F;
    private static final int LAVA_LIGHT_LEVEL = 15;
    protected static final float[] LEAVES_SAPLING_CHANCES = {0.1F, 0.125F, 0.25F, 0.5F};
    protected static final float[] LEAVES_STICK_CHANCES = {0.05F, 0.075F, 0.1F, 0.125F, 0.25F};

    private static final PoRegistrate REGISTRATE = PoopSky.registrate();

    private enum BlockTab {
        BASIC_BLOCKS,
        DECO_MATERIALS,
        DECO_TILE,
        NO_TAB
    }

    private static final EnumMap<BlockTab, Set<BlockEntry<? extends Block>>> TAB_ITEMS = new EnumMap<>(BlockTab.class);

    private static final BlockBehaviour.StateArgumentPredicate<EntityType<?>> ALWAYS_SPAWNABLE = Blocks::always;

    public static final BlockEntry<ShitBlock> SHIT = registerShitBlock("shit");
    public static final BlockEntry<ShitBlock> CHILI_SHIT = registerShitBlock("chili_shit");
    public static final BlockEntry<ShitBlock> GOLDEN_SHIT = registerShitBlock("golden_shit");

    public static final BlockEntry<PoopCakeBlock> POOP_CAKE = registerBlock("poop_cake", 88,
            props -> new PoopCakeBlock(poopCakeProperties()),
            (loot, block) -> loot.add(block, BlockLootSubProvider.noDrop()));

    private static final Map<Block, BlockEntry<PoopCandleCakeBlock>> POOP_CANDLE_CAKES = registerPoopCandleCakes();

    public static final BlockEntry<PoopBlock> POOP_BLOCK = registerPoopBlock("poop_block",
            props -> new PoopBlock(poopProperties()
                    .randomTicks()
                    .speedFactor(0.4F)
                    .isValidSpawn(ALWAYS_SPAWNABLE)
                    .isRedstoneConductor(PoBlocks::always)
                    .isSuffocating(PoBlocks::always)
                    .instrument(NoteBlockInstrument.COW_BELL)));
    public static final BlockEntry<PoopFarmlandBlock> POOP_FARMLAND = registerBlock("poop_farmland", 88,
            props -> new PoopFarmlandBlock(BlockBehaviour.Properties.ofFullCopy(POOP_BLOCK.get())
                    .randomTicks()),
            (loot, block) -> loot.add(block, LootTable.lootTable()
                    .withPool(LootPool.lootPool()
                            .setRolls(ConstantValue.exactly(1.0F))
                            .when(ExplosionCondition.survivesExplosion())
                            .add(LootItem.lootTableItem(POOP_BLOCK.get())))));
    public static final BlockEntry<PoolimeMaggotsBlock> POOLIME_MAGGOTS_BLOCK = registerBlock("poolime_maggots_block", 88,
            props -> new PoolimeMaggotsBlock(poopProperties(1.0F)
                    .randomTicks()
                    .speedFactor(0.4F)
                    .isValidSpawn(ALWAYS_SPAWNABLE)
                    .instrument(NoteBlockInstrument.COW_BELL)));
    public static final BlockEntry<PoolimeBlock> POOLIME_BLOCK = registerBlock("poolime_block", 88,
            props -> new PoolimeBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BROWN)
                    .friction(0.8F)
                    .sound(SoundType.SLIME_BLOCK)
                    .noOcclusion()));

    public static final BlockEntry<StairBlock> POOP_STAIRS = registerDecoMaterialBlock("poop_stairs", 88,
            props -> new StairBlock(POOP_BLOCK.get().defaultBlockState(), poopProperties()));

    public static final BlockEntry<SlabBlock> POOP_SLAB = registerDecoMaterialBlock("poop_slab", 88,
            props -> new SlabBlock(poopProperties()));
    public static final BlockEntry<VerticalSlabBlock> POOP_VERTICAL_SLAB = registerDecoMaterialBlock("poop_vertical_slab", 88,
            props -> new VerticalSlabBlock(poopProperties()),
            PoBlocks::createVerticalSlabDrops);
    public static final BlockEntry<PoopPieceBlock> POOP_PIECE = registerDecoMaterialBlock("poop_piece", 88,
            props -> new PoopPieceBlock(poopProperties(0.1F)
                    .replaceable()
                    .randomTicks()
                    .requiresCorrectToolForDrops()
                    .isViewBlocking((state, getter, pos) -> state.getValue(PoopPieceBlock.LAYERS) >= 8)
                    .pushReaction(PushReaction.DESTROY)),
            (loot, block) -> loot.add(block, createPoopPieceDrop(loot, block, PoItems.POOP_BALL.get())));
    public static final BlockEntry<FenceBlock> POOP_FENCE = registerDecoMaterialBlock("poop_fence", 88,
            props -> new FenceBlock(poopProperties()));
    public static final BlockEntry<FenceGateBlock> POOP_FENCE_GATE = registerDecoMaterialBlock("poop_fence_gate", 88,
            props -> new FenceGateBlock(PoWoodType.POOP, poopProperties()));
    public static final BlockEntry<WallBlock> POOP_WALL = registerDecoMaterialBlock("poop_wall", 88,
            props -> new WallBlock(poopProperties()));
    public static final BlockFamily POOP_FAMILY = new BlockFamily(POOP_BLOCK, POOP_STAIRS, POOP_SLAB, POOP_VERTICAL_SLAB, POOP_WALL);

    public static final BlockEntry<DoorBlock> POOP_DOOR = registerDecoMaterialBlock("poop_door", 88,
            props -> new DoorBlock(PoBlockSetType.POOP, poopProperties()
                    .noOcclusion()
                    .pushReaction(PushReaction.DESTROY)),
            (loot, block) -> loot.add(block, loot.createDoorTable(block)));
    public static final BlockEntry<TrapDoorBlock> POOP_TRAPDOOR = registerDecoMaterialBlock("poop_trapdoor", 88,
            props -> new TrapDoorBlock(PoBlockSetType.POOP, poopProperties()
                    .noOcclusion()
                    .isValidSpawn(Blocks::never)));
    public static final BlockEntry<PressurePlateBlock> POOP_PRESSURE_PLATE = registerDecoMaterialBlock("poop_pressure_plate", 88,
            props -> new PressurePlateBlock(PoBlockSetType.POOP, poopProperties().forceSolidOn().noCollission().pushReaction(PushReaction.DESTROY)));
    public static final BlockEntry<ButtonBlock> POOP_BUTTON = registerDecoMaterialBlock("poop_button", 88,
            props -> new ButtonBlock(PoBlockSetType.POOP, 200, poopProperties().noCollission().pushReaction(PushReaction.DESTROY)));

    public static final BlockEntry<Block> POOP_BRICKS = registerDecoMaterialBlock("poop_bricks", 88,
            props -> new Block(hardenedProperties(MapColor.COLOR_BROWN, SoundType.FROGLIGHT)));
    public static final BlockEntry<Block> CRACKED_POOP_BRICKS = registerDecoMaterialBlock("cracked_poop_bricks", 88,
            props -> new Block(BlockBehaviour.Properties.ofFullCopy(POOP_BRICKS.get())));
    public static final BlockFamily POOP_BRICK_FAMILY = registerBlockFamily("poop_brick", POOP_BRICKS, false);
    public static final BlockEntry<StairBlock> POOP_BRICK_STAIRS = POOP_BRICK_FAMILY.stairs();
    public static final BlockEntry<SlabBlock> POOP_BRICK_SLAB = POOP_BRICK_FAMILY.slab();
    public static final BlockEntry<VerticalSlabBlock> POOP_BRICK_VERTICAL_SLAB = POOP_BRICK_FAMILY.verticalSlab();
    public static final BlockEntry<WallBlock> POOP_BRICK_WALL = POOP_BRICK_FAMILY.wall();

    public static final BlockEntry<Block> MOSSY_POOP_BRICKS = registerDecoMaterialBlock("mossy_poop_bricks", 88,
            props -> new Block(hardenedProperties(MapColor.COLOR_GREEN, SoundType.FROGLIGHT)));
    public static final BlockFamily MOSSY_POOP_BRICK_FAMILY = registerBlockFamily("mossy_poop_brick", MOSSY_POOP_BRICKS, false);

    public static final BlockEntry<DriedPoopBlock> DRIED_POOP_BLOCK = registerPoopBlock("dried_poop_block",
            props -> new DriedPoopBlock(hardenedProperties(MapColor.COLOR_ORANGE, SoundType.TUFF)
                    .instrument(NoteBlockInstrument.COW_BELL)));
    public static final BlockEntry<ColoredFallingBlock> POOP_SAND = registerBlock("poop_sand", 88,
            props -> new ColoredFallingBlock(new ColorRGBA(9131563),
                    BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_ORANGE).instrument(NoteBlockInstrument.SNARE).strength(0.5F).sound(SoundType.SAND)));
    public static final BlockFamily DRIED_POOP_BLOCK_FAMILY = registerBlockFamily("dried_poop_block", DRIED_POOP_BLOCK, false);
    public static final BlockEntry<Block> SMOOTH_POOP_BLOCK = registerDecoMaterialBlock("smooth_poop_block", 88,
            props -> new Block(hardenedProperties(MapColor.COLOR_ORANGE, SoundType.CALCITE)));
    public static final BlockFamily SMOOTH_POOP_BLOCK_FAMILY = registerBlockFamily("smooth_poop_block", SMOOTH_POOP_BLOCK, false);

    public static final BlockEntry<Block> CUT_POOP_BLOCK = registerDecoMaterialBlock("cut_poop_block", 88,
            props -> new Block(hardenedProperties(MapColor.COLOR_ORANGE, SoundType.POLISHED_TUFF)));
    public static final BlockFamily CUT_POOP_BLOCK_FAMILY = registerBlockFamily("cut_poop_block", CUT_POOP_BLOCK, false);
    public static final BlockEntry<StairBlock> CUT_POOP_BLOCK_STAIRS = CUT_POOP_BLOCK_FAMILY.stairs();
    public static final BlockEntry<SlabBlock> CUT_POOP_BLOCK_SLAB = CUT_POOP_BLOCK_FAMILY.slab();
    public static final BlockEntry<VerticalSlabBlock> CUT_POOP_BLOCK_VERTICAL_SLAB = CUT_POOP_BLOCK_FAMILY.verticalSlab();
    public static final BlockEntry<WallBlock> CUT_POOP_BLOCK_WALL = CUT_POOP_BLOCK_FAMILY.wall();

    public static final BlockEntry<ChiliPoopBlock> CHILI_POOP_BLOCK = registerPoopBlock("chili_poop_block",
            props -> new ChiliPoopBlock(poopProperties()
                    .requiresCorrectToolForDrops()
                    .speedFactor(0.4F)
                    .isValidSpawn(ALWAYS_SPAWNABLE)
                    .isRedstoneConductor(PoBlocks::always)
                    .isSuffocating(PoBlocks::always)
                    .instrument(NoteBlockInstrument.COW_BELL)));
    public static final BlockEntry<Block> DRIED_CHILI_POOP_BLOCK = registerBlock("dried_chili_poop_block",
            props -> new Block(hardenedProperties(MapColor.COLOR_RED, SoundType.TUFF)
                    .instrument(NoteBlockInstrument.COW_BELL)));
    public static final BlockFamily CHILI_POOP_FAMILY = registerBlockFamily("chili_poop", CHILI_POOP_BLOCK, false);

    public static final BlockEntry<GoldenPoopBlock> GOLDEN_POOP_BLOCK = registerPoopBlock("golden_poop_block",
            props -> new GoldenPoopBlock(simpleProperties(MapColor.GOLD, 0.65F, SoundType.MUD)
                    .requiresCorrectToolForDrops()
                    .speedFactor(0.4F)
                    .isValidSpawn(ALWAYS_SPAWNABLE)
                    .isRedstoneConductor(PoBlocks::always)
                    .isSuffocating(PoBlocks::always)
                    .instrument(NoteBlockInstrument.BELL)));
    public static final BlockEntry<Block> DRIED_GOLDEN_POOP_BLOCK = registerBlock("dried_golden_poop_block",
            props -> new Block(hardenedProperties(MapColor.GOLD, SoundType.TUFF)
                    .instrument(NoteBlockInstrument.COW_BELL)));
    public static final BlockFamily GOLDEN_POOP_FAMILY = registerBlockFamily("golden_poop", GOLDEN_POOP_BLOCK, false);

    private static final Map<DyeColor, ColoredTile> COLORED_TILES = registerColoredTiles();
    public static final BlockEntry<Block> WHITE_TILE_BLOCK = coloredTile(DyeColor.WHITE).block();
    public static final BlockEntry<Block> LIGHT_GRAY_TILE_BLOCK = coloredTile(DyeColor.LIGHT_GRAY).block();
    public static final BlockEntry<Block> GRAY_TILE_BLOCK = coloredTile(DyeColor.GRAY).block();
    public static final BlockEntry<Block> BLACK_TILE_BLOCK = coloredTile(DyeColor.BLACK).block();
    public static final BlockEntry<Block> BROWN_TILE_BLOCK = coloredTile(DyeColor.BROWN).block();
    public static final BlockEntry<Block> RED_TILE_BLOCK = coloredTile(DyeColor.RED).block();
    public static final BlockEntry<Block> ORANGE_TILE_BLOCK = coloredTile(DyeColor.ORANGE).block();
    public static final BlockEntry<Block> YELLOW_TILE_BLOCK = coloredTile(DyeColor.YELLOW).block();
    public static final BlockEntry<Block> LIME_TILE_BLOCK = coloredTile(DyeColor.LIME).block();
    public static final BlockEntry<Block> GREEN_TILE_BLOCK = coloredTile(DyeColor.GREEN).block();
    public static final BlockEntry<Block> CYAN_TILE_BLOCK = coloredTile(DyeColor.CYAN).block();
    public static final BlockEntry<Block> LIGHT_BLUE_TILE_BLOCK = coloredTile(DyeColor.LIGHT_BLUE).block();
    public static final BlockEntry<Block> BLUE_TILE_BLOCK = coloredTile(DyeColor.BLUE).block();
    public static final BlockEntry<Block> PURPLE_TILE_BLOCK = coloredTile(DyeColor.PURPLE).block();
    public static final BlockEntry<Block> MAGENTA_TILE_BLOCK = coloredTile(DyeColor.MAGENTA).block();
    public static final BlockEntry<Block> PINK_TILE_BLOCK = coloredTile(DyeColor.PINK).block();
    public static final BlockFamily WHITE_TILE_BLOCK_FAMILY = coloredTile(DyeColor.WHITE).family();
    public static final BlockFamily LIGHT_GRAY_TILE_BLOCK_FAMILY = coloredTile(DyeColor.LIGHT_GRAY).family();
    public static final BlockFamily GRAY_TILE_BLOCK_FAMILY = coloredTile(DyeColor.GRAY).family();
    public static final BlockFamily BLACK_TILE_BLOCK_FAMILY = coloredTile(DyeColor.BLACK).family();
    public static final BlockFamily BROWN_TILE_BLOCK_FAMILY = coloredTile(DyeColor.BROWN).family();
    public static final BlockFamily RED_TILE_BLOCK_FAMILY = coloredTile(DyeColor.RED).family();
    public static final BlockFamily ORANGE_TILE_BLOCK_FAMILY = coloredTile(DyeColor.ORANGE).family();
    public static final BlockFamily YELLOW_TILE_BLOCK_FAMILY = coloredTile(DyeColor.YELLOW).family();
    public static final BlockFamily LIME_TILE_BLOCK_FAMILY = coloredTile(DyeColor.LIME).family();
    public static final BlockFamily GREEN_TILE_BLOCK_FAMILY = coloredTile(DyeColor.GREEN).family();
    public static final BlockFamily CYAN_TILE_BLOCK_FAMILY = coloredTile(DyeColor.CYAN).family();
    public static final BlockFamily LIGHT_BLUE_TILE_BLOCK_FAMILY = coloredTile(DyeColor.LIGHT_BLUE).family();
    public static final BlockFamily BLUE_TILE_BLOCK_FAMILY = coloredTile(DyeColor.BLUE).family();
    public static final BlockFamily PURPLE_TILE_BLOCK_FAMILY = coloredTile(DyeColor.PURPLE).family();
    public static final BlockFamily MAGENTA_TILE_BLOCK_FAMILY = coloredTile(DyeColor.MAGENTA).family();
    public static final BlockFamily PINK_TILE_BLOCK_FAMILY = coloredTile(DyeColor.PINK).family();

    public static final BlockEntry<Block> RAW_POOP_BLOCK = registerBlock("raw_poop_block", 88,
            props -> new Block(simpleProperties(MapColor.COLOR_BROWN, 0.65F, SoundType.MUD)
                    .randomTicks()
                    .isValidSpawn(ALWAYS_SPAWNABLE)
                    .instrument(NoteBlockInstrument.COW_BELL)));
    public static final BlockEntry<RawSaplingBlock> RAW_SAPLING_POOP_BLOCK = registerBlock("raw_sapling_poop_block", 88,
            props -> new RawSaplingBlock(BlockBehaviour.Properties.ofFullCopy(RAW_POOP_BLOCK.get()).sound(SoundType.ROOTED_DIRT)));
    public static final BlockEntry<RawSeaBlock> RAW_SEA_POOP_BLOCK = registerBlock("raw_sea_poop_block", 88,
            props -> new RawSeaBlock(BlockBehaviour.Properties.ofFullCopy(RAW_POOP_BLOCK.get()).sound(SoundType.ROOTED_DIRT)));
    public static final BlockEntry<RawWitherBlock> RAW_WITHER_POOP_BLOCK = registerBlock("raw_wither_poop_block", 88,
            props -> new RawWitherBlock(BlockBehaviour.Properties.ofFullCopy(RAW_POOP_BLOCK.get()).sound(SoundType.ROOTED_DIRT)));

    public static final BlockEntry<ChairBlock> STOOL = registerBlock("stool", 88,
            props -> new ChairBlock(poopProperties().pushReaction(PushReaction.DESTROY).noOcclusion()));
    public static final BlockEntry<PoopCraftingTableBlock> POOP_CRAFTING_TABLE = registerBlock("poop_crafting_table", 88,
            props -> new PoopCraftingTableBlock(poopProperties().noOcclusion()));
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
    public static final BlockEntry<WaterCompooperBlock> WATER_COMPOOPER = registerBlockNoTab("water_compooper", 64,
            props -> new WaterCompooperBlock(BlockBehaviour.Properties.ofFullCopy(COMPOOPER.get())),
            (loot, block) -> loot.dropOther(block, COMPOOPER.get()));
    public static final BlockEntry<LavaCompooperBlock> LAVA_COMPOOPER = registerBlockNoTab("lava_compooper", 64,
            props -> new LavaCompooperBlock(BlockBehaviour.Properties.ofFullCopy(COMPOOPER.get()).lightLevel(state -> LAVA_LIGHT_LEVEL)),
            (loot, block) -> loot.dropOther(block, COMPOOPER.get()));
    public static final BlockEntry<PowderSnowCompooperBlock> POWDER_SNOW_COMPOOPER = registerBlockNoTab("powder_snow_compooper", 64,
            props -> new PowderSnowCompooperBlock(BlockBehaviour.Properties.ofFullCopy(COMPOOPER.get())),
            (loot, block) -> loot.dropOther(block, COMPOOPER.get()));
    public static final BlockEntry<UrineCompooperBlock> URINE_COMPOOPER = registerBlockNoTab("urine_compooper", 64,
            props -> new UrineCompooperBlock(BlockBehaviour.Properties.ofFullCopy(COMPOOPER.get()).randomTicks()),
            (loot, block) -> loot.add(block, LootTable.lootTable()
                    .withPool(LootPool.lootPool()
                            .add(loot.applyExplosionDecay(block, LootItem.lootTableItem(COMPOOPER.get()))))
                    .withPool(LootPool.lootPool()
                            .add(LootItem.lootTableItem(PoItems.MAGGOTS_SEEDS.get()))
                            .when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(block)
                                    .setProperties(StatePropertiesPredicate.Builder.properties()
                                            .hasProperty(UrineCompooperBlock.MAGGOTS, true))))));
    public static final BlockEntry<PlacerBlock> PLACER = registerBlock("placer",
            props -> new PlacerBlock(BlockBehaviour.Properties.of().mapColor(MapColor.STONE)
                    .instrument(NoteBlockInstrument.BASEDRUM)
                    .requiresCorrectToolForDrops().strength(3.5F)));
    public static final BlockEntry<SieveBlock> SIEVE = registerBlock("sieve_stable",
            props -> new SieveBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.STONE)
                    .strength(1.5f, 3.0f)
                    .requiresCorrectToolForDrops()
                    .isValidSpawn(Blocks::never)
                    .noOcclusion()));
    public static final BlockEntry<PoopTntBlock> POOP_TNT = registerBlock("poop_tnt", 88,
            props -> new PoopTntBlock(simpleProperties(MapColor.FIRE, 0.0F, SoundType.GRASS)
                    .ignitedByLava()
                    .pushReaction(PushReaction.DESTROY)));
    public static final BlockEntry<FlyBarrelBlock> FLY_BARREL = registerBlock("fly_barrel",
            props -> new FlyBarrelBlock(simpleProperties(MapColor.COLOR_BROWN, 0.5F, SoundType.WOOD)
                    .noOcclusion()));
    public static final BlockEntry<BreedingChestBlock> BREEDING_CHEST = registerBlock("breeding_chest",
            props -> new BreedingChestBlock(simpleProperties(MapColor.COLOR_BROWN, 1.0F, SoundType.POLISHED_TUFF)
                    .requiresCorrectToolForDrops()
                    .noOcclusion()));
    public static final BlockEntry<MaggotsChunkLoaderBlock> MAGGOTS_CHUNK_LOADER = registerBlock("maggots_chunk_loader",
            props -> new MaggotsChunkLoaderBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.TERRACOTTA_WHITE)
                    .strength(3.0F)
                    .sound(SoundType.BASALT)
                    .requiresCorrectToolForDrops()
                    .lightLevel(state -> state.getValue(MaggotsChunkLoaderBlock.POWERED) ? 7 : 0)));

    public static final BlockEntry<PoopLogBlock> POOP_LOG = registerPoopBlock("poop_log",
            props -> new PoopLogBlock(logProperties(MapColor.COLOR_BROWN, SoundType.STEM).randomTicks()),
            (loot, block) -> loot.add(block, createSpallOreDrops(loot, block)));
    public static final BlockEntry<PoopLogBlock> POOP_WOOD = registerDecoMaterialBlock("poop_wood", 88,
            props -> new PoopLogBlock(logProperties(MapColor.COLOR_BROWN, SoundType.STEM).randomTicks()),
            (loot, block) -> loot.add(block, createSpallOreDrops(loot, block)));
    public static final BlockEntry<PoopEmptyLogBlock> POOP_EMPTY_LOG = registerPoopBlock("poop_empty_log",
            props -> new PoopEmptyLogBlock(logProperties(MapColor.COLOR_BROWN, SoundType.BAMBOO_WOOD).noOcclusion()));
    public static final BlockEntry<PoopLogBlock> STRIPPED_POOP_LOG = registerPoopBlock("stripped_poop_log",
            props -> new PoopLogBlock(logProperties(MapColor.COLOR_BROWN, SoundType.STEM).randomTicks()),
            (loot, block) -> loot.add(block, createSpallOreDrops(loot, block)));
    public static final BlockEntry<PoopLogBlock> STRIPPED_POOP_WOOD = registerDecoMaterialBlock("stripped_poop_wood", 88,
            props -> new PoopLogBlock(logProperties(MapColor.COLOR_BROWN, SoundType.STEM).randomTicks()),
            (loot, block) -> loot.add(block, createSpallOreDrops(loot, block)));
    public static final BlockEntry<PoopEmptyLogBlock> STRIPPED_POOP_EMPTY_LOG = registerPoopBlock("stripped_poop_empty_log",
            props -> new PoopEmptyLogBlock(logProperties(MapColor.COLOR_BROWN, SoundType.BAMBOO_WOOD).noOcclusion()));
    public static final BlockEntry<LogBlock> GINKGO_LOG = registerDecoMaterialBlock("ginkgo_log", 64,
            props -> new LogBlock(logProperties(MapColor.COLOR_YELLOW, SoundType.WOOD).ignitedByLava()));
    public static final BlockEntry<LogBlock> GINKGO_WOOD = registerDecoMaterialBlock("ginkgo_wood", 64,
            props -> new LogBlock(logProperties(MapColor.COLOR_YELLOW, SoundType.WOOD).ignitedByLava()));
    public static final BlockEntry<LogBlock> STRIPPED_GINKGO_LOG = registerDecoMaterialBlock("stripped_ginkgo_log", 64,
            props -> new LogBlock(logProperties(MapColor.COLOR_YELLOW, SoundType.WOOD).ignitedByLava()));
    public static final BlockEntry<LogBlock> STRIPPED_GINKGO_WOOD = registerDecoMaterialBlock("stripped_ginkgo_wood", 64,
            props -> new LogBlock(logProperties(MapColor.COLOR_YELLOW, SoundType.WOOD).ignitedByLava()));
    public static final BlockEntry<FlamPlanksBlock> GINKGO_PLANKS = registerDecoMaterialBlock("ginkgo_planks", 64,
            props -> new FlamPlanksBlock(logProperties(MapColor.COLOR_YELLOW, SoundType.WOOD).ignitedByLava()));
    public static final BlockEntry<FlamStairBlock> GINKGO_STAIRS = registerDecoMaterialBlock("ginkgo_stairs", 64,
            props -> new FlamStairBlock(GINKGO_PLANKS.get().defaultBlockState(), BlockBehaviour.Properties.ofFullCopy(GINKGO_PLANKS.get())));
    public static final BlockEntry<FlamSlabBlock> GINKGO_SLAB = registerDecoMaterialBlock("ginkgo_slab", 64,
            props -> new FlamSlabBlock(BlockBehaviour.Properties.ofFullCopy(GINKGO_PLANKS.get())),
            (loot, block) -> loot.add(block, loot.createSlabItemTable(block)));
    public static final BlockEntry<FlamVerticalSlabBlock> GINKGO_VERTICAL_SLAB = registerDecoMaterialBlock("ginkgo_vertical_slab", 64,
            props -> new FlamVerticalSlabBlock(BlockBehaviour.Properties.ofFullCopy(GINKGO_PLANKS.get())),
            PoBlocks::createVerticalSlabDrops);
    public static final BlockEntry<ButtonBlock> GINKGO_BUTTON = registerDecoMaterialBlock("ginkgo_button", 64,
            props -> new ButtonBlock(BlockSetType.OAK, 30, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_BUTTON)));
    public static final BlockEntry<PressurePlateBlock> GINKGO_PRESSURE_PLATE = registerDecoMaterialBlock("ginkgo_pressure_plate", 64,
            props -> new PressurePlateBlock(BlockSetType.OAK, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PRESSURE_PLATE)));
    public static final BlockEntry<FlamFenceBlock> GINKGO_FENCE = registerDecoMaterialBlock("ginkgo_fence", 64,
            props -> new FlamFenceBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_FENCE).mapColor(MapColor.COLOR_YELLOW)));
    public static final BlockEntry<FlamFenceGateBlock> GINKGO_FENCE_GATE = registerDecoMaterialBlock("ginkgo_fence_gate", 64,
            props -> new FlamFenceGateBlock(WoodType.OAK, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_FENCE_GATE).mapColor(MapColor.COLOR_YELLOW)));
    public static final BlockEntry<DoorBlock> GINKGO_DOOR = registerDecoMaterialBlock("ginkgo_door", 64,
            props -> new DoorBlock(BlockSetType.OAK, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_DOOR).mapColor(MapColor.COLOR_YELLOW)),
            (loot, block) -> loot.add(block, loot.createDoorTable(block)));
    public static final BlockEntry<TrapDoorBlock> GINKGO_TRAPDOOR = registerDecoMaterialBlock("ginkgo_trapdoor", 64,
            props -> new TrapDoorBlock(BlockSetType.OAK, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_TRAPDOOR).mapColor(MapColor.COLOR_YELLOW)));
    public static final BlockEntry<ParticleLeavesBlock> POOP_LEAVES = registerBlock("poop_leaves", 88,
            props -> new ParticleLeavesBlock(0x5E4228, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_LEAVES)
                    .mapColor(MapColor.COLOR_BROWN)
                    .sound(SoundType.SCULK_SENSOR)),
            (loot, block) -> loot.add(block, createLeavesDrops(loot, block, PoItems.POOP.get())));
    public static final BlockEntry<ParticleLeavesBlock> POOP_LEAVES_IRON = registerBlock("poop_leaves_iron", 88,
            props -> new ParticleLeavesBlock(0xFFFFFF, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_LEAVES)
                    .mapColor(MapColor.TERRACOTTA_WHITE)
                    .sound(SoundType.SCULK_SENSOR)),
            (loot, block) -> loot.add(block, createIronLeavesDrops(loot, block)));
    public static final BlockEntry<ParticleLeavesBlock> POOP_LEAVES_GOLD = registerBlock("poop_leaves_gold", 88,
            props -> new ParticleLeavesBlock(0xFFD700, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_LEAVES)
                    .mapColor(MapColor.COLOR_YELLOW)
                    .sound(SoundType.SCULK_SENSOR)),
            (loot, block) -> loot.add(block, createGoldLeavesDrops(loot, block)));
    public static final BlockEntry<LeavesBlock> GINKGO_LEAVES = registerBlock("ginkgo_leaves", 64,
            props -> new ParticleLeavesBlock(0xF0DB3E, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_LEAVES)
                    .mapColor(MapColor.COLOR_YELLOW)),
            (loot, block) -> loot.add(block, createGinkgoLeavesDrops(loot, block)));
    public static final BlockEntry<PoopTreeBlock> POOP_SAPLING = registerBlock("poop_sapling", 88,
            props -> new PoopTreeBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BROWN)
                    .noCollission()
                    .noOcclusion()
                    .instabreak()
                    .randomTicks()
                    .sound(SoundType.MUD)
                    .offsetType(BlockBehaviour.OffsetType.XZ)
                    .pushReaction(PushReaction.DESTROY)));
    public static final BlockEntry<SaplingBlock> GINKGO_SAPLING = registerBlock("ginkgo_sapling", 64,
            props -> new SaplingBlock(PoTreeGrower.GINKGO, BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_YELLOW)
                    .noCollission()
                    .instabreak()
                    .randomTicks()
                    .sound(SoundType.GRASS)
                    .offsetType(BlockBehaviour.OffsetType.XZ)
                    .pushReaction(PushReaction.DESTROY)));
    public static final BlockEntry<FlowerPotBlock> POTTED_GINKGO_SAPLING = registerBlockNoItem("potted_ginkgo_sapling",
            props -> new FlowerPotBlock(() -> (FlowerPotBlock) Blocks.FLOWER_POT, GINKGO_SAPLING,
                    BlockBehaviour.Properties.ofFullCopy(Blocks.POTTED_OAK_SAPLING)),
            RegistrateBlockLootTables::dropPottedContents);

    public static final BlockEntry<SaltpeterBlock> SALTPETER_BLOCK = registerBlock("saltpeter_block",
            props -> new SaltpeterBlock(BlockBehaviour.Properties.of()
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
                    .randomTicks()
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

    public static final BlockEntry<Block> MAGGOTS_BLOCK = registerBlock("maggots_block", 88,
            props -> new Block(simpleProperties(MapColor.TERRACOTTA_WHITE, POOP, SoundType.WEEPING_VINES)));
    public static final BlockEntry<Block> ROUNDWORM_BLOCK = registerBlock("roundworm_block", 88,
            props -> new Block(simpleProperties(MapColor.TERRACOTTA_WHITE, POOP, SoundType.TWISTING_VINES)));

    public static final BlockEntry<MaggotsBlock> MAGGOTS = registerBlockNoItem("maggots",
            props -> new MaggotsBlock(plantProperties(MapColor.COLOR_YELLOW, SoundType.CROP)
                    .noCollission()
                    .randomTicks()),
            PoBlocks::createMaggotsLoot);
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
            PoBlocks::createRoundwormVinesPlantLoot);

    public static final BlockEntry<ChiliVinesBlock> CHILI_VINES = registerBlockNoItem("chili_vines",
            props -> new ChiliVinesBlock(
                    plantProperties(MapColor.PLANT, SoundType.CAVE_VINES)
                            .lightLevel(ChiliVines.emission(1))
                            .noCollission()),
            (loot, block) -> loot.add(block, createChiliVinesDrop(block)));

    public static final BlockEntry<ChiliVinesPlantBlock> CHILI_VINES_PLANT = registerBlockNoItem("chili_vines_plant",
            props -> new ChiliVinesPlantBlock(
                    plantProperties(MapColor.PLANT, SoundType.CAVE_VINES)
                            .lightLevel(ChiliVines.emission(1))
                            .noCollission()),
            (loot, block) -> loot.add(block, createChiliVinesDrop(block)));

    // Toilet
    public static final BlockEntry<WoodToiletBlock> WOODEN_TOILET = registerToiletBlock("wooden_toilet",
            props -> new WoodToiletBlock(toiletProperties(MapColor.WOOD, WOODEN_STRENGTH, SoundType.WOOD, NoteBlockInstrument.BASS)
                    .randomTicks()
                    .ignitedByLava()),
            (loot, block) -> loot.add(block, createToiletDrop(block)));

    public static final BlockEntry<HardToiletBlock> HARD_TOILET = registerToiletBlock("hard_toilet",
            props -> new HardToiletBlock(toiletProperties(MapColor.STONE, HARD_STRENGTH, SoundType.STONE, NoteBlockInstrument.BASEDRUM)
                    .lightLevel(lavaLightLevel())
                    .requiresCorrectToolForDrops()
                    .randomTicks()
                    .ignitedByLava()),
            (loot, block) -> loot.add(block, createToiletDrop(block)));

    public static final BlockEntry<FlushToiletBlock> FLUSH_TOILET = registerFlushToilet("flush_toilet", DyeColor.WHITE);
    public static final BlockEntry<FlushToiletBlock> GOLDEN_FLUSH_TOILET = registerFlushToilet("golden_flush_toilet", DyeColor.YELLOW);

    public static final BlockEntry<PortableToiletBlock> GINKGO_TOILET = registerBlock("ginkgo_toilet", 8,
            props -> new PortableToiletBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_YELLOW)
                    .strength(LOG, HARD_STRENGTH)
                    .sound(SoundType.WOOD)
                    .noOcclusion()),
            (loot, block) -> loot.add(block, loot.createDoorTable(block)));
    public static final BlockEntry<PortableToiletBlock> PORTABLE_TOILET = registerBlock("portable_toilet", 8,
            props -> new PortableToiletBlock(BlockBehaviour.Properties.of()
                    .mapColor(DyeColor.WHITE)
                    .strength(HARDEN, HARD_STRENGTH)
                    .sound(SoundType.NETHERITE_BLOCK)
                    .noOcclusion()),
            (loot, block) -> loot.add(block, loot.createDoorTable(block)));

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

    private record ColoredTile(BlockEntry<Block> block, BlockFamily family) {
    }

    public static final List<BlockFamily> HARDENED_POOP_FAMILIES = List.of(POOP_BRICK_FAMILY, MOSSY_POOP_BRICK_FAMILY, DRIED_POOP_BLOCK_FAMILY, SMOOTH_POOP_BLOCK_FAMILY, CUT_POOP_BLOCK_FAMILY);
    public static final List<BlockFamily> COLORED_TILE_BLOCK_FAMILIES = List.of(
            WHITE_TILE_BLOCK_FAMILY, LIGHT_GRAY_TILE_BLOCK_FAMILY, GRAY_TILE_BLOCK_FAMILY, BLACK_TILE_BLOCK_FAMILY,
            BROWN_TILE_BLOCK_FAMILY, RED_TILE_BLOCK_FAMILY, ORANGE_TILE_BLOCK_FAMILY, YELLOW_TILE_BLOCK_FAMILY,
            LIME_TILE_BLOCK_FAMILY, GREEN_TILE_BLOCK_FAMILY, CYAN_TILE_BLOCK_FAMILY, LIGHT_BLUE_TILE_BLOCK_FAMILY,
            BLUE_TILE_BLOCK_FAMILY, PURPLE_TILE_BLOCK_FAMILY, MAGENTA_TILE_BLOCK_FAMILY, PINK_TILE_BLOCK_FAMILY);
    public static final List<BlockFamily> SIMPLE_MODEL_FAMILIES = Stream.of(
            List.of(CHILI_POOP_FAMILY, GOLDEN_POOP_FAMILY, POOP_BRICK_FAMILY, MOSSY_POOP_BRICK_FAMILY, DRIED_POOP_BLOCK_FAMILY, SMOOTH_POOP_BLOCK_FAMILY, CUT_POOP_BLOCK_FAMILY),
            COLORED_TILE_BLOCK_FAMILIES
    ).flatMap(List::stream).toList();
    public static final List<BlockFamily> WALL_TAG_FAMILIES = withPoopFamily(SIMPLE_MODEL_FAMILIES);

    private static List<BlockFamily> withPoopFamily(List<BlockFamily> input) {
        return Stream.concat(input.stream(), Stream.of(POOP_FAMILY)).toList();
    }

    private static BlockEntry<ShitBlock> registerShitBlock(String name) {
        return registerBlock(name, 88,
                props -> new ShitBlock(poopProperties(0.1F)
                        .pushReaction(PushReaction.DESTROY)));
    }

    private static BlockEntry<FlushToiletBlock> registerFlushToilet(String name, DyeColor color) {
        return registerBlock(name, 64,
                props -> new FlushToiletBlock(BlockBehaviour.Properties.of()
                        .mapColor(color)
                        .strength(HARDEN, HARD_STRENGTH)
                        .requiresCorrectToolForDrops()
                        .noOcclusion()));
    }

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

    private static BlockBehaviour.Properties logProperties(MapColor color, SoundType sound) {
        return simpleProperties(color, LOG, sound).instrument(NoteBlockInstrument.BASS);
    }

    private static BlockBehaviour.Properties plantProperties(MapColor color, SoundType sound) {
        return BlockBehaviour.Properties.of()
                .mapColor(color)
                .instabreak()
                .sound(sound)
                .pushReaction(PushReaction.DESTROY);
    }

    private static BlockBehaviour.Properties toiletProperties(MapColor color, float strength, SoundType
            sound, NoteBlockInstrument instrument) {
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
        Block[] POOP_CAKE_CANDLES = {
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
        for (Block candle : POOP_CAKE_CANDLES) {
            candleCakes.put(candle, registerPoopCandleCake(candle));
        }
        return Collections.unmodifiableMap(candleCakes);
    }

    private static Map<DyeColor, ColoredTile> registerColoredTiles() {
        List<DyeColor> colors = List.of(
                DyeColor.WHITE, DyeColor.LIGHT_GRAY, DyeColor.GRAY, DyeColor.BLACK,
                DyeColor.BROWN, DyeColor.RED, DyeColor.ORANGE, DyeColor.YELLOW,
                DyeColor.LIME, DyeColor.GREEN, DyeColor.CYAN, DyeColor.LIGHT_BLUE,
                DyeColor.BLUE, DyeColor.PURPLE, DyeColor.MAGENTA, DyeColor.PINK);
        Map<DyeColor, ColoredTile> tiles = new EnumMap<>(DyeColor.class);
        for (DyeColor color : colors) {
            String name = color.getName() + "_tile_block";
            BlockEntry<Block> block = registerTileBlock(name, 64, props -> new Block(hardenedProperties(color.getMapColor(), SoundType.STONE)));
            tiles.put(color, new ColoredTile(block, registerTileBlockFamily(name, block)));
        }
        return Collections.unmodifiableMap(tiles);
    }

    private static ColoredTile coloredTile(DyeColor color) {
        return Objects.requireNonNull(COLORED_TILES.get(color), "Missing colored tile for " + color.getName());
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

    public static @Nullable BlockState getPoopCandleCake(CandleBlock candle) {
        BlockEntry<PoopCandleCakeBlock> candleCake = POOP_CANDLE_CAKES.get(candle);
        return candleCake == null ? null : candleCake.get().defaultBlockState();
    }

    private static boolean always(BlockState state, BlockGetter level, BlockPos pos) {
        return true;
    }

    public static <T extends
            Block> BlockEntry<T> registerBlock(String name, NonNullFunction<BlockBehaviour.Properties, T> factory) {
        return registerBlock(name, 64, factory, RegistrateBlockLootTables::dropSelf);
    }

    public static <T extends
            Block> BlockEntry<T> registerBlock(String name, NonNullFunction<BlockBehaviour.Properties, T> factory, NonNullBiConsumer<RegistrateBlockLootTables, T> loot) {
        return registerBlock(name, 64, factory, loot);
    }

    public static <T extends Block> BlockEntry<T> registerBlock(String name, int stackSize, NonNullFunction<
            BlockBehaviour.Properties, T> factory) {
        return registerBlock(name, stackSize, factory, RegistrateBlockLootTables::dropSelf);
    }

    public static <T extends Block> BlockEntry<T> registerBlock(String name, int stackSize, NonNullFunction<
            BlockBehaviour.Properties, T> factory, NonNullBiConsumer<RegistrateBlockLootTables, T> loot) {
        return registerBlockWithItem(name, stackSize, factory, loot, BlockItem::new, BlockTab.BASIC_BLOCKS);
    }

    public static <T extends Block> BlockEntry<T> registerDecoMaterialBlock(String name, int stackSize, NonNullFunction<BlockBehaviour.Properties, T> factory) {
        return registerDecoMaterialBlock(name, stackSize, factory, RegistrateBlockLootTables::dropSelf);
    }

    public static <T extends Block> BlockEntry<T> registerDecoMaterialBlock(String name, int stackSize, NonNullFunction<BlockBehaviour.Properties, T> factory, NonNullBiConsumer<RegistrateBlockLootTables, T> loot) {
        return registerBlockWithItem(name, stackSize, factory, loot, BlockItem::new, BlockTab.DECO_MATERIALS);
    }

    public static <T extends Block> BlockEntry<T> registerPoopBlock(String name, NonNullFunction<BlockBehaviour.Properties, T> factory) {
        return registerPoopBlock(name, factory, RegistrateBlockLootTables::dropSelf);
    }

    public static <T extends Block> BlockEntry<T> registerPoopBlock(String name, NonNullFunction<BlockBehaviour.Properties, T> factory, NonNullBiConsumer<RegistrateBlockLootTables, T> loot) {
        return registerBlockWithItem(name, 88, factory, loot, BlockItem::new, BlockTab.BASIC_BLOCKS, BlockTab.DECO_MATERIALS);
    }

    public static <T extends Block> BlockEntry<T> registerTileBlock(String name, int stackSize, NonNullFunction<BlockBehaviour.Properties, T> factory) {
        return registerTileBlock(name, stackSize, factory, RegistrateBlockLootTables::dropSelf);
    }

    public static <T extends Block> BlockEntry<T> registerTileBlock(String name, int stackSize, NonNullFunction<BlockBehaviour.Properties, T> factory, NonNullBiConsumer<RegistrateBlockLootTables, T> loot) {
        return registerBlockWithItem(name, stackSize, factory, loot, BlockItem::new, BlockTab.DECO_TILE);
    }

    public static <T extends
            Block> BlockEntry<T> registerBlockNoItem(String name, NonNullFunction<BlockBehaviour.Properties, T> factory, NonNullBiConsumer<RegistrateBlockLootTables, T> loot) {
        return REGISTRATE.block(name, factory)
                .blockstate((ctx, prov) -> {
                })
                .loot(loot)
                .register();
    }

    public static <T extends Block> BlockEntry<T> registerBlockNoTab(String name, int stackSize, NonNullFunction<BlockBehaviour.Properties, T> factory, NonNullBiConsumer<RegistrateBlockLootTables, T> loot) {
        return registerBlockWithItem(name, stackSize, factory, loot, BlockItem::new, BlockTab.NO_TAB);
    }

    private static <T extends Block> BlockEntry<T> registerBlockWithItem(
            String name,
            int stackSize,
            NonNullFunction<BlockBehaviour.Properties, T> factory,
            NonNullBiConsumer<RegistrateBlockLootTables, T> loot,
            BiFunction<Block, Item.Properties, ? extends BlockItem> itemFactory,
            BlockTab... tabs
    ) {
        BlockEntry<T> entry = REGISTRATE.block(name, factory)
                .blockstate((ctx, prov) -> {
                })
                .loot(loot)
                .item((b, p) -> itemFactory.apply(b, p.stacksTo(stackSize)))
                .model((ctx, prov) -> {
                })
                .build()
                .register();
        for (BlockTab tab : tabs) {
            TAB_ITEMS.computeIfAbsent(tab, ignored -> new HashSet<>()).add(entry);
        }
        return entry;
    }

    public static <T extends Block> BlockEntry<T> registerCompooperBlock(String name, NonNullFunction<BlockBehaviour.Properties, T> factory, NonNullBiConsumer<RegistrateBlockLootTables, T> loot) {
        return registerBlockWithItem(name, 64, factory, loot, CompooperBlockItem::new, BlockTab.BASIC_BLOCKS);
    }

    public static <T extends Block> BlockEntry<T> registerToiletBlock(String name, NonNullFunction<BlockBehaviour.Properties, T> factory, NonNullBiConsumer<RegistrateBlockLootTables, T> loot) {
        return registerBlockWithItem(name, 88, factory, loot, ToiletBlockItem::new, BlockTab.BASIC_BLOCKS);
    }

    private static BlockBehaviour.Properties familyProperties(Block base) {
        return BlockBehaviour.Properties.ofFullCopy(base)
                .isValidSpawn(PoBlocks::defaultSpawnable);
    }

    private static boolean defaultSpawnable(BlockState state, BlockGetter level, BlockPos pos, EntityType<?> entityType) {
        return state.isFaceSturdy(level, pos, Direction.UP)
                && state.getLightEmission(level, pos) < 14;
    }

    private static BlockFamily registerBlockFamily(String name, BlockEntry<? extends Block> base, boolean defaultBlockItem) {
        int stackSize = defaultBlockItem ? 64 : 88;
        return new BlockFamily(
                base,
                registerDecoMaterialBlock(name + "_stairs", stackSize,
                        props -> new StairBlock(base.get().defaultBlockState(), familyProperties(base.get()))),
                registerDecoMaterialBlock(name + "_slab", stackSize,
                        props -> new SlabBlock(familyProperties(base.get())),
                        (loot, block) -> loot.add(block, loot.createSlabItemTable(block))),
                registerDecoMaterialBlock(name + "_vertical_slab", stackSize,
                        props -> new VerticalSlabBlock(familyProperties(base.get())), PoBlocks::createVerticalSlabDrops),
                registerDecoMaterialBlock(name + "_wall", stackSize,
                        props -> new WallBlock(familyProperties(base.get())))
        );
    }

    private static BlockFamily registerTileBlockFamily(String name, BlockEntry<? extends Block> base) {
        int stackSize = 64;
        return new BlockFamily(
                base,
                registerTileBlock(name + "_stairs", stackSize,
                        props -> new StairBlock(base.get().defaultBlockState(), familyProperties(base.get()))),
                registerTileBlock(name + "_slab", stackSize,
                        props -> new SlabBlock(familyProperties(base.get())),
                        (loot, block) -> loot.add(block, loot.createSlabItemTable(block))),
                registerTileBlock(name + "_vertical_slab", stackSize,
                        props -> new VerticalSlabBlock(familyProperties(base.get())), PoBlocks::createVerticalSlabDrops),
                registerTileBlock(name + "_wall", stackSize,
                        props -> new WallBlock(familyProperties(base.get())))
        );
    }

    private static ToIntFunction<BlockState> lavaLightLevel() {
        return state -> state.getValue(BaseToiletLavaBlock.LAVA) ? LAVA_LIGHT_LEVEL : 0;
    }

    public static void register() {
        BuiltInRegistries.BLOCK.addAlias(
                PoopSky.loc("tile_block"),
                PoopSky.loc("cyan_tile_block")
        );
        BuiltInRegistries.BLOCK.addAlias(
                PoopSky.loc("tile_block_stairs"),
                PoopSky.loc("cyan_tile_block_stairs")
        );
        BuiltInRegistries.BLOCK.addAlias(
                PoopSky.loc("tile_block_slab"),
                PoopSky.loc("cyan_tile_block_slab")
        );
        BuiltInRegistries.BLOCK.addAlias(
                PoopSky.loc("tile_block_vertical_slab"),
                PoopSky.loc("cyan_tile_block_vertical_slab")
        );
        BuiltInRegistries.BLOCK.addAlias(
                PoopSky.loc("tile_block_wall"),
                PoopSky.loc("cyan_tile_block_wall")
        );
    }

    // Loot
    private static void createMaggotsLoot(RegistrateBlockLootTables loot, MaggotsBlock block) {
        LootItemCondition.Builder grownCondition = LootItemBlockStatePropertyCondition.hasBlockStateProperties(block)
                .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(CropBlock.AGE, CropBlock.MAX_AGE));
        var registrylookup = loot.getRegistries().lookupOrThrow(Registries.ENCHANTMENT);
        loot.add(block, loot.applyExplosionDecay(block,
                LootTable.lootTable()
                        .withPool(LootPool.lootPool()
                                .add(LootItem.lootTableItem(Items.BEETROOT_SEEDS).when(grownCondition).otherwise(LootItem.lootTableItem(PoItems.MAGGOTS_SEEDS.get())))
                                .add(LootItem.lootTableItem(Items.SWEET_BERRIES).when(grownCondition).otherwise(LootItem.lootTableItem(PoItems.MAGGOTS_SEEDS.get())))
                                .add(LootItem.lootTableItem(Items.CARROT).when(grownCondition).otherwise(LootItem.lootTableItem(PoItems.MAGGOTS_SEEDS.get())))
                                .add(LootItem.lootTableItem(Items.POTATO).when(grownCondition).otherwise(LootItem.lootTableItem(PoItems.MAGGOTS_SEEDS.get()))))
                        .withPool(LootPool.lootPool()
                                .when(grownCondition)
                                .add(LootItem.lootTableItem(PoItems.MAGGOTS_SEEDS.get())
                                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 3.0F)))
                                        .apply(ApplyBonusCount.addBonusBinomialDistributionCount(registrylookup.getOrThrow(Enchantments.FORTUNE), 0.5714286F, 3))
                                ))
        ));
    }

    private static void createRoundwormVinesPlantLoot
            (RegistrateBlockLootTables loot, RoundwormVinesPlantBlock block) {
        LootItemCondition.Builder seedsCondition = LootItemBlockStatePropertyCondition.hasBlockStateProperties(block)
                .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(RoundwormVinesPlantBlock.SEEDS, true));
        var registrylookup = loot.getRegistries().lookupOrThrow(Registries.ENCHANTMENT);
        loot.add(block, LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(Items.PUMPKIN_SEEDS).when(seedsCondition).otherwise(LootItem.lootTableItem(PoItems.ROUNDWORM.get())))
                        .add(LootItem.lootTableItem(Items.MELON_SEEDS).when(seedsCondition).otherwise(LootItem.lootTableItem(PoItems.ROUNDWORM.get())))
                        .add(LootItem.lootTableItem(Items.FROGSPAWN).when(seedsCondition).otherwise(LootItem.lootTableItem(PoItems.ROUNDWORM.get())))
                        .add(LootItem.lootTableItem(Items.BEETROOT_SEEDS).when(seedsCondition).otherwise(LootItem.lootTableItem(PoItems.ROUNDWORM.get()))))
                .withPool(LootPool.lootPool()
                        .when(seedsCondition)
                        .add(LootItem.lootTableItem(PoItems.ROUNDWORM.get())
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 3.0F)))
                                .apply(ApplyBonusCount.addBonusBinomialDistributionCount(registrylookup.getOrThrow(Enchantments.FORTUNE), 0.5714286F, 3))
                        ))
        );
    }

    private static LootTable.Builder createChiliVinesDrop(Block block) {
        return LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(PoItems.DRAGON_BREATH_CHILI.get()))
                        .when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(block)
                                .setProperties(StatePropertiesPredicate.Builder.properties()
                                        .hasProperty(ChiliVines.CHILI, true))));
    }

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
        LootItemCondition.Builder hasSilkTouch = hasSilkTouch(loot);

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
        return createNuggetLeavesDrops(loot, block, Items.IRON_NUGGET, 3.0F);
    }

    private static LootTable.Builder createGoldLeavesDrops(RegistrateBlockLootTables loot, Block block) {
        return createNuggetLeavesDrops(loot, block, Items.GOLD_NUGGET, 2.0F);
    }

    private static LootTable.Builder createNuggetLeavesDrops
            (RegistrateBlockLootTables loot, Block block, Item nugget, float maxCount) {
        var registrylookup = loot.getRegistries().lookupOrThrow(Registries.ENCHANTMENT);
        return createShearsOrSilkTouchDispatchTable(loot, block,
                LootItem.lootTableItem(nugget)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(0.0F, maxCount)))
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

    private static LootTable.Builder createGinkgoLeavesDrops(RegistrateBlockLootTables loot, Block block) {
        return createVanillaLeavesDrops(loot, block, PoBlocks.GINKGO_SAPLING.get());
    }

    private static LootTable.Builder createVanillaLeavesDrops
            (RegistrateBlockLootTables loot, Block block, Block sapling) {
        var registrylookup = loot.getRegistries().lookupOrThrow(Registries.ENCHANTMENT);
        LootItemCondition.Builder hasShearsOrSilkTouch = hasShearsOrSilkTouch(loot);
        return createShearsOrSilkTouchDispatchTable(loot, block,
                ((LootPoolSingletonContainer.Builder<?>)
                        loot.applyExplosionCondition(block, LootItem.lootTableItem(sapling)))
                        .when(BonusLevelTableCondition.bonusLevelFlatChance(registrylookup.getOrThrow(Enchantments.FORTUNE), LEAVES_SAPLING_CHANCES))
        ).withPool(LootPool.lootPool()
                .setRolls(ConstantValue.exactly(1.0F))
                .when(hasShearsOrSilkTouch.invert())
                .add(
                        ((LootPoolSingletonContainer.Builder<?>)
                                loot.applyExplosionDecay(block, LootItem.lootTableItem(Items.STICK)
                                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 2.0F)))))
                                .when(BonusLevelTableCondition.bonusLevelFlatChance(registrylookup.getOrThrow(Enchantments.FORTUNE), LEAVES_STICK_CHANCES)))
                .add(
                        ((LootPoolSingletonContainer.Builder<?>)
                                loot.applyExplosionDecay(block, LootItem.lootTableItem(Items.GOLDEN_APPLE)
                                        .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1.0F)))))
                                .when(BonusLevelTableCondition.bonusLevelFlatChance(registrylookup.getOrThrow(Enchantments.FORTUNE), LEAVES_STICK_CHANCES)))
        );
    }

    private static LootTable.Builder createShearsOrSilkTouchDispatchTable
            (RegistrateBlockLootTables loot, Block block, LootPoolSingletonContainer.Builder<?> fallback) {
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
        return MatchTool.toolMatches(ItemPredicate.Builder.item().of(Items.SHEARS))
                .or(hasSilkTouch(loot));
    }

    private static LootItemCondition.Builder hasSilkTouch(RegistrateBlockLootTables loot) {
        var registrylookup = loot.getRegistries().lookupOrThrow(Registries.ENCHANTMENT);
        return MatchTool.toolMatches(
                ItemPredicate.Builder.item()
                        .withSubPredicate(
                                ItemSubPredicates.ENCHANTMENTS,
                                ItemEnchantmentsPredicate.enchantments(
                                        List.of(new EnchantmentPredicate(registrylookup.getOrThrow(Enchantments.SILK_TOUCH), MinMaxBounds.Ints.atLeast(1)))
                                )
                        ));
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

    private static void createVerticalSlabDrops(RegistrateBlockLootTables loot, VerticalSlabBlock block) {
        loot.add(block, LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1.0F))
                        .add(loot.applyExplosionDecay(block, LootItem.lootTableItem(block)
                                .apply(SetItemCountFunction.setCount(ConstantValue.exactly(2.0F))
                                        .when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(block)
                                                .setProperties(StatePropertiesPredicate.Builder.properties()
                                                        .hasProperty(VerticalSlabBlock.DOUBLE, true))))))));
    }

    public static boolean isDecoMaterialItem(Item item) {
        return isInTab(item, BlockTab.DECO_MATERIALS);
    }

    public static boolean isDecoTileItem(Item item) {
        return isInTab(item, BlockTab.DECO_TILE);
    }

    public static boolean isBasicBlockItem(Item item) {
        return isInTab(item, BlockTab.BASIC_BLOCKS);
    }

    public static boolean isNoTabItem(Item item) {
        return isInTab(item, BlockTab.NO_TAB);
    }

    private static boolean isInTab(Item item, BlockTab tab) {
        return TAB_ITEMS.getOrDefault(tab, Set.of()).stream().anyMatch(entry -> entry.asItem() == item);
    }
}