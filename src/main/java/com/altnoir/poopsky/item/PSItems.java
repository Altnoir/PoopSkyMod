package com.altnoir.poopsky.item;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.block.PSBlocks;
import com.altnoir.poopsky.component.PSComponents;
import com.altnoir.poopsky.component.PSFoodComponents;
import com.altnoir.poopsky.sound.PSSoundEvents;
import net.minecraft.block.Blocks;
import net.minecraft.component.type.FoodComponents;
import net.minecraft.item.AliasedBlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;

public class PSItems {
    public static final Item POOP = registerItem("poop",
            new Poop(new Item.Settings().food(PSFoodComponents.POOP).maxCount(88)));
    public static final Item POOP_BALL = registerItem("poop_ball",
            new PoopBall(new Item.Settings().maxCount(88)));
    public static final Item SPALL = registerItem("spall",
            new Item(new Item.Settings()));
    public static final Item TOILET_PLUG = registerItem("toilet_plug",
            new ToiletPlug(new Item.Settings().maxCount(1)));
    public static final Item TOILET_LINKER = registerItem("toilet_linker",
            new ToiletLinker(new Item.Settings()
                    .component(PSComponents.TOILET_COMPONENT, new PSComponents.ToiletComponent("","",0,0,0,0,0,0))
                    .maxCount(1)));

    public static final Item LAWRENCE_MUSIC_DISC = registerItem("music_disc_lawrence",
            new Item(new Item.Settings().jukeboxPlayable(PSSoundEvents.LAWRENCE_KEY).rarity(Rarity.RARE).maxCount(1)));
    public static final Item URINE_BOTTLE = registerItem("urine_bottle",
            new UrineBottle(new Item.Settings().recipeRemainder(Items.GLASS_BOTTLE).food(PSFoodComponents.URINE_BOTTLE).maxCount(16)));

    public static final Item BAKED_MAGGOTS = registerItem("baked_maggots",
            new Item(new Item.Settings().food(PSFoodComponents.BAKED_MAGGOTS).maxCount(88)));
    public static final Item POOP_BREAD = registerItem("poop_bread",
            new Item(new Item.Settings().food(PSFoodComponents.POOP_BREAD).maxCount(88)));
    public static final Item POOP_DUMPLINGS = registerItem("poop_dumplings",
            new Item(new Item.Settings().food(PSFoodComponents.POOP_DUMPLINGS).maxCount(88)));
    public static final Item POOP_SOUP = registerItem("poop_soup",
            new Item(new Item.Settings().food(PSFoodComponents.POOP_SOUP).maxCount(8)) {
                @Override
                public SoundEvent getEatSound() {
                    return SoundEvents.ITEM_HONEY_BOTTLE_DRINK;
                }
            }
        );
    public static final Item DRAGON_BREATH_CHILI = registerItem("dragon_breath_chili",
            new Chili(new Item.Settings().food(PSFoodComponents.DRAGON_BREATH_CHILI).rarity(Rarity.UNCOMMON)));

    public static Item MAGGOTS_SEEDS;
    private static Item registerItem(String name, Item item) {
        return Registry.register(Registries.ITEM, Identifier.of(PoopSky.MOD_ID, name),item);
    }
    public static void registerModItems() {
        PoopSky.LOGGER.info("Registering Mod Items for " + PoopSky.MOD_ID);
    }
}
