package com.altnoir.poopsky.item;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.block.PSBlocks;
import com.altnoir.poopsky.component.PFoods;
import com.altnoir.poopsky.component.PSComponents;
import com.altnoir.poopsky.component.ToiletComponent;
import com.altnoir.poopsky.entity.PSEntityType;
import com.altnoir.poopsky.fluid.PSFluids;
import com.altnoir.poopsky.item.p.*;
import com.altnoir.poopsky.sound.PSSoundEvents;
import net.minecraft.world.item.*;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;


public class PSItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(PoopSky.MOD_ID);

    public static final DeferredItem<Item> POOP = ITEMS.register("poop", () ->
            new PoopItem(new Item.Properties().food(PFoods.POOP).stacksTo(88)));
    public static final DeferredItem<Item> CHILI_POOP = ITEMS.register("chili_poop", () ->
            new ChiliPoopItem(new Item.Properties().food(PFoods.POOP).stacksTo(88)));
    public static final DeferredItem<Item> GOLDEN_POOP = ITEMS.register("golden_poop", () ->
            new PoopItem(new Item.Properties().food(PFoods.GOLDEN_POOP).stacksTo(88)));
    public static final DeferredItem<Item> FOLIUM_SENNAE = ITEMS.register("folium_sennae", () ->
            new Item(new Item.Properties()));

    public static final DeferredItem<Item> POOP_BALL = ITEMS.register("poop_ball", () ->
            new PoopBallItem(new Item.Properties().stacksTo(88)));
    public static final DeferredItem<Item> SAPING_POOP_BALL = ITEMS.register("saping_poop_ball", () ->
            new SapingBallItem(new Item.Properties().food(PFoods.SAPING_BALL).stacksTo(88)));
    public static final DeferredItem<Item> WITHER_POOP_BALL = ITEMS.register("wither_poop_ball", () ->
            new WitherPoopBallItem(new Item.Properties().stacksTo(88)));

    public static final DeferredItem<Item> BAKED_MAGGOTS = ITEMS.register("baked_maggots", () ->
            new Item(new Item.Properties().food(PFoods.BAKED_MAGGOTS).stacksTo(88)));
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
    public static final DeferredItem<Item> TOILET_PLUG_WAND = ITEMS.register("toilet_plug_wand", () ->
            new ToiletLinkerItem(new Item.Properties()
                    .component(PSComponents.TOILET_COMPONENT, ToiletComponent.EMPTY)
                    .stacksTo(1)));
    public static final DeferredItem<Item> TIME_BELL = ITEMS.register("time_bell", () ->
            new TimeBellItem(new Item.Properties().stacksTo(1)));
    public static final DeferredItem<Item> SPALL = ITEMS.register("spall", () ->
            new Item(new Item.Properties()));

    public static final DeferredItem<Item> MAGGOTS_SEEDS = ITEMS.register("maggots_seeds", () ->
            new ItemNameBlockItem(PSBlocks.MAGGOTS.get(), new Item.Properties().food(PFoods.MAGGOTS_SEEDS).stacksTo(88)));
    public static final DeferredItem<Item> ROUNDWORM = ITEMS.register("roundworm", () ->
            new ItemNameBlockItem(PSBlocks.ROUNDWORM_VINES.get(), new Item.Properties().food(PFoods.ROUNDWORM).stacksTo(88)));


    public static final DeferredItem<Item> URINE_BOTTLE = ITEMS.register("urine_bottle",
            () -> new UrineBottleItem(new Item.Properties()
                    .craftRemainder(Items.GLASS_BOTTLE)
                    .food(PFoods.URINE_BOTTLE)
                    .stacksTo(18)
            )
    );
    public static final DeferredItem<BucketItem> POOP_BUCKET = ITEMS.register("poop_bucket",
            () -> new BucketItem(PSFluids.POOP.get(), new Item.Properties().stacksTo(1)));

    public static final DeferredItem<Item> POOLIME_SPAWN_EGG = ITEMS.register("poolime_spawn_egg",
            () -> new DeferredSpawnEggItem(PSEntityType.POOLIME, 0x7D5F36, 0x5E4228,
                    new Item.Properties()));

    public static final DeferredItem<Item> LAWRENCE_MUSIC_DISC = ITEMS.register("music_disc_lawrence", () ->
            new Item(new Item.Properties().jukeboxPlayable(PSSoundEvents.LAWRENCE_KEY).rarity(Rarity.RARE).stacksTo(1)));
    public static final DeferredItem<Item> LIGHT_DANCE_MUSIC_DISC = ITEMS.register("music_disc_light_dance", () ->
            new Item(new Item.Properties().jukeboxPlayable(PSSoundEvents.LIGHT_DANCE_KEY).rarity(Rarity.RARE).stacksTo(1)));
    public static final DeferredItem<Item> MOON_BOWL_MUSIC_DISC = ITEMS.register("music_disc_moon_bowl", () ->
            new Item(new Item.Properties().jukeboxPlayable(PSSoundEvents.MOON_BOWL_KEY).rarity(Rarity.RARE).stacksTo(1)));


    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
