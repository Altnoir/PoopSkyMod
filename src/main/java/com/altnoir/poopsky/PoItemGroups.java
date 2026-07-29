package com.altnoir.poopsky;

import com.altnoir.poopsky.compat.PoMods;
import com.altnoir.poopsky.content.FlyTypeManager;
import com.altnoir.poopsky.content.ToiletType;
import com.altnoir.poopsky.content.item.p.FlyItem;
import com.altnoir.poopsky.content.item.p.ToiletBlockItem;
import com.altnoir.poopsky.impl.registrate.PoRegistrate;
import com.altnoir.poopsky.init.FlyTypes;
import com.altnoir.poopsky.init.PoBlocks;
import com.altnoir.poopsky.init.PoItems;
import com.altnoir.poopsky.init.ToiletTypes;
import com.tterrag.registrate.util.entry.RegistryEntry;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.*;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.level.block.Block;

import java.util.Set;

public class PoItemGroups {
    private static final PoRegistrate REGISTRATE = PoopSky.registrate();

    public static final RegistryEntry<CreativeModeTab, CreativeModeTab> POOPSKY_TAB = REGISTRATE.generic("poopsky", Registries.CREATIVE_MODE_TAB, () -> CreativeModeTab.builder()
            .title(Component.translatable("itemgroup.poopsky"))
            .icon(PoBlocks.WOODEN_TOILET::asStack)
            .displayItems((parameters, output) -> {
                PoItems.getAllItems().stream()
                        .filter(item -> !PoBlocks.isDecorativeItem(item))
                        .filter(item -> !(item instanceof BlockItem && !(item instanceof ItemNameBlockItem)))
                        .filter(item -> !(item instanceof FlyItem))
                        .forEach(output::accept);

                Set<Block> skip = Set.of(PoBlocks.URINE_LIQUID.get(), PoBlocks.WATER_COMPOOPER.get(), PoBlocks.LAVA_COMPOOPER.get(),
                        PoBlocks.POWDER_SNOW_COMPOOPER.get(), PoBlocks.URINE_COMPOOPER.get(), PoBlocks.ROUNDWORM_VINES_PLANT.get(),
                        PoBlocks.WOODEN_TOILET.get(), PoBlocks.HARD_TOILET.get());
                PoItems.getAllItems().stream()
                        .filter(item -> item instanceof BlockItem)
                        .map(Item::getDefaultInstance)
                        .filter(stack -> stack.getItem() != Items.AIR)
                        .filter(stack -> !skip.contains(((BlockItem) stack.getItem()).getBlock()))
                        .filter(stack -> !PoBlocks.isDecorativeItem(stack.getItem()))
                        .forEach(output::accept);

                for (String id : FlyTypeManager.INSTANCE.getFlyTypes()) {
                    if (createFly(id) && !PoMods.CREATE.isLoaded()) continue;
                    if (ae2Fly(id) && !PoMods.AE2.isLoaded()) continue;
                    if (mekFly(id) && !PoMods.MEKANISM.isLoaded()) continue;
                    output.accept(FlyItem.withType(id));
                }
                output.accept(ToiletBlockItem.withType(PoBlocks.WOODEN_TOILET.get(), ToiletTypes.OAK));
                output.accept(ToiletBlockItem.withType(PoBlocks.HARD_TOILET.get(), ToiletTypes.WHITE_TILE));
                output.accept(ToiletBlockItem.withType(PoBlocks.HARD_TOILET.get(), ToiletTypes.GOLD));
                output.accept(ToiletBlockItem.withType(PoBlocks.HARD_TOILET.get(), ToiletTypes.REDSTONE));
                output.accept(ToiletBlockItem.withType(PoBlocks.HARD_TOILET.get(), ToiletTypes.OBSIDIAN));

                parameters.holders()
                        .lookup(Registries.POTION)
                        .ifPresent(
                                potion -> {
                                    potionEffectTypes(
                                            output, potion, Items.TIPPED_ARROW, CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS, parameters.enabledFeatures()
                                    );
                                    potionEffectTypes(
                                            output, potion, Items.POTION, CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS, parameters.enabledFeatures()
                                    );
                                    potionEffectTypes(
                                            output, potion, Items.SPLASH_POTION, CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS, parameters.enabledFeatures()
                                    );
                                    potionEffectTypes(
                                            output, potion, Items.LINGERING_POTION, CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS, parameters.enabledFeatures()
                                    );
                                }
                        );
            }).build()).register();

    public static final RegistryEntry<CreativeModeTab, CreativeModeTab> POOPSKY_DECORATIVE = REGISTRATE.generic("poopsky_deco", Registries.CREATIVE_MODE_TAB, () -> CreativeModeTab.builder()
            .title(Component.translatable("itemgroup.poopsky_deco"))
            .icon(PoBlocks.BROWN_TILE_BLOCK::asStack)
            .displayItems((parameters, output) -> {
                PoItems.getAllItems().stream()
                        .filter(item -> PoBlocks.isDecorativeItem(item) || PoBlocks.isAllTabItem(item))
                        .forEach(output::accept);

                for (var type : ToiletType.getByCategory(ToiletType.Category.WOOD).values()) {
                    output.accept(ToiletBlockItem.withType(PoBlocks.WOODEN_TOILET.get(), type));
                }
                for (var type : ToiletType.getByCategory(ToiletType.Category.HARD).values()) {
                    output.accept(ToiletBlockItem.withType(PoBlocks.HARD_TOILET.get(), type));
                }
            }).build()).register();

    private static boolean createFly(String id) {
        return FlyTypes.ZINC.id().equals(id);
    }

    private static boolean ae2Fly(String id) {
        return FlyTypes.CERTUS.id().equals(id) || FlyTypes.SKY_DUST.id().equals(id);
    }

    private static boolean mekFly(String id) {
        return FlyTypes.OSMIUM.id().equals(id) || FlyTypes.TIN.id().equals(id) || FlyTypes.LEAD.id().equals(id) || FlyTypes.URANIUM.id().equals(id) || FlyTypes.FLUORITE.id().equals(id);
    }

    private static void potionEffectTypes(
            CreativeModeTab.Output output, HolderLookup<Potion> potions, Item item, CreativeModeTab.TabVisibility tabVisibility, FeatureFlagSet requiredFeatures
    ) {
        potions.listElements()
                .filter(reference -> reference.value().isEnabled(requiredFeatures))
                .filter(reference -> reference.key().location().getNamespace().equals(PoopSky.MOD_ID))
                .map(reference -> PotionContents.createItemStack(item, reference))
                .forEach(itemStack -> output.accept(itemStack, tabVisibility));
    }

    public static void register() {
    }
}
