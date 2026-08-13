package com.altnoir.poopsky.client.model;

import com.altnoir.poopsky.content.ToiletType;
import com.altnoir.poopsky.init.PoComponents;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.properties.select.SelectItemModelProperty;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public final class ToiletTypeItemModelProperty implements SelectItemModelProperty<String> {
    public static final ToiletTypeItemModelProperty INSTANCE = new ToiletTypeItemModelProperty();
    public static final MapCodec<ToiletTypeItemModelProperty> MAP_CODEC = MapCodec.unit(INSTANCE);
    public static final Type<ToiletTypeItemModelProperty, String> TYPE = Type.create(MAP_CODEC, Codec.STRING);

    private ToiletTypeItemModelProperty() {
    }

    @Override
    public String get(ItemStack stack, ClientLevel level, LivingEntity entity, int seed, ItemDisplayContext displayContext) {
        ToiletType type = stack.get(PoComponents.TOILET_TYPE.get());
        return type == null ? null : type.id();
    }

    @Override
    public Codec<String> valueCodec() {
        return Codec.STRING;
    }

    @Override
    public Type<ToiletTypeItemModelProperty, String> type() {
        return TYPE;
    }
}
