package com.altnoir.poopsky.impl.creative;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public final class PoCreativeTabSection {
    private final ResourceKey<CreativeModeTab> tab;
    private final ResourceLocation id;
    private final Component title;
    private final @Nullable ResourceLocation bannerSprite;
    private final Set<ResourceLocation> itemIds = new LinkedHashSet<>();

    public PoCreativeTabSection(ResourceKey<CreativeModeTab> tab, ResourceLocation id, Component title) {
        this(tab, id, title, null);
    }

    public PoCreativeTabSection(
            ResourceKey<CreativeModeTab> tab,
            ResourceLocation id,
            Component title,
            @Nullable ResourceLocation bannerSprite
    ) {
        this.tab = tab;
        this.id = id;
        this.title = title;
        this.bannerSprite = bannerSprite;
    }

    public ResourceKey<CreativeModeTab> tab() {
        return tab;
    }

    public ResourceLocation id() {
        return id;
    }

    public Component title() {
        return title;
    }

    public Optional<ResourceLocation> bannerSprite() {
        return Optional.ofNullable(bannerSprite);
    }

    public void add(ResourceLocation itemId) {
        itemIds.add(itemId);
    }

    public List<ItemStack> itemStacks() {
        return itemIds.stream()
                .map(BuiltInRegistries.ITEM::get)
                .filter(item -> item != Items.AIR)
                .map(Item::getDefaultInstance)
                .toList();
    }
}