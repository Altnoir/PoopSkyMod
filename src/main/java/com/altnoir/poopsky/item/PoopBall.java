package com.altnoir.poopsky.item;

import com.altnoir.poopsky.component.PSComponents;
import com.altnoir.poopsky.entity.PoopBallEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.thrown.SnowballEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ProjectileItem;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Position;
import net.minecraft.world.World;

import javax.swing.*;
import java.util.List;

public class PoopBall extends Item implements ProjectileItem {
    public PoopBall(Item.Settings settings) {
        super(settings);
    }
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);
        world.playSound((PlayerEntity)null, user.getX(), user.getY(), user.getZ(), SoundEvents.ENTITY_SNOWBALL_THROW, SoundCategory.NEUTRAL, 0.5F, 0.4F / (world.getRandom().nextFloat() * 0.4F + 0.8F));

        if (!world.isClient) {
            PoopBallEntity poopballEntity = new PoopBallEntity(world, user);

            poopballEntity.setItem(itemStack);
            poopballEntity.setVelocity(user, user.getPitch(), user.getYaw(), 0.0F, 1.5F, 1.0F);
            world.spawnEntity(poopballEntity);

            if (itemStack.contains(PSComponents.POOP_BALL_COMPONENT)) {
                itemStack.remove(PSComponents.POOP_BALL_COMPONENT);
            }
        }
        user.incrementStat(Stats.USED.getOrCreateStat(this));
        itemStack.decrementUnlessCreative(1, user);

        return TypedActionResult.success(itemStack, world.isClient());
    }

    public ProjectileEntity createEntity(World world, Position pos, ItemStack stack, Direction direction) {
        PoopBallEntity poopballEntity = new PoopBallEntity(
                world,
                pos.getX(),
                pos.getY(),
                pos.getZ()
        );
        poopballEntity.setItem(stack);
        return poopballEntity;
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        if (stack.contains(PSComponents.POOP_BALL_COMPONENT)) {
            int count = stack.get(PSComponents.POOP_BALL_COMPONENT);
            if (count == 1) {
                tooltip.add(Text.translatable("tooltip.poopsky.poop_ball.info_1").formatted(Formatting.GOLD));
            } else if (count == 2) {
                tooltip.add(Text.translatable("tooltip.poopsky.poop_ball.info_2", count).formatted(Formatting.DARK_RED));
            }
        }
    }
}
