package com.altnoir.poopsky.datagen;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.block.PSBlocks;
import com.altnoir.poopsky.item.PSItems;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.armortrim.TrimMaterial;
import net.minecraft.world.item.armortrim.TrimMaterials;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;

import java.util.LinkedHashMap;

public class PSItemModelProvider extends ItemModelProvider {
    private static LinkedHashMap<ResourceKey<TrimMaterial>, Float> trimMaterials = new LinkedHashMap<>();

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
        basicItem(PSItems.POOP.get());
        basicItem(PSItems.CHILI_POOP.get());
        basicItem(PSItems.GOLDEN_POOP.get());
        basicItem(PSItems.FOLIUM_SENNAE.get());
        basicItem(PSItems.POOP_BALL.get());
        basicItem(PSItems.SAPLING_POOP_BALL.get());
        basicItem(PSItems.SEA_POOP_BALL.get());
        basicItem(PSItems.WITHER_POOP_BALL.get());
        basicItem(PSItems.POOP_BREAD.get());
        basicItem(PSItems.POOP_DUMPLINGS.get());
        basicItem(PSItems.POOP_SOUP.get());
        basicItem(PSItems.POOP_VEGETABLE_STICKS.get());
        basicItem(PSItems.POOBURGER_MEAT.get());
        basicItem(PSItems.POOBURGER.get());
        basicItem(PSItems.POOP_PASTA.get());
        basicItem(PSItems.POODDING.get());
        basicItem(PSItems.DRAGON_BREATH_CHILI.get());
        basicItem(PSItems.KING_OF_DRAGON_FRUIT.get());
        basicItem(PSItems.TOILET_PLUG.get());
        basicItem(PSItems.SPALL.get());
        basicItem(PSItems.LAWRENCE_MUSIC_DISC.get());
        basicItem(PSItems.LIGHT_DANCE_MUSIC_DISC.get());
        basicItem(PSItems.MOON_BOWL_MUSIC_DISC.get());
        basicItem(PSItems.TOILET_PLUG_WAND.get());
        basicItem(PSItems.URINE_BOTTLE.get());
        basicItem(PSItems.URINE_BUCKET.get());
        basicItem(PSItems.MAGGOTS_SEEDS.get());
        basicItem(PSItems.ROUNDWORM.get());
        basicItem(PSItems.BAKED_MAGGOTS.get());

        basicItem(PSItems.TIME_BELL.get());

        wallItem(PSBlocks.CHILI_POOP_WALL, PSBlocks.CHILI_POOP_BLOCK);
        wallItem(PSBlocks.GOLDEN_POOP_WALL, PSBlocks.GOLDEN_POOP_BLOCK);
        wallItem(PSBlocks.POOP_BRICK_WALL, PSBlocks.POOP_BRICKS);
        wallItem(PSBlocks.MOSSY_POOP_BRICK_WALL, PSBlocks.MOSSY_POOP_BRICKS);
        wallItem(PSBlocks.DRIED_POOP_BLOCK_WALL, PSBlocks.DRIED_POOP_BLOCK);
        wallItem(PSBlocks.SMOOTH_POOP_BLOCK_WALL, PSBlocks.SMOOTH_POOP_BLOCK);
        wallItem(PSBlocks.CUT_POOP_BLOCK_WALL, PSBlocks.CUT_POOP_BLOCK);
        wallItem(PSBlocks.TILE_BLOCK_WALL, PSBlocks.TILE_BLOCK);

        // 添加液体桶的模型
        //withExistingParent("urine_bucket", mcLoc("item/generated")).texture("layer0", modLoc("item/urine_bucket"));

        withExistingParent(PSItems.POOLIME_SPAWN_EGG.getId().getPath(), mcLoc("item/template_spawn_egg"));

        handheldItem(PSItems.MILOS_SWORD.get());
        trimmedArmorItem(PSItems.OMEN_HELMET);
        trimmedArmorItem(PSItems.OMEN_CHESTPLATE);
        trimmedArmorItem(PSItems.OMEN_LEGGINGS);
        trimmedArmorItem(PSItems.OMEN_BOOTS);
    }

    private void wallItem(DeferredBlock<?> block, DeferredBlock<?> baseBlock) {
        this.withExistingParent(block.getId().getPath(), mcLoc("block/wall_inventory"))
                .texture("wall", PoopSky.loc("block/" + baseBlock.getId().getPath()));
    }

    private void trimmedArmorItem(DeferredItem<ArmorItem> itemDeferredItem) {
        if (itemDeferredItem.get() instanceof ArmorItem armorItem) {
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
    }
}