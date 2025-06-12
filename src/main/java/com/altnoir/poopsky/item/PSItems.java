package com.altnoir.poopsky.item;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.component.PFoods;
import com.altnoir.poopsky.component.PSComponents;
import com.altnoir.poopsky.component.ToiletComponent;
import com.altnoir.poopsky.sound.PSSoundEvents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;


public class PSItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(PoopSky.MOD_ID);

    public static final DeferredItem<Item> POOP = ITEMS.register("poop", () ->
            new PoopItem(new Item.Properties().food(PFoods.POOP).stacksTo(88)));
    public static final DeferredItem<Item> POOP_BALL = ITEMS.register("poop_ball", () ->
            new PoopBallItem(new Item.Properties().stacksTo(88)));
    public static final DeferredItem<Item> POOP_DUMPLINGS = ITEMS.register("poop_dumplings", () ->
            new Item(new Item.Properties().food(PFoods.POOP_DUMPLINGS).stacksTo(88)));
    public static final DeferredItem<Item> TOILET_PLUG = ITEMS.register("toilet_plug", () ->
            new ToiletPlugItem(new Item.Properties().stacksTo(1)));
    public static final DeferredItem<Item> SPALL = ITEMS.register("spall", () ->
            new Item(new Item.Properties()));
    public static final DeferredItem<Item> TOILET_LINKER = ITEMS.register("toilet_linker", () ->
            new ToiletLinkerItem(new Item.Properties()
                    .component(PSComponents.TOILET_COMPONENT, ToiletComponent.EMPTY)
                    .stacksTo(1)));
    public static final DeferredItem<Item> URINE_BOTTLE = ITEMS.register("urine_bottle",
            () -> new UrineBottleItem(new Item.Properties()
                    .craftRemainder(Items.GLASS_BOTTLE)
                    .food(PFoods.URINE_BOTTLE)
                    .stacksTo(16)
            )
    );

    public static final DeferredItem<Item> LAWRENCE_MUSIC_DISC = ITEMS.register("music_disc_lawrence", () ->
            new Item(new Item.Properties().jukeboxPlayable(PSSoundEvents.LAWRENCE_KEY).rarity(Rarity.RARE).stacksTo(1)));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
