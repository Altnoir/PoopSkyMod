package com.altnoir.poopsky.content.item.p;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.content.entity.p.GashaponEntity;
import com.altnoir.poopsky.init.PoComponents;
import com.altnoir.poopsky.init.PoItems;
import com.altnoir.poopsky.init.PoSoundEvents;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.core.component.DataComponents;
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
import net.minecraft.world.item.ProjectileItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomModelData;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

public class GashaponItem extends Item implements ProjectileItem {
    public static final String PINK = "pink";
    public static final String RED = "red";
    public static final String YELLOW = "yellow";
    public static final String BLUE = "blue";
    public static final String[] COLORS = {PINK, RED, YELLOW, BLUE};

    public GashaponItem(Properties properties) {
        super(properties);
    }

    public static ItemStack withColor(String color) {
        ItemStack stack = new ItemStack(PoItems.GASHAPON.get());
        stack.set(PoComponents.GASHAPON_COLOR.get(), color);
        stack.set(DataComponents.CUSTOM_MODEL_DATA, new CustomModelData(getColorModelData(color)));
        return stack;
    }

    public static ItemStack withColorAndMob(String color, String mobId) {
        ItemStack stack = withColor(color);
        stack.set(PoComponents.GASHAPON_MOB.get(), mobId);
        return stack;
    }

    public static String getColor(ItemStack stack) {
        String color = stack.get(PoComponents.GASHAPON_COLOR.get());
        return color != null ? color : PINK;
    }

    public static @Nullable String getMobId(ItemStack stack) {
        return stack.get(PoComponents.GASHAPON_MOB.get());
    }

    public static int getColorModelData(String color) {
        return switch (color) {
            case YELLOW -> 1;
            case RED -> 2;
            case BLUE -> 3;
            default -> 0;
        };
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack itemstack = player.getItemInHand(usedHand);
        if (!level.isClientSide) {
            GashaponEntity projectile = new GashaponEntity(level, player);
            projectile.setVariant(GashaponEntity.variantFromColor(getColor(itemstack)));
            projectile.setMobId(Objects.requireNonNull(getMobId(itemstack)));
            projectile.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 1.5F, 1.0F);
            level.addFreshEntity(projectile);
        }
        level.playSound(player, player.getX(), player.getY(), player.getZ(), PoSoundEvents.ENTITY_POOP_BALL_THROW.get(),
                SoundSource.NEUTRAL, 1.0F, level.getRandom().nextFloat() * 0.4F + 0.8F);
        player.awardStat(Stats.ITEM_USED.get(this));
        itemstack.shrink(1);
        return InteractionResultHolder.sidedSuccess(itemstack, level.isClientSide());
    }

    @Override
    public Projectile asProjectile(Level level, Position position, ItemStack itemStack, Direction direction) {
        GashaponEntity projectile = new GashaponEntity(level, position.x(), position.y(), position.z());
        projectile.setVariant(GashaponEntity.variantFromColor(getColor(itemStack)));
        projectile.setMobId(Objects.requireNonNull(getMobId(itemStack)));
        return projectile;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        String mobId = getMobId(stack);
        if (mobId != null && !mobId.isBlank()) {
            ResourceLocation id = PoopSky.tryParse(mobId);
            var entityType = id != null ? BuiltInRegistries.ENTITY_TYPE.get(id) : null;
            tooltipComponents.add(entityType != null
                    ? Component.translatable(entityType.getDescriptionId()).withStyle(ChatFormatting.GOLD)
                    : Component.literal(mobId).withStyle(ChatFormatting.GOLD));
        }
    }
}