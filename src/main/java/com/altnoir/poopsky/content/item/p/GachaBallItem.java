package com.altnoir.poopsky.content.item.p;

import com.altnoir.poopsky.content.entity.p.GachaBallEntity;
import com.altnoir.poopsky.init.PoComponents;
import com.altnoir.poopsky.init.PoSoundEvents;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.item.ProjectileItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class GachaBallItem extends Item implements ProjectileItem {
    public GachaBallItem(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                PoSoundEvents.ENTITY_POOP_BALL_THROW.get(), SoundSource.NEUTRAL, 0.5F,
                0.8F / (level.getRandom().nextFloat() * 0.4F + 0.8F));
        if (!level.isClientSide) {
            GachaBallEntity entity = new GachaBallEntity(level, player);
            entity.setItem(stack);
            entity.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 1.5F, 1.0F);
            level.addFreshEntity(entity);
        }
        player.awardStat(Stats.ITEM_USED.get(this));
        stack.consume(1, player);
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }

    @Override
    public @NotNull Projectile asProjectile(Level level, Position pos, ItemStack stack, Direction direction) {
        GachaBallEntity entity = new GachaBallEntity(level, pos.x(), pos.y(), pos.z());
        entity.setItem(stack);
        return entity;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        String id = stack.get(PoComponents.GACHA_ENTITY.get());
        if (id != null) {
            ResourceLocation location = ResourceLocation.tryParse(id);
            if (location != null) {
                BuiltInRegistries.ENTITY_TYPE.getOptional(location).ifPresent(type ->
                        tooltip.add(Component.translatable("item.poopsky.gacha_ball.entity",
                                Component.translatable(type.getDescriptionId()))));
            }
        }
        super.appendHoverText(stack, context, tooltip, flag);
    }
}
