package com.altnoir.poopsky.block;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.component.PSFoodComponents;
import com.altnoir.poopsky.item.PSItems;
import com.altnoir.poopsky.lib.PoopBlockSetType;
import com.altnoir.poopsky.lib.PoopWoodType;
import net.minecraft.block.*;
import net.minecraft.block.enums.NoteBlockInstrument;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.item.AliasedBlockItem;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;

public class PSBlocks {
    private static final float POOP = 0.5F;
    private static final float HARDEN = 1.5F;
    private static final float LOG = 2.0F;

    public static final Block POOP_BLOCK = registerBlock("poop_block",
            new PoopBlock(AbstractBlock.Settings.create()
                    .mapColor(MapColor.BROWN)
                    .allowsSpawning(Blocks::always) // 是否刷怪
                    .solidBlock(Blocks::always) // 是否完整
                    .suffocates(Blocks::always) // 窒息
                    .instrument(NoteBlockInstrument.COW_BELL)
                    .strength(POOP)
                    .sounds(BlockSoundGroup.MUD)
            )
    );
    public static final Block POOP_STAIRS = registerBlock("poop_stairs",
            new StairsBlock(POOP_BLOCK.getDefaultState(), AbstractBlock.Settings.create()
                    .mapColor(MapColor.BROWN)
                    .strength(POOP)
                    .sounds(BlockSoundGroup.MUD)
            )
    );
    public static final Block POOP_SLAB = registerBlock("poop_slab",
            new SlabBlock(AbstractBlock.Settings.create()
                    .mapColor(MapColor.BROWN)
                    .strength(POOP)
                    .sounds(BlockSoundGroup.MUD)
            )
    );
    public static final Block POOP_VERTICAL_SLAB = registerBlock("poop_vertical_slab",
            new VerticalSlabBlock(AbstractBlock.Settings.create()
                    .mapColor(MapColor.BROWN)
                    .strength(POOP)
                    .sounds(BlockSoundGroup.MUD)
            )
    );
    public static final Block POOP_BUTTON = registerBlock("poop_button",
            new ButtonBlock(PoopBlockSetType.POOP,200,AbstractBlock.Settings.create()
                    .mapColor(MapColor.BROWN)
                    .strength(POOP)
                    .sounds(BlockSoundGroup.MUD)
                    .noCollision()
            )
    );
    public static final Block POOP_PRESSURE_PLATE = registerBlock("poop_pressure_plate",
            new PressurePlateBlock(PoopBlockSetType.POOP, AbstractBlock.Settings.create()
                    .mapColor(MapColor.BROWN)
                    .strength(POOP)
                    .sounds(BlockSoundGroup.MUD)
                    .noCollision()
            )
    );
    public static final Block POOP_FENCE = registerBlock("poop_fence",
            new FenceBlock(AbstractBlock.Settings.create()
                    .mapColor(MapColor.BROWN)
                    .strength(POOP)
                    .sounds(BlockSoundGroup.MUD)
            )
    );
    public static final Block POOP_FENCE_GATE = registerBlock("poop_fence_gate",
            new FenceGateBlock(PoopWoodType.POOP, AbstractBlock.Settings.create()
                    .mapColor(MapColor.BROWN)
                    .strength(POOP)
                    .sounds(BlockSoundGroup.MUD)
            )
    );
    public static final Block POOP_WALL = registerBlock("poop_wall",
            new WallBlock(AbstractBlock.Settings.create()
                    .mapColor(MapColor.BROWN)
                    .strength(POOP)
                    .sounds(BlockSoundGroup.MUD)
            )
    );

    public static final Block POOP_DOOR = registerBlock("poop_door",
            new DoorBlock(PoopBlockSetType.POOP,AbstractBlock.Settings.create()
                    .mapColor(MapColor.BROWN)
                    .strength(POOP)
                    .sounds(BlockSoundGroup.MUD)
                    .nonOpaque()
            )
    );
    public static final Block POOP_TRAPDOOR = registerBlock("poop_trapdoor",
            new TrapdoorBlock(PoopBlockSetType.POOP,AbstractBlock.Settings.create()
                    .mapColor(MapColor.BROWN)
                    .strength(POOP)
                    .sounds(BlockSoundGroup.MUD)
                    .nonOpaque()
            )
    );

    public static final Block POOP_PIECE = registerBlock("poop_piece",
            new PoopPiece(AbstractBlock.Settings.create()
                    .mapColor(MapColor.BROWN)
                    .replaceable()
                    .notSolid()
                    .strength(POOP)
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
                    .strength(LOG)
                    .sounds(BlockSoundGroup.STONE)
            )
    );
    public static final Block POOP_EMPTY_LOG = registerBlock("poop_empty_log",
            new EmptyPillarBlock(AbstractBlock.Settings.create()
                    .mapColor(MapColor.BROWN)
                    .notSolid()
                    .instrument(NoteBlockInstrument.BASS)
                    .strength(LOG)
                    .sounds(BlockSoundGroup.BAMBOO_WOOD)
            )
    );
    public static final Block STRIPPED_POOP_LOG = registerBlock("stripped_poop_log",
            new PillarBlock(AbstractBlock.Settings.create()
                    .mapColor(MapColor.BROWN)
                    .notSolid()
                    .instrument(NoteBlockInstrument.BASS)
                    .strength(LOG)
                    .sounds(BlockSoundGroup.STONE)
            )
    );
    public static final Block STRIPPED_POOP_EMPTY_LOG = registerBlock("stripped_poop_empty_log",
            new EmptyPillarBlock(AbstractBlock.Settings.create()
                    .mapColor(MapColor.BROWN)
                    .notSolid()
                    .instrument(NoteBlockInstrument.BASS)
                    .strength(LOG)
                    .sounds(BlockSoundGroup.BAMBOO_WOOD)
            )
    );
    public static final Block POOP_SAPLING = registerBlock("poop_sapling",
            new PoopTreeBlock(AbstractBlock.Settings.create()
                    .mapColor(MapColor.BROWN)
                    .notSolid()
                    .breakInstantly()
                    .nonOpaque()
                    .sounds(BlockSoundGroup.MUD)
                    .offset(AbstractBlock.OffsetType.XYZ)
                    .dynamicBounds()
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
    public static final Block POOP_LEAVES_IRON = registerBlock("poop_leaves_iron",
            new LeavesBlock(AbstractBlock.Settings.create()
                    .mapColor(MapColor.WHITE)
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
    public static final Block POOP_LEAVES_GOLD = registerBlock("poop_leaves_gold",
            new LeavesBlock(AbstractBlock.Settings.create()
                    .mapColor(MapColor.GOLD)
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
                    .strength(POOP)
                    .sounds(BlockSoundGroup.MUD)
                    .pistonBehavior(PistonBehavior.DESTROY)
            )
    );
    public static final Block COMPOOPER = registerBlock("compooper",
            new CompooperBlock(AbstractBlock.Settings.create()
                    .mapColor(MapColor.OAK_TAN)
                    .instrument(NoteBlockInstrument.BASS)
                    .requiresTool()
                    .strength(0.6F)
                    .sounds(BlockSoundGroup.METAL)
            )
    );

    public static final Block POOP_BRICKS = registerBlock("poop_bricks",
            new Block(AbstractBlock.Settings.create()
                    .mapColor(MapColor.ORANGE)
                    .requiresTool()
                    .strength(HARDEN)
                    .sounds(BlockSoundGroup.MUD_BRICKS)
            )
    );
    public static final Block CRACKED_POOP_BRICKS = registerBlock("cracked_poop_bricks",
            new Block(AbstractBlock.Settings.create()
                    .mapColor(MapColor.ORANGE)
                    .requiresTool()
                    .strength(HARDEN)
                    .sounds(BlockSoundGroup.MUD_BRICKS)
            )
    );
    public static final Block POOP_BRICK_STAIRS = registerBlock("poop_brick_stairs",
            new StairsBlock(POOP_BLOCK.getDefaultState(), AbstractBlock.Settings.create()
                    .mapColor(MapColor.ORANGE)
                    .requiresTool()
                    .strength(HARDEN)
                    .sounds(BlockSoundGroup.MUD_BRICKS)
            )
    );
    public static final Block POOP_BRICK_SLAB = registerBlock("poop_brick_slab",
            new SlabBlock(AbstractBlock.Settings.create()
                    .mapColor(MapColor.ORANGE)
                    .requiresTool()
                    .strength(HARDEN)
                    .sounds(BlockSoundGroup.MUD_BRICKS)
            )
    );
    public static final Block POOP_BRICK_VERTICAL_SLAB = registerBlock("poop_brick_vertical_slab",
            new VerticalSlabBlock(AbstractBlock.Settings.create()
                    .mapColor(MapColor.ORANGE)
                    .requiresTool()
                    .strength(HARDEN)
                    .sounds(BlockSoundGroup.MUD_BRICKS)
            )
    );
    public static final Block POOP_BRICK_WALL = registerBlock("poop_brick_wall",
            new WallBlock(AbstractBlock.Settings.create()
                    .mapColor(MapColor.ORANGE)
                    .requiresTool()
                    .strength(HARDEN)
                    .sounds(BlockSoundGroup.MUD_BRICKS)
            )
    );

    public static final Block MOSSY_POOP_BRICKS = registerBlock("mossy_poop_bricks",
            new Block(AbstractBlock.Settings.create()
                    .mapColor(MapColor.ORANGE)
                    .requiresTool()
                    .strength(HARDEN)
                    .sounds(BlockSoundGroup.MUD_BRICKS)
            )
    );
    public static final Block MOSSY_POOP_BRICK_STAIRS = registerBlock("mossy_poop_brick_stairs",
            new StairsBlock(POOP_BLOCK.getDefaultState(), AbstractBlock.Settings.create()
                    .mapColor(MapColor.DARK_GREEN)
                    .requiresTool()
                    .strength(HARDEN)
                    .sounds(BlockSoundGroup.MUD_BRICKS)
            )
    );
    public static final Block MOSSY_POOP_BRICK_SLAB = registerBlock("mossy_poop_brick_slab",
            new SlabBlock(AbstractBlock.Settings.create()
                    .mapColor(MapColor.DARK_GREEN)
                    .requiresTool()
                    .strength(HARDEN)
                    .sounds(BlockSoundGroup.MUD_BRICKS)
            )
    );
    public static final Block MOSSY_POOP_BRICK_VERTICAL_SLAB = registerBlock("mossy_poop_brick_vertical_slab",
            new VerticalSlabBlock(AbstractBlock.Settings.create()
                    .mapColor(MapColor.DARK_GREEN)
                    .requiresTool()
                    .strength(HARDEN)
                    .sounds(BlockSoundGroup.MUD_BRICKS)
            )
    );
    public static final Block MOSSY_POOP_BRICK_WALL = registerBlock("mossy_poop_brick_wall",
            new WallBlock(AbstractBlock.Settings.create()
                    .mapColor(MapColor.DARK_GREEN)
                    .requiresTool()
                    .strength(HARDEN)
                    .sounds(BlockSoundGroup.MUD_BRICKS)
            )
    );

    public static final Block DRIED_POOP_BLOCK = registerBlock("dried_poop_block",
            new DriedPoopBlock(AbstractBlock.Settings.create()
                    .mapColor(MapColor.ORANGE)
                    .requiresTool()
                    .strength(HARDEN)
                    .instrument(NoteBlockInstrument.COW_BELL)
                    .sounds(BlockSoundGroup.TUFF)
            )
    );
    public static final Block DRIED_POOP_BLOCK_STAIRS = registerBlock("dried_poop_block_stairs",
            new StairsBlock(POOP_BLOCK.getDefaultState(), AbstractBlock.Settings.create()
                    .mapColor(MapColor.DARK_GREEN)
                    .requiresTool()
                    .strength(HARDEN)
                    .sounds(BlockSoundGroup.TUFF)
            )
    );
    public static final Block DRIED_POOP_BLOCK_SLAB = registerBlock("dried_poop_block_slab",
            new SlabBlock(AbstractBlock.Settings.create()
                    .mapColor(MapColor.DARK_GREEN)
                    .requiresTool()
                    .strength(HARDEN)
                    .sounds(BlockSoundGroup.TUFF)
            )
    );
    public static final Block DRIED_POOP_BLOCK_VERTICAL_SLAB = registerBlock("dried_poop_block_vertical_slab",
            new VerticalSlabBlock(AbstractBlock.Settings.create()
                    .mapColor(MapColor.DARK_GREEN)
                    .requiresTool()
                    .strength(HARDEN)
                    .sounds(BlockSoundGroup.TUFF)
            )
    );
    public static final Block DRIED_POOP_BLOCK_WALL = registerBlock("dried_poop_block_wall",
            new WallBlock(AbstractBlock.Settings.create()
                    .mapColor(MapColor.DARK_GREEN)
                    .requiresTool()
                    .strength(HARDEN)
                    .sounds(BlockSoundGroup.TUFF)
            )
    );

    public static final Block SMOOTH_POOP_BLOCK = registerBlock("smooth_poop_block",
            new Block(AbstractBlock.Settings.create()
                    .mapColor(MapColor.ORANGE)
                    .requiresTool()
                    .strength(HARDEN)
                    .sounds(BlockSoundGroup.CALCITE)
            )
    );
    public static final Block SMOOTH_POOP_BLOCK_STAIRS = registerBlock("smooth_poop_block_stairs",
            new StairsBlock(POOP_BLOCK.getDefaultState(), AbstractBlock.Settings.create()
                    .mapColor(MapColor.DARK_GREEN)
                    .requiresTool()
                    .strength(HARDEN)
                    .sounds(BlockSoundGroup.CALCITE)
            )
    );
    public static final Block SMOOTH_POOP_BLOCK_SLAB = registerBlock("smooth_poop_block_slab",
            new SlabBlock(AbstractBlock.Settings.create()
                    .mapColor(MapColor.DARK_GREEN)
                    .requiresTool()
                    .strength(HARDEN)
                    .sounds(BlockSoundGroup.CALCITE)
            )
    );
    public static final Block SMOOTH_POOP_BLOCK_VERTICAL_SLAB = registerBlock("smooth_poop_block_vertical_slab",
            new VerticalSlabBlock(AbstractBlock.Settings.create()
                    .mapColor(MapColor.DARK_GREEN)
                    .requiresTool()
                    .strength(HARDEN)
                    .sounds(BlockSoundGroup.CALCITE)
            )
    );
    public static final Block SMOOTH_POOP_BLOCK_WALL = registerBlock("smooth_poop_block_wall",
            new WallBlock(AbstractBlock.Settings.create()
                    .mapColor(MapColor.DARK_GREEN)
                    .requiresTool()
                    .strength(HARDEN)
                    .sounds(BlockSoundGroup.CALCITE)
            )
    );

    public static final Block CUT_POOP_BLOCK = registerBlock("cut_poop_block",
            new Block(AbstractBlock.Settings.create()
                    .mapColor(MapColor.ORANGE)
                    .requiresTool()
                    .strength(HARDEN)
                    .sounds(BlockSoundGroup.POLISHED_TUFF)
            )
    );
    public static final Block CUT_POOP_BLOCK_STAIRS = registerBlock("cut_poop_block_stairs",
            new StairsBlock(POOP_BLOCK.getDefaultState(), AbstractBlock.Settings.create()
                    .mapColor(MapColor.DARK_GREEN)
                    .requiresTool()
                    .strength(HARDEN)
                    .sounds(BlockSoundGroup.POLISHED_TUFF)
            )
    );
    public static final Block CUT_POOP_BLOCK_SLAB = registerBlock("cut_poop_block_slab",
            new SlabBlock(AbstractBlock.Settings.create()
                    .mapColor(MapColor.DARK_GREEN)
                    .requiresTool()
                    .strength(HARDEN)
                    .sounds(BlockSoundGroup.POLISHED_TUFF)
            )
    );
    public static final Block CUT_POOP_BLOCK_VERTICAL_SLAB = registerBlock("cut_poop_block_vertical_slab",
            new VerticalSlabBlock(AbstractBlock.Settings.create()
                    .mapColor(MapColor.DARK_GREEN)
                    .requiresTool()
                    .strength(HARDEN)
                    .sounds(BlockSoundGroup.POLISHED_TUFF)
            )
    );
    public static final Block CUT_POOP_BLOCK_WALL = registerBlock("cut_poop_block_wall",
            new WallBlock(AbstractBlock.Settings.create()
                    .mapColor(MapColor.DARK_GREEN)
                    .requiresTool()
                    .strength(HARDEN)
                    .sounds(BlockSoundGroup.POLISHED_TUFF)
            )
    );

    public static final Block POOP_CAKE = registerBlock("poop_cake",
            new PoopCakeBlock(AbstractBlock.Settings.create()
                    .solid().strength(0.5F)
                    .sounds(BlockSoundGroup.WOOL)
                    .pistonBehavior(PistonBehavior.DESTROY))
    );

    public static final Block MAGGOTS = registerBlockOnly("maggots",
            new MaggotsBlock(AbstractBlock.Settings.create()
                    .mapColor(MapColor.DARK_GREEN)
                    .noCollision()
                    .ticksRandomly()
                    .breakInstantly()
                    .sounds(BlockSoundGroup.CROP)
                    .pistonBehavior(PistonBehavior.DESTROY)
            )
    );

    private static Block registerBlockOnly(String name, Block block) {
        return Registry.register(Registries.BLOCK, Identifier.of(PoopSky.MOD_ID, name), block);
    }
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

        PSItems.MAGGOTS_SEEDS = Registry.register(Registries.ITEM, Identifier.of(PoopSky.MOD_ID, "maggots_seeds"),
               new AliasedBlockItem(PSBlocks.MAGGOTS, new Item.Settings().food(PSFoodComponents.MAGGOTS_SEEDS).maxCount(88)));
    }
}
