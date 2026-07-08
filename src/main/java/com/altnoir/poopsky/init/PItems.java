package com.altnoir.poopsky.init;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.common.block.ToiletComponent;
import com.altnoir.poopsky.common.item.PArmorMaterials;
import com.altnoir.poopsky.common.item.PFoods;
import com.altnoir.poopsky.common.item.PToolTiers;
import com.altnoir.poopsky.common.item.p.*;
import com.tterrag.registrate.Registrate;
import com.tterrag.registrate.util.entry.ItemEntry;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.*;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.ArrayList;
import java.util.List;

public class PItems {
    public static final Registrate ITEMS = PoopSky.REGISTRATE;

    public static final ItemEntry<PoopItem> POOP = ITEMS.item("poop",
            props -> new PoopItem(props.food(PFoods.POOP).stacksTo(88))).register();
    public static final ItemEntry<ChiliPoopItem> CHILI_POOP = ITEMS.item("chili_poop",
            props -> new ChiliPoopItem(props.food(PFoods.POOP).stacksTo(88))).register();
    public static final ItemEntry<PoopItem> GOLDEN_POOP = ITEMS.item("golden_poop",
            props -> new PoopItem(props.food(PFoods.GOLDEN_POOP).stacksTo(88))).register();
    public static final ItemEntry<Item> SEEDBED_CURSE = ITEMS.item("seedbed_curse",
            props -> new Item(props.stacksTo(88))).register();
    public static final ItemEntry<Item> FOLIUM_SENNAE = ITEMS.item("folium_sennae", Item::new).register();

    public static final ItemEntry<PoopBallItem> POOP_BALL = ITEMS.item("poop_ball",
            props -> new PoopBallItem(props.stacksTo(88))).register();
    public static final ItemEntry<SaplingBallItem> SAPLING_POOP_BALL = ITEMS.item("sapling_poop_ball",
            props -> new SaplingBallItem(props.food(PFoods.SAPLING_BALL).stacksTo(88))).register();
    public static final ItemEntry<SeaPoopBallItem> SEA_POOP_BALL = ITEMS.item("sea_poop_ball",
            props -> new SeaPoopBallItem(props.stacksTo(88))).register();
    public static final ItemEntry<WitherPoopBallItem> WITHER_POOP_BALL = ITEMS.item("wither_poop_ball",
            props -> new WitherPoopBallItem(props.stacksTo(88))).register();

    public static final ItemEntry<SimpleFeedableItem> POOP_MOONCAKE = ITEMS.item("poop_mooncake",
            props -> new SimpleFeedableItem(props.food(PFoods.POOP_MOONCAKE).stacksTo(88))).register();
    public static final ItemEntry<SimpleFeedableItem> CHILI_POOP_MOONCAKE = ITEMS.item("chili_poop_mooncake",
            props -> new SimpleFeedableItem(props.food(PFoods.CHILI_POOP_MOONCAKE).stacksTo(88))).register();
    public static final ItemEntry<SimpleFeedableItem> GOLDEN_POOP_MOONCAKE = ITEMS.item("golden_poop_mooncake",
            props -> new SimpleFeedableItem(props.food(PFoods.GOLDEN_POOP_MOONCAKE).stacksTo(88))).register();
    public static final ItemEntry<SimpleFeedableItem> BAKED_MAGGOTS = ITEMS.item("baked_maggots",
            props -> new SimpleFeedableItem(props.food(PFoods.BAKED_MAGGOTS).stacksTo(88))).register();
    public static final ItemEntry<SimpleFeedableItem> POOP_BREAD = ITEMS.item("poop_bread",
            props -> new SimpleFeedableItem(props.food(PFoods.POOP_BREAD).stacksTo(88))).register();
    public static final ItemEntry<SimpleFeedableItem> POOP_DUMPLINGS = ITEMS.item("poop_dumplings",
            props -> new SimpleFeedableItem(props.food(PFoods.POOP_DUMPLINGS).stacksTo(88))).register();
    public static final ItemEntry<SimpleFeedableItem> POOP_SOUP = ITEMS.item("poop_soup",
            props -> new SimpleFeedableItem(props.food(PFoods.POOP_SOUP).stacksTo(88))).register();
    public static final ItemEntry<SimpleFeedableItem> POOP_VEGETABLE_STICKS = ITEMS.item("poop_vegetable_sticks",
            props -> new SimpleFeedableItem(props.food(PFoods.POOP_VEGETABLE_STICKS).stacksTo(88))).register();
    public static final ItemEntry<SimpleFeedableItem> POOBURGER_MEAT = ITEMS.item("pooburger_meat",
            props -> new SimpleFeedableItem(props.food(PFoods.POOBURGER_MEAT).stacksTo(88))).register();
    public static final ItemEntry<SimpleFeedableItem> POOBURGER = ITEMS.item("pooburger",
            props -> new SimpleFeedableItem(props.food(PFoods.POOBURGER).stacksTo(88))).register();
    public static final ItemEntry<SimpleFeedableItem> POOP_PASTA = ITEMS.item("poop_pasta",
            props -> new SimpleFeedableItem(props.food(PFoods.POOP_PASTA).stacksTo(88))).register();
    public static final ItemEntry<SimpleFeedableItem> POODDING = ITEMS.item("poodding",
            props -> new SimpleFeedableItem(props.food(PFoods.POODDING).stacksTo(88))).register();

    public static final ItemEntry<ChiliItem> DRAGON_BREATH_CHILI = ITEMS.item("dragon_breath_chili",
            props -> new ChiliItem(props.food(PFoods.DRAGON_BREATH_CHILI))).register();
    public static final ItemEntry<DragonFruitRItem> KING_OF_DRAGON_FRUIT = ITEMS.item("king_of_dragon_fruit",
            props -> new DragonFruitRItem(props.food(PFoods.KING_OF_DRAGON_FRUIT))).register();

    public static final ItemEntry<ToiletPlugItem> TOILET_PLUG = ITEMS.item("toilet_plug",
            props -> new ToiletPlugItem(props.attributes(ToiletPlugItem.createWeaponAttributes())
                    .stacksTo(1))).register();
    public static final ItemEntry<ToiletLinkerItem> TOILET_PLUG_WAND = ITEMS.item("toilet_plug_wand",
            props -> new ToiletLinkerItem(props.attributes(ToiletPlugItem.createWeaponAttributes())
                    .component(PComponents.TOILET_COMPONENT, ToiletComponent.EMPTY)
                    .stacksTo(1))).register();
    public static final ItemEntry<FeedableBlockItem> MAGGOTS_SEEDS = ITEMS.item("maggots_seeds",
            props -> new FeedableBlockItem(PBlocks.MAGGOTS.get(), new Item.Properties().food(PFoods.MAGGOTS_SEEDS).stacksTo(88))).register();
    public static final ItemEntry<FeedableBlockItem> ROUNDWORM = ITEMS.item("roundworm",
            props -> new FeedableBlockItem(PBlocks.ROUNDWORM_VINES.get(), new Item.Properties().food(PFoods.ROUNDWORM).stacksTo(88))).register();

    public static final ItemEntry<Item> OMINOUS_FILTHY_INGOT = ITEMS.item("ominous_filthy_ingot", Item::new).register();
    public static final ItemEntry<MilosSwordItem> MILOS_SWORD = ITEMS.item("milos_sword",
            prop -> new MilosSwordItem(PToolTiers.MILOS,
                    prop.attributes(MilosSwordItem.createAttributes(PToolTiers.MILOS, 2, 1, -3.4F))
            )).register();

    public static final ItemEntry<OmenArmorItem> OMEN_HELMET = ITEMS.item("omen_helmet",
            prop -> new OmenArmorItem(PArmorMaterials.OMEN_ARMOR_MATERIAL, ArmorItem.Type.HELMET,
                    prop.durability(ArmorItem.Type.HELMET.getDurability(24)))).register();
    public static final ItemEntry<ArmorItem> OMEN_CHESTPLATE = ITEMS.item("omen_chestplate",
            prop -> new ArmorItem(PArmorMaterials.OMEN_ARMOR_MATERIAL, ArmorItem.Type.CHESTPLATE,
                    new Item.Properties().durability(ArmorItem.Type.HELMET.getDurability(24)))).register();
    public static final ItemEntry<ArmorItem> OMEN_LEGGINGS = ITEMS.item("omen_leggings",
            prop -> new ArmorItem(PArmorMaterials.OMEN_ARMOR_MATERIAL, ArmorItem.Type.LEGGINGS,
                    new Item.Properties().durability(ArmorItem.Type.HELMET.getDurability(24)))).register();
    public static final ItemEntry<ArmorItem> OMEN_BOOTS = ITEMS.item("omen_boots",
            prop -> new ArmorItem(PArmorMaterials.OMEN_ARMOR_MATERIAL, ArmorItem.Type.BOOTS,
                    new Item.Properties().durability(ArmorItem.Type.HELMET.getDurability(24)))).register();

    public static final ItemEntry<SmithingTemplateItem> OMEN_UPGRADE_SMITHING_TEMPLATE = ITEMS.item("omen_upgrade_smithing_template",
            props -> OmenSmithingTemplateItem.createOmenUpgradeTemplate()).register();

    public static final ItemEntry<FlyCatcherItem> FLY_CATCHER = ITEMS.item("fly_catcher",
            props -> new FlyCatcherItem(props.stacksTo(1).durability(88))).register();
    public static final ItemEntry<TimeBellItem> TIME_BELL = ITEMS.item("time_bell",
            props -> new TimeBellItem(props.stacksTo(1))).register();
    public static final ItemEntry<Item> SPALL = ITEMS.item("spall", Item::new).register();
    public static final ItemEntry<UrineBottleItem> URINE_BOTTLE = ITEMS.item("urine_bottle",
            prop -> new UrineBottleItem(prop
                    .craftRemainder(Items.GLASS_BOTTLE)
                    .food(PFoods.URINE_BOTTLE)
                    .stacksTo(18)
            )).register();
    public static final ItemEntry<BucketItem> URINE_BUCKET = ITEMS.item("urine_bucket",
            prop -> new BucketItem(PFluids.URINE.get(), prop.stacksTo(1))).register();

    public static final ItemEntry<DeferredSpawnEggItem> POOLIME_SPAWN_EGG = ITEMS.item("poolime_spawn_egg",
            prop -> new DeferredSpawnEggItem(PEntityType.POOLIME, 0x7D5F36, 0x5E4228, prop)).register();
    public static final ItemEntry<DeferredSpawnEggItem> FLY_SPAWN_EGG = ITEMS.item("fly_spawn_egg",
            prop -> new DeferredSpawnEggItem(PEntityType.FLY, 0x3B4346, 0x900D2D, prop)).register();
    public static final ItemEntry<Item> LAWRENCE_MUSIC_DISC = ITEMS.item("music_disc_lawrence",
            props -> new Item(props.jukeboxPlayable(PSoundEvents.LAWRENCE_KEY).rarity(Rarity.RARE).stacksTo(1))).register();
    public static final ItemEntry<Item> LIGHT_DANCE_MUSIC_DISC = ITEMS.item("music_disc_light_dance",
            props -> new Item(props.jukeboxPlayable(PSoundEvents.LIGHT_DANCE_KEY).rarity(Rarity.RARE).stacksTo(1))).register();
    public static final ItemEntry<Item> MOON_BOWL_MUSIC_DISC = ITEMS.item("music_disc_moon_bowl",
            props -> new Item(props.jukeboxPlayable(PSoundEvents.MOON_BOWL_KEY).rarity(Rarity.RARE).stacksTo(1))).register();

    public static final ItemEntry<FlyItem> FLY = ITEMS.item("fly",
            props -> new FlyItem(props.stacksTo(88))).register();

    public static List<Item> getAllItems() {
        var registrateItems = PoopSky.REGISTRATE.getAll(Registries.ITEM).stream()
                .map(DeferredHolder::get)
                .toList();

        return new ArrayList<>(registrateItems);
    }
}