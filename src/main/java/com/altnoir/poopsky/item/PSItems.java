package com.altnoir.poopsky.item;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.component.PFoods;
import com.altnoir.poopsky.sound.PSSoundEvents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;


public class PSItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(PoopSky.MOD_ID);

    public static final DeferredItem<Item> POOP = ITEMS.register("poop", () ->
            new Poop(new Item.Properties().food(PFoods.POOP).stacksTo(88)));
    public static final DeferredItem<Item> POOP_BALL = ITEMS.register("poop_ball", () ->
            new PoopBall(new Item.Properties().stacksTo(88)));
    public static final DeferredItem<Item> POOP_DUMPLINGS = ITEMS.register("poop_dumplings", () ->
            new Item(new Item.Properties().food(PFoods.POOP_DUMPLINGS).stacksTo(88)));
    public static final DeferredItem<Item> Toilet_Plug = ITEMS.register("toilet_plug", () ->
            new Item(new Item.Properties().stacksTo(1)));
    public static final DeferredItem<Item> SPALL = ITEMS.register("spall", () ->
            new Item(new Item.Properties()));

    public static final DeferredItem<Item> LAWRENCE_MUSIC_DISC = ITEMS.register("music_disc_lawrence", () ->
            new Item(new Item.Properties().jukeboxPlayable(PSSoundEvents.LAWRENCE_KEY).rarity(Rarity.RARE).stacksTo(1)));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
