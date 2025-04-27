package com.altnoir.poopsky.item;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.component.PSComponents;
import com.altnoir.poopsky.sound.PSSoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;

public class PSItems {
    public static final Item POOP = registerItem("poop",
            new Poop(new Item.Settings().food(Poop.POOP).maxCount(88)));
    public static final Item POOP_BALL = registerItem("poop_ball",
            new PoopBall(new Item.Settings().maxCount(88)));
    public static final Item TOILET_PLUG = registerItem("toilet_plug",
            new ToiletPlug(new Item.Settings().maxCount(1)));
    public static final Item TOILET_LINKER = registerItem("toilet_linker",
            new ToiletLinker(new Item.Settings()
                    .component(PSComponents.TOILET_COMPONENT_1, new PSComponents.ToiletComponent(null,0,0,0))
                    .component(PSComponents.TOILET_COMPONENT_2, new PSComponents.ToiletComponent(null,0,0,0))
                    .maxCount(1)));

    public static final Item KIITEOOKINI_MUSIC_DISC = registerItem("music_disc_kiiteokini",
            new Item(new Item.Settings().jukeboxPlayable(PSSoundEvents.KIITEOOKINI_KEY).rarity(Rarity.RARE).maxCount(1)));
    private static Item registerItem(String name, Item item) {
        return Registry.register(Registries.ITEM, Identifier.of(PoopSky.MOD_ID, name),item);
    }
    public static void registerModItems() {
        PoopSky.LOGGER.info("Registering Mod Items for " + PoopSky.MOD_ID);
    }
}
