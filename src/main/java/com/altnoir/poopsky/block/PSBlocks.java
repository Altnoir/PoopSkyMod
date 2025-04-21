package com.altnoir.poopsky.block;

import com.altnoir.poopsky.Fluid.PSFluids;
import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.lib.PoopBlockSetType;
import com.altnoir.poopsky.lib.PoopWoodType;
import net.minecraft.block.*;
import net.minecraft.block.enums.NoteBlockInstrument;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.item.BlockItem;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;

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
    public static final Block URINE_BLOCK = registerBlock("urine",
            new FluidBlock(PSFluids.URINE, AbstractBlock.Settings.create()
                    .mapColor(MapColor.BROWN)
                    .replaceable()
                    .noCollision()
                    .strength(100.0F)
                    .pistonBehavior(PistonBehavior.DESTROY)
                    .dropsNothing().liquid()
                    .sounds(BlockSoundGroup.INTENTIONALLY_EMPTY)
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
