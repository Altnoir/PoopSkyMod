package com.altnoir.poopsky.init;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.common.block.ToiletComponent;
import com.altnoir.poopsky.common.item.PArmorMaterials;
import com.altnoir.poopsky.common.item.PFoods;
import com.altnoir.poopsky.common.item.PToolTiers;
import com.altnoir.poopsky.common.item.p.*;
import com.tterrag.registrate.Registrate;
import com.tterrag.registrate.builders.ItemBuilder;
import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.ProviderType;
import com.tterrag.registrate.providers.RegistrateItemModelProvider;
import com.tterrag.registrate.util.entry.ItemEntry;
import com.tterrag.registrate.util.nullness.NonNullFunction;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.*;
import net.minecraft.world.item.armortrim.TrimMaterial;
import net.minecraft.world.item.armortrim.TrimMaterials;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.client.model.generators.loaders.SeparateTransformsModelBuilder;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class PItems {
    public static final Registrate ITEMS = PoopSky.REGISTRATE;
    private static final LinkedHashMap<ResourceKey<TrimMaterial>, Float> TRIM_MATERIALS = new LinkedHashMap<>();

    static {
        TRIM_MATERIALS.put(TrimMaterials.QUARTZ, 0.1F);
        TRIM_MATERIALS.put(TrimMaterials.IRON, 0.2F);
        TRIM_MATERIALS.put(TrimMaterials.NETHERITE, 0.3F);
        TRIM_MATERIALS.put(TrimMaterials.REDSTONE, 0.4F);
        TRIM_MATERIALS.put(TrimMaterials.COPPER, 0.5F);
        TRIM_MATERIALS.put(TrimMaterials.GOLD, 0.6F);
        TRIM_MATERIALS.put(TrimMaterials.EMERALD, 0.7F);
        TRIM_MATERIALS.put(TrimMaterials.DIAMOND, 0.8F);
        TRIM_MATERIALS.put(TrimMaterials.LAPIS, 0.9F);
        TRIM_MATERIALS.put(TrimMaterials.AMETHYST, 1.0F);
    }

    public static <T extends Item> ItemBuilder<T, Registrate> itemNoLang(String name, NonNullFunction<Item.Properties, T> factory) {
        return ITEMS.item(name, factory)
                .setData(ProviderType.LANG, (ctx, prov) -> {
                });
    }

    public static final ItemEntry<PoopItem> POOP = itemNoLang("poop",
            props -> new PoopItem(props.food(PFoods.POOP).stacksTo(88))).register();
    public static final ItemEntry<ChiliPoopItem> CHILI_POOP = itemNoLang("chili_poop",
            props -> new ChiliPoopItem(props.food(PFoods.POOP).stacksTo(88))).register();
    public static final ItemEntry<PoopItem> GOLDEN_POOP = itemNoLang("golden_poop",
            props -> new PoopItem(props.food(PFoods.GOLDEN_POOP).stacksTo(88))).register();
    public static final ItemEntry<Item> SEEDBED_CURSE = itemNoLang("seedbed_curse",
            props -> new Item(props.stacksTo(88))).register();
    public static final ItemEntry<Item> FOLIUM_SENNAE = itemNoLang("folium_sennae", Item::new).register();

    public static final ItemEntry<PoopBallItem> POOP_BALL = itemNoLang("poop_ball",
            props -> new PoopBallItem(props.stacksTo(88))).register();
    public static final ItemEntry<SaplingBallItem> SAPLING_POOP_BALL = itemNoLang("sapling_poop_ball",
            props -> new SaplingBallItem(props.food(PFoods.SAPLING_BALL).stacksTo(88))).register();
    public static final ItemEntry<SeaPoopBallItem> SEA_POOP_BALL = itemNoLang("sea_poop_ball",
            props -> new SeaPoopBallItem(props.stacksTo(88))).register();
    public static final ItemEntry<WitherPoopBallItem> WITHER_POOP_BALL = itemNoLang("wither_poop_ball",
            props -> new WitherPoopBallItem(props.stacksTo(88))).register();

    public static final ItemEntry<SimpleFeedableItem> POOP_MOONCAKE = itemNoLang("poop_mooncake",
            props -> new SimpleFeedableItem(props.food(PFoods.POOP_MOONCAKE).stacksTo(88))).register();
    public static final ItemEntry<SimpleFeedableItem> CHILI_POOP_MOONCAKE = itemNoLang("chili_poop_mooncake",
            props -> new SimpleFeedableItem(props.food(PFoods.CHILI_POOP_MOONCAKE).stacksTo(88))).register();
    public static final ItemEntry<SimpleFeedableItem> GOLDEN_POOP_MOONCAKE = itemNoLang("golden_poop_mooncake",
            props -> new SimpleFeedableItem(props.food(PFoods.GOLDEN_POOP_MOONCAKE).stacksTo(88))).register();
    public static final ItemEntry<SimpleFeedableItem> BAKED_MAGGOTS = itemNoLang("baked_maggots",
            props -> new SimpleFeedableItem(props.food(PFoods.BAKED_MAGGOTS).stacksTo(88))).register();
    public static final ItemEntry<SimpleFeedableItem> POOP_BREAD = itemNoLang("poop_bread",
            props -> new SimpleFeedableItem(props.food(PFoods.POOP_BREAD).stacksTo(88))).register();
    public static final ItemEntry<SimpleFeedableItem> POOP_DUMPLINGS = itemNoLang("poop_dumplings",
            props -> new SimpleFeedableItem(props.food(PFoods.POOP_DUMPLINGS).stacksTo(88))).register();
    public static final ItemEntry<SimpleFeedableItem> POOP_SOUP = itemNoLang("poop_soup",
            props -> new SimpleFeedableItem(props.food(PFoods.POOP_SOUP).stacksTo(88))).register();
    public static final ItemEntry<SimpleFeedableItem> POOP_VEGETABLE_STICKS = itemNoLang("poop_vegetable_sticks",
            props -> new SimpleFeedableItem(props.food(PFoods.POOP_VEGETABLE_STICKS).stacksTo(88))).register();
    public static final ItemEntry<SimpleFeedableItem> POOBURGER_MEAT = itemNoLang("pooburger_meat",
            props -> new SimpleFeedableItem(props.food(PFoods.POOBURGER_MEAT).stacksTo(88))).register();
    public static final ItemEntry<SimpleFeedableItem> POOBURGER = itemNoLang("pooburger",
            props -> new SimpleFeedableItem(props.food(PFoods.POOBURGER).stacksTo(88))).register();
    public static final ItemEntry<SimpleFeedableItem> POOP_PASTA = itemNoLang("poop_pasta",
            props -> new SimpleFeedableItem(props.food(PFoods.POOP_PASTA).stacksTo(88))).register();
    public static final ItemEntry<SimpleFeedableItem> POODDING = itemNoLang("poodding",
            props -> new SimpleFeedableItem(props.food(PFoods.POODDING).stacksTo(88))).register();

    public static final ItemEntry<ChiliItem> DRAGON_BREATH_CHILI = itemNoLang("dragon_breath_chili",
            props -> new ChiliItem(props.food(PFoods.DRAGON_BREATH_CHILI))).register();
    public static final ItemEntry<DragonFruitRItem> KING_OF_DRAGON_FRUIT = itemNoLang("king_of_dragon_fruit",
            props -> new DragonFruitRItem(props.food(PFoods.KING_OF_DRAGON_FRUIT))).register();

    public static final ItemEntry<ToiletPlugItem> TOILET_PLUG = itemNoLang("toilet_plug",
            props -> new ToiletPlugItem(props.attributes(ToiletPlugItem.createWeaponAttributes())
                    .stacksTo(1)))
            .model(PItems::toiletPlugItemModel)
            .register();
    public static final ItemEntry<ToiletLinkerItem> TOILET_PLUG_WAND = itemNoLang("toilet_plug_wand",
            props -> new ToiletLinkerItem(props.attributes(ToiletPlugItem.createWeaponAttributes())
                    .component(PComponents.TOILET_COMPONENT, ToiletComponent.EMPTY)
                    .stacksTo(1))).register();
    public static final ItemEntry<FeedableBlockItem> MAGGOTS_SEEDS = itemNoLang("maggots_seeds",
            props -> new FeedableBlockItem(PBlocks.MAGGOTS.get(), new Item.Properties().food(PFoods.MAGGOTS_SEEDS).stacksTo(88))).register();
    public static final ItemEntry<FeedableBlockItem> ROUNDWORM = itemNoLang("roundworm",
            props -> new FeedableBlockItem(PBlocks.ROUNDWORM_VINES.get(), new Item.Properties().food(PFoods.ROUNDWORM).stacksTo(88))).register();

    public static final ItemEntry<Item> OMINOUS_FILTHY_INGOT = itemNoLang("ominous_filthy_ingot", Item::new).register();
    public static final ItemEntry<MilosSwordItem> MILOS_SWORD = itemNoLang("milos_sword",
            prop -> new MilosSwordItem(PToolTiers.MILOS,
                    prop.attributes(MilosSwordItem.createAttributes(PToolTiers.MILOS, 2, 1, -3.4F))
            ))
            .model(PItems::bigSwordItemModel)
            .register();

    public static final ItemEntry<OmenArmorItem> OMEN_HELMET = itemNoLang("omen_helmet",
            prop -> new OmenArmorItem(PArmorMaterials.OMEN_ARMOR_MATERIAL, ArmorItem.Type.HELMET,
                    prop.durability(ArmorItem.Type.HELMET.getDurability(24))))
            .model(PItems::trimmedArmorItemModel)
            .register();
    public static final ItemEntry<ArmorItem> OMEN_CHESTPLATE = itemNoLang("omen_chestplate",
            prop -> new ArmorItem(PArmorMaterials.OMEN_ARMOR_MATERIAL, ArmorItem.Type.CHESTPLATE,
                    new Item.Properties().durability(ArmorItem.Type.HELMET.getDurability(24))))
            .model(PItems::trimmedArmorItemModel)
            .register();
    public static final ItemEntry<ArmorItem> OMEN_LEGGINGS = itemNoLang("omen_leggings",
            prop -> new ArmorItem(PArmorMaterials.OMEN_ARMOR_MATERIAL, ArmorItem.Type.LEGGINGS,
                    new Item.Properties().durability(ArmorItem.Type.HELMET.getDurability(24))))
            .model(PItems::trimmedArmorItemModel)
            .register();
    public static final ItemEntry<ArmorItem> OMEN_BOOTS = itemNoLang("omen_boots",
            prop -> new ArmorItem(PArmorMaterials.OMEN_ARMOR_MATERIAL, ArmorItem.Type.BOOTS,
                    new Item.Properties().durability(ArmorItem.Type.HELMET.getDurability(24))))
            .model(PItems::trimmedArmorItemModel)
            .register();

    public static final ItemEntry<SmithingTemplateItem> OMEN_UPGRADE_SMITHING_TEMPLATE = itemNoLang("omen_upgrade_smithing_template",
            props -> OmenSmithingTemplateItem.createOmenUpgradeTemplate()).register();

    public static final ItemEntry<FlyCatcherItem> FLY_CATCHER = itemNoLang("fly_catcher",
            props -> new FlyCatcherItem(props.stacksTo(1).durability(88))).register();
    public static final ItemEntry<TimeBellItem> TIME_BELL = itemNoLang("time_bell",
            props -> new TimeBellItem(props.stacksTo(1))).register();
    public static final ItemEntry<Item> SPALL = itemNoLang("spall", Item::new).register();
    public static final ItemEntry<UrineBottleItem> URINE_BOTTLE = itemNoLang("urine_bottle",
            prop -> new UrineBottleItem(prop
                    .craftRemainder(Items.GLASS_BOTTLE)
                    .food(PFoods.URINE_BOTTLE)
                    .stacksTo(18)
            )).register();
    public static final ItemEntry<BucketItem> URINE_BUCKET = itemNoLang("urine_bucket",
            prop -> new BucketItem(PFluids.URINE.get(), prop.stacksTo(1))).register();

    public static final ItemEntry<DeferredSpawnEggItem> POOLIME_SPAWN_EGG = itemNoLang("poolime_spawn_egg",
            prop -> new DeferredSpawnEggItem(PEntityType.POOLIME, 0x7D5F36, 0x5E4228, prop))
            .model(PItems::spawnEggItemModel)
            .register();
    public static final ItemEntry<DeferredSpawnEggItem> FLY_SPAWN_EGG = itemNoLang("fly_spawn_egg",
            prop -> new DeferredSpawnEggItem(PEntityType.FLY, 0x3B4346, 0x900D2D, prop))
            .model(PItems::spawnEggItemModel)
            .register();
    public static final ItemEntry<Item> LAWRENCE_MUSIC_DISC = itemNoLang("music_disc_lawrence",
            props -> new Item(props.jukeboxPlayable(PSoundEvents.LAWRENCE_KEY).rarity(Rarity.RARE).stacksTo(1))).register();
    public static final ItemEntry<Item> LIGHT_DANCE_MUSIC_DISC = itemNoLang("music_disc_light_dance",
            props -> new Item(props.jukeboxPlayable(PSoundEvents.LIGHT_DANCE_KEY).rarity(Rarity.RARE).stacksTo(1))).register();
    public static final ItemEntry<Item> MOON_BOWL_MUSIC_DISC = itemNoLang("music_disc_moon_bowl",
            props -> new Item(props.jukeboxPlayable(PSoundEvents.MOON_BOWL_KEY).rarity(Rarity.RARE).stacksTo(1))).register();

    public static final ItemEntry<FlyItem> FLY = itemNoLang("fly",
            props -> new FlyItem(props.stacksTo(88)))
            .model((ctx, prov) -> {
            })
            .register();

    public static List<Item> getAllItems() {
        var registrateItems = PoopSky.REGISTRATE.getAll(Registries.ITEM).stream()
                .map(DeferredHolder::get)
                .toList();

        return new ArrayList<>(registrateItems);
    }

    private static void spawnEggItemModel(DataGenContext<Item, ? extends Item> ctx, RegistrateItemModelProvider prov) {
        prov.withExistingParent(ctx.getName(), ResourceLocation.withDefaultNamespace("item/template_spawn_egg"));
    }

    private static void bigSwordItemModel(DataGenContext<Item, ? extends Item> ctx, RegistrateItemModelProvider prov) {
        prov.withExistingParent(ctx.getName(), PoopSky.loc("item/big_sword"))
                .texture("layer0", PoopSky.loc("item/" + ctx.getName()));
    }

    private static void toiletPlugItemModel(DataGenContext<Item, ? extends Item> ctx, RegistrateItemModelProvider prov) {
        var baseModel = prov.nested()
                .parent(new ModelFile.UncheckedModelFile("builtin/entity"));
        var guiModel = prov.nested()
                .parent(new ModelFile.UncheckedModelFile("item/generated"))
                .texture("layer0", PoopSky.loc("item/" + ctx.getName()));

        prov.getBuilder(ctx.getName())
                .guiLight(BlockModel.GuiLight.FRONT)
                .customLoader(SeparateTransformsModelBuilder::begin)
                .base(baseModel)
                .perspective(ItemDisplayContext.GUI, guiModel)
                .perspective(ItemDisplayContext.GROUND, guiModel)
                .perspective(ItemDisplayContext.FIXED, guiModel)
                .end();
    }

    private static void trimmedArmorItemModel(DataGenContext<Item, ? extends ArmorItem> ctx, RegistrateItemModelProvider prov) {
        ArmorItem armorItem = ctx.getEntry();
        String armorType = switch (armorItem.getEquipmentSlot()) {
            case HEAD -> "helmet";
            case CHEST -> "chestplate";
            case LEGS -> "leggings";
            case FEET -> "boots";
            default -> "";
        };

        var builder = prov.withExistingParent(ctx.getName(), ResourceLocation.withDefaultNamespace("item/generated"))
                .texture("layer0", PoopSky.loc("item/" + ctx.getName()));

        TRIM_MATERIALS.forEach((trimMaterial, trimValue) -> {
            String trimName = ctx.getName() + "_" + trimMaterial.location().getPath() + "_trim";
            builder.override()
                    .model(new ModelFile.UncheckedModelFile(PoopSky.MOD_ID + ":item/" + trimName))
                    .predicate(ResourceLocation.withDefaultNamespace("trim_type"), trimValue)
                    .end();

            prov.getBuilder(trimName)
                    .parent(new ModelFile.UncheckedModelFile("item/generated"))
                    .texture("layer0", PoopSky.loc("item/" + ctx.getName()))
                    .texture("layer1", ResourceLocation.withDefaultNamespace("trims/items/" + armorType + "_trim_" + trimMaterial.location().getPath()));
        });
    }
}
