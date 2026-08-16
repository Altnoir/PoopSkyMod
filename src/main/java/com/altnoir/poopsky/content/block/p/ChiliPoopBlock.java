package com.altnoir.poopsky.content.block.p;

import com.altnoir.poopsky.init.PoBlocks;
import com.altnoir.poopsky.worldgen.PoConfigureFeatures;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;

public class ChiliPoopBlock extends PoopBlock implements BonemealableBlock {
    public ChiliPoopBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        PotionContents potioncontents = stack.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY);

        if (potioncontents.is(Potions.WATER)) {
            if (!level.isClientSide) {
                ServerLevel serverlevel = (ServerLevel) level;

                for (int i = 0; i < 5; i++) {
                    serverlevel.sendParticles(
                            ParticleTypes.SPLASH,
                            (double) pos.getX() + level.random.nextDouble(),
                            pos.getY() + 1,
                            (double) pos.getZ() + level.random.nextDouble(),
                            1,
                            0.0,
                            0.0,
                            0.0,
                            1.0
                    );
                }
            }
            level.playSound(null, pos, SoundEvents.GENERIC_SPLASH, SoundSource.BLOCKS, 1.0F, 1.0F);
            level.playSound(null, pos, SoundEvents.BOTTLE_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F);
            level.gameEvent(null, GameEvent.FLUID_PLACE, pos);
            level.setBlockAndUpdate(pos, PoBlocks.MYCELIUM_BLOCK.get().defaultBlockState());

            ItemStack itemStack = ItemUtils.createFilledResult(stack, player, Items.GLASS_BOTTLE.getDefaultInstance());
            player.setItemInHand(hand, itemStack);

            return ItemInteractionResult.sidedSuccess(level.isClientSide);
        }
        return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
    }

    @Override
    public void performBonemeal(ServerLevel level, RandomSource random, BlockPos pos, BlockState state) {
        level.registryAccess()
                .registry(Registries.CONFIGURED_FEATURE)
                .flatMap(holder -> holder.getHolder(PoConfigureFeatures.CHILI_POOP_PATCH_BONEMEAL))
                .ifPresent(reference -> reference.value().place(level, level.getChunkSource().getGenerator(), random, pos.above()));
    }
}