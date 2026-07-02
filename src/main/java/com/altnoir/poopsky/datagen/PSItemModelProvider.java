package com.altnoir.poopsky.datagen;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.common.FlyType;
import com.altnoir.poopsky.init.PBlocks;
import com.altnoir.poopsky.init.PItems;
import com.altnoir.poopsky.common.item.PFlyTypes;
import net.minecraft.client.renderer.block.model.BlockModel.GuiLight;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.armortrim.TrimMaterial;
import net.minecraft.world.item.armortrim.TrimMaterials;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.client.model.generators.loaders.SeparateTransformsModelBuilder;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;

import java.util.LinkedHashMap;

public class PSItemModelProvider extends ItemModelProvider {
    private static final LinkedHashMap<ResourceKey<TrimMaterial>, Float> trimMaterials = new LinkedHashMap<>();

    static {
        trimMaterials.put(TrimMaterials.QUARTZ, 0.1F);
        trimMaterials.put(TrimMaterials.IRON, 0.2F);
        trimMaterials.put(TrimMaterials.NETHERITE, 0.3F);
        trimMaterials.put(TrimMaterials.REDSTONE, 0.4F);
        trimMaterials.put(TrimMaterials.COPPER, 0.5F);
        trimMaterials.put(TrimMaterials.GOLD, 0.6F);
        trimMaterials.put(TrimMaterials.EMERALD, 0.7F);
        trimMaterials.put(TrimMaterials.DIAMOND, 0.8F);
        trimMaterials.put(TrimMaterials.LAPIS, 0.9F);
        trimMaterials.put(TrimMaterials.AMETHYST, 1.0F);
    }

    public PSItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, PoopSky.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        basicItem(PItems.POOP.get());
        basicItem(PItems.CHILI_POOP.get());
        basicItem(PItems.GOLDEN_POOP.get());
        basicItem(PItems.SEEDBED_CURSE.get());
        basicItem(PItems.FOLIUM_SENNAE.get());
        basicItem(PItems.POOP_BALL.get());
        basicItem(PItems.SAPLING_POOP_BALL.get());
        basicItem(PItems.SEA_POOP_BALL.get());
        basicItem(PItems.WITHER_POOP_BALL.get());
        basicItem(PItems.POOP_BREAD.get());
        basicItem(PItems.POOP_DUMPLINGS.get());
        basicItem(PItems.POOP_MOONCAKE.get());
        basicItem(PItems.CHILI_POOP_MOONCAKE.get());
        basicItem(PItems.GOLDEN_POOP_MOONCAKE.get());
        basicItem(PItems.POOP_SOUP.get());
        basicItem(PItems.POOP_VEGETABLE_STICKS.get());
        basicItem(PItems.POOBURGER_MEAT.get());
        basicItem(PItems.POOBURGER.get());
        basicItem(PItems.POOP_PASTA.get());
        basicItem(PItems.POODDING.get());
        basicItem(PItems.DRAGON_BREATH_CHILI.get());
        basicItem(PItems.KING_OF_DRAGON_FRUIT.get());
        toiletPlugItem();
        basicItem(PItems.SPALL.get());
        basicItem(PItems.LAWRENCE_MUSIC_DISC.get());
        basicItem(PItems.LIGHT_DANCE_MUSIC_DISC.get());
        basicItem(PItems.MOON_BOWL_MUSIC_DISC.get());
        basicItem(PItems.TOILET_PLUG_WAND.get());
        basicItem(PItems.URINE_BOTTLE.get());
        basicItem(PItems.URINE_BUCKET.get());
        basicItem(PItems.MAGGOTS_SEEDS.get());
        basicItem(PItems.ROUNDWORM.get());
        basicItem(PItems.BAKED_MAGGOTS.get());

        basicItem(PItems.FLY_CATCHER.get());
        basicItem(PItems.TIME_BELL.get());

        wallItem(PBlocks.CHILI_POOP_WALL, PBlocks.CHILI_POOP_BLOCK);
        wallItem(PBlocks.GOLDEN_POOP_WALL, PBlocks.GOLDEN_POOP_BLOCK);
        wallItem(PBlocks.POOP_BRICK_WALL, PBlocks.POOP_BRICKS);
        wallItem(PBlocks.MOSSY_POOP_BRICK_WALL, PBlocks.MOSSY_POOP_BRICKS);
        wallItem(PBlocks.DRIED_POOP_BLOCK_WALL, PBlocks.DRIED_POOP_BLOCK);
        wallItem(PBlocks.SMOOTH_POOP_BLOCK_WALL, PBlocks.SMOOTH_POOP_BLOCK);
        wallItem(PBlocks.CUT_POOP_BLOCK_WALL, PBlocks.CUT_POOP_BLOCK);
        wallItem(PBlocks.TILE_BLOCK_WALL, PBlocks.TILE_BLOCK);

        // 添加液体桶的模型
        //withExistingParent("urine_bucket", mcLoc("item/generated")).texture("layer0", modLoc("item/urine_bucket"));

        withExistingParent(PItems.POOLIME_SPAWN_EGG.getId().getPath(), mcLoc("item/template_spawn_egg"));
        withExistingParent(PItems.FLY_SPAWN_EGG.getId().getPath(), mcLoc("item/template_spawn_egg"));

        basicItem(PItems.OMINOUS_FILTHY_INGOT.get());
        basicItem(PItems.OMEN_UPGRADE_SMITHING_TEMPLATE.get());
        bigSowordItem(PItems.MILOS_SWORD.get());
        trimmedArmorItem(PItems.OMEN_HELMET);
        trimmedArmorItem(PItems.OMEN_CHESTPLATE);
        trimmedArmorItem(PItems.OMEN_LEGGINGS);
        trimmedArmorItem(PItems.OMEN_BOOTS);

        flyItemWithOverrides();
        blockItemModel(PBlocks.FLY_NEST);
        blockItemModel(PBlocks.BREEDING_BOX);
    }

    private void blockItemModel(DeferredBlock<?> block) {
        withExistingParent(block.getId().getPath(), modLoc("block/" + block.getId().getPath()));
    }

    private void toiletPlugItem() {
        var baseModel = nested()
                .parent(new ModelFile.UncheckedModelFile("builtin/entity"));

        var guiModel = nested()
                .parent(new ModelFile.UncheckedModelFile("item/generated"))
                .texture("layer0", modLoc("item/toilet_plug"));

        getBuilder("toilet_plug")
                .guiLight(GuiLight.FRONT)
                .customLoader(SeparateTransformsModelBuilder::begin)
                .base(baseModel)
                .perspective(ItemDisplayContext.GUI, guiModel)
                .perspective(ItemDisplayContext.GROUND, guiModel)
                .perspective(ItemDisplayContext.FIXED, guiModel)
                .end();
    }

    private void bigSowordItem(Item item) {
        this.withExistingParent(getItemPath(item), modLoc("item/big_sword"))
                .texture("layer0", PoopSky.loc("item/" + getItemPath(item)));
    }

    private void wallItem(DeferredBlock<?> block, DeferredBlock<?> baseBlock) {
        this.withExistingParent(block.getId().getPath(), mcLoc("block/wall_inventory"))
                .texture("wall", PoopSky.loc("block/" + baseBlock.getId().getPath()));
    }

    private void flyItemWithOverrides() {
        var flyBuilder = getBuilder("fly")
                .parent(new ModelFile.UncheckedModelFile("item/generated"))
                .texture("layer0", PoopSky.loc("item/fly"));

        int index = 0;
        int total = FlyType.FLY_TYPES.size();
        for (String id : FlyType.FLY_TYPES) {
            String flyId = id.equals(PFlyTypes.NORMAL.id()) ? "fly" : "fly_" + id;
            getBuilder(flyId)
                    .parent(new ModelFile.UncheckedModelFile("item/generated"))
                    .texture("layer0", PoopSky.loc("item/" + flyId));
            flyBuilder.override()
                    .predicate(PoopSky.loc("fly_type"), (float) index / total)
                    .model(new ModelFile.UncheckedModelFile(PoopSky.MOD_ID + ":item/" + flyId))
                    .end();
            index++;
        }
    }

    private void trimmedArmorItem(DeferredItem<ArmorItem> itemDeferredItem) {
        ArmorItem armorItem = itemDeferredItem.get();
        trimMaterials.forEach((trimMaterial, value) -> {
            float trimValue = value;

            String armorType = switch (armorItem.getEquipmentSlot()) {
                case HEAD -> "helmet";
                case CHEST -> "chestplate";
                case LEGS -> "leggings";
                case FEET -> "boots";
                default -> "";
            };

            String armorItemPath = armorItem.toString();
            String trimPath = "trims/items/" + armorType + "_trim_" + trimMaterial.location().getPath();
            String currentTrimName = armorItemPath + "_" + trimMaterial.location().getPath() + "_trim";
            ResourceLocation armorItemResLoc = ResourceLocation.parse(armorItemPath);
            ResourceLocation trimResLoc = ResourceLocation.parse(trimPath); // minecraft namespace
            ResourceLocation trimNameResLoc = ResourceLocation.parse(currentTrimName);

            existingFileHelper.trackGenerated(trimResLoc, PackType.CLIENT_RESOURCES, ".png", "textures");

            getBuilder(currentTrimName)
                    .parent(new ModelFile.UncheckedModelFile("item/generated"))
                    .texture("layer0", armorItemResLoc.getNamespace() + ":item/" + armorItemResLoc.getPath())
                    .texture("layer1", trimResLoc);

            this.withExistingParent(itemDeferredItem.getId().getPath(),
                            mcLoc("item/generated"))
                    .override()
                    .model(new ModelFile.UncheckedModelFile(trimNameResLoc.getNamespace() + ":item/" + trimNameResLoc.getPath()))
                    .predicate(mcLoc("trim_type"), trimValue).end()
                    .texture("layer0",
                            PoopSky.loc("item/" + itemDeferredItem.getId().getPath()));
        });
    }

    private String getItemPath(Item item) {
        return getItemKey(item).getPath();
    }

    private ResourceLocation getItemKey(Item item) {
        return BuiltInRegistries.ITEM.getKey(item);
    }
}