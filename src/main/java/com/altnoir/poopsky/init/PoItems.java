package com.altnoir.poopsky.init;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.content.block.ToiletComponent;
import com.altnoir.poopsky.content.item.PArmorMaterials;
import com.altnoir.poopsky.content.item.PFoods;
import com.altnoir.poopsky.content.item.PToolTiers;
import com.altnoir.poopsky.content.item.p.*;
import com.altnoir.poopsky.impl.registrate.PoRegistrate;
import com.tterrag.registrate.util.entry.ItemEntry;
import com.tterrag.registrate.util.nullness.NonNullFunction;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.*;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.ArrayList;
import java.util.List;

public class PoItems {
    private static final PoRegistrate REGISTRATE = PoopSky.registrate();

    public static final ItemEntry<PoopItem> POOP = registerItem("poop",
            props -> new PoopItem(props.food(PFoods.POOP).stacksTo(88)));
    public static final ItemEntry<ChiliPoopItem> CHILI_POOP = registerItem("chili_poop",
            props -> new ChiliPoopItem(props.food(PFoods.POOP).stacksTo(88)));
    public static final ItemEntry<PoopItem> GOLDEN_POOP = registerItem("golden_poop",
            props -> new PoopItem(props.food(PFoods.GOLDEN_POOP).stacksTo(88)));
    public static final ItemEntry<Item> SEEDBED_CURSE = registerItem("seedbed_curse",
            props -> new Item(props.stacksTo(88)));
    public static final ItemEntry<Item> FOLIUM_SENNAE = registerItem("folium_sennae", Item::new);

    public static final ItemEntry<PoopBallItem> POOP_BALL = registerItem("poop_ball",
            props -> new PoopBallItem(props.stacksTo(88)));
    public static final ItemEntry<SaplingBallItem> SAPLING_POOP_BALL = registerItem("sapling_poop_ball",
            props -> new SaplingBallItem(props.food(PFoods.SAPLING_BALL).stacksTo(88)));
    public static final ItemEntry<SeaPoopBallItem> SEA_POOP_BALL = registerItem("sea_poop_ball",
            props -> new SeaPoopBallItem(props.stacksTo(88)));
    public static final ItemEntry<WitherPoopBallItem> WITHER_POOP_BALL = registerItem("wither_poop_ball",
            props -> new WitherPoopBallItem(props.stacksTo(88)));

    public static final ItemEntry<SimpleFeedableItem> POOP_MOONCAKE = registerItem("poop_mooncake",
            props -> new SimpleFeedableItem(props.food(PFoods.POOP_MOONCAKE).stacksTo(88)));
    public static final ItemEntry<SimpleFeedableItem> CHILI_POOP_MOONCAKE = registerItem("chili_poop_mooncake",
            props -> new SimpleFeedableItem(props.food(PFoods.CHILI_POOP_MOONCAKE).stacksTo(88)));
    public static final ItemEntry<SimpleFeedableItem> GOLDEN_POOP_MOONCAKE = registerItem("golden_poop_mooncake",
            props -> new SimpleFeedableItem(props.food(PFoods.GOLDEN_POOP_MOONCAKE).stacksTo(88)));
    public static final ItemEntry<SimpleFeedableItem> BAKED_MAGGOTS = registerItem("baked_maggots",
            props -> new SimpleFeedableItem(props.food(PFoods.BAKED_MAGGOTS).stacksTo(88)));
    public static final ItemEntry<SimpleFeedableItem> POOP_BREAD = registerItem("poop_bread",
            props -> new SimpleFeedableItem(props.food(PFoods.POOP_BREAD).stacksTo(88)));
    public static final ItemEntry<SimpleFeedableItem> POOP_DUMPLINGS = registerItem("poop_dumplings",
            props -> new SimpleFeedableItem(props.food(PFoods.POOP_DUMPLINGS).stacksTo(88)));
    public static final ItemEntry<SimpleFeedableItem> POOP_SOUP = registerItem("poop_soup",
            props -> new SimpleFeedableItem(props.food(PFoods.POOP_SOUP).stacksTo(88)));
    public static final ItemEntry<SimpleFeedableItem> POOP_VEGETABLE_STICKS = registerItem("poop_vegetable_sticks",
            props -> new SimpleFeedableItem(props.food(PFoods.POOP_VEGETABLE_STICKS).stacksTo(88)));
    public static final ItemEntry<SimpleFeedableItem> POOBURGER_MEAT = registerItem("pooburger_meat",
            props -> new SimpleFeedableItem(props.food(PFoods.POOBURGER_MEAT).stacksTo(88)));
    public static final ItemEntry<SimpleFeedableItem> POOBURGER = registerItem("pooburger",
            props -> new SimpleFeedableItem(props.food(PFoods.POOBURGER).stacksTo(88)));
    public static final ItemEntry<SimpleFeedableItem> POOP_PASTA = registerItem("poop_pasta",
            props -> new SimpleFeedableItem(props.food(PFoods.POOP_PASTA).stacksTo(88)));
    public static final ItemEntry<SimpleFeedableItem> POODDING = registerItem("poodding",
            props -> new SimpleFeedableItem(props.food(PFoods.POODDING).stacksTo(88)));

    public static final ItemEntry<ChiliItem> DRAGON_BREATH_CHILI = registerItem("dragon_breath_chili",
            props -> new ChiliItem(props.food(PFoods.DRAGON_BREATH_CHILI)));
    public static final ItemEntry<DragonFruitRItem> KING_OF_DRAGON_FRUIT = registerItem("king_of_dragon_fruit",
            props -> new DragonFruitRItem(props.food(PFoods.KING_OF_DRAGON_FRUIT)));

    public static final ItemEntry<ToiletPlugItem> TOILET_PLUG = registerItem("toilet_plug",
            props -> new ToiletPlugItem(props.attributes(ToiletPlugItem.createWeaponAttributes())
                    .stacksTo(1)));
    public static final ItemEntry<ToiletLinkerItem> TOILET_PLUG_WAND = registerItem("toilet_plug_wand",
            props -> new ToiletLinkerItem(props.attributes(ToiletPlugItem.createWeaponAttributes())
                    .component(PoComponents.TOILET_COMPONENT, ToiletComponent.EMPTY)
                    .stacksTo(1)));
    public static final ItemEntry<FeedableBlockItem> MAGGOTS_SEEDS = registerItem("maggots_seeds",
            props -> new FeedableBlockItem(PoBlocks.MAGGOTS.get(), new Item.Properties().food(PFoods.MAGGOTS_SEEDS).stacksTo(88)));
    public static final ItemEntry<FeedableBlockItem> ROUNDWORM = registerItem("roundworm",
            props -> new FeedableBlockItem(PoBlocks.ROUNDWORM_VINES.get(), new Item.Properties().food(PFoods.ROUNDWORM).stacksTo(88)));

    public static final ItemEntry<Item> OMINOUS_FILTHY_INGOT = registerItem("ominous_filthy_ingot", Item::new);
    public static final ItemEntry<MilosSwordItem> MILOS_SWORD = registerItem("milos_sword",
            prop -> new MilosSwordItem(PToolTiers.MILOS,
                    prop.attributes(MilosSwordItem.createAttributes(PToolTiers.MILOS, 2, 1, -3.4F))
            ));
    public static final ItemEntry<PickaxeItem> MOSSY_COBBLESTONE_PICKAXE = registerItem("mossy_cobblestone_pickaxe",
            prop -> new PickaxeItem(Tiers.STONE,
                    prop.attributes(PickaxeItem.createAttributes(Tiers.STONE, 1.0F, -2.8F))));
    public static final ItemEntry<AxeItem> MOSSY_COBBLESTONE_AXE = registerItem("mossy_cobblestone_axe",
            prop -> new AxeItem(Tiers.STONE,
                    prop.attributes(AxeItem.createAttributes(Tiers.STONE, 6.0F, -3.2F))));
    public static final ItemEntry<ShovelItem> MOSSY_COBBLESTONE_SHOVEL = registerItem("mossy_cobblestone_shovel",
            prop -> new ShovelItem(Tiers.STONE,
                    prop.attributes(ShovelItem.createAttributes(Tiers.STONE, 1.5F, -3.0F))));
    public static final ItemEntry<HoeItem> MOSSY_COBBLESTONE_HOE = registerItem("mossy_cobblestone_hoe",
            prop -> new HoeItem(Tiers.STONE,
                    prop.attributes(HoeItem.createAttributes(Tiers.STONE, -1.0F, -2.0F))));
    public static final ItemEntry<SwordItem> MOSSY_COBBLESTONE_SWORD = registerItem("mossy_cobblestone_sword",
            prop -> new SwordItem(Tiers.STONE,
                    prop.attributes(SwordItem.createAttributes(Tiers.STONE, 3, -2.4F))));

    public static final ItemEntry<OmenArmorItem> OMEN_HELMET = registerItem("omen_helmet",
            prop -> new OmenArmorItem(PArmorMaterials.OMEN_ARMOR_MATERIAL, ArmorItem.Type.HELMET,
                    prop.durability(ArmorItem.Type.HELMET.getDurability(24))));
    public static final ItemEntry<ArmorItem> OMEN_CHESTPLATE = registerItem("omen_chestplate",
            prop -> new ArmorItem(PArmorMaterials.OMEN_ARMOR_MATERIAL, ArmorItem.Type.CHESTPLATE,
                    new Item.Properties().durability(ArmorItem.Type.HELMET.getDurability(24))));
    public static final ItemEntry<ArmorItem> OMEN_LEGGINGS = registerItem("omen_leggings",
            prop -> new ArmorItem(PArmorMaterials.OMEN_ARMOR_MATERIAL, ArmorItem.Type.LEGGINGS,
                    new Item.Properties().durability(ArmorItem.Type.HELMET.getDurability(24))));
    public static final ItemEntry<ArmorItem> OMEN_BOOTS = registerItem("omen_boots",
            prop -> new ArmorItem(PArmorMaterials.OMEN_ARMOR_MATERIAL, ArmorItem.Type.BOOTS,
                    new Item.Properties().durability(ArmorItem.Type.HELMET.getDurability(24))));

    public static final ItemEntry<SmithingTemplateItem> OMEN_UPGRADE_SMITHING_TEMPLATE = registerItem("omen_upgrade_smithing_template",
            props -> OmenSmithingTemplateItem.createOmenUpgradeTemplate());

    public static final ItemEntry<FlyCatcherItem> FLY_CATCHER = registerItem("fly_catcher",
            props -> new FlyCatcherItem(props.stacksTo(1).durability(88)));
    public static final ItemEntry<TimeBellItem> TIME_BELL = registerItem("time_bell",
            props -> new TimeBellItem(props.stacksTo(1)));
    public static final ItemEntry<Item> SPALL = registerItem("spall", Item::new);
    public static final ItemEntry<UrineBottleItem> URINE_BOTTLE = registerItem("urine_bottle",
            prop -> new UrineBottleItem(prop
                    .craftRemainder(Items.GLASS_BOTTLE)
                    .food(PFoods.URINE_BOTTLE)
                    .stacksTo(18)
            ));
    public static final ItemEntry<BucketItem> URINE_BUCKET = PoFluids.URINE_BUCKET;

    public static final ItemEntry<DeferredSpawnEggItem> POOLIME_SPAWN_EGG = registerItem("poolime_spawn_egg",
            prop -> new DeferredSpawnEggItem(PoEntityType.POOLIME, 0x7D5F36, 0x5E4228, prop));
    public static final ItemEntry<DeferredSpawnEggItem> FLY_SPAWN_EGG = registerItem("fly_spawn_egg",
            prop -> new DeferredSpawnEggItem(PoEntityType.FLY, 0x3B4346, 0x900D2D, prop));
    public static final ItemEntry<Item> LAWRENCE_MUSIC_DISC = registerItem("music_disc_lawrence",
            props -> new Item(props.jukeboxPlayable(PoSoundEvents.LAWRENCE_KEY).rarity(Rarity.RARE).stacksTo(1)));
    public static final ItemEntry<Item> LIGHT_DANCE_MUSIC_DISC = registerItem("music_disc_light_dance",
            props -> new Item(props.jukeboxPlayable(PoSoundEvents.LIGHT_DANCE_KEY).rarity(Rarity.RARE).stacksTo(1)));
    public static final ItemEntry<Item> MOON_BOWL_MUSIC_DISC = registerItem("music_disc_moon_bowl",
            props -> new Item(props.jukeboxPlayable(PoSoundEvents.MOON_BOWL_KEY).rarity(Rarity.RARE).stacksTo(1)));

    public static final ItemEntry<FlyItem> FLY = registerItem("fly",
            props -> new FlyItem(props.stacksTo(88)));

    public static List<Item> getAllItems() {
        var registrateItems = REGISTRATE.getAll(Registries.ITEM).stream()
                .map(DeferredHolder::get)
                .toList();

        return new ArrayList<>(registrateItems);
    }

    private static <T extends Item> ItemEntry<T> registerItem(String name, NonNullFunction<Item.Properties, T> factory) {
        return REGISTRATE.item(name, factory)
                .model((ctx, prov) -> {
                })
                .register();
    }

    public static void register() {
    }
}
