package com.altnoir.poopsky.block;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.block.p.*;
import com.altnoir.poopsky.item.PSItems;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
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

    public static final DeferredBlock<Block> POOP_BLOCK = registerBlock("poop_block",
            () -> new PoopBlock(BlockBehaviour.Properties.of()
                    .strength(POOP)
                    .mapColor(MapColor.COLOR_BROWN)
                    .isValidSpawn(Blocks::always)
                    .instrument(NoteBlockInstrument.COW_BELL)
                    .sound(SoundType.MUD)));

    public static final DeferredBlock<Block> POOP_PIECE = registerBlock("poop_piece",
            () -> new PoopPiece(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_BROWN)
                    .replaceable()
                    .forceSolidOff()
                    .randomTicks()
                    .strength(0.1F)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.MUD)
                    .isViewBlocking((state, getter, pos) -> state.getValue(PoopPiece.LAYERS) >= 8)
                    .pushReaction(PushReaction.DESTROY)
            )
    );

    public static final DeferredBlock<Block> COMPOOPER = registerBlock("compooper",
            () -> new CompooperBlock(BlockBehaviour.Properties.of()
                    .randomTicks()
                    .noOcclusion()
                    .mapColor(MapColor.COLOR_BROWN)
                    .instrument(NoteBlockInstrument.BASS)
                    .requiresCorrectToolForDrops()
                    .strength(0.6F)
                    .sound(SoundType.METAL)
            )
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
                    .isSuffocating(PSBlockProperties::neverSuffocate)
                    .isViewBlocking(PSBlockProperties::neverBlockVision)
                    .ignitedByLava()
                    .pushReaction(PushReaction.DESTROY)
                    .isRedstoneConductor(PSBlockProperties::never)
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
                    .isSuffocating(PSBlockProperties::neverSuffocate)
                    .isViewBlocking(PSBlockProperties::neverBlockVision)
                    .ignitedByLava()
                    .pushReaction(PushReaction.DESTROY)
                    .isRedstoneConductor(PSBlockProperties::never)
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
                    .isSuffocating(PSBlockProperties::neverSuffocate)
                    .isViewBlocking(PSBlockProperties::neverBlockVision)
                    .ignitedByLava()
                    .pushReaction(PushReaction.DESTROY)
                    .isRedstoneConductor(PSBlockProperties::never)
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
