package com.altnoir.poopsky.compat.create.content.kinetics.fan.processing;

import com.altnoir.poopsky.PTags;
import com.altnoir.poopsky.compat.create.PSRecipeTypes;
import com.altnoir.poopsky.init.PParticles;
import com.simibubi.create.content.kinetics.fan.processing.FanProcessingType;
import com.simibubi.create.foundation.recipe.RecipeApplier;
import net.createmod.catnip.theme.Color;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.List;

public class DigestingFanProcessingType implements FanProcessingType {

    @Override
    public boolean isValidAt(Level level, BlockPos pos) {
        FluidState fluidState = level.getFluidState(pos);
        if (fluidState.is(PTags.Fluids.FAN_PROCESSING_CATALYSTS_DIGESTING)) {
            return true;
        }

        BlockState blockState = level.getBlockState(pos);
        return blockState.is(PTags.Blocks.FAN_PROCESSING_CATALYSTS_DIGESTING);
    }

    @Override
    public int getPriority() {
        return 1200;
    }

    @Override
    public boolean canProcess(ItemStack stack, Level level) {
        var input = new SingleRecipeInput(stack);
        return level.getRecipeManager()
                .getRecipeFor(PSRecipeTypes.DIGESTING.getType(), input, level)
                .isPresent();
    }

    @Override
    public @Nullable List<ItemStack> process(ItemStack stack, Level level) {
        var input = new SingleRecipeInput(stack);
        return level.getRecipeManager()
                .getRecipeFor(PSRecipeTypes.DIGESTING.getType(), input, level)
                .map(recipe -> RecipeApplier.applyRecipeOn(level, stack, recipe.value(), true))
                .orElse(null);
    }

    @Override
    public void spawnProcessingParticles(Level level, Vec3 pos) {
        if (level.random.nextInt(8) != 0)
            return;
        Vector3f color = new Color(0x8B6914).asVectorF();
        level.addParticle(new DustParticleOptions(color, 1), pos.x + (level.random.nextFloat() - .5f) * .5f,
                pos.y + .5f, pos.z + (level.random.nextFloat() - .5f) * .5f, 0, 1 / 8f, 0);
        if (level.random.nextInt(4) == 0) {
            level.addParticle(PParticles.POOP_PARTICLE.get(), pos.x + (level.random.nextFloat() - .5f) * .5f,
                    pos.y + .5f, pos.z + (level.random.nextFloat() - .5f) * .5f, 0, -0.05, 0);
        }
    }

    @Override
    public void morphAirFlow(AirFlowParticleAccess particleAccess, RandomSource random) {
        particleAccess.setColor(Color.mixColors(0x8B6914, 0x5C4400, random.nextFloat()));
        particleAccess.setAlpha(1f);
        if (random.nextFloat() < 1 / 64f)
            particleAccess.spawnExtraParticle(PParticles.POOP_PARTICLE.get(), .125f);
        if (random.nextFloat() < 1 / 128f)
            particleAccess.spawnExtraParticle(ParticleTypes.DUST_PLUME, .075f);
    }

    @Override
    public void affectEntity(Entity entity, Level level) {
        if (level.isClientSide) {
            return;
        }
        if (entity instanceof LivingEntity livingEntity) {
            livingEntity.addEffect(new MobEffectInstance(MobEffects.POISON, 200, 0, true, true));
            livingEntity.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 200, 0, true, true));
        }
    }
}