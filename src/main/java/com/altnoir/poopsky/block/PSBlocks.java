package com.altnoir.poopsky.block;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.lib.PoopBlockSetType;
import com.altnoir.poopsky.lib.PoopWoodType;
import net.minecraft.block.*;
import net.minecraft.block.enums.NoteBlockInstrument;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.ItemActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

public class PSBlocks {

    public static final Block POOP_BLOCK = registerBlock("poop_block",
            new PoopBlock(AbstractBlock.Settings.create()
                    .mapColor(MapColor.BROWN)
                    .instrument(NoteBlockInstrument.COW_BELL)
                    .strength(0.1F)
                    .sounds(BlockSoundGroup.MUD)
            )
    );
    public static final Block POOP_STAIRS = registerBlock("poop_stairs",
            new StairsBlock(POOP_BLOCK.getDefaultState(), AbstractBlock.Settings.create()
                    .mapColor(MapColor.BROWN)
                    .strength(0.1F)
                    .sounds(BlockSoundGroup.MUD)
            )
    );
    public static final Block POOP_SLAB = registerBlock("poop_slab",
            new SlabBlock(AbstractBlock.Settings.create()
                    .mapColor(MapColor.BROWN)
                    .strength(0.1F)
                    .sounds(BlockSoundGroup.MUD)
            )
    );
    public static final Block POOP_VERTICAL_SLAB = registerBlock("poop_vertical_slab",
            new VerticalSlabBlock(AbstractBlock.Settings.create()
                    .mapColor(MapColor.BROWN)
                    .strength(0.1F)
                    .sounds(BlockSoundGroup.MUD)
            )
    );
    public static final Block POOP_BUTTON = registerBlock("poop_button",
            new ButtonBlock(PoopBlockSetType.POOP,200,AbstractBlock.Settings.create()
                    .mapColor(MapColor.BROWN)
                    .strength(0.1F)
                    .sounds(BlockSoundGroup.MUD)
                    .noCollision()
            )
    );
    public static final Block POOP_PRESSURE_PLATE = registerBlock("poop_pressure_plate",
            new PressurePlateBlock(PoopBlockSetType.POOP, AbstractBlock.Settings.create()
                    .mapColor(MapColor.BROWN)
                    .strength(0.1F)
                    .sounds(BlockSoundGroup.MUD)
                    .noCollision()
            )
    );
    public static final Block POOP_FENCE = registerBlock("poop_fence",
            new FenceBlock(AbstractBlock.Settings.create()
                    .mapColor(MapColor.BROWN)
                    .strength(0.1F)
                    .sounds(BlockSoundGroup.MUD)
            )
    );
    public static final Block POOP_FENCE_GATE = registerBlock("poop_fence_gate",
            new FenceGateBlock(PoopWoodType.POOP, AbstractBlock.Settings.create()
                    .mapColor(MapColor.BROWN)
                    .strength(0.1F)
                    .sounds(BlockSoundGroup.MUD)
            )
    );
    public static final Block POOP_WALL = registerBlock("poop_wall",
            new WallBlock(AbstractBlock.Settings.create()
                    .mapColor(MapColor.BROWN)
                    .strength(0.1F)
                    .sounds(BlockSoundGroup.MUD)
            )
    );

    public static final Block POOP_DOOR = registerBlock("poop_door",
            new DoorBlock(PoopBlockSetType.POOP,AbstractBlock.Settings.create()
                    .mapColor(MapColor.BROWN)
                    .strength(0.1F)
                    .sounds(BlockSoundGroup.MUD)
                    .nonOpaque()
            )
    );
    public static final Block POOP_TRAPDOOR = registerBlock("poop_trapdoor",
            new TrapdoorBlock(PoopBlockSetType.POOP,AbstractBlock.Settings.create()
                    .mapColor(MapColor.BROWN)
                    .strength(0.1F)
                    .sounds(BlockSoundGroup.MUD)
                    .nonOpaque()
            )
    );

    public static final Block POOP_PIECE = registerBlock("poop_piece",
            new PoopPiece(AbstractBlock.Settings.create()
                    .mapColor(MapColor.BROWN)
                    .replaceable()
                    .notSolid()
                    .strength(0.1F)
                    .sounds(BlockSoundGroup.MUD)
                    .blockVision((state, world, pos) -> (Integer)state.get(SnowBlock.LAYERS) >= 8)
                    .pistonBehavior(PistonBehavior.DESTROY)
            )
    );
    public static final Block POOP_LOG = registerBlock("poop_log",
            new PillarBlock(AbstractBlock.Settings.create()
                    .mapColor(MapColor.BROWN)
                    .notSolid()
                    .instrument(NoteBlockInstrument.BASS)
                    .strength(2.0F)
                    .sounds(BlockSoundGroup.STONE)
            ){
                @Override
                protected ItemActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
                    if (stack.isIn(ItemTags.AXES)) {
                        BlockState state1 = PSBlocks.STRIPPED_POOP_LOG.getDefaultState().with(EmptyPillarBlock.AXIS, state.get(EmptyPillarBlock.AXIS));

                        world.playSound(player, pos, SoundEvents.ITEM_AXE_STRIP, SoundCategory.BLOCKS, 1.0F, 1.0F);
                        world.setBlockState(pos, state1);
                        world.emitGameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Emitter.of(player, state1));

                        return ItemActionResult.SUCCESS;
                    }
                    return super.onUseWithItem(stack, state, world, pos, player, hand, hit);
                }
            }
    );
    public static final Block POOP_EMPTY_LOG = registerBlock("poop_empty_log",
            new EmptyPillarBlock(AbstractBlock.Settings.create()
                    .mapColor(MapColor.BROWN)
                    .notSolid()
                    .instrument(NoteBlockInstrument.BASS)
                    .strength(2.0F)
                    .sounds(BlockSoundGroup.BAMBOO_WOOD)
            ){
                @Override
                protected ItemActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
                    if (stack.isIn(ItemTags.AXES)) {
                        BlockState state1 = PSBlocks.STRIPPED_POOP_EMPTY_LOG.getDefaultState().with(EmptyPillarBlock.AXIS, state.get(EmptyPillarBlock.AXIS));

                        world.playSound(player, pos, SoundEvents.ITEM_AXE_STRIP, SoundCategory.BLOCKS, 1.0F, 1.0F);
                        world.setBlockState(pos, state1);
                        world.emitGameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Emitter.of(player, state1));

                        return ItemActionResult.SUCCESS;
                    }
                    return super.onUseWithItem(stack, state, world, pos, player, hand, hit);
                }
            }
    );
    public static final Block STRIPPED_POOP_LOG = registerBlock("stripped_poop_log",
            new PillarBlock(AbstractBlock.Settings.create()
                    .mapColor(MapColor.BROWN)
                    .notSolid()
                    .instrument(NoteBlockInstrument.BASS)
                    .strength(2.0F)
                    .sounds(BlockSoundGroup.STONE)
            )
    );
    public static final Block STRIPPED_POOP_EMPTY_LOG = registerBlock("stripped_poop_empty_log",
            new EmptyPillarBlock(AbstractBlock.Settings.create()
                    .mapColor(MapColor.BROWN)
                    .notSolid()
                    .instrument(NoteBlockInstrument.BASS)
                    .strength(2.0F)
                    .sounds(BlockSoundGroup.BAMBOO_WOOD)
            )
    );
    public static final Block POOP_SAPLING = registerBlock("poop_sapling",
            new PoopTreeBlock(AbstractBlock.Settings.create()
                    .mapColor(MapColor.BROWN)
                    .notSolid()
                    .breakInstantly()
                    .sounds(BlockSoundGroup.MUD)
                    .nonOpaque()
                    .pistonBehavior(PistonBehavior.DESTROY)
            )
    );
    public static final Block POOP_LEAVES = registerBlock("poop_leaves",
            new LeavesBlock(AbstractBlock.Settings.create()
                    .mapColor(MapColor.BROWN)
                    .strength(0.2F)
                    .ticksRandomly()
                    .sounds(BlockSoundGroup.SCULK_SENSOR)
                    .nonOpaque()
                    .allowsSpawning(Blocks::canSpawnOnLeaves)
                    .suffocates(Blocks::never)
                    .blockVision(Blocks::never)
                    .burnable()
                    .pistonBehavior(PistonBehavior.DESTROY)
                    .solidBlock(Blocks::never)
            )
    );
    public static final Block STOOL = registerBlock("stool",
            new ChairBlock(AbstractBlock.Settings.create()
                    .mapColor(MapColor.BROWN)
                    .strength(0.5F)
                    .sounds(BlockSoundGroup.MUD)
                    .pistonBehavior(PistonBehavior.DESTROY)
            )
    );

    private static Block registerBlock(String name, Block block) {
        registerBlockItem(name, block);
        return Registry.register(Registries.BLOCK, Identifier.of(PoopSky.MOD_ID, name), block);
    }
    private static void registerBlockItem(String name, Block block) {
        Registry.register(Registries.ITEM, Identifier.of(PoopSky.MOD_ID, name),
                new BlockItem(block, new BlockItem.Settings().maxCount(88)));
    }
    public static void registerModBlocks() {
        PoopSky.LOGGER.info("Registering Mod Blocks for " + PoopSky.MOD_ID);
    }
}
