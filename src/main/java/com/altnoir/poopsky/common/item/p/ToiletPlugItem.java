package com.altnoir.poopsky.common.item.p;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.init.PEntityType;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class ToiletPlugItem extends Item {
    private static final float ATTACK_DAMAGE_MODIFIER = 1.0F;
    private static final float ATTACK_KNOCKBACK_MODIFIER = 1.0F;
    private static final ResourceLocation BASE_ATTACK_KNOCKBACK_ID = PoopSky.loc("base_attack_knockback");
    private static final int POISON_DURATION = 60;

    public ToiletPlugItem(Properties properties) {
        super(properties);
    }

    public static ItemAttributeModifiers createWeaponAttributes() {
        return ItemAttributeModifiers.builder()
                .add(
                        Attributes.ATTACK_DAMAGE,
                        new AttributeModifier(BASE_ATTACK_DAMAGE_ID, ATTACK_DAMAGE_MODIFIER, AttributeModifier.Operation.ADD_VALUE),
                        EquipmentSlotGroup.MAINHAND
                )
                .add(
                        Attributes.ATTACK_KNOCKBACK,
                        new AttributeModifier(BASE_ATTACK_KNOCKBACK_ID, ATTACK_KNOCKBACK_MODIFIER, AttributeModifier.Operation.ADD_VALUE),
                        EquipmentSlotGroup.MAINHAND
                )
                .build();
    }

    public static boolean poisonOnHit(LivingEntity target, LivingEntity attacker) {
        target.addEffect(new MobEffectInstance(MobEffects.POISON, POISON_DURATION), attacker);
        return true;
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        return poisonOnHit(target, attacker);
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext context) {
        var level = context.getLevel();
        var player = context.getPlayer();

        if (player == null) {
            return InteractionResult.PASS;
        }

        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }
        var pos = context.getClickedPos();
        var face = context.getClickedFace();
        var stack = context.getItemInHand();

        var plug = PEntityType.TOILET_PLUG.get().create(level);

        if (plug == null)
            return InteractionResult.FAIL;

        var height = 1f;
        var x = pos.getX() + 0.5f;
        var y = pos.getY() + height;
        var z = pos.getZ() + 0.5f;

        if (face == Direction.DOWN) {
            y = pos.getY() - height;
        } else if (face != Direction.UP) {
            var offset = new Vec3(face.getStepX(), face.getStepY(), face.getStepZ());
            x += (float) offset.x;
            y += (float) offset.y;
            z += (float) offset.z;
        }

        plug.setPos(x, y, z);
        level.addFreshEntity(plug);
        player.level().playSound(null, plug.getX(), plug.getY(), plug.getZ(), SoundEvents.ITEM_FRAME_REMOVE_ITEM, SoundSource.PLAYERS, 0.5F, 1.0F);

        if (!player.isCreative()) stack.shrink(1);

        return InteractionResult.SUCCESS;
    }
}
