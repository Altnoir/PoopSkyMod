package com.altnoir.poopsky.block;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.block.p.*;
import com.altnoir.poopsky.fluid.PSFluids;
import com.altnoir.poopsky.fluid.PoopLiquidBlock;
import com.altnoir.poopsky.item.PSItems;
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

import java.util.function.Supplier;

public class PSBlocks {
    private static final float POOP = 0.5F;
    private static final float HARDEN = 1.5F;
    private static final float LOG = 2.0F;
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(PoopSky.MOD_ID);

    public static final DeferredBlock<Block> POOP_PIECE = registerBlock("poop_piece",
            () -> new PoopPieceBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_BROWN)
                    .replaceable()
                    .forceSolidOff()
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
            () -> new ButtonBlock(PSBlockSetType.POOP, 200, BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_BROWN)
                    .strength(POOP)
                    .sound(SoundType.MUD)
                    .noCollission())
    );
    public static final DeferredBlock<Block> POOP_PRESSURE_PLATE = registerBlock("poop_pressure_plate",
            () -> new PressurePlateBlock(PSBlockSetType.POOP, BlockBehaviour.Properties.of()
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
            () -> new FenceGateBlock(PSWoodType.POOP, BlockBehaviour.Properties.of()
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
            () -> new DoorBlock(PSBlockSetType.POOP, BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_BROWN)
                    .strength(POOP)
                    .sound(SoundType.MUD)
                    .noOcclusion()
                    .pushReaction(PushReaction.DESTROY))
    );
    public static final DeferredBlock<Block> POOP_TRAPDOOR = registerBlock("poop_trapdoor",
            () -> new TrapDoorBlock(PSBlockSetType.POOP, BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_BROWN)
                    .strength(POOP)
                    .sound(SoundType.MUD)
                    .noOcclusion()
                    .isValidSpawn(Blocks::never))
    );

    public static final DeferredBlock<Block> POOPLIME_BLOCK = registerBlock("pooplime_block",
            () -> new PooplimeBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_BROWN)
                    .friction(0.8F)
                    .sound(SoundType.SLIME_BLOCK)
                    .noOcclusion()
            )
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

    public static final DeferredBlock<Block> DRIED_POOP_BLOCK = registerBlock("dried_poop_block",
            () -> new DriedPoopBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_BROWN)
                    .requiresCorrectToolForDrops()
                    .strength(HARDEN)
                    .instrument(NoteBlockInstrument.COW_BELL)
                    .sound(SoundType.TUFF)
            )
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
    public static final DeferredBlock<Block> COMPOOPER = registerBlock("compooper",
            () -> new CompooperBlock(BlockBehaviour.Properties.of()
                    .noOcclusion()
                    .mapColor(MapColor.COLOR_BROWN)
                    .instrument(NoteBlockInstrument.BASS)
                    .requiresCorrectToolForDrops()
                    .strength(0.6F)
                    .sound(SoundType.METAL)
            )
    );
    public static final DeferredBlock<Block> WATER_COMPOOPER = registerBlock("water_compooper",
            () -> new WaterCompooperBlock(BlockBehaviour.Properties.ofFullCopy(COMPOOPER.get()))
    );
    public static final DeferredBlock<Block> LAVA_COMPOOPER = registerBlock("lava_compooper",
            () -> new LavaCompooperBlock(BlockBehaviour.Properties.ofFullCopy(COMPOOPER.get()).lightLevel(state -> 15))
    );
    public static final DeferredBlock<Block> URINE_COMPOOPER = registerBlock("urine_compooper",
            () -> new UrineCompooperBlock(BlockBehaviour.Properties.ofFullCopy(COMPOOPER.get()))
    );

    public static final DeferredBlock<Block> POOP_LOG = registerBlock("poop_log",
            () -> new RotatedPillarBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_BROWN)
                    .noOcclusion()
                    .instrument(NoteBlockInstrument.BASS)
                    .strength(LOG)
                    .sound(SoundType.STONE)
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
            () -> new RotatedPillarBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_BROWN)
                    .noOcclusion()
                    .instrument(NoteBlockInstrument.BASS)
                    .strength(LOG)
                    .sound(SoundType.STONE)
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
                    .isSuffocating(PSBlocks::neverSuffocate)
                    .isViewBlocking(PSBlocks::neverBlockVision)
                    .ignitedByLava()
                    .pushReaction(PushReaction.DESTROY)
                    .isRedstoneConductor(PSBlocks::never)
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
                    .isSuffocating(PSBlocks::neverSuffocate)
                    .isViewBlocking(PSBlocks::neverBlockVision)
                    .ignitedByLava()
                    .pushReaction(PushReaction.DESTROY)
                    .isRedstoneConductor(PSBlocks::never)
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
                    .isSuffocating(PSBlocks::neverSuffocate)
                    .isViewBlocking(PSBlocks::neverBlockVision)
                    .ignitedByLava()
                    .pushReaction(PushReaction.DESTROY)
                    .isRedstoneConductor(PSBlocks::never)
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

    public static final DeferredBlock<LiquidBlock> POOP_LIQUID = registerBlock("poop_liquid",
            () -> new PoopLiquidBlock(
                    PSFluids.POOP.get(),
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

    public static boolean neverSuffocate(BlockState state, BlockGetter world, BlockPos pos) {
        return false;
    }

    public static boolean never(BlockState state, BlockGetter world, BlockPos pos) {
        return false;
    }

    public static boolean neverBlockVision(BlockState state, BlockGetter world, BlockPos pos) {
        return false;
    }

    public static <T extends Block> DeferredBlock<T> registerBlock(String name, Supplier<T> block) {
        DeferredBlock<T> toReturn = BLOCKS.register(name, block);
        registerBlockItem(name, toReturn);
        return toReturn;
    }

    private static <T extends Block> void registerBlockItem(String name, DeferredBlock<T> block) {
        PSItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties().stacksTo(88)));
    }

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}