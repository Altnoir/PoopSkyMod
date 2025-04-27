package com.altnoir.poopsky.item;

import com.altnoir.poopsky.entity.PSEntities;
import com.altnoir.poopsky.entity.ToiletPlugEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
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
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);
        var result = raycast(world,user, RaycastContext.FluidHandling.ANY);

        if (result.getType() == HitResult.Type.BLOCK){
            BlockPos blockPos = result.getBlockPos();
            Direction face = result.getSide();

            if (!world.isClient()){
                ToiletPlugEntity entity = PSEntities.TOILET_PLUG_ENTITY_TYPE.create(world);

                float h = 1.0F;
                double x = blockPos.getX() + 0.5;
                double y = blockPos.getY() + h;
                double z = blockPos.getZ() + 0.5;

                if (face == Direction.DOWN) {
                    y = blockPos.getY() - h;
                } else if (face != Direction.UP) {
                    Vec3d offset = new Vec3d(face.getOffsetX(), face.getOffsetY(), face.getOffsetZ());
                    x += offset.x;
                    y += offset.y - h;
                    z += offset.z;
                }
                entity.setPosition(x, y, z);
                world.spawnEntity(entity);

                if (!user.isCreative()) itemStack.decrement(1);
            }
        }
        return super.use(world, user, hand);
    }

    @Override
    public int getItemBarStep(ItemStack stack) {
        return super.getItemBarStep(stack);
    }
}
