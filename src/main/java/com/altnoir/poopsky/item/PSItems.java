package com.altnoir.poopsky.item;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.component.PSComponents;
import com.altnoir.poopsky.sound.PSSoundEvents;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;

public class PSItems {
    public static final Item POOP = registerItem("poop",
            new Poop(new Item.Settings().food(Poop.POOP).maxCount(88)));
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
            new UrineBottle(new Item.Settings().maxCount(16)));

    private static Item registerItem(String name, Item item) {
        return Registry.register(Registries.ITEM, Identifier.of(PoopSky.MOD_ID, name),item);
    }
    public static void registerModItems() {
        PoopSky.LOGGER.info("Registering Mod Items for " + PoopSky.MOD_ID);
    }
}
