package com.altnoir.poopsky.block;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.block.fluid.UrineLiquidBlock;
import com.altnoir.poopsky.block.p.*;
import com.altnoir.poopsky.init.PBlockSetType;
import com.altnoir.poopsky.init.PFluids;
import com.altnoir.poopsky.init.PWoodType;
import com.altnoir.poopsky.item.PItems;
import com.altnoir.poopsky.item.p.CompooperBlockItem;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.BlockItem;
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

import java.util.Map;
import java.util.function.Supplier;

public class PBlocks {
    private static final float POOP = 0.5F;
    private static final float HARDEN = 1.5F;
    private static final float LOG = 2.0F;
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(PoopSky.MOD_ID);

    public static final DeferredBlock<Block> POOP_CAKE = registerBlock("poop_cake",
            () -> new PoopCakeBlock(poopCakeProperties())
    );

    private static final Map<Block, DeferredBlock<Block>> POOP_CANDLE_CAKES = Map.ofEntries(
            registerPoopCandleCake("poop_candle_cake", Blocks.CANDLE),
            registerPoopCandleCake("white_poop_candle_cake", Blocks.WHITE_CANDLE),
            registerPoopCandleCake("orange_poop_candle_cake", Blocks.ORANGE_CANDLE),
            registerPoopCandleCake("magenta_poop_candle_cake", Blocks.MAGENTA_CANDLE),
            registerPoopCandleCake("light_blue_poop_candle_cake", Blocks.LIGHT_BLUE_CANDLE),
            registerPoopCandleCake("yellow_poop_candle_cake", Blocks.YELLOW_CANDLE),
            registerPoopCandleCake("lime_poop_candle_cake", Blocks.LIME_CANDLE),
            registerPoopCandleCake("pink_poop_candle_cake", Blocks.PINK_CANDLE),
            registerPoopCandleCake("gray_poop_candle_cake", Blocks.GRAY_CANDLE),
            registerPoopCandleCake("light_gray_poop_candle_cake", Blocks.LIGHT_GRAY_CANDLE),
            registerPoopCandleCake("cyan_poop_candle_cake", Blocks.CYAN_CANDLE),
            registerPoopCandleCake("purple_poop_candle_cake", Blocks.PURPLE_CANDLE),
            registerPoopCandleCake("blue_poop_candle_cake", Blocks.BLUE_CANDLE),
            registerPoopCandleCake("brown_poop_candle_cake", Blocks.BROWN_CANDLE),
            registerPoopCandleCake("green_poop_candle_cake", Blocks.GREEN_CANDLE),
            registerPoopCandleCake("red_poop_candle_cake", Blocks.RED_CANDLE),
            registerPoopCandleCake("black_poop_candle_cake", Blocks.BLACK_CANDLE)
    );

    public static final DeferredBlock<Block> POOP_PIECE = registerBlock("poop_piece",
            () -> new PoopPieceBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_BROWN)
                    .replaceable()
                    .randomTicks()
                    .strength(0.1F)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.MUD)
                    .isViewBlocking((state, getter, pos) -> state.getValue(PoopPieceBlock.LAYERS) >= 8)
                    .pushReaction(PushReaction.DESTROY)
            )
    );
    public static final DeferredBlock<Block> POOP_BLOCK = registerBlock("poop_block",
            () -> new PoopBlock(BlockBehaviour.Properties.of()
                    .randomTicks()
                    .strength(POOP)
                    .mapColor(MapColor.COLOR_BROWN)
                    .speedFactor(0.4F)
                    .isValidSpawn(Blocks::always)
                    .instrument(NoteBlockInstrument.COW_BELL)
                    .sound(SoundType.MUD))
    );
    public static final DeferredBlock<Block> POOLIME_POOP_BLOCK = registerBlock("poolime_poop_block",
            () -> new PoolimePoopBlock(BlockBehaviour.Properties.of()
                    .randomTicks()
                    .strength(1.0F)
                    .mapColor(MapColor.COLOR_BROWN)
                    .speedFactor(0.4F)
                    .isValidSpawn(Blocks::always)
                    .instrument(NoteBlockInstrument.COW_BELL)
                    .sound(SoundType.MUD))
    );
    public static final DeferredBlock<Block> POOP_STAIRS = registerBlock("poop_stairs",
            () -> new StairBlock(POOP_BLOCK.get().defaultBlockState(), BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_BROWN)
                    .strength(POOP)
                    .sound(SoundType.MUD))
    );

    public static final DeferredBlock<Block> POOP_SLAB = registerBlock("poop_slab",
            () -> new SlabBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_BROWN)
                    .strength(POOP)
                    .sound(SoundType.MUD))
    );
    public static final DeferredBlock<Block> POOP_VERTICAL_SLAB = registerBlock("poop_vertical_slab",
            () -> new VerticalSlabBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_BROWN)
                    .strength(POOP)
                    .sound(SoundType.MUD))
    );
    public static final DeferredBlock<Block> POOP_BUTTON = registerBlock("poop_button",
            () -> new ButtonBlock(PBlockSetType.POOP, 200, BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_BROWN)
                    .strength(POOP)
                    .sound(SoundType.MUD)
                    .noCollission())
    );
    public static final DeferredBlock<Block> POOP_PRESSURE_PLATE = registerBlock("poop_pressure_plate",
            () -> new PressurePlateBlock(PBlockSetType.POOP, BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_BROWN)
                    .strength(POOP)
                    .sound(SoundType.MUD)
                    .noCollission())
    );
    public static final DeferredBlock<Block> POOP_FENCE = registerBlock("poop_fence",
            () -> new FenceBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_BROWN)
                    .strength(POOP)
                    .sound(SoundType.MUD))
    );
    public static final DeferredBlock<Block> POOP_FENCE_GATE = registerBlock("poop_fence_gate",
            () -> new FenceGateBlock(PWoodType.POOP, BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_BROWN)
                    .strength(POOP)
                    .sound(SoundType.MUD))
    );
    public static final DeferredBlock<Block> POOP_WALL = registerBlock("poop_wall",
            () -> new WallBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_BROWN)
                    .strength(POOP)
                    .sound(SoundType.MUD))
    );

    public static final DeferredBlock<Block> POOP_DOOR = registerBlock("poop_door",
            () -> new DoorBlock(PBlockSetType.POOP, BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_BROWN)
                    .strength(POOP)
                    .sound(SoundType.MUD)
                    .noOcclusion()
                    .pushReaction(PushReaction.DESTROY))
    );
    public static final DeferredBlock<Block> POOP_TRAPDOOR = registerBlock("poop_trapdoor",
            () -> new TrapDoorBlock(PBlockSetType.POOP, BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_BROWN)
                    .strength(POOP)
                    .sound(SoundType.MUD)
                    .noOcclusion()
                    .isValidSpawn(Blocks::never))
    );

    public static final DeferredBlock<Block> STOOL = registerBlock("stool",
            () -> new ChairBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_BROWN)
                    .strength(POOP)
                    .sound(SoundType.MUD)
                    .pushReaction(PushReaction.DESTROY)
                    .noOcclusion()
            )
    );
    public static final DeferredBlock<Block> POOLIME_BLOCK = registerBlock("poolime_block",
            () -> new PoolimeBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_BROWN)
                    .friction(0.8F)
                    .sound(SoundType.SLIME_BLOCK)
                    .noOcclusion()
            )
    );

    public static final DeferredBlock<Block> POOP_BRICKS = registerBlock("poop_bricks",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_BROWN)
                    .requiresCorrectToolForDrops()
                    .strength(HARDEN)
                    .sound(SoundType.FROGLIGHT)
            )
    );
    public static final DeferredBlock<Block> CRACKED_POOP_BRICKS = registerBlock("cracked_poop_bricks",
            () -> new Block(BlockBehaviour.Properties.ofFullCopy(POOP_BRICKS.get()))
    );
    public static final DeferredBlock<Block> POOP_BRICK_STAIRS = registerBlock("poop_brick_stairs",
            () -> new StairBlock(POOP_BRICKS.get().defaultBlockState(), BlockBehaviour.Properties.ofFullCopy(POOP_BRICKS.get()))
    );
    public static final DeferredBlock<Block> POOP_BRICK_SLAB = registerBlock("poop_brick_slab",
            () -> new SlabBlock(BlockBehaviour.Properties.ofFullCopy(POOP_BRICKS.get()))
    );
    public static final DeferredBlock<Block> POOP_BRICK_VERTICAL_SLAB = registerBlock("poop_brick_vertical_slab",
            () -> new VerticalSlabBlock(BlockBehaviour.Properties.ofFullCopy(POOP_BRICKS.get()))
    );
    public static final DeferredBlock<Block> POOP_BRICK_WALL = registerBlock("poop_brick_wall",
            () -> new WallBlock(BlockBehaviour.Properties.ofFullCopy(POOP_BRICKS.get()))
    );

    public static final DeferredBlock<Block> MOSSY_POOP_BRICKS = registerBlock("mossy_poop_bricks",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_GREEN)
                    .requiresCorrectToolForDrops()
                    .strength(HARDEN)
                    .sound(SoundType.FROGLIGHT)
            )
    );
    public static final DeferredBlock<Block> MOSSY_POOP_BRICK_STAIRS = registerBlock("mossy_poop_brick_stairs",
            () -> new StairBlock(MOSSY_POOP_BRICKS.get().defaultBlockState(), BlockBehaviour.Properties.ofFullCopy(MOSSY_POOP_BRICKS.get()))
    );
    public static final DeferredBlock<Block> MOSSY_POOP_BRICK_SLAB = registerBlock("mossy_poop_brick_slab",
            () -> new SlabBlock(BlockBehaviour.Properties.ofFullCopy(MOSSY_POOP_BRICKS.get()))
    );
    public static final DeferredBlock<Block> MOSSY_POOP_BRICK_VERTICAL_SLAB = registerBlock("mossy_poop_brick_vertical_slab",
            () -> new VerticalSlabBlock(BlockBehaviour.Properties.ofFullCopy(MOSSY_POOP_BRICKS.get()))
    );
    public static final DeferredBlock<Block> MOSSY_POOP_BRICK_WALL = registerBlock("mossy_poop_brick_wall",
            () -> new WallBlock(BlockBehaviour.Properties.ofFullCopy(MOSSY_POOP_BRICKS.get()))
    );

    public static final DeferredBlock<Block> CHILI_POOP_BLOCK = registerBlock("chili_poop_block",
            () -> new ChiliPoopBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_BROWN)
                    .requiresCorrectToolForDrops()
                    .strength(POOP)
                    .speedFactor(0.4F)
                    .isValidSpawn(Blocks::always)
                    .instrument(NoteBlockInstrument.COW_BELL)
                    .sound(SoundType.MUD)
            )
    );
    public static final DeferredBlock<Block> CHILI_POOP_STAIRS = registerBlock("chili_poop_stairs",
            () -> new StairBlock(CHILI_POOP_BLOCK.get().defaultBlockState(), BlockBehaviour.Properties.ofFullCopy(CHILI_POOP_BLOCK.get()))
    );
    public static final DeferredBlock<Block> CHILI_POOP_SLAB = registerBlock("chili_poop_slab",
            () -> new SlabBlock(BlockBehaviour.Properties.ofFullCopy(CHILI_POOP_BLOCK.get()))
    );
    public static final DeferredBlock<Block> CHILI_POOP_VERTICAL_SLAB = registerBlock("chili_poop_vertical_slab",
            () -> new VerticalSlabBlock(BlockBehaviour.Properties.ofFullCopy(CHILI_POOP_BLOCK.get()))
    );
    public static final DeferredBlock<Block> CHILI_POOP_WALL = registerBlock("chili_poop_wall",
            () -> new WallBlock(BlockBehaviour.Properties.ofFullCopy(CHILI_POOP_BLOCK.get()))
    );
    public static final DeferredBlock<Block> GOLDEN_POOP_BLOCK = registerBlock("golden_poop_block",
            () -> new GoldenPoopBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.GOLD)
                    .requiresCorrectToolForDrops()
                    .strength(0.65F)
                    .speedFactor(0.4F)
                    .isValidSpawn(Blocks::always)
                    .instrument(NoteBlockInstrument.BELL)
                    .sound(SoundType.MUD)
            )
    );
    public static final DeferredBlock<Block> GOLDEN_POOP_STAIRS = registerBlock("golden_poop_stairs",
            () -> new StairBlock(GOLDEN_POOP_BLOCK.get().defaultBlockState(), BlockBehaviour.Properties.ofFullCopy(GOLDEN_POOP_BLOCK.get()))
    );
    public static final DeferredBlock<Block> GOLDEN_POOP_SLAB = registerBlock("golden_poop_slab",
            () -> new SlabBlock(BlockBehaviour.Properties.ofFullCopy(GOLDEN_POOP_BLOCK.get()))
    );
    public static final DeferredBlock<Block> GOLDEN_POOP_VERTICAL_SLAB = registerBlock("golden_poop_vertical_slab",
            () -> new VerticalSlabBlock(BlockBehaviour.Properties.ofFullCopy(GOLDEN_POOP_BLOCK.get()))
    );
    public static final DeferredBlock<Block> GOLDEN_POOP_WALL = registerBlock("golden_poop_wall",
            () -> new WallBlock(BlockBehaviour.Properties.ofFullCopy(GOLDEN_POOP_BLOCK.get()))
    );

    public static final DeferredBlock<Block> DRIED_POOP_BLOCK = registerBlock("dried_poop_block",
            () -> new DriedPoopBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_ORANGE)
                    .requiresCorrectToolForDrops()
                    .strength(HARDEN)
                    .instrument(NoteBlockInstrument.COW_BELL)
                    .sound(SoundType.TUFF))
    );
    public static final DeferredBlock<Block> DRIED_POOP_BLOCK_STAIRS = registerBlock("dried_poop_block_stairs",
            () -> new StairBlock(DRIED_POOP_BLOCK.get().defaultBlockState(), BlockBehaviour.Properties.ofFullCopy(DRIED_POOP_BLOCK.get()))
    );
    public static final DeferredBlock<Block> DRIED_POOP_BLOCK_SLAB = registerBlock("dried_poop_block_slab",
            () -> new SlabBlock(BlockBehaviour.Properties.ofFullCopy(DRIED_POOP_BLOCK.get()))
    );
    public static final DeferredBlock<Block> DRIED_POOP_BLOCK_VERTICAL_SLAB = registerBlock("dried_poop_block_vertical_slab",
            () -> new VerticalSlabBlock(BlockBehaviour.Properties.ofFullCopy(DRIED_POOP_BLOCK.get()))
    );
    public static final DeferredBlock<Block> DRIED_POOP_BLOCK_WALL = registerBlock("dried_poop_block_wall",
            () -> new WallBlock(BlockBehaviour.Properties.ofFullCopy(DRIED_POOP_BLOCK.get()))
    );

    public static final DeferredBlock<Block> SMOOTH_POOP_BLOCK = registerBlock("smooth_poop_block",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_ORANGE)
                    .requiresCorrectToolForDrops()
                    .strength(HARDEN)
                    .sound(SoundType.CALCITE))
    );
    public static final DeferredBlock<Block> SMOOTH_POOP_BLOCK_STAIRS = registerBlock("smooth_poop_block_stairs",
            () -> new StairBlock(SMOOTH_POOP_BLOCK.get().defaultBlockState(), BlockBehaviour.Properties.ofFullCopy(SMOOTH_POOP_BLOCK.get()))
    );
    public static final DeferredBlock<Block> SMOOTH_POOP_BLOCK_SLAB = registerBlock("smooth_poop_block_slab",
            () -> new SlabBlock(BlockBehaviour.Properties.ofFullCopy(SMOOTH_POOP_BLOCK.get()))
    );
    public static final DeferredBlock<Block> SMOOTH_POOP_BLOCK_VERTICAL_SLAB = registerBlock("smooth_poop_block_vertical_slab",
            () -> new VerticalSlabBlock(BlockBehaviour.Properties.ofFullCopy(SMOOTH_POOP_BLOCK.get()))
    );
    public static final DeferredBlock<Block> SMOOTH_POOP_BLOCK_WALL = registerBlock("smooth_poop_block_wall",
            () -> new WallBlock(BlockBehaviour.Properties.ofFullCopy(SMOOTH_POOP_BLOCK.get()))
    );

    public static final DeferredBlock<Block> CUT_POOP_BLOCK = registerBlock("cut_poop_block",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_ORANGE)
                    .requiresCorrectToolForDrops()
                    .strength(HARDEN)
                    .sound(SoundType.POLISHED_TUFF))
    );
    public static final DeferredBlock<Block> CUT_POOP_BLOCK_STAIRS = registerBlock("cut_poop_block_stairs",
            () -> new StairBlock(CUT_POOP_BLOCK.get().defaultBlockState(), BlockBehaviour.Properties.ofFullCopy(CUT_POOP_BLOCK.get()))
    );
    public static final DeferredBlock<Block> CUT_POOP_BLOCK_SLAB = registerBlock("cut_poop_block_slab",
            () -> new SlabBlock(BlockBehaviour.Properties.ofFullCopy(CUT_POOP_BLOCK.get()))
    );
    public static final DeferredBlock<Block> CUT_POOP_BLOCK_VERTICAL_SLAB = registerBlock("cut_poop_block_vertical_slab",
            () -> new VerticalSlabBlock(BlockBehaviour.Properties.ofFullCopy(CUT_POOP_BLOCK.get()))
    );
    public static final DeferredBlock<Block> CUT_POOP_BLOCK_WALL = registerBlock("cut_poop_block_wall",
            () -> new WallBlock(BlockBehaviour.Properties.ofFullCopy(CUT_POOP_BLOCK.get()))
    );

    public static final DeferredBlock<Block> TILE_BLOCK = registerDefaultBlock("tile_block",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_LIGHT_BLUE)
                    .requiresCorrectToolForDrops()
                    .strength(HARDEN)
                    .sound(SoundType.STONE))
    );
    public static final DeferredBlock<Block> TILE_BLOCK_STAIRS = registerDefaultBlock("tile_block_stairs",
            () -> new StairBlock(TILE_BLOCK.get().defaultBlockState(), BlockBehaviour.Properties.ofFullCopy(TILE_BLOCK.get()))
    );
    public static final DeferredBlock<Block> TILE_BLOCK_SLAB = registerDefaultBlock("tile_block_slab",
            () -> new SlabBlock(BlockBehaviour.Properties.ofFullCopy(TILE_BLOCK.get()))
    );
    public static final DeferredBlock<Block> TILE_BLOCK_VERTICAL_SLAB = registerDefaultBlock("tile_block_vertical_slab",
            () -> new VerticalSlabBlock(BlockBehaviour.Properties.ofFullCopy(TILE_BLOCK.get()))
    );
    public static final DeferredBlock<Block> TILE_BLOCK_WALL = registerDefaultBlock("tile_block_wall",
            () -> new WallBlock(BlockBehaviour.Properties.ofFullCopy(TILE_BLOCK.get()))
    );

    public static final DeferredBlock<Block> COMPOOPER = registerCompooperBlock("compooper",
            () -> new CompooperBlock(BlockBehaviour.Properties.of()
                    .noOcclusion()
                    .mapColor(MapColor.COLOR_BROWN)
                    .instrument(NoteBlockInstrument.BASS)
                    .requiresCorrectToolForDrops()
                    .strength(0.6F)
                    .sound(SoundType.METAL)
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
            () -> new PlacerBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.STONE)
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
            () -> new PoopTntBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.FIRE)
                    .strength(0.0F)
                    .sound(SoundType.GRASS)
                    .ignitedByLava()
                    .pushReaction(PushReaction.DESTROY)));

    public static final DeferredBlock<Block> RAW_POOP_BLOCK = registerBlock("raw_poop_block",
            () -> new Block(BlockBehaviour.Properties.of()
                    .randomTicks()
                    .strength(0.65F)
                    .mapColor(MapColor.COLOR_BROWN)
                    .isValidSpawn(Blocks::always)
                    .instrument(NoteBlockInstrument.COW_BELL)
                    .sound(SoundType.MUD))
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
            () -> new PoopLogBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_BROWN)
                    .randomTicks()
                    .instrument(NoteBlockInstrument.BASS)
                    .strength(LOG)
                    .sound(SoundType.STEM)
            )
    );
    public static final DeferredBlock<Block> POOP_EMPTY_LOG = registerBlock("poop_empty_log",
            () -> new EmptyRotatedPillarBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_BROWN)
                    .noOcclusion()
                    .instrument(NoteBlockInstrument.BASS)
                    .strength(LOG)
                    .sound(SoundType.BAMBOO_WOOD)
            )
    );
    public static final DeferredBlock<Block> STRIPPED_POOP_LOG = registerBlock("stripped_poop_log",
            () -> new PoopLogBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_BROWN)
                    .randomTicks()
                    .instrument(NoteBlockInstrument.BASS)
                    .strength(LOG)
                    .sound(SoundType.STEM)
            )
    );
    public static final DeferredBlock<Block> STRIPPED_POOP_EMPTY_LOG = registerBlock("stripped_poop_empty_log",
            () -> new EmptyRotatedPillarBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_BROWN)
                    .noOcclusion()
                    .instrument(NoteBlockInstrument.BASS)
                    .strength(LOG)
                    .sound(SoundType.BAMBOO_WOOD)
            )
    );

    public static final DeferredBlock<Block> POOP_LEAVES = registerBlock("poop_leaves",
            () -> new LeavesBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_BROWN)
                    .strength(0.2F)
                    .randomTicks()
                    .noOcclusion()
                    .sound(SoundType.SCULK_SENSOR)
                    .isValidSpawn(Blocks::ocelotOrParrot)
                    .isSuffocating(PBlocks::neverSuffocate)
                    .isViewBlocking(PBlocks::neverBlockVision)
                    .ignitedByLava()
                    .pushReaction(PushReaction.DESTROY)
                    .isRedstoneConductor(PBlocks::never)
            )
    );
    public static final DeferredBlock<Block> POOP_LEAVES_IRON = registerBlock("poop_leaves_iron",
            () -> new LeavesBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.TERRACOTTA_WHITE)
                    .strength(0.2F)
                    .randomTicks()
                    .noOcclusion()
                    .sound(SoundType.SCULK_SENSOR)
                    .isValidSpawn(Blocks::ocelotOrParrot)
                    .isSuffocating(PBlocks::neverSuffocate)
                    .isViewBlocking(PBlocks::neverBlockVision)
                    .ignitedByLava()
                    .pushReaction(PushReaction.DESTROY)
                    .isRedstoneConductor(PBlocks::never)
            )
    );
    public static final DeferredBlock<Block> POOP_LEAVES_GOLD = registerBlock("poop_leaves_gold",
            () -> new LeavesBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_YELLOW)
                    .strength(0.2F)
                    .randomTicks()
                    .noOcclusion()
                    .sound(SoundType.SCULK_SENSOR)
                    .isValidSpawn(Blocks::ocelotOrParrot)
                    .isSuffocating(PBlocks::neverSuffocate)
                    .isViewBlocking(PBlocks::neverBlockVision)
                    .ignitedByLava()
                    .pushReaction(PushReaction.DESTROY)
                    .isRedstoneConductor(PBlocks::never)
            )
    );

    public static final DeferredBlock<Block> POOP_SAPLING = registerBlock("poop_sapling",
            () -> new PoopTreeBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_BROWN)
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

    public static final DeferredBlock<Block> MAGGOTS = BLOCKS.register("maggots",
            () -> new MaggotsBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_YELLOW)
                    .noCollission()
                    .randomTicks()
                    .instabreak()
                    .sound(SoundType.CROP)
                    .pushReaction(PushReaction.DESTROY)
            )
    );

    public static final DeferredBlock<Block> ROUNDWORM_VINES = BLOCKS.register("roundworm_vines",
            () -> new RoundwormVinesBlock(
                    BlockBehaviour.Properties.of()
                            .mapColor(MapColor.TERRACOTTA_WHITE)
                            .randomTicks()
                            .noCollission()
                            .instabreak()
                            .sound(SoundType.TWISTING_VINES)
                            .pushReaction(PushReaction.DESTROY)
            )
    );
    public static final DeferredBlock<Block> ROUNDWORM_VINES_PLANT = BLOCKS.register("roundworm_vines_plant",
            () -> new RoundwormVinesPlantBlock(
                    BlockBehaviour.Properties.of()
                            .mapColor(MapColor.TERRACOTTA_WHITE)
                            .noCollission()
                            .instabreak()
                            .sound(SoundType.TWISTING_VINES)
                            .pushReaction(PushReaction.DESTROY)
            )
    );

    public static final DeferredBlock<Block> FLY_NEST = registerDefaultBlock("fly_nest",
            () -> new FlyNestBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_BROWN)
                    .strength(0.5F)
                    .sound(SoundType.WOOD)
                    .noOcclusion()
            )
    );

    public static final DeferredBlock<Block> BREEDING_BOX = registerDefaultBlock("breeding_box",
            () -> new BreedingBoxBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_BROWN)
                    .strength(1.0F)
                    .sound(SoundType.WOOD)
                    .noOcclusion()
            )
    );

    private static BlockBehaviour.Properties poopCakeProperties() {
        return BlockBehaviour.Properties.of()
                .forceSolidOn()
                .strength(0.5F)
                .sound(SoundType.WOOL)
                .pushReaction(PushReaction.DESTROY);
    }

    private static Map.Entry<Block, DeferredBlock<Block>> registerPoopCandleCake(String name, Block candle) {
        if (!(candle instanceof CandleBlock)) {
            throw new IllegalArgumentException("Expected candle block: " + candle);
        }

        DeferredBlock<Block> candleCake = BLOCKS.register(name,
                () -> new PoopCandleCakeBlock(candle, poopCakeProperties()
                        .lightLevel(state -> state.getValue(PoopCandleCakeBlock.LIT) ? 3 : 0)));

        return Map.entry(candle, candleCake);
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

    public static boolean neverBlockVision(BlockState state, BlockGetter world, BlockPos pos) {
        return false;
    }

    public static <T extends Block> DeferredBlock<T> registerDefaultBlock(String name, Supplier<T> block) {
        DeferredBlock<T> toReturn = BLOCKS.register(name, block);
        registerBlockItem(name, toReturn);
        return toReturn;
    }

    public static <T extends Block> DeferredBlock<T> registerCompooperBlock(String name, Supplier<T> block) {
        DeferredBlock<T> toReturn = BLOCKS.register(name, block);
        registerCompooperBlockItem(name, toReturn);
        return toReturn;
    }

    public static <T extends Block> DeferredBlock<T> registerBlock(String name, Supplier<T> block) {
        DeferredBlock<T> toReturn = BLOCKS.register(name, block);
        registerBlockItem(name, toReturn, 88);
        return toReturn;
    }

    private static <T extends Block> void registerBlockItem(String name, DeferredBlock<T> block) {
        PItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
    }

    private static <T extends Block> void registerCompooperBlockItem(String name, DeferredBlock<T> block) {
        PItems.ITEMS.register(name, () -> new CompooperBlockItem(block.get(), new Item.Properties()));
    }

    private static <T extends Block> void registerBlockItem(String name, DeferredBlock<T> block, int stacksTo) {
        PItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties().stacksTo(stacksTo)));
    }

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}

