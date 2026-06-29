package com.altnoir.poopsky.loot;

import com.altnoir.poopsky.block.p.MetalToiletBlock;
import com.altnoir.poopsky.block.p.StoneToiletBlock;
import com.altnoir.poopsky.block.p.ToiletBlock;
import com.altnoir.poopsky.init.PComponents;
import com.altnoir.poopsky.init.PLootFunctions;
import com.altnoir.poopsky.init.ToiletType;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
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

        Block block = state.getBlock();
        ToiletType type = null;

        if (block instanceof ToiletBlock) {
            type = state.getValue(ToiletBlock.WOOD_TYPE).getToiletType();
        } else if (block instanceof StoneToiletBlock) {
            type = state.getValue(StoneToiletBlock.STONE_TYPE).getToiletType();
        } else if (block instanceof MetalToiletBlock) {
            type = state.getValue(MetalToiletBlock.METAL_TYPE).getToiletType();
        }

        if (type != null) {
            stack.set(PComponents.TOILET_TYPE.get(), type);
        }

        return stack;
    }

    @Override
    public LootItemFunctionType getType() {
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