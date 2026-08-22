package com.altnoir.poopsky.client.model;

import com.altnoir.poopsky.content.item.p.GachaponItem;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.properties.select.SelectItemModelProperty;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public final class GachaponColorItemModelProperty implements SelectItemModelProperty<String> {
    public static final GachaponColorItemModelProperty INSTANCE = new GachaponColorItemModelProperty();
    public static final MapCodec<GachaponColorItemModelProperty> MAP_CODEC = MapCodec.unit(INSTANCE);
    public static final Type<GachaponColorItemModelProperty, String> TYPE = Type.create(MAP_CODEC, Codec.STRING);

    private GachaponColorItemModelProperty() {
    }

    @Override
    public String get(ItemStack stack, ClientLevel level, LivingEntity entity, int seed,
                      ItemDisplayContext displayContext) {
        return GachaponItem.getColor(stack);
    }

    @Override
    public Codec<String> valueCodec() {
        return Codec.STRING;
    }

    @Override
    public Type<GachaponColorItemModelProperty, String> type() {
        return TYPE;
    }
}
