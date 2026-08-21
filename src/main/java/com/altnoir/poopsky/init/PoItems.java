package com.altnoir.poopsky.init;

import com.altnoir.poopsky.PoItemGroups;
import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.content.block.ToiletComponent;
import com.altnoir.poopsky.content.item.PArmorMaterials;
import com.altnoir.poopsky.content.item.PFoods;
import com.altnoir.poopsky.content.item.p.*;
import com.altnoir.poopsky.impl.registrate.PoRegistrate;
import com.tterrag.registrate.util.entry.ItemEntry;
import com.tterrag.registrate.util.nullness.NonNullFunction;
import net.minecraft.ChatFormatting;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.*;
import net.minecraft.world.item.equipment.ArmorType;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

public class PoItems {
    private static final PoRegistrate REGISTRATE = PoopSky.registrate();

    static {
        REGISTRATE.defaultCreativeSection(PoItemGroups.TS_ITEMS);
    }

    public static final ItemEntry<PoopItem> POOP = registerItem("poop",
            props -> new PoopItem(PFoods.apply(props, PFoods.POOP).stacksTo(88)));
    public static final ItemEntry<ChiliPoopItem> CHILI_POOP = registerItem("chili_poop",
            props -> new ChiliPoopItem(PFoods.apply(props, PFoods.POOP).stacksTo(88)));
    public static final ItemEntry<PoopItem> GOLDEN_POOP = registerItem("golden_poop",
            props -> new PoopItem(PFoods.apply(props, PFoods.GOLDEN_POOP).stacksTo(88)));
    public static final ItemEntry<Item> SEEDBED_CURSE = registerItem("seedbed_curse",
            props -> new Item(props.stacksTo(88)));
    public static final ItemEntry<Item> FOLIUM_SENNAE = registerItem("folium_sennae", Item::new);

    public static final ItemEntry<PoopBallItem> POOP_BALL = registerItem("poop_ball",
            props -> new PoopBallItem(props.stacksTo(88)));
    public static final ItemEntry<SaplingBallItem> SAPLING_POOP_BALL = registerItem("sapling_poop_ball",
            props -> new SaplingBallItem(PFoods.apply(props, PFoods.SAPLING_BALL).stacksTo(88)));
    public static final ItemEntry<SeaPoopBallItem> SEA_POOP_BALL = registerItem("sea_poop_ball",
            props -> new SeaPoopBallItem(props.stacksTo(88)));
    public static final ItemEntry<WitherPoopBallItem> WITHER_POOP_BALL = registerItem("wither_poop_ball",
            props -> new WitherPoopBallItem(props.stacksTo(88)));

    public static final ItemEntry<SimpleFeedableItem> POOP_MOONCAKE = registerFood("poop_mooncake", PFoods.POOP_MOONCAKE);
    public static final ItemEntry<SimpleFeedableItem> CHILI_POOP_MOONCAKE = registerFood("chili_poop_mooncake", PFoods.CHILI_POOP_MOONCAKE);
    public static final ItemEntry<SimpleFeedableItem> GOLDEN_POOP_MOONCAKE = registerFood("golden_poop_mooncake", PFoods.GOLDEN_POOP_MOONCAKE);
    public static final ItemEntry<SimpleFeedableItem> BAKED_MAGGOTS = registerFood("baked_maggots", PFoods.BAKED_MAGGOTS);
    public static final ItemEntry<SimpleFeedableItem> POOP_BREAD = registerFood("poop_bread", PFoods.POOP_BREAD);
    public static final ItemEntry<SimpleFeedableItem> POOP_DUMPLINGS = registerFood("poop_dumplings", PFoods.POOP_DUMPLINGS);
    public static final ItemEntry<SimpleFeedableItem> POOP_SOUP = registerFood("poop_soup", PFoods.POOP_SOUP);
    public static final ItemEntry<SimpleFeedableItem> POOP_VEGETABLE_STICKS = registerFood("poop_vegetable_sticks", PFoods.POOP_VEGETABLE_STICKS);
    public static final ItemEntry<SimpleFeedableItem> POOBURGER_MEAT = registerFood("pooburger_meat", PFoods.POOBURGER_MEAT);
    public static final ItemEntry<SimpleFeedableItem> POOBURGER = registerFood("pooburger", PFoods.POOBURGER);
    public static final ItemEntry<SimpleFeedableItem> POOPSICLE = registerFood("poopsicle", PFoods.POOPSICLE);
    public static final ItemEntry<SimpleFeedableItem> POOP_PASTA = registerFood("poop_pasta", PFoods.POOP_PASTA);
    public static final ItemEntry<SimpleFeedableItem> POODDING = registerFood("poodding", PFoods.POODDING);

    public static final ItemEntry<ChiliItem> DRAGON_BREATH_CHILI = registerItem("dragon_breath_chili",
            props -> new ChiliItem(PoBlocks.CHILI_VINES.get(), PFoods.apply(props, PFoods.DRAGON_BREATH_CHILI)));
    public static final ItemEntry<DragonFruitRItem> KING_OF_DRAGON_FRUIT = registerItem("king_of_dragon_fruit",
            props -> new DragonFruitRItem(PFoods.apply(props, PFoods.KING_OF_DRAGON_FRUIT)));

    public static final ItemEntry<ToiletPlugItem> TOILET_PLUG = registerItemNoModel("toilet_plug",
            props -> new ToiletPlugItem(props.attributes(ToiletPlugItem.createWeaponAttributes())
                    .stacksTo(1)));
    public static final ItemEntry<ToiletLinkerItem> TOILET_PLUG_WAND = registerItem("toilet_plug_wand",
            props -> new ToiletLinkerItem(props.attributes(ToiletPlugItem.createWeaponAttributes())
                    .component(PoComponents.TOILET_COMPONENT, ToiletComponent.EMPTY)
                    .stacksTo(1)));

    public static final ItemEntry<Item> OMINOUS_FILTHY_INGOT = registerItem("ominous_filthy_ingot", Item::new);
    public static final ItemEntry<MilosSwordItem> MILOS_SWORD = registerItemNoModel("milos_sword",
            prop -> new MilosSwordItem(prop
                    .durability(4088)
                    .repairable(OMINOUS_FILTHY_INGOT.get())
                    .attributes(MilosSwordItem.createAttributes(2, 11, -3.2F))));

    public static final ItemEntry<OmenArmorItem> OMEN_HELMET = registerItemNoModel("omen_helmet",
            prop -> new OmenArmorItem(PArmorMaterials.OMEN_ARMOR_MATERIAL, ArmorType.HELMET,
                    prop));
    public static final ItemEntry<Item> OMEN_CHESTPLATE = registerItemNoModel("omen_chestplate",
            prop -> new Item(prop.humanoidArmor(PArmorMaterials.OMEN_ARMOR_MATERIAL, ArmorType.CHESTPLATE)));
    public static final ItemEntry<Item> OMEN_LEGGINGS = registerItemNoModel("omen_leggings",
            prop -> new Item(prop.humanoidArmor(PArmorMaterials.OMEN_ARMOR_MATERIAL, ArmorType.LEGGINGS)));
    public static final ItemEntry<Item> OMEN_BOOTS = registerItemNoModel("omen_boots",
            prop -> new Item(prop.humanoidArmor(PArmorMaterials.OMEN_ARMOR_MATERIAL, ArmorType.BOOTS)));

    public static final ItemEntry<SmithingTemplateItem> OMEN_UPGRADE_SMITHING_TEMPLATE = registerItem("omen_upgrade_smithing_template",
            OmenSmithingTemplateItem::createOmenUpgradeTemplate);

    public static final ItemEntry<FlyCatcherItem> FLY_CATCHER = registerItemNoModel("fly_catcher",
            props -> new FlyCatcherItem(props.stacksTo(1).durability(88)));
    public static final ItemEntry<JinKeLaItem> JINKELA = registerItem("jinkela", JinKeLaItem::new);
    public static final ItemEntry<ReturnTotemItem> RETURN_TOTEM = registerItem("return_totem",
            props -> new ReturnTotemItem(props.stacksTo(8)));
    public static final ItemEntry<Item> TOTEM_OF_UNPOOPING = registerItem("totem_of_unpooping",
            props -> new Item(props.stacksTo(8)));
    public static final ItemEntry<TokenItem> TOKEN = registerItem("token",
            props -> new TokenItem(props.stacksTo(88)));
    public static final ItemEntry<GashaponItem> GASHAPON = registerItemNoModel("gashapon",
            props -> new GashaponItem(props.stacksTo(16)));
    public static final ItemEntry<TimeBellItem> TIME_BELL = registerItem("time_bell",
            props -> new TimeBellItem(props.stacksTo(1)));
    public static final ItemEntry<Item> SPALL = registerItem("spall", Item::new);
    public static final ItemEntry<Item> SPALL_SWORD = registerHandheldItem("spall_sword",
            prop -> new Item(prop.sword(ToolMaterial.STONE, 3, -2.4F)));

    public static final ItemEntry<Item> SPALL_SHOVEL = registerHandheldItem("spall_shovel",
            prop -> new Item(prop.shovel(ToolMaterial.STONE, 1.5F, -3.0F)));
    public static final ItemEntry<Item> SPALL_PICKAXE = registerHandheldItem("spall_pickaxe",
            prop -> new Item(prop.pickaxe(ToolMaterial.STONE, 1.0F, -2.8F)));
    public static final ItemEntry<Item> SPALL_AXE = registerHandheldItem("spall_axe",
            prop -> new Item(prop.axe(ToolMaterial.STONE, 6.0F, -3.2F)));
    public static final ItemEntry<Item> SPALL_HOE = registerHandheldItem("spall_hoe",
            prop -> new Item(prop.hoe(ToolMaterial.STONE, -1.0F, -2.0F)));

    public static final ItemEntry<Item> SALTPETER_SHARD = REGISTRATE.item("saltpeter_shard", Item::new)
            .lang("Saltpeter")
            .register();
    public static final ItemEntry<Item> UREA = registerItem("urea", Item::new);

    public static final ItemEntry<UrineBottleItem> URINE_BOTTLE = registerItem("urine_bottle",
            prop -> new UrineBottleItem(PFoods.apply(prop
                    .craftRemainder(Items.GLASS_BOTTLE), PFoods.URINE_BOTTLE)
                    .stacksTo(18)
            ));
    public static final ItemEntry<BucketItem> URINE_BUCKET = PoFluids.URINE_BUCKET;
    public static final ItemEntry<Item> LAWRENCE_MUSIC_DISC = registerMusicDisc("music_disc_lawrence", PoSoundEvents.LAWRENCE_KEY);
    public static final ItemEntry<Item> LIGHT_DANCE_MUSIC_DISC = registerMusicDisc("music_disc_light_dance", PoSoundEvents.LIGHT_DANCE_KEY);
    public static final ItemEntry<Item> MOON_BOWL_MUSIC_DISC = registerMusicDisc("music_disc_moon_bowl", PoSoundEvents.MOON_BOWL_KEY);
    public static final ItemEntry<Item> THEME_MUSIC_DISC = registerMusicDisc("music_disc_theme", PoSoundEvents.THEME_KEY);
    public static final ItemEntry<GameDiscItem> GAME_DISC_ROUNDWORM = registerItem("game_disc_roundworm",
            props -> new GameDiscItem(props.rarity(Rarity.RARE), Component.translatable("gamediscs.roundworm").withStyle(ChatFormatting.YELLOW)));
    public static final ItemEntry<GameDiscItem> GAME_DISC_BLOCKTRIS = registerItem("game_disc_blocktris",
            props -> new GameDiscItem(props.rarity(Rarity.RARE), Component.translatable("gamediscs.blocktris").withStyle(ChatFormatting.YELLOW)));
    public static final ItemEntry<GameDiscItem> GAME_DISC_PONG = registerItem("game_disc_pong",
            props -> new GameDiscItem(props.rarity(Rarity.RARE), Component.translatable("gamediscs.pong").withStyle(ChatFormatting.GRAY)));
    public static final ItemEntry<GinkgoBoatItem> GINKGO_BOAT = registerItem("ginkgo_boat",
            props -> new GinkgoBoatItem(PoEntityType.GINKGO_BOAT.get(), props.stacksTo(1)));
    public static final ItemEntry<GinkgoBoatItem> GINKGO_CHEST_BOAT = REGISTRATE
            .item("ginkgo_chest_boat", props -> new GinkgoBoatItem(PoEntityType.GINKGO_CHEST_BOAT.get(), props.stacksTo(1)))
            .lang("Ginkgo Boat with Chest")
            .register();

    public static final ItemEntry<FlushToiletCartItem> FLUSH_TOILET_CART = registerItem("flush_toilet_cart",
            props -> new FlushToiletCartItem(PoEntityType.FLUSH_TOILET_CART, props.stacksTo(1)));
    public static final ItemEntry<FlushToiletCartItem> GOLDEN_FLUSH_TOILET_CART = registerItem("golden_flush_toilet_cart",
            props -> new FlushToiletCartItem(PoEntityType.GOLDEN_FLUSH_TOILET_CART, props.stacksTo(1)));
    public static final ItemEntry<SpawnEggItem> POOLIME_SPAWN_EGG = registerItemNoModel("poolime_spawn_egg",
            prop -> new SpawnEggItem(prop.spawnEgg(PoEntityType.POOLIME.get())));
    public static final ItemEntry<SpawnEggItem> FLY_SPAWN_EGG = registerItemNoModel("fly_spawn_egg",
            prop -> new SpawnEggItem(prop.spawnEgg(PoEntityType.FLY.get())));

    public static final ItemEntry<FeedableBlockItem> MAGGOTS_SEEDS = registerItem("maggots_seeds",
            props -> new FeedableBlockItem(PoBlocks.MAGGOTS.get(), PFoods.apply(props, PFoods.MAGGOTS_SEEDS).stacksTo(88)));
    public static final ItemEntry<FeedableBlockItem> ROUNDWORM = registerItem("roundworm",
            props -> new FeedableBlockItem(PoBlocks.ROUNDWORM_VINES.get(), PFoods.apply(props, PFoods.ROUNDWORM).stacksTo(88)));

    public static final ItemEntry<SimpleFeedableItem> FASTING_PILL = registerFood("fasting_pill", PFoods.FASTING_PILL);

    public static final ItemEntry<FlyItem> FLY = registerItemNoModel("fly",
            props -> new FlyItem(props.stacksTo(88)));

    public static List<Item> getAllItems() {
        var registrateItems = REGISTRATE.getAll(Registries.ITEM).stream()
                .map(DeferredHolder::get)
                .toList();
        ArrayList<Item> ordered = new ArrayList<>(registrateItems);
        List<String> migratedOrder = List.of(
                "maggots_block", "roundworm_block", "poop_log", "poop_wood", "poop_empty_log",
                "stripped_poop_log", "stripped_poop_wood", "stripped_poop_empty_log", "ginkgo_log",
                "ginkgo_wood", "stripped_ginkgo_log", "stripped_ginkgo_wood", "ginkgo_planks",
                "ginkgo_stairs", "ginkgo_slab", "ginkgo_vertical_slab", "ginkgo_button",
                "ginkgo_pressure_plate", "ginkgo_fence", "ginkgo_fence_gate", "ginkgo_door",
                "ginkgo_trapdoor", "primo_stem", "primo_hyphae", "stripped_primo_stem",
                "stripped_primo_hyphae", "primo_planks", "primo_stairs", "primo_slab",
                "primo_vertical_slab", "primo_button", "primo_pressure_plate", "primo_fence",
                "primo_fence_gate", "primo_door", "primo_trapdoor", "poop_leaves", "poop_leaves_iron",
                "poop_leaves_gold", "ginkgo_leaves", "primo_cap", "glow_primo_cap", "poop_sapling",
                "ginkgo_sapling", "primo_fungus", "glow_primo_fungus", "saltpeter_block",
                "saltpeter_cluster", "large_saltpeter_bud", "medium_saltpeter_bud", "small_saltpeter_bud",
                "red_arcade", "blue_arcade", "gacha_machine", "wooden_toilet", "hard_toilet",
                "flush_toilet", "golden_flush_toilet", "ginkgo_toilet", "portable_toilet", "urine_liquid"
        );
        Map<String, Integer> ranks = IntStream.range(0, migratedOrder.size()).boxed()
                .collect(java.util.stream.Collectors.toMap(migratedOrder::get, index -> index));
        List<Item> migrated = ordered.stream()
                .filter(item -> ranks.containsKey(BuiltInRegistries.ITEM.getKey(item).getPath()))
                .sorted(java.util.Comparator.comparingInt(item -> ranks.get(BuiltInRegistries.ITEM.getKey(item).getPath())))
                .toList();
        int migratedIndex = 0;
        for (int index = 0; index < ordered.size(); index++) {
            if (ranks.containsKey(BuiltInRegistries.ITEM.getKey(ordered.get(index)).getPath())) {
                ordered.set(index, migrated.get(migratedIndex++));
            }
        }
        return ordered;
    }

    private static <T extends Item> ItemEntry<T> registerItem(String name, NonNullFunction<Item.Properties, T> factory) {
        return REGISTRATE.item(name, factory).register();
    }

    private static ItemEntry<SimpleFeedableItem> registerFood(String name, FoodProperties food) {
        return registerItem(name, props -> new SimpleFeedableItem(PFoods.apply(props, food).stacksTo(88)));
    }

    private static ItemEntry<Item> registerMusicDisc(String name, ResourceKey<JukeboxSong> song) {
        return registerItem(name,
                props -> new Item(props.jukeboxPlayable(song)
                        .rarity(Rarity.RARE)
                        .stacksTo(1)));
    }

    private static <T extends Item> ItemEntry<T> registerHandheldItem(String name, NonNullFunction<Item.Properties, T> factory) {
        return REGISTRATE.item(name, factory)
                .model(() -> (ctx, prov) -> prov.generateFlatItem(ctx.get(), ModelTemplates.FLAT_HANDHELD_ITEM, prov.modItemTexture(ctx.getName())))
                .register();
    }

    private static <T extends Item> ItemEntry<T> registerItemNoModel(String name, NonNullFunction<Item.Properties, T> factory) {
        return REGISTRATE.item(name, factory).model(() -> (ctx, prov) -> {
        }).register();
    }

    public static void register() {
    }
}
