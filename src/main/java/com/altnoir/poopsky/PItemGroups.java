package com.altnoir.poopsky;

import com.altnoir.poopsky.content.FlyTypeManager;
import com.altnoir.poopsky.content.block.ToiletType;
import com.altnoir.poopsky.content.item.p.FlyItem;
import com.altnoir.poopsky.content.item.p.ToiletBlockItem;
import com.altnoir.poopsky.init.PBlocks;
import com.altnoir.poopsky.init.PItems;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Set;
import java.util.function.Supplier;

public class PItemGroups {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TAB = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, PoopSky.MOD_ID);

    public static final Supplier<CreativeModeTab> POOPSKY_TAB = CREATIVE_MODE_TAB.register("poopsky_tab", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemgroup.poopsky"))
            .icon(() -> new ItemStack(PBlocks.WOODEN_TOILET.get().asItem()))
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
            .build());

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TAB.register(eventBus);
    }
}