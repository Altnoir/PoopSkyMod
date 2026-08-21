package com.altnoir.poopsky.client.model;

import com.altnoir.poopsky.content.item.p.GashaponItem;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.properties.select.SelectItemModelProperty;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public final class GashaponColorItemModelProperty implements SelectItemModelProperty<String> {
    public static final GashaponColorItemModelProperty INSTANCE = new GashaponColorItemModelProperty();
    public static final MapCodec<GashaponColorItemModelProperty> MAP_CODEC = MapCodec.unit(INSTANCE);
    public static final Type<GashaponColorItemModelProperty, String> TYPE = Type.create(MAP_CODEC, Codec.STRING);

    private GashaponColorItemModelProperty() {
    }

    @Override
    public String get(ItemStack stack, ClientLevel level, LivingEntity entity, int seed,
                      ItemDisplayContext displayContext) {
        return GashaponItem.getColor(stack);
    }

    @Override
    public Codec<String> valueCodec() {
        return Codec.STRING;
    }

    @Override
    public Type<GashaponColorItemModelProperty, String> type() {
        return TYPE;
    }
}
