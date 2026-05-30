package com.altnoir.poopsky.item.p;

import net.minecraft.network.chat.Component;
import net.minecraft.server.ServerTickRateManager;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class TimeBellItem extends Item {
    public TimeBellItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        if (!level.isClientSide && player != null) {
            if (level.getServer() != null) {
                ServerTickRateManager tickRateManager = level.getServer().tickRateManager();

                // 切换冻结状态
                boolean newState = !tickRateManager.isFrozen();
                tickRateManager.setFrozen(newState);
                level.playSound(null, player.getOnPos(),
                        newState? SoundEvents.BELL_BLOCK : SoundEvents.BELL_RESONATE,
                        SoundSource.PLAYERS, 1.0F, 1.0F);

                // 发送反馈消息
                String message = newState ? "已冻结游戏时间" : "已解冻游戏时间";
                player.sendSystemMessage(Component.literal(message));

                return InteractionResultHolder.success(new ItemStack(this));
            }
        }
        return super.use(level, player, usedHand);
    }
}
