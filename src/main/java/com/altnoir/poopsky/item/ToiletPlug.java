package com.altnoir.poopsky.item;

import com.altnoir.poopsky.entity.PSEntities;
import com.altnoir.poopsky.entity.ToiletPlugEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;

public class ToiletPlug extends Item {
    public ToiletPlug(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        if (!context.getWorld().isClient && context.getPlayer() != null) {
            World world = context.getWorld();
            BlockPos pos = context.getBlockPos();
            Direction face = context.getSide();
            PlayerEntity player = context.getPlayer();
            ItemStack stack = context.getStack();
            ToiletPlugEntity plug = PSEntities.TOILET_PLUG_ENTITY.create(world);

            float h = 1.0F;
            double x = pos.getX() + 0.5;
            double y = pos.getY() + h;
            double z = pos.getZ() + 0.5;

            if (face == Direction.DOWN) {
                y = pos.getY() - h;
            } else if (face != Direction.UP) {
                Vec3d offset = new Vec3d(face.getOffsetX(), face.getOffsetY(), face.getOffsetZ());
                x += offset.x;
                y += offset.y - h;
                z += offset.z;
            }
            plug.setPosition(x, y, z);
            world.spawnEntity(plug);
            player.getWorld().playSound(null, plug.getX(),  plug.getY(), plug.getZ(), SoundEvents.ENTITY_ITEM_FRAME_REMOVE_ITEM,  plug.getSoundCategory(), 0.5F, 1.0F);

            if (!player.isCreative()) stack.decrement(1);
        }
        return super.useOnBlock(context);
    }

    @Override
    public int getItemBarStep(ItemStack stack) {
        return super.getItemBarStep(stack);
    }
}
