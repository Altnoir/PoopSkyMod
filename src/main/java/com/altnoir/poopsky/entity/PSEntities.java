package com.altnoir.poopsky.entity;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.block.ToiletBlocks;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class PSEntities {
    //普通实体
    public static final EntityType<ToiletPlugEntity> TOILET_PLUG_ENTITY = Registry.register(Registries.ENTITY_TYPE,
            Identifier.of(PoopSky.MOD_ID, "toilet_plug"),
            EntityType.Builder.create(ToiletPlugEntity::new, SpawnGroup.MISC)
                    .dimensions(0.75f, 0.35f).build()
    );
    public static final EntityType<ChairEntity> STOOL_ENTITY = Registry.register(Registries.ENTITY_TYPE,
            Identifier.of(PoopSky.MOD_ID, "stool_entity"),
            EntityType.Builder.create(ChairEntity::new, SpawnGroup.MISC)
                    .dimensions(0.5f, 0.5f).build()
    );

    //方块实体
    public static final BlockEntityType<ToiletBlockEntity> TOILET_BLOCK_ENTITY = Registry.register(
            Registries.BLOCK_ENTITY_TYPE, Identifier.of(PoopSky.MOD_ID, "toilet_block_entity"),
            BlockEntityType.Builder.create(ToiletBlockEntity::new,
                    ToiletBlocks.OAK_TOILET,
                    ToiletBlocks.SPRUCE_TOILET,
                    ToiletBlocks.BIRCH_TOILET,
                    ToiletBlocks.JUNGLE_TOILET,
                    ToiletBlocks.ACACIA_TOILET,
                    ToiletBlocks.DARK_OAK_TOILET,
                    ToiletBlocks.MANGROVE_TOILET,
                    ToiletBlocks.CHERRY_TOILET,
                    ToiletBlocks.BAMBOO_TOILET,
                    ToiletBlocks.CRIMSON_TOILET,
                    ToiletBlocks.WARPED_TOILET,

                    ToiletBlocks.STONE_TOILET,
                    ToiletBlocks.COBBLESTONE_TOILET,
                    ToiletBlocks.MOSSY_COBBLESTONE_TOILET,
                    ToiletBlocks.SMOOTH_STONE_TOILET,
                    ToiletBlocks.STONE_BRICK_TOILET,
                    ToiletBlocks.MOSSY_STONE_BRICK_TOILET,

                    ToiletBlocks.WHITE_CONCRETE_TOILET,
                    ToiletBlocks.LIGHT_GRAY_CONCRETE_TOILET,
                    ToiletBlocks.GRAY_CONCRETE_TOILET,
                    ToiletBlocks.BLACK_CONCRETE_TOILET,
                    ToiletBlocks.BROWN_CONCRETE_TOILET,
                    ToiletBlocks.RED_CONCRETE_TOILET,
                    ToiletBlocks.ORANGE_CONCRETE_TOILET,
                    ToiletBlocks.YELLOW_CONCRETE_TOILET,
                    ToiletBlocks.LIME_CONCRETE_TOILET,
                    ToiletBlocks.GREEN_CONCRETE_TOILET,
                    ToiletBlocks.CYAN_CONCRETE_TOILET,
                    ToiletBlocks.LIGHT_BLUE_CONCRETE_TOILET,
                    ToiletBlocks.BLUE_CONCRETE_TOILET,
                    ToiletBlocks.PURPLE_CONCRETE_TOILET,
                    ToiletBlocks.MAGENTA_CONCRETE_TOILET,
                    ToiletBlocks.PINK_CONCRETE_TOILET,
                    ToiletBlocks.RAINBOW_TOILET
            ).build(null)
    );

    public static void registerEntities() {
        PoopSky.LOGGER.info("Registering entities for " + PoopSky.MOD_ID);
    }
}
