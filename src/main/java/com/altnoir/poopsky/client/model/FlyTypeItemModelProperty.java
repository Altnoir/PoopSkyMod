package com.altnoir.poopsky.client.model;

import com.altnoir.poopsky.init.PoComponents;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.properties.select.SelectItemModelProperty;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public final class FlyTypeItemModelProperty implements SelectItemModelProperty<String> {
    public static final FlyTypeItemModelProperty INSTANCE = new FlyTypeItemModelProperty();
    public static final MapCodec<FlyTypeItemModelProperty> MAP_CODEC = MapCodec.unit(INSTANCE);
    public static final Type<FlyTypeItemModelProperty, String> TYPE = Type.create(MAP_CODEC, Codec.STRING);

    private FlyTypeItemModelProperty() {
    }

    @Override
    public String get(ItemStack stack, ClientLevel level, LivingEntity entity, int seed, ItemDisplayContext displayContext) {
        return stack.get(PoComponents.FLY_TYPE.get());
    }

    @Override
    public Codec<String> valueCodec() {
        return Codec.STRING;
    }

    @Override
    public Type<FlyTypeItemModelProperty, String> type() {
        return TYPE;
    }
}
