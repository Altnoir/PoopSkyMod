package com.altnoir.poopsky.item;

import com.altnoir.poopsky.block.Toilet;
import com.altnoir.poopsky.component.PSComponents;
import com.altnoir.poopsky.entity.ToiletBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ChunkTicketType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;

import java.util.List;

public class ToiletLinker extends Item {
    public ToiletLinker(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getStackInHand(hand);
        if (player.isSneaking() && !world.isClient()) {
            stack.set(PSComponents.TOILET_COMPONENT_1, new PSComponents.ToiletComponent(null, 0, 0, 0));
            stack.set(PSComponents.TOILET_COMPONENT_2, new PSComponents.ToiletComponent(null, 0, 0, 0));
            player.sendMessage(Text.translatable("message.poopsky.toilet_linker.4"), true);
            return TypedActionResult.success(stack);
        }
        return super.use(world, player, hand);
    }
    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        World world = context.getWorld();
        BlockPos pos = context.getBlockPos();
        PlayerEntity player = context.getPlayer();
        ItemStack stack = context.getStack();

        if (world.getBlockState(pos).getBlock() instanceof Toilet) {
            PSComponents.ToiletComponent comp1 = stack.getOrDefault(PSComponents.TOILET_COMPONENT_1, new PSComponents.ToiletComponent(null,0,0,0));
            PSComponents.ToiletComponent comp2 = stack.getOrDefault(PSComponents.TOILET_COMPONENT_2, new PSComponents.ToiletComponent(null,0,0,0));

            if (!player.isSneaking()) {
                if (stack.contains(PSComponents.TOILET_COMPONENT_1) && comp1.world() == null) {
                    stack.set(PSComponents.TOILET_COMPONENT_1, new PSComponents.ToiletComponent(
                            world.getRegistryKey().getValue().toString(),
                            pos.getX(),
                            pos.getY(),
                            pos.getZ()
                    ));
                    if (comp2.world() == null) {
                        player.sendMessage(Text.translatable("message.poopsky.toilet_linker.1"), true);
                    }else {
                        linkToilets(stack, world, player);
                    }
                }
            } else {
                if (stack.contains(PSComponents.TOILET_COMPONENT_2) && comp2.world() == null) {
                    stack.set(PSComponents.TOILET_COMPONENT_2, new PSComponents.ToiletComponent(
                            world.getRegistryKey().getValue().toString(),
                            pos.getX(),
                            pos.getY(),
                            pos.getZ()
                    ));
                    if (comp1.world() == null) {
                        player.sendMessage(Text.translatable("message.poopsky.toilet_linker.2"), true);
                    }else {
                        linkToilets(stack, world, player);
                    }
                }
            }
        }
        return ActionResult.SUCCESS;
    }
    private void linkToilets(ItemStack stack, World world, PlayerEntity player) {
        if (!world.isClient && world instanceof ServerWorld serverWorld) {
            PSComponents.ToiletComponent comp1 = stack.get(PSComponents.TOILET_COMPONENT_1);
            PSComponents.ToiletComponent comp2 = stack.get(PSComponents.TOILET_COMPONENT_2);

            MinecraftServer server = serverWorld.getServer();

            ServerWorld world1 = server.getWorld(RegistryKey.of(
                    RegistryKeys.WORLD,
                    Identifier.of(comp1.world()))
            );
            ServerWorld world2 = server.getWorld(RegistryKey.of(
                    RegistryKeys.WORLD,
                    Identifier.of(comp2.world()))
            );

            BlockPos pos1 = new BlockPos(comp1.x(), comp1.y(), comp1.z());
            BlockPos pos2 = new BlockPos(comp2.x(), comp2.y(), comp2.z());

            ToiletBlockEntity toilet1 = (ToiletBlockEntity) world1.getBlockEntity(pos1);
            ToiletBlockEntity toilet2 = (ToiletBlockEntity) world2.getBlockEntity(pos2);

            if (toilet1 != null && toilet2 != null) {

                toilet1.setLinkedPos(pos2, world2);
                toilet2.setLinkedPos(pos1, world1);

                toilet1.markDirty();
                toilet2.markDirty();

                world1.getChunkManager().addTicket(ChunkTicketType.PORTAL, new ChunkPos(pos2), 1, new BlockPos(pos2));
                world2.getChunkManager().addTicket(ChunkTicketType.PORTAL, new ChunkPos(pos1), 1, new BlockPos(pos1));
                world1.updateListeners(pos1, serverWorld.getBlockState(pos1), serverWorld.getBlockState(pos1), Block.NOTIFY_ALL);
                world2.updateListeners(pos2, serverWorld.getBlockState(pos2), serverWorld.getBlockState(pos2), Block.NOTIFY_ALL);

                player.sendMessage(Text.translatable("message.poopsky.toilet_linker.3"), true);
                stack.set(PSComponents.TOILET_COMPONENT_1, new PSComponents.ToiletComponent(null, 0, 0, 0));
                stack.set(PSComponents.TOILET_COMPONENT_2, new PSComponents.ToiletComponent(null, 0, 0, 0));
            }
        }
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        if (stack.contains(PSComponents.TOILET_COMPONENT_1)) {
            PSComponents.ToiletComponent comp1 = stack.get(PSComponents.TOILET_COMPONENT_1);
            String dim1 = comp1.world();
            if (dim1 != null) {
                int x1 = comp1.x(), y1 = comp1.y(), z1 = comp1.z();

                tooltip.add(Text.translatable("tooltip.poopsky.toilet_linker.info_1", dim1, x1, y1, z1).formatted(Formatting.AQUA));
            }
        }
        if (stack.contains(PSComponents.TOILET_COMPONENT_2)) {
            PSComponents.ToiletComponent comp2 = stack.get(PSComponents.TOILET_COMPONENT_2);
            String dim2 = comp2.world();
            if (dim2 != null) {
                int x2 = comp2.x(), y2 = comp2.y(), z2 = comp2.z();
                tooltip.add(Text.translatable("tooltip.poopsky.toilet_linker.info_2", dim2, x2, y2, z2).formatted(Formatting.AQUA));
            }
        }
    }

}
