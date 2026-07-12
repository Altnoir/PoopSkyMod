package com.altnoir.poopsky.content.block.p;

import com.altnoir.poopsky.init.PoBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CandleCakeBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Map;

public class PoopCandleCakeBlock extends CandleCakeBlock {
    private static final Map<Block, Block> VANILLA_CANDLE_CAKES = Map.ofEntries(
            Map.entry(Blocks.CANDLE, Blocks.CANDLE_CAKE),
            Map.entry(Blocks.WHITE_CANDLE, Blocks.WHITE_CANDLE_CAKE),
            Map.entry(Blocks.ORANGE_CANDLE, Blocks.ORANGE_CANDLE_CAKE),
            Map.entry(Blocks.MAGENTA_CANDLE, Blocks.MAGENTA_CANDLE_CAKE),
            Map.entry(Blocks.LIGHT_BLUE_CANDLE, Blocks.LIGHT_BLUE_CANDLE_CAKE),
            Map.entry(Blocks.YELLOW_CANDLE, Blocks.YELLOW_CANDLE_CAKE),
            Map.entry(Blocks.LIME_CANDLE, Blocks.LIME_CANDLE_CAKE),
            Map.entry(Blocks.PINK_CANDLE, Blocks.PINK_CANDLE_CAKE),
            Map.entry(Blocks.GRAY_CANDLE, Blocks.GRAY_CANDLE_CAKE),
            Map.entry(Blocks.LIGHT_GRAY_CANDLE, Blocks.LIGHT_GRAY_CANDLE_CAKE),
            Map.entry(Blocks.CYAN_CANDLE, Blocks.CYAN_CANDLE_CAKE),
            Map.entry(Blocks.PURPLE_CANDLE, Blocks.PURPLE_CANDLE_CAKE),
            Map.entry(Blocks.BLUE_CANDLE, Blocks.BLUE_CANDLE_CAKE),
            Map.entry(Blocks.BROWN_CANDLE, Blocks.BROWN_CANDLE_CAKE),
            Map.entry(Blocks.GREEN_CANDLE, Blocks.GREEN_CANDLE_CAKE),
            Map.entry(Blocks.RED_CANDLE, Blocks.RED_CANDLE_CAKE),
            Map.entry(Blocks.BLACK_CANDLE, Blocks.BLACK_CANDLE_CAKE)
    );
    private final Block candle;

    public PoopCandleCakeBlock(Block candle, Properties properties) {
        super(candle, properties);
        this.candle = candle;
        restoreVanillaCandleCake(candle);
    }

    @SuppressWarnings("unchecked")
    private static void restoreVanillaCandleCake(Block candle) {
        Block vanillaCandleCake = VANILLA_CANDLE_CAKES.get(candle);
        if (vanillaCandleCake == null) {
            return;
        }

        try {
            for (Field field : CandleCakeBlock.class.getDeclaredFields()) {
                if (Modifier.isStatic(field.getModifiers()) && Map.class.isAssignableFrom(field.getType())) {
                    field.setAccessible(true);
                    ((Map<Block, Block>) field.get(null)).put(candle, vanillaCandleCake);
                    return;
                }
            }
        } catch (ReflectiveOperationException exception) {
            throw new IllegalStateException("Unable to restore vanilla candle cake mapping", exception);
        }
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (state.getValue(LIT) && isHittingCandle(hitResult, pos)) {
            extinguish(state, level, pos, player);
            return InteractionResult.sidedSuccess(level.isClientSide);
        }

        InteractionResult result = PoopCakeBlock.eat(level, pos, PoBlocks.POOP_CAKE.get().defaultBlockState(), player);
        if (result.consumesAction() && !level.isClientSide) {
            popResource(level, pos, new ItemStack(candle));
        }
        return result;
    }

    private static boolean isHittingCandle(BlockHitResult hitResult, BlockPos pos) {
        return hitResult.getLocation().y - pos.getY() > 0.5;
    }

    private static void extinguish(BlockState state, Level level, BlockPos pos, Player player) {
        if (!level.isClientSide) {
            level.playSound(null, pos, SoundEvents.CANDLE_EXTINGUISH, SoundSource.BLOCKS, 1.0F, 1.0F);
            level.setBlock(pos, state.setValue(LIT, false), 11);
            level.gameEvent(player, GameEvent.BLOCK_CHANGE, pos);
        }
    }

    @Override
    public ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state) {
        return PoBlocks.POOP_CAKE.get().asItem().getDefaultInstance();
    }
}
