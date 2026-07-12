package com.altnoir.poopsky;

import com.altnoir.poopsky.content.FlyTypeManager;
import com.altnoir.poopsky.content.block.ToiletType;
import com.altnoir.poopsky.content.item.p.FlyItem;
import com.altnoir.poopsky.content.item.p.ToiletBlockItem;
import com.altnoir.poopsky.impl.registrate.PoRegistrate;
import com.altnoir.poopsky.init.PoBlocks;
import com.altnoir.poopsky.init.PoItems;
import com.tterrag.registrate.util.entry.RegistryEntry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;

import java.util.Set;

public class PoItemGroups {
    private static final PoRegistrate REGISTRATE = PoopSky.registrate();

    public static final RegistryEntry<CreativeModeTab, CreativeModeTab> POOPSKY_TAB = REGISTRATE.generic("poopsky_tab", Registries.CREATIVE_MODE_TAB, () -> CreativeModeTab.builder()
            .title(Component.translatable("itemgroup.poopsky"))
            .icon(PoBlocks.WOODEN_TOILET::asStack)
            .displayItems((parameters, output) -> {
                PoItems.getAllItems().stream()
                        .filter(item -> !(item instanceof BlockItem))
                        .filter(item -> !(item instanceof FlyItem))
                        .forEach(output::accept);

                Set<Block> skip = Set.of(
                        PoBlocks.URINE_LIQUID.get(),
                        PoBlocks.WATER_COMPOOPER.get(),
                        PoBlocks.LAVA_COMPOOPER.get(),
                        PoBlocks.POWDER_SNOW_COMPOOPER.get(),
                        PoBlocks.URINE_COMPOOPER.get(),
                        PoBlocks.ROUNDWORM_VINES_PLANT.get(),
                        PoBlocks.WOODEN_TOILET.get(),
                        PoBlocks.HARD_TOILET.get()
                );
                PoItems.getAllItems().stream()
                        .filter(item -> item instanceof BlockItem)
                        .map(Item::getDefaultInstance)
                        .filter(stack -> stack.getItem() != Items.AIR)
                        .filter(stack -> !skip.contains(((BlockItem) stack.getItem()).getBlock()))
                        .forEach(output::accept);

                for (String id : FlyTypeManager.INSTANCE.getFlyTypes()) {
                    output.accept(FlyItem.withType(id));
                }

                for (var type : ToiletType.getByCategory(ToiletType.Category.WOOD).values()) {
                    output.accept(ToiletBlockItem.withType(PoBlocks.WOODEN_TOILET.get(), type));
                }
                for (var type : ToiletType.getByCategory(ToiletType.Category.HARD).values()) {
                    output.accept(ToiletBlockItem.withType(PoBlocks.HARD_TOILET.get(), type));
                }
            })
            .build()).register();

    public static void register() {
    }
}
