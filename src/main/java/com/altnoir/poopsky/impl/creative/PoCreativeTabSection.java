package com.altnoir.poopsky.impl.creative;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public final class PoCreativeTabSection {
    private final Component title;
    private final List<Supplier<ItemStack>> entries = new ArrayList<>();

    public PoCreativeTabSection(Component title) {
        this.title = title;
    }

    public Component title() {
        return title;
    }

    public void add(ItemLike item) {
        add(() -> item.asItem().getDefaultInstance());
    }

    public void add(ItemStack stack) {
        add(stack::copy);
    }

    public void add(Supplier<ItemStack> stack) {
        entries.add(stack);
    }

    public void clear() {
        entries.clear();
    }

    public List<ItemStack> itemStacks() {
        return entries.stream()
                .map(Supplier::get)
                .filter(stack -> !stack.isEmpty())
                .map(ItemStack::copy)
                .toList();
    }
}
