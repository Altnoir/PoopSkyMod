package com.altnoir.poopsky;

import com.altnoir.poopsky.block.PBlocks;
import com.altnoir.poopsky.block.AllToiletBlocks;
import com.altnoir.poopsky.item.PItems;
import com.altnoir.poopsky.item.p.FlyItem;
import com.altnoir.poopsky.init.PFlyTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Set;
import java.util.function.Supplier;

public class PSItemGroups {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TAB = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, PoopSky.MOD_ID);

    public static final Supplier<CreativeModeTab> POOPSKY_TAB = CREATIVE_MODE_TAB.register("poopsky_tab", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemgroup.poopsky"))
            .icon(() -> new ItemStack(PItems.POOP.get()))
            .displayItems((parameters, output) -> {
                PItems.ITEMS.getEntries().stream()
                        .map(DeferredHolder::get)
                        .filter(item -> !(item instanceof BlockItem))
                        .filter(item -> !(item instanceof FlyItem))
                        .forEach(output::accept);

                Set<Block> skip = Set.of(
                        PBlocks.URINE_LIQUID.get(),
                        PBlocks.WATER_COMPOOPER.get(),
                        PBlocks.LAVA_COMPOOPER.get(),
                        PBlocks.POWDER_SNOW_COMPOOPER.get(),
                        PBlocks.URINE_COMPOOPER.get(),
                        PBlocks.ROUNDWORM_VINES_PLANT.get()
                );
                PBlocks.BLOCKS.getEntries().stream()
                        .map(DeferredHolder::get)
                        .filter(block -> block.asItem() != Items.AIR)
                        .filter(block -> !skip.contains(block))
                        .forEach(output::accept);

                for (var type : PFlyTypes.getAll().values()) {
                    output.accept(FlyItem.withType(type));
                }

                AllToiletBlocks.BLOCKS.getEntries().stream()
                        .map(DeferredHolder::get)
                        .forEach(output::accept);
            })
            .build());

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TAB.register(eventBus);
    }
}
