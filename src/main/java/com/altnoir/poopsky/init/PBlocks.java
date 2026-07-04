package com.altnoir.poopsky.init;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.common.block.fluid.UrineLiquidBlock;
import com.altnoir.poopsky.common.block.p.*;
import com.altnoir.poopsky.common.item.p.CompooperBlockItem;
import com.altnoir.poopsky.common.item.p.ToiletBlockItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Supplier;
import java.util.function.ToIntFunction;

public class PBlocks {
    private static final float POOP = 0.5F;
    private static final float HARDEN = 1.5F;
    private static final float LOG = 2.0F;
    private static final float WOODEN_STRENGTH = 4.0F;
    private static final float HARD_STRENGTH = 10.0F;
    private static final float TOILET_RESISTANCE = 1200.0F;
    private static final int LAVA_LIGHT_LEVEL = 15;
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(PoopSky.MOD_ID);

    public static final DeferredBlock<Block> POOP_CAKE = registerBlock("poop_cake",
            () -> new PoopCakeBlock(poopCakeProperties())
    );

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

    private static final Map<Block, DeferredBlock<Block>> POOP_CANDLE_CAKES = registerPoopCandleCakes();

    public static final DeferredBlock<Block> POOP_PIECE = registerBlock("poop_piece",
            () -> new PoopPieceBlock(poopProperties(0.1F)
                    .replaceable()
                    .randomTicks()
                    .requiresCorrectToolForDrops()
                    .isViewBlocking((state, getter, pos) -> state.getValue(PoopPieceBlock.LAYERS) >= 8)
                    .pushReaction(PushReaction.DESTROY)
            )
    );
    public static final DeferredBlock<Block> POOP_BLOCK = registerBlock("poop_block",
            () -> new PoopBlock(poopProperties()
                    .randomTicks()
                    .speedFactor(0.4F)
                    .isValidSpawn(Blocks::always)
                    .isRedstoneConductor(PBlocks::always)
                    .isSuffocating(PBlocks::always)
                    .instrument(NoteBlockInstrument.COW_BELL))
    );
    public static final DeferredBlock<Block> POOLIME_MAGGOTS_BLOCK = registerBlock("poolime_maggots_block",
            () -> new PoolimeMaggotsBlock(poopProperties(1.0F)
                    .randomTicks()
                    .speedFactor(0.4F)
                    .isValidSpawn(Blocks::always)
                    .instrument(NoteBlockInstrument.COW_BELL))
    );
    public static final DeferredBlock<Block> POOP_STAIRS = registerBlock("poop_stairs",
            () -> new StairBlock(POOP_BLOCK.get().defaultBlockState(), poopProperties())
    );

    public static final DeferredBlock<Block> POOP_SLAB = registerBlock("poop_slab",
            () -> new SlabBlock(poopProperties())
    );
    public static final DeferredBlock<Block> POOP_VERTICAL_SLAB = registerBlock("poop_vertical_slab",
            () -> new VerticalSlabBlock(poopProperties())
    );
    public static final DeferredBlock<Block> POOP_BUTTON = registerBlock("poop_button",
            () -> new ButtonBlock(PBlockSetType.POOP, 200, poopProperties()
                    .noCollission())
    );
    public static final DeferredBlock<Block> POOP_PRESSURE_PLATE = registerBlock("poop_pressure_plate",
            () -> new PressurePlateBlock(PBlockSetType.POOP, poopProperties()
                    .noCollission())
    );
    public static final DeferredBlock<Block> POOP_FENCE = registerBlock("poop_fence",
            () -> new FenceBlock(poopProperties())
    );
    public static final DeferredBlock<Block> POOP_FENCE_GATE = registerBlock("poop_fence_gate",
            () -> new FenceGateBlock(PWoodType.POOP, poopProperties())
    );
    public static final DeferredBlock<Block> POOP_WALL = registerBlock("poop_wall",
            () -> new WallBlock(poopProperties())
    );
    public static final BlockFamily POOP_FAMILY = new BlockFamily(POOP_BLOCK, POOP_STAIRS, POOP_SLAB, POOP_VERTICAL_SLAB, POOP_WALL);

    public static final DeferredBlock<Block> POOP_DOOR = registerBlock("poop_door",
            () -> new DoorBlock(PBlockSetType.POOP, poopProperties()
                    .noOcclusion()
                    .pushReaction(PushReaction.DESTROY))
    );
    public static final DeferredBlock<Block> POOP_TRAPDOOR = registerBlock("poop_trapdoor",
            () -> new TrapDoorBlock(PBlockSetType.POOP, poopProperties()
                    .noOcclusion()
                    .isValidSpawn(Blocks::never))
    );

    public static final DeferredBlock<Block> STOOL = registerBlock("stool",
            () -> new ChairBlock(poopProperties()
                    .pushReaction(PushReaction.DESTROY)
                    .noOcclusion()
            )
    );
    public static final DeferredBlock<Block> POOLIME_BLOCK = registerBlock("poolime_block",
            () -> new PoolimeBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BROWN)
                    .friction(0.8F)
                    .sound(SoundType.SLIME_BLOCK)
                    .noOcclusion()
            )
    );

    public static final DeferredBlock<Block> POOP_BRICKS = registerBlock("poop_bricks",
            () -> new Block(hardenedProperties(MapColor.COLOR_BROWN, SoundType.FROGLIGHT))
    );
    public static final DeferredBlock<Block> CRACKED_POOP_BRICKS = registerBlock("cracked_poop_bricks",
            () -> new Block(BlockBehaviour.Properties.ofFullCopy(POOP_BRICKS.get()))
    );
    public static final BlockFamily POOP_BRICK_FAMILY = registerBlockFamily("poop_brick", POOP_BRICKS, false);
    public static final DeferredBlock<Block> POOP_BRICK_STAIRS = POOP_BRICK_FAMILY.stairs(), POOP_BRICK_SLAB = POOP_BRICK_FAMILY.slab(), POOP_BRICK_VERTICAL_SLAB = POOP_BRICK_FAMILY.verticalSlab(), POOP_BRICK_WALL = POOP_BRICK_FAMILY.wall();

    public static final DeferredBlock<Block> MOSSY_POOP_BRICKS = registerBlock("mossy_poop_bricks",
            () -> new Block(hardenedProperties(MapColor.COLOR_GREEN, SoundType.FROGLIGHT))
    );
    public static final BlockFamily MOSSY_POOP_BRICK_FAMILY = registerBlockFamily("mossy_poop_brick", MOSSY_POOP_BRICKS, false);
    public static final DeferredBlock<Block> MOSSY_POOP_BRICK_STAIRS = MOSSY_POOP_BRICK_FAMILY.stairs(), MOSSY_POOP_BRICK_SLAB = MOSSY_POOP_BRICK_FAMILY.slab(), MOSSY_POOP_BRICK_VERTICAL_SLAB = MOSSY_POOP_BRICK_FAMILY.verticalSlab(), MOSSY_POOP_BRICK_WALL = MOSSY_POOP_BRICK_FAMILY.wall();

    public static final DeferredBlock<Block> CHILI_POOP_BLOCK = registerBlock("chili_poop_block",
            () -> new ChiliPoopBlock(poopProperties()
                    .requiresCorrectToolForDrops()
                    .speedFactor(0.4F)
                    .isValidSpawn(Blocks::always)
                    .isRedstoneConductor(PBlocks::always)
                    .isSuffocating(PBlocks::always)
                    .instrument(NoteBlockInstrument.COW_BELL)
            )
    );
    public static final BlockFamily CHILI_POOP_FAMILY = registerBlockFamily("chili_poop", CHILI_POOP_BLOCK, false);
    public static final DeferredBlock<Block> CHILI_POOP_STAIRS = CHILI_POOP_FAMILY.stairs(), CHILI_POOP_SLAB = CHILI_POOP_FAMILY.slab(), CHILI_POOP_VERTICAL_SLAB = CHILI_POOP_FAMILY.verticalSlab(), CHILI_POOP_WALL = CHILI_POOP_FAMILY.wall();
    public static final DeferredBlock<Block> GOLDEN_POOP_BLOCK = registerBlock("golden_poop_block",
            () -> new GoldenPoopBlock(simpleProperties(MapColor.GOLD, 0.65F, SoundType.MUD)
                    .requiresCorrectToolForDrops()
                    .speedFactor(0.4F)
                    .isValidSpawn(Blocks::always)
                    .isRedstoneConductor(PBlocks::always)
                    .isSuffocating(PBlocks::always)
                    .instrument(NoteBlockInstrument.BELL)
            )
    );
    public static final BlockFamily GOLDEN_POOP_FAMILY = registerBlockFamily("golden_poop", GOLDEN_POOP_BLOCK, false);
    public static final DeferredBlock<Block> GOLDEN_POOP_STAIRS = GOLDEN_POOP_FAMILY.stairs(), GOLDEN_POOP_SLAB = GOLDEN_POOP_FAMILY.slab(), GOLDEN_POOP_VERTICAL_SLAB = GOLDEN_POOP_FAMILY.verticalSlab(), GOLDEN_POOP_WALL = GOLDEN_POOP_FAMILY.wall();

    public static final DeferredBlock<Block> DRIED_POOP_BLOCK = registerBlock("dried_poop_block",
            () -> new DriedPoopBlock(hardenedProperties(MapColor.COLOR_ORANGE, SoundType.TUFF)
                    .instrument(NoteBlockInstrument.COW_BELL))
    );
    public static final BlockFamily DRIED_POOP_BLOCK_FAMILY = registerBlockFamily("dried_poop_block", DRIED_POOP_BLOCK, false);
    public static final DeferredBlock<Block> DRIED_POOP_BLOCK_STAIRS = DRIED_POOP_BLOCK_FAMILY.stairs(), DRIED_POOP_BLOCK_SLAB = DRIED_POOP_BLOCK_FAMILY.slab(), DRIED_POOP_BLOCK_VERTICAL_SLAB = DRIED_POOP_BLOCK_FAMILY.verticalSlab(), DRIED_POOP_BLOCK_WALL = DRIED_POOP_BLOCK_FAMILY.wall();

    public static final DeferredBlock<Block> SMOOTH_POOP_BLOCK = registerBlock("smooth_poop_block",
            () -> new Block(hardenedProperties(MapColor.COLOR_ORANGE, SoundType.CALCITE))
    );
    public static final BlockFamily SMOOTH_POOP_BLOCK_FAMILY = registerBlockFamily("smooth_poop_block", SMOOTH_POOP_BLOCK, false);
    public static final DeferredBlock<Block> SMOOTH_POOP_BLOCK_STAIRS = SMOOTH_POOP_BLOCK_FAMILY.stairs(), SMOOTH_POOP_BLOCK_SLAB = SMOOTH_POOP_BLOCK_FAMILY.slab(), SMOOTH_POOP_BLOCK_VERTICAL_SLAB = SMOOTH_POOP_BLOCK_FAMILY.verticalSlab(), SMOOTH_POOP_BLOCK_WALL = SMOOTH_POOP_BLOCK_FAMILY.wall();

    public static final DeferredBlock<Block> CUT_POOP_BLOCK = registerBlock("cut_poop_block",
            () -> new Block(hardenedProperties(MapColor.COLOR_ORANGE, SoundType.POLISHED_TUFF))
    );
    public static final BlockFamily CUT_POOP_BLOCK_FAMILY = registerBlockFamily("cut_poop_block", CUT_POOP_BLOCK, false);
    public static final DeferredBlock<Block> CUT_POOP_BLOCK_STAIRS = CUT_POOP_BLOCK_FAMILY.stairs(), CUT_POOP_BLOCK_SLAB = CUT_POOP_BLOCK_FAMILY.slab(), CUT_POOP_BLOCK_VERTICAL_SLAB = CUT_POOP_BLOCK_FAMILY.verticalSlab(), CUT_POOP_BLOCK_WALL = CUT_POOP_BLOCK_FAMILY.wall();

    public static final DeferredBlock<Block> TILE_BLOCK = registerDefaultBlock("tile_block",
            () -> new Block(hardenedProperties(MapColor.COLOR_LIGHT_BLUE, SoundType.STONE))
    );
    public static final BlockFamily TILE_BLOCK_FAMILY = registerBlockFamily("tile_block", TILE_BLOCK, true);
    public static final DeferredBlock<Block> TILE_BLOCK_STAIRS = TILE_BLOCK_FAMILY.stairs(), TILE_BLOCK_SLAB = TILE_BLOCK_FAMILY.slab(), TILE_BLOCK_VERTICAL_SLAB = TILE_BLOCK_FAMILY.verticalSlab(), TILE_BLOCK_WALL = TILE_BLOCK_FAMILY.wall();

    public static final DeferredBlock<Block> COMPOOPER = registerCompooperBlock("compooper",
            () -> new CompooperBlock(simpleProperties(MapColor.COLOR_BROWN, 0.6F, SoundType.METAL)
                    .noOcclusion()
                    .instrument(NoteBlockInstrument.BASS)
                    .requiresCorrectToolForDrops()
            )
    );
    public static final DeferredBlock<Block> WATER_COMPOOPER = registerDefaultBlock("water_compooper",
            () -> new WaterCompooperBlock(BlockBehaviour.Properties.ofFullCopy(COMPOOPER.get()))
    );
    public static final DeferredBlock<Block> LAVA_COMPOOPER = registerDefaultBlock("lava_compooper",
            () -> new LavaCompooperBlock(BlockBehaviour.Properties.ofFullCopy(COMPOOPER.get()).lightLevel(state -> 15))
    );
    public static final DeferredBlock<Block> POWDER_SNOW_COMPOOPER = registerDefaultBlock("powder_snow_compooper",
            () -> new PowderSnowCompooperBlock(BlockBehaviour.Properties.ofFullCopy(COMPOOPER.get()))
    );
    public static final DeferredBlock<Block> URINE_COMPOOPER = registerDefaultBlock("urine_compooper",
            () -> new UrineCompooperBlock(BlockBehaviour.Properties.ofFullCopy(COMPOOPER.get()).randomTicks())
    );
    public static final DeferredBlock<Block> PLACER = registerDefaultBlock("placer",
            () -> new PlacerBlock(BlockBehaviour.Properties.of().mapColor(MapColor.STONE)
                    .instrument(NoteBlockInstrument.BASEDRUM)
                    .requiresCorrectToolForDrops().strength(3.5F)
            )
    );
    public static final DeferredBlock<SieveBlock> SIEVE = registerDefaultBlock("sieve_stable",
            () -> new SieveBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.STONE)
                    .strength(1.5f, 3.0f)
                    .requiresCorrectToolForDrops()
                    .isValidSpawn(Blocks::never)
                    .noOcclusion()));

    public static final DeferredBlock<Block> POOP_TNT = registerBlock("poop_tnt",
            () -> new PoopTntBlock(simpleProperties(MapColor.FIRE, 0.0F, SoundType.GRASS)
                    .ignitedByLava()
                    .pushReaction(PushReaction.DESTROY)));
    public static final DeferredBlock<Block> FLY_BARREL = registerDefaultBlock("fly_barrel",
            () -> new FlyBarrelBlock(simpleProperties(MapColor.COLOR_BROWN, 0.5F, SoundType.WOOD)
                    .noOcclusion())
    );
    public static final DeferredBlock<Block> BREEDING_CHEST = registerDefaultBlock("breeding_chest",
            () -> new BreedingChestBlock(simpleProperties(MapColor.COLOR_BROWN, 1.0F, SoundType.POLISHED_TUFF)
                    .requiresCorrectToolForDrops()
                    .noOcclusion())
    );

    public static final DeferredBlock<Block> RAW_POOP_BLOCK = registerBlock("raw_poop_block",
            () -> new Block(simpleProperties(MapColor.COLOR_BROWN, 0.65F, SoundType.MUD)
                    .randomTicks()
                    .isValidSpawn(Blocks::always)
                    .instrument(NoteBlockInstrument.COW_BELL))
    );
    public static final DeferredBlock<Block> RAW_SAPLING_POOP_BLOCK = registerBlock("raw_sapling_poop_block",
            () -> new RawSaplingBlock(BlockBehaviour.Properties.ofFullCopy(RAW_POOP_BLOCK.get()).sound(SoundType.ROOTED_DIRT))
    );
    public static final DeferredBlock<Block> RAW_SEA_POOP_BLOCK = registerBlock("raw_sea_poop_block",
            () -> new RawSeaBlock(BlockBehaviour.Properties.ofFullCopy(RAW_POOP_BLOCK.get()).sound(SoundType.ROOTED_DIRT))
    );
    public static final DeferredBlock<Block> RAW_WITHER_POOP_BLOCK = registerBlock("raw_wither_poop_block",
            () -> new RawWitherBlock(BlockBehaviour.Properties.ofFullCopy(RAW_POOP_BLOCK.get()).sound(SoundType.ROOTED_DIRT))
    );

    public static final DeferredBlock<Block> POOP_LOG = registerBlock("poop_log",
            () -> new PoopLogBlock(logProperties(SoundType.STEM)
                    .randomTicks()
            )
    );
    public static final DeferredBlock<Block> POOP_EMPTY_LOG = registerBlock("poop_empty_log",
            () -> new PoopEmptyLogBlock(logProperties(SoundType.BAMBOO_WOOD)
                    .noOcclusion()
            )
    );
    public static final DeferredBlock<Block> STRIPPED_POOP_LOG = registerBlock("stripped_poop_log",
            () -> new PoopLogBlock(logProperties(SoundType.STEM)
                    .randomTicks()
            )
    );
    public static final DeferredBlock<Block> STRIPPED_POOP_EMPTY_LOG = registerBlock("stripped_poop_empty_log",
            () -> new PoopEmptyLogBlock(logProperties(SoundType.BAMBOO_WOOD)
                    .noOcclusion()
            )
    );

    public static final DeferredBlock<Block> POOP_LEAVES = registerBlock("poop_leaves",
            () -> new PoopLeavesBlock(0x5E4228, leavesProperties(MapColor.COLOR_BROWN))
    );
    public static final DeferredBlock<Block> POOP_LEAVES_IRON = registerBlock("poop_leaves_iron",
            () -> new PoopLeavesBlock(0xFFFFFF, leavesProperties(MapColor.TERRACOTTA_WHITE))
    );
    public static final DeferredBlock<Block> POOP_LEAVES_GOLD = registerBlock("poop_leaves_gold",
            () -> new PoopLeavesBlock(0xFFD700, leavesProperties(MapColor.COLOR_YELLOW))
    );

    public static final DeferredBlock<Block> POOP_SAPLING = registerBlock("poop_sapling",
            () -> new PoopTreeBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BROWN)
                    .noCollission()
                    .noOcclusion()
                    .instabreak()
                    .sound(SoundType.MUD)
                    .offsetType(BlockBehaviour.OffsetType.XZ)
                    .pushReaction(PushReaction.DESTROY)
            )
    );

    public static final DeferredBlock<LiquidBlock> URINE_LIQUID = registerBlock("urine_liquid",
            () -> new UrineLiquidBlock(
                    PFluids.URINE.get(),
                    BlockBehaviour.Properties.of()
                            .mapColor(MapColor.COLOR_BROWN)
                            .replaceable()
                            .noCollission()
                            .randomTicks()
                            .strength(100.0F)
                            .lightLevel(state -> 7)
                            .pushReaction(PushReaction.DESTROY)
                            .noLootTable()
                            .liquid()
                            .sound(SoundType.EMPTY)
            )
    );

    public static final DeferredBlock<Block> MAGGOTS_BLOCK = registerBlock("maggots_block",
            () -> new Block(simpleProperties(MapColor.TERRACOTTA_WHITE, POOP, SoundType.WEEPING_VINES))
    );
    public static final DeferredBlock<Block> ROUNDWORM_BLOCK = registerBlock("roundworm_block",
            () -> new Block(simpleProperties(MapColor.TERRACOTTA_WHITE, POOP, SoundType.TWISTING_VINES))
    );
    public static final DeferredBlock<Block> MAGGOTS = BLOCKS.register("maggots",
            () -> new MaggotsBlock(plantProperties(MapColor.COLOR_YELLOW, SoundType.CROP)
                    .noCollission()
                    .randomTicks()
            )
    );
    public static final DeferredBlock<Block> ROUNDWORM_VINES = BLOCKS.register("roundworm_vines",
            () -> new RoundwormVinesBlock(
                    plantProperties(MapColor.TERRACOTTA_WHITE, SoundType.TWISTING_VINES)
                            .randomTicks()
                            .noCollission()
            )
    );
    public static final DeferredBlock<Block> ROUNDWORM_VINES_PLANT = BLOCKS.register("roundworm_vines_plant",
            () -> new RoundwormVinesPlantBlock(
                    plantProperties(MapColor.TERRACOTTA_WHITE, SoundType.TWISTING_VINES)
                            .noCollission()
            )
    );

    // Toilet
    public static final DeferredBlock<Block> WOODEN_TOILET = registerToiletBlock(
            "wooden_toilet",
            () -> new ToiletBlock(toiletProperties(MapColor.WOOD, WOODEN_STRENGTH, SoundType.WOOD, NoteBlockInstrument.BASS)
                    .ignitedByLava())
    );

    public static final DeferredBlock<Block> HARD_TOILET = registerToiletBlock(
            "hard_toilet",
            () -> new LavaToiletBlock(toiletProperties(MapColor.STONE, HARD_STRENGTH, SoundType.STONE, NoteBlockInstrument.BASEDRUM)
                    .lightLevel(lavaLightLevel())
                    .requiresCorrectToolForDrops()
                    .ignitedByLava())
    );

    public record BlockFamily(
            DeferredBlock<Block> block,
            DeferredBlock<Block> stairs,
            DeferredBlock<Block> slab,
            DeferredBlock<Block> verticalSlab,
            DeferredBlock<Block> wall
    ) {
        public List<DeferredBlock<Block>> blocks() {
            return List.of(block, stairs, slab, verticalSlab, wall);
        }
    }

    public static final List<BlockFamily> POOP_BUILDING_FAMILIES = List.of(POOP_FAMILY, CHILI_POOP_FAMILY, GOLDEN_POOP_FAMILY);
    public static final List<BlockFamily> HARDENED_POOP_FAMILIES = List.of(POOP_BRICK_FAMILY, MOSSY_POOP_BRICK_FAMILY, DRIED_POOP_BLOCK_FAMILY, SMOOTH_POOP_BLOCK_FAMILY, CUT_POOP_BLOCK_FAMILY);
    public static final List<BlockFamily> SIMPLE_MODEL_FAMILIES = List.of(CHILI_POOP_FAMILY, GOLDEN_POOP_FAMILY, POOP_BRICK_FAMILY, MOSSY_POOP_BRICK_FAMILY, DRIED_POOP_BLOCK_FAMILY, SMOOTH_POOP_BLOCK_FAMILY, CUT_POOP_BLOCK_FAMILY, TILE_BLOCK_FAMILY);
    public static final List<BlockFamily> WALL_TAG_FAMILIES = List.of(POOP_FAMILY, CHILI_POOP_FAMILY, GOLDEN_POOP_FAMILY, DRIED_POOP_BLOCK_FAMILY, SMOOTH_POOP_BLOCK_FAMILY, CUT_POOP_BLOCK_FAMILY, TILE_BLOCK_FAMILY);

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
                .isSuffocating(PBlocks::neverSuffocate)
                .isViewBlocking(PBlocks::neverBlockVision)
                .ignitedByLava()
                .pushReaction(PushReaction.DESTROY)
                .isRedstoneConductor(PBlocks::never);
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
                .isRedstoneConductor(PBlocks::always)
                .isSuffocating(PBlocks::always)
                .sound(sound);
    }

    private static BlockBehaviour.Properties simpleProperties(MapColor color, float strength, SoundType sound) {
        return BlockBehaviour.Properties.of()
                .mapColor(color)
                .strength(strength)
                .sound(sound);
    }

    private static Map<Block, DeferredBlock<Block>> registerPoopCandleCakes() {
        Map<Block, DeferredBlock<Block>> candleCakes = new LinkedHashMap<>();
        for (Block candle : POOP_CAKE_CANDLES) {
            candleCakes.put(candle, registerPoopCandleCake(candle));
        }
        return Collections.unmodifiableMap(candleCakes);
    }

    private static DeferredBlock<Block> registerPoopCandleCake(Block candle) {
        if (!(candle instanceof CandleBlock)) {
            throw new IllegalArgumentException("Expected candle block: " + candle);
        }

        String candleName = BuiltInRegistries.BLOCK.getKey(candle).getPath();
        String name = candle == Blocks.CANDLE ? "poop_candle_cake" : candleName.replace("_candle", "_poop_candle_cake");

        return BLOCKS.register(name,
                () -> new PoopCandleCakeBlock(candle, poopCakeProperties()
                        .lightLevel(state -> state.getValue(PoopCandleCakeBlock.LIT) ? 3 : 0)));
    }

    public static Map<Block, DeferredBlock<Block>> getPoopCandleCakes() {
        return POOP_CANDLE_CAKES;
    }

    public static BlockState getPoopCandleCake(CandleBlock candle) {
        DeferredBlock<Block> candleCake = POOP_CANDLE_CAKES.get(candle);
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

    public static <T extends Block> DeferredBlock<T> registerDefaultBlock(String name, Supplier<T> block) {
        return registerSimpleBlock(name, block, new Item.Properties());
    }

    public static <T extends Block> DeferredBlock<T> registerCompooperBlock(String name, Supplier<T> block) {
        return registerBlockWithItem(name, block, PBlocks::registerCompooperBlockItem);
    }

    public static <T extends Block> DeferredBlock<T> registerToiletBlock(String name, Supplier<T> block) {
        return registerBlockWithItem(name, block, PBlocks::registerToiletBlockItem);
    }

    public static <T extends Block> DeferredBlock<T> registerBlock(String name, Supplier<T> block) {
        return registerSimpleBlock(name, block, new Item.Properties().stacksTo(88));
    }

    private static BlockFamily registerBlockFamily(String name, DeferredBlock<Block> base, boolean defaultBlockItem) {
        return new BlockFamily(
                base,
                registerFamilyBlock(name + "_stairs", () -> new StairBlock(base.get().defaultBlockState(), BlockBehaviour.Properties.ofFullCopy(base.get())), defaultBlockItem),
                registerFamilyBlock(name + "_slab", () -> new SlabBlock(BlockBehaviour.Properties.ofFullCopy(base.get())), defaultBlockItem),
                registerFamilyBlock(name + "_vertical_slab", () -> new VerticalSlabBlock(BlockBehaviour.Properties.ofFullCopy(base.get())), defaultBlockItem),
                registerFamilyBlock(name + "_wall", () -> new WallBlock(BlockBehaviour.Properties.ofFullCopy(base.get())), defaultBlockItem)
        );
    }

    private static DeferredBlock<Block> registerFamilyBlock(String name, Supplier<Block> block, boolean defaultBlockItem) {
        return defaultBlockItem ? registerDefaultBlock(name, block) : registerBlock(name, block);
    }

    private static ToIntFunction<BlockState> lavaLightLevel() {
        return state -> state.getValue(BaseToiletLavaBlock.LAVA) ? LAVA_LIGHT_LEVEL : 0;
    }

    private static <T extends Block> DeferredBlock<T> registerSimpleBlock(String name, Supplier<T> block, Item.Properties properties) {
        return registerBlockWithItem(name, block, (blockName, registeredBlock) -> PItems.ITEMS.registerSimpleBlockItem(blockName, registeredBlock, properties));
    }

    private static <T extends Block> DeferredBlock<T> registerBlockWithItem(String name, Supplier<T> block, BiConsumer<String, DeferredBlock<T>> itemRegistrar) {
        DeferredBlock<T> toReturn = BLOCKS.register(name, block);
        itemRegistrar.accept(name, toReturn);
        return toReturn;
    }

    private static <T extends Block> void registerCompooperBlockItem(String name, DeferredBlock<T> block) {
        PItems.ITEMS.register(name, () -> new CompooperBlockItem(block.get(), new Item.Properties()));
    }

    private static <T extends Block> void registerToiletBlockItem(String name, DeferredBlock<T> block) {
        PItems.ITEMS.register(name, () -> new ToiletBlockItem(block.get(), new Item.Properties().stacksTo(88)));
    }

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}
