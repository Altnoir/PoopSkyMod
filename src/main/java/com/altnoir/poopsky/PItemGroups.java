package com.altnoir.poopsky;

import com.altnoir.poopsky.content.FlyTypeManager;
import com.altnoir.poopsky.content.block.ToiletType;
import com.altnoir.poopsky.content.item.p.FlyItem;
import com.altnoir.poopsky.content.item.p.ToiletBlockItem;
import com.altnoir.poopsky.impl.registrate.PoRegistrate;
import com.altnoir.poopsky.init.PBlocks;
import com.altnoir.poopsky.init.PItems;
import com.tterrag.registrate.util.entry.RegistryEntry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;

import java.util.Set;

public class PItemGroups {
    private static final PoRegistrate REGISTRATE = PoopSky.registrate();

    public static final RegistryEntry<CreativeModeTab, CreativeModeTab> POOPSKY_TAB = REGISTRATE.generic("poopsky_tab", Registries.CREATIVE_MODE_TAB, () -> CreativeModeTab.builder()
            .title(Component.translatable("itemgroup.poopsky"))
            .icon(PBlocks.WOODEN_TOILET::asStack)
            .displayItems((parameters, output) -> {
                PItems.getAllItems().stream()
                        .filter(item -> !(item instanceof BlockItem))
                        .filter(item -> !(item instanceof FlyItem))
                        .forEach(output::accept);

                Set<Block> skip = Set.of(
                        PBlocks.URINE_LIQUID.get(),
                        PBlocks.WATER_COMPOOPER.get(),
                        PBlocks.LAVA_COMPOOPER.get(),
                        PBlocks.POWDER_SNOW_COMPOOPER.get(),
                        PBlocks.URINE_COMPOOPER.get(),
                        PBlocks.ROUNDWORM_VINES_PLANT.get(),
                        PBlocks.WOODEN_TOILET.get(),
                        PBlocks.HARD_TOILET.get()
                );
                PItems.getAllItems().stream()
                        .filter(item -> item instanceof BlockItem)
                        .map(Item::getDefaultInstance)
                        .filter(stack -> stack.getItem() != Items.AIR)
                        .filter(stack -> !skip.contains(((BlockItem) stack.getItem()).getBlock()))
                        .forEach(output::accept);

                for (String id : FlyTypeManager.INSTANCE.getFlyTypes()) {
                    output.accept(FlyItem.withType(id));
                }

                for (var type : ToiletType.getByCategory(ToiletType.Category.WOOD).values()) {
                    output.accept(ToiletBlockItem.withType(PBlocks.WOODEN_TOILET.get(), type));
                }
                for (var type : ToiletType.getByCategory(ToiletType.Category.HARD).values()) {
                    output.accept(ToiletBlockItem.withType(PBlocks.HARD_TOILET.get(), type));
                }
            })
            .build()).register();

    public static void register() {
    }
}
