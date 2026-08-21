package com.altnoir.poopsky.content.item.p;

import com.altnoir.poopsky.content.entity.p.GashaponEntity;
import com.altnoir.poopsky.init.PoComponents;
import com.altnoir.poopsky.init.PoItems;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomModelData;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.Consumer;

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
        stack.set(DataComponents.CUSTOM_MODEL_DATA,
                new CustomModelData(java.util.List.of(), java.util.List.of(), java.util.List.of(),
                        java.util.List.of(getColorModelData(color))));
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
    public InteractionResult use(Level level, Player player, InteractionHand usedHand) {
        ItemStack itemstack = player.getItemInHand(usedHand);
        if (!level.isClientSide()) {
            GashaponEntity projectile = new GashaponEntity(level, player);
            projectile.setVariant(GashaponEntity.variantFromColor(getColor(itemstack)));
            projectile.setMobId(Objects.requireNonNull(getMobId(itemstack)));
            projectile.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 1.5F, 1.0F);
            level.addFreshEntity(projectile);
        }
        level.playSound(player, player.getX(), player.getY(), player.getZ(), SoundEvents.EGG_THROW, SoundSource.PLAYERS,
                0.5F, level.getRandom().nextFloat() * 0.4F + 0.5F);
        player.awardStat(Stats.ITEM_USED.get(this));
        itemstack.shrink(1);
        return level.isClientSide() ? InteractionResult.SUCCESS : InteractionResult.SUCCESS_SERVER;
    }

    @Override
    public Projectile asProjectile(Level level, Position position, ItemStack itemStack, Direction direction) {
        GashaponEntity projectile = new GashaponEntity(level, position.x(), position.y(), position.z());
        projectile.setVariant(GashaponEntity.variantFromColor(getColor(itemStack)));
        projectile.setMobId(Objects.requireNonNull(getMobId(itemStack)));
        return projectile;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display,
                                Consumer<Component> tooltip, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, display, tooltip, tooltipFlag);
        String mobId = getMobId(stack);
        if (mobId != null && !mobId.isBlank()) {
            Identifier id = Identifier.tryParse(mobId);
            var entityType = id != null ? BuiltInRegistries.ENTITY_TYPE.getValue(id) : null;
            tooltip.accept(entityType != null
                    ? Component.translatable(entityType.getDescriptionId()).withStyle(ChatFormatting.GOLD)
                    : Component.literal(mobId).withStyle(ChatFormatting.GOLD));
        }
    }
}
