package com.altnoir.poopsky.content.effect;

import com.altnoir.poopsky.impl.util.PoopTntUtil;
import com.altnoir.poopsky.init.PoEffects;
import com.altnoir.poopsky.init.PoRecipes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.Objects;

public class OnTheVergeEffect extends MobEffect {
    public OnTheVergeEffect(MobEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public boolean applyEffectTick(LivingEntity livingEntity, int amplifier) {
        Level level = livingEntity.level();
        boolean result = false;
        boolean result2 = false;

        if (!level.isClientSide) {
            int duration = Objects.requireNonNull(livingEntity.getEffect(PoEffects.ON_THE_VERGE)).getDuration();
            Vec3 vec3 = livingEntity.getDeltaMovement().add(new Vec3(0, 0.125, 0));
            boolean openTheDoor = false;

            if (livingEntity instanceof Player player) {
                if (player.isShiftKeyDown()) {
                    if (player.hasEffect(PoEffects.INTESTINAL_SPASM)) {
                        BlockPos pos = livingEntity.blockPosition().below();
                        BlockPos stonePos = pos.below();
                        BlockState state = level.getBlockState(pos);
                        BlockState stoneState = level.getBlockState(stonePos);

                        for (var holder : level.getRecipeManager().getAllRecipesFor(PoRecipes.ANAL_PRESSING.type().get())) {
                            var recipe = holder.value();
                            if (recipe.input().test(new ItemStack(state.getBlock().asItem())) && recipe.replaceTarget() == stoneState.getBlock()) {
                                level.removeBlock(pos, false);
                                recipe.applyConversion(level, stonePos);
                                level.explode(player, player.getX(), player.getY(0.0625), player.getZ(), 2, Level.ExplosionInteraction.NONE);
                                result2 = true;
                                break;
                            }
                        }
                    }
                    openTheDoor = true;
                    result = true;
                } else if (duration <= 1) {
                    openTheDoor = true;
                    result = true;
                }
            }
            if (amplifier >= 1 && duration > 200) {
                openTheDoor = true;
            }

            if (openTheDoor) {
                livingEntity.setDeltaMovement(vec3);
                int radius = Math.min(18, amplifier + 2);
                PoopTntUtil.triggerExplosion(livingEntity, radius);
            }
        }

        if (result) {
            livingEntity.removeEffect(PoEffects.ON_THE_VERGE);
        }
        if (result2) {
            livingEntity.removeEffect(PoEffects.INTESTINAL_SPASM);
        }
        return true;
    }


    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }
}