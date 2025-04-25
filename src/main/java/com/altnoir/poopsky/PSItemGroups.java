package com.altnoir.poopsky;

import com.altnoir.poopsky.Fluid.PSFluids;
import com.altnoir.poopsky.block.PSBlocks;
import com.altnoir.poopsky.block.ToiletBlocks;
import com.altnoir.poopsky.item.PSItems;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class PSItemGroups {
    public static final ItemGroup POOPSKY = Registry.register(Registries.ITEM_GROUP,
            Identifier.of(PoopSky.MOD_ID, "poop"),
            FabricItemGroup.builder()
                    .icon(() -> new ItemStack(PSItems.POOP))
                    .displayName(Text.translatable("itemgroup.poopsky"))
                    .entries((displayContext, entries) -> {
                        entries.add(PSItems.POOP);
                        entries.add(PSItems.TOILET_LINKER);
                        entries.add(PSItems.POOP_BALL);
                        entries.add(PSBlocks.POOP_SAPLING);
                        entries.add(PSBlocks.POOP_LEAVES);
                        entries.add(PSBlocks.POOP_LOG);
                        entries.add(PSBlocks.STRIPPED_POOP_LOG);
                        entries.add(PSBlocks.POOP_PIECE);
                        entries.add(PSBlocks.POOP_BLOCK);
                        entries.add(PSBlocks.POOP_STAIRS);
                        entries.add(PSBlocks.POOP_SLAB);
                        entries.add(PSBlocks.POOP_VERTICAL_SLAB);
                        entries.add(PSBlocks.POOP_BUTTON);
                        entries.add(PSBlocks.POOP_PRESSURE_PLATE);
                        entries.add(PSBlocks.POOP_FENCE);
                        entries.add(PSBlocks.POOP_FENCE_GATE);
                        entries.add(PSBlocks.POOP_WALL);
                        entries.add(PSBlocks.POOP_DOOR);
                        entries.add(PSBlocks.POOP_TRAPDOOR);
                        entries.add(PSItems.KIITEOOKINI_MUSIC_DISC);
                        entries.add(PSFluids.URINE_BUCKET);
                        //撤锁大家庭
                        entries.add(ToiletBlocks.OAK_TOILET);
                        entries.add(ToiletBlocks.SPRUCE_TOILET);
                        entries.add(ToiletBlocks.BIRCH_TOILET);
                        entries.add(ToiletBlocks.JUNGLE_TOILET);
                        entries.add(ToiletBlocks.ACACIA_TOILET);
                        entries.add(ToiletBlocks.DARK_OAK_TOILET);
                        entries.add(ToiletBlocks.MANGROVE_TOILET);
                        entries.add(ToiletBlocks.CHERRY_TOILET);
                        entries.add(ToiletBlocks.BAMBOO_TOILET);
                        entries.add(ToiletBlocks.CRIMSON_TOILET);
                        entries.add(ToiletBlocks.WARPED_TOILET);
                        entries.add(ToiletBlocks.WHITE_CONCRETE_TOILET);
                        entries.add(ToiletBlocks.LIGHT_GRAY_CONCRETE_TOILET);
                        entries.add(ToiletBlocks.GRAY_CONCRETE_TOILET);
                        entries.add(ToiletBlocks.BLACK_CONCRETE_TOILET);
                        entries.add(ToiletBlocks.BROWN_CONCRETE_TOILET);
                        entries.add(ToiletBlocks.RED_CONCRETE_TOILET);
                        entries.add(ToiletBlocks.ORANGE_CONCRETE_TOILET);
                        entries.add(ToiletBlocks.YELLOW_CONCRETE_TOILET);
                        entries.add(ToiletBlocks.LIME_CONCRETE_TOILET);
                        entries.add(ToiletBlocks.GREEN_CONCRETE_TOILET);
                        entries.add(ToiletBlocks.CYAN_CONCRETE_TOILET);
                        entries.add(ToiletBlocks.LIGHT_BLUE_CONCRETE_TOILET);
                        entries.add(ToiletBlocks.BLUE_CONCRETE_TOILET);
                        entries.add(ToiletBlocks.PURPLE_CONCRETE_TOILET);
                        entries.add(ToiletBlocks.MAGENTA_CONCRETE_TOILET);
                        entries.add(ToiletBlocks.PINK_CONCRETE_TOILET);
                        entries.add(ToiletBlocks.RAINBOW_TOILET);
                    })
                    .build()
            );
    public static void registerItemGroups() {
        PoopSky.LOGGER.info("Registering Mod Item Groups for " + PoopSky.MOD_ID);
    }
}
