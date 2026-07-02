package com.altnoir.poopsky.common;

import com.altnoir.poopsky.block.ToiletType;
import com.altnoir.poopsky.block.entity.ToiletBlockEntity;
import com.altnoir.poopsky.init.PComponents;
import com.altnoir.poopsky.init.PLootFunctions;
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

    public static final MapCodec<SetToiletTypeFunction> CODEC = RecordCodecBuilder.mapCodec(
            instance -> commonFields(instance).apply(instance, SetToiletTypeFunction::new)
    );

    protected SetToiletTypeFunction(List<LootItemCondition> conditions) {
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
            stack.set(PComponents.TOILET_TYPE.get(), type);
        }

        return stack;
    }

    @Override
    public LootItemFunctionType<SetToiletTypeFunction> getType() {
        return PLootFunctions.SET_TOILET_TYPE.get();
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
