package com.altnoir.poopsky.effect;

import com.altnoir.poopsky.init.PEffects;
import com.altnoir.poopsky.init.PRecipes;
import com.altnoir.poopsky.util.PoopTntUtil;
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
            int duration = Objects.requireNonNull(livingEntity.getEffect(PEffects.ON_THE_VERGE)).getDuration();
            Vec3 vec3 = livingEntity.getDeltaMovement().add(new Vec3(0, 0.125, 0));
            boolean openTheDoor = false;

            if ((livingEntity instanceof Player player && player.isShiftKeyDown())) {
                if (player.hasEffect(PEffects.INTESTINAL_SPASM)) {
                    BlockPos pos = livingEntity.blockPosition().below();
                    BlockPos stonePos = pos.below();
                    BlockState state = level.getBlockState(pos);
                    BlockState stoneState = level.getBlockState(stonePos);

                    for (var holder : level.getRecipeManager().getAllRecipesFor(PRecipes.ANAL_PRESSING_TYPE.get())) {
                        var recipe = holder.value();
                        if (recipe.input().test(new ItemStack(state.getBlock().asItem())) && recipe.replaceTarget() == stoneState.getBlock()) {
                            level.removeBlock(pos, false);
                            recipe.applyConversion(level, stonePos);
                            level.explode(player, player.getX(), player.getY(0.0625), player.getZ(), 2, Level.ExplosionInteraction.NONE);
                            result = true;
                            result2 = true;
                            break;
                        }
                    }
                }
                if (duration <= 1) {
                    openTheDoor = true;
                    result = true;
                }
            } else if (amplifier >= 1 && duration > 200) {
                openTheDoor = true;
            }

            if (openTheDoor) {
                livingEntity.setDeltaMovement(vec3);
                int radius = Math.min(18, amplifier + 2);
                PoopTntUtil.triggerExplosion(livingEntity, radius);
            }
        }

        if (result) {
            livingEntity.removeEffect(PEffects.ON_THE_VERGE);
        }
        if (result2) {
            livingEntity.removeEffect(PEffects.INTESTINAL_SPASM);
        }
        return true;
    }


    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }
}