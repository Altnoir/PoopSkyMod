package com.altnoir.poopsky;

import com.altnoir.poopsky.block.PSBlocks;
import com.altnoir.poopsky.block.ToiletBlocks;
import com.altnoir.poopsky.item.PSItems;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class PSItemGroups {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TAB = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, PoopSky.MOD_ID);

    public static final Supplier<CreativeModeTab> POOPSKY_TAB = CREATIVE_MODE_TAB.register("poopsky_tab", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemgroup.poopsky"))
            .icon(() -> new ItemStack(PSItems.POOP.get()))
            .displayItems((parameters, output) -> {
                PSItems.ITEMS.getEntries().stream()
                        .map(DeferredHolder::get)
                        .filter(item -> !(item instanceof BlockItem))
                        .forEach(output::accept);

                PSBlocks.BLOCKS.getEntries().stream()
                        .map(DeferredHolder::get)
                        .forEach(output::accept);

                ToiletBlocks.BLOCKS.getEntries().stream()
                        .map(DeferredHolder::get)
                        .forEach(output::accept);
            })
            .build());
    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TAB.register(eventBus);
    }
}
