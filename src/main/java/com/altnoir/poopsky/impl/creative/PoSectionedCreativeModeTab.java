package com.altnoir.poopsky.impl.creative;

import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackLinkedSet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public final class PoSectionedCreativeModeTab extends CreativeModeTab {
    private static final int COLUMNS = 9;
    private static final int VISIBLE_ROWS = 5;

    private final List<PoCreativeTabSection> sections;
    private final Consumer<ItemDisplayParameters> populator;
    private Collection<ItemStack> displayItems = List.of();
    private Set<ItemStack> searchItems = ItemStackLinkedSet.createTypeAndComponentsSet();
    private List<SectionLayout> sectionLayouts = List.of();

    private PoSectionedCreativeModeTab(Builder builder, List<PoCreativeTabSection> sections, Consumer<ItemDisplayParameters> populator) {
        super(builder);
        this.sections = List.copyOf(sections);
        this.populator = populator;
    }

    public static Builder configure(Builder builder, Consumer<ItemDisplayParameters> populator, PoCreativeTabSection... sections) {
        List<PoCreativeTabSection> sectionList = List.of(sections);
        return builder.withTabFactory(tabBuilder -> new PoSectionedCreativeModeTab(tabBuilder, sectionList, populator));
    }

    @Override
    public void buildContents(ItemDisplayParameters parameters) {
        sections.forEach(PoCreativeTabSection::clear);
        populator.accept(parameters);

        List<ItemStack> newDisplayItems = new ArrayList<>();
        Set<ItemStack> newSearchItems = ItemStackLinkedSet.createTypeAndComponentsSet();
        Set<ItemStack> seenDisplayItems = ItemStackLinkedSet.createTypeAndComponentsSet();
        List<SectionLayout> newLayouts = new ArrayList<>();

        for (PoCreativeTabSection section : sections) {
            List<ItemStack> enabledItems = section.itemStacks().stream()
                    .filter(stack -> stack.getItem().isEnabled(parameters.enabledFeatures()))
                    .filter(seenDisplayItems::add)
                    .toList();
            if (enabledItems.isEmpty()) {
                continue;
            }

            int headingRow = newDisplayItems.size() / COLUMNS;
            newLayouts.add(new SectionLayout(section.title(), headingRow));
            addEmptyRow(newDisplayItems);
            newDisplayItems.addAll(enabledItems);
            newSearchItems.addAll(enabledItems);
            padToCompleteRow(newDisplayItems);
        }

        displayItems = List.copyOf(newDisplayItems);
        searchItems = newSearchItems;
        sectionLayouts = List.copyOf(newLayouts);
    }

    @Override
    public Collection<ItemStack> getDisplayItems() {
        return displayItems;
    }

    @Override
    public Collection<ItemStack> getSearchTabDisplayItems() {
        return searchItems;
    }

    @Override
    public boolean contains(ItemStack stack) {
        return searchItems.contains(stack);
    }

    @Override
    public boolean hasAnyItems() {
        return !searchItems.isEmpty();
    }

    public List<SectionLayout> sectionLayouts() {
        return sectionLayouts;
    }

    public int visibleStartRow(float scrollOffset) {
        int hiddenRows = Math.max(Mth.positiveCeilDiv(displayItems.size(), COLUMNS) - VISIBLE_ROWS, 0);
        return Math.max((int) (scrollOffset * hiddenRows + 0.5F), 0);
    }

    private static void addEmptyRow(List<ItemStack> items) {
        for (int column = 0; column < COLUMNS; column++) {
            items.add(ItemStack.EMPTY);
        }
    }

    private static void padToCompleteRow(List<ItemStack> items) {
        int remainder = items.size() % COLUMNS;
        if (remainder == 0) {
            return;
        }
        for (int column = remainder; column < COLUMNS; column++) {
            items.add(ItemStack.EMPTY);
        }
    }

    public record SectionLayout(Component title, int headingRow) {
    }
}
