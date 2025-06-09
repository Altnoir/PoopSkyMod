package com.altnoir.poopsky.item;

import com.altnoir.poopsky.entity.PSEntities;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class ToiletPlugItem extends Item {
    public ToiletPlugItem(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext context) {
        if (context.getLevel().isClientSide() || context.getPlayer() == null)
            return super.useOn(context);

        var level = context.getLevel();
        var player = context.getPlayer();
        var pos = context.getClickedPos();
        var face = context.getClickedFace();
        var stack = context.getItemInHand();

        var plug = PSEntities.TOILET_PLUG.get().create(level);

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

        return super.useOn(context);
    }
}
