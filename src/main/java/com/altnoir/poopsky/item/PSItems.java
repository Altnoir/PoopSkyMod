package com.altnoir.poopsky.item;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.component.PFoods;
import com.altnoir.poopsky.component.PSComponents;
import com.altnoir.poopsky.component.ToiletComponent;
import com.altnoir.poopsky.entity.PSEntityType;
import com.altnoir.poopsky.fluid.PSFluids;
import com.altnoir.poopsky.item.p.*;
import com.altnoir.poopsky.sound.PSSoundEvents;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;


public class PSItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(PoopSky.MOD_ID);

    public static final DeferredItem<Item> POOP = ITEMS.register("poop", () ->
            new PoopItem(new Item.Properties().food(PFoods.POOP).stacksTo(88)));
    public static final DeferredItem<BucketItem> POOP_BUCKET = ITEMS.register("poop_bucket",
            () -> new BucketItem(PSFluids.POOP.get(), new Item.Properties().stacksTo(1)));
    public static final DeferredItem<Item> POOP_BALL = ITEMS.register("poop_ball", () ->
            new PoopBallItem(new Item.Properties().stacksTo(88)));
    public static final DeferredItem<Item> SAPING_BALL = ITEMS.register("saping_ball", () ->
            new SapingBallItem(new Item.Properties().food(PFoods.SAPING_BALL).stacksTo(88)));

    public static final DeferredItem<Item> POOP_BREAD = ITEMS.register("poop_bread", () ->
            new Item(new Item.Properties().food(PFoods.POOP_BREAD).stacksTo(88)));
    public static final DeferredItem<Item> POOP_DUMPLINGS = ITEMS.register("poop_dumplings", () ->
            new Item(new Item.Properties().food(PFoods.POOP_DUMPLINGS).stacksTo(88)));
    public static final DeferredItem<Item> POOP_SOUP = ITEMS.register("poop_soup", () ->
            new Item(new Item.Properties().food(PFoods.POOP_SOUP).stacksTo(88)));
    public static final DeferredItem<Item> POOP_VEGETABLE_STICKS = ITEMS.register("poop_vegetable_sticks", () ->
            new Item(new Item.Properties().food(PFoods.POOP_VEGETABLE_STICKS).stacksTo(88)));
    public static final DeferredItem<Item> POOBURGER_MEAT = ITEMS.register("pooburger_meat", () ->
            new Item(new Item.Properties().food(PFoods.POOBURGER_MEAT).stacksTo(88)));
    public static final DeferredItem<Item> POOBURGER = ITEMS.register("pooburger", () ->
            new Item(new Item.Properties().food(PFoods.POOBURGER).stacksTo(88)));
    public static final DeferredItem<Item> POOP_PASTA = ITEMS.register("poop_pasta", () ->
            new Item(new Item.Properties().food(PFoods.POOP_PASTA).stacksTo(88)));
    public static final DeferredItem<Item> POODDING = ITEMS.register("poodding", () ->
            new Item(new Item.Properties().food(PFoods.POODDING).stacksTo(88)));
    public static final DeferredItem<Item> DRAGON_BREATH_CHILI = ITEMS.register("dragon_breath_chili", () ->
            new Item(new Item.Properties().food(PFoods.DRAGON_BREATH_CHILI).stacksTo(88)));

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

    public static final DeferredItem<Item> POOPLIME_SPAWN_EGG = ITEMS.register("pooplime_spawn_egg",
            () -> new DeferredSpawnEggItem(PSEntityType.POOPLIME, 0x7D5F36, 0x5E4228,
                    new Item.Properties()));

    public static final DeferredItem<Item> LAWRENCE_MUSIC_DISC = ITEMS.register("music_disc_lawrence", () ->
            new Item(new Item.Properties().jukeboxPlayable(PSSoundEvents.LAWRENCE_KEY).rarity(Rarity.RARE).stacksTo(1)));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
