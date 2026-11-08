package com.altnoir.poopsky.content;

import com.altnoir.poopsky.content.block.entity.ToiletBlockEntity;
import com.altnoir.poopsky.init.PoComponents;
import com.altnoir.poopsky.init.PoLootFunctions;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

import java.util.List;

public class SetToiletTypeFunction extends LootItemConditionalFunction {

    public static class Serializer extends LootItemConditionalFunction.Serializer<SetToiletTypeFunction> {
        @Override public SetToiletTypeFunction deserialize(com.google.gson.JsonObject json, com.google.gson.JsonDeserializationContext context, LootItemCondition[] conditions) {
            return new SetToiletTypeFunction(conditions);
        }
    }

    protected SetToiletTypeFunction(LootItemCondition[] conditions) {
        super(conditions);
    }

    @Override
    protected ItemStack run(ItemStack stack, LootContext context) {
        BlockState state = context.getParamOrNull(LootContextParams.BLOCK_STATE);
        if (state == null) return stack;

        ToiletType type = null;

        BlockEntity be = context.getParamOrNull(LootContextParams.BLOCK_ENTITY);
        if (be instanceof ToiletBlockEntity toiletBE) {
            type = toiletBE.getToiletType();
        }

        if (type != null) {
            PoComponents.TOILET_TYPE.set(stack, type);
        }

        return stack;
    }

    @Override
    public LootItemFunctionType getType() {
        return PoLootFunctions.SET_TOILET_TYPE.get();
    }

    public static Builder setType() {
        return new Builder();
    }

    public static class Builder extends LootItemConditionalFunction.Builder<SetToiletTypeFunction.Builder> {
        @Override
        protected Builder getThis() {
            return this;
        }

        @Override
        public SetToiletTypeFunction build() {
            return new SetToiletTypeFunction(getConditions());
        }
    }
}
