package com.altnoir.poopsky.item;

import com.altnoir.poopsky.block.Toilet;
import com.altnoir.poopsky.component.PSComponents;
import com.altnoir.poopsky.entity.ToiletBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.client.gui.screen.Screen;
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
            stack.set(PSComponents.TOILET_COMPONENT, new PSComponents.ToiletComponent("", "", 0, 0, 0, 0, 0, 0));
            player.sendMessage(Text.translatable("message.poopsky.toilet_linker.4").formatted(Formatting.DARK_RED), true);
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
            if (stack.contains(PSComponents.TOILET_COMPONENT)) {
                PSComponents.ToiletComponent comp = stack.getOrDefault(PSComponents.TOILET_COMPONENT, new PSComponents.ToiletComponent("", "", 0, 0, 0, 0, 0, 0));

                if (player != null && !player.isSneaking()) {
                    if ( comp.world1().isEmpty()) {
                        stack.set(PSComponents.TOILET_COMPONENT, new PSComponents.ToiletComponent(
                                world.getRegistryKey().getValue().toString(),
                                comp.world2(),
                                pos.getX(), pos.getY(), pos.getZ(),
                                comp.x2(), comp.y2(), comp.z2()
                        ));
                        if (comp.world2().isEmpty()) {
                            player.sendMessage(Text.translatable("message.poopsky.toilet_linker.1"), true);
                        }else {
                            linkToilets(stack, world, player);
                        }
                    }
                } else if (comp.world2().isEmpty()) {
                    stack.set(PSComponents.TOILET_COMPONENT, new PSComponents.ToiletComponent(
                            comp.world1(),
                            world.getRegistryKey().getValue().toString(),
                            comp.x1(), comp.y1(), comp.z1(),
                            pos.getX(), pos.getY(), pos.getZ()
                    ));
                    if (comp.world1().isEmpty()) {
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
            PSComponents.ToiletComponent comp = stack.get(PSComponents.TOILET_COMPONENT);

            MinecraftServer server = serverWorld.getServer();

            ServerWorld world1 = server.getWorld(RegistryKey.of(
                    RegistryKeys.WORLD,
                    Identifier.of(comp.world1()))
            );
            ServerWorld world2 = server.getWorld(RegistryKey.of(
                    RegistryKeys.WORLD,
                    Identifier.of(comp.world2()))
            );

            BlockPos pos1 = new BlockPos(comp.x1(), comp.y1(), comp.z1());
            BlockPos pos2 = new BlockPos(comp.x2(), comp.y2(), comp.z2());

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

                player.sendMessage(Text.translatable("message.poopsky.toilet_linker.3").formatted(Formatting.GREEN), true);
                stack.set(PSComponents.TOILET_COMPONENT, new PSComponents.ToiletComponent("", "", 0, 0, 0, 0, 0, 0));
            }
        }
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        PSComponents.ToiletComponent comp = stack.get(PSComponents.TOILET_COMPONENT);
        String dim1 = comp.world1();
        String dim2 = comp.world2();

        if (!dim1.isEmpty() || !dim2.isEmpty()) {
            if (!Screen.hasShiftDown()) {
                tooltip.add(Text.translatable("tooltip.poopsky.toilet_linker.info_0"));
            } else if (!dim1.isEmpty()) {
                int x1 = comp.x1(), y1 = comp.y1(), z1 = comp.z1();
                tooltip.add(Text.translatable("tooltip.poopsky.toilet_linker.info_1", dim1, x1, y1, z1).formatted(Formatting.GRAY));
            }
            if (!dim2.isEmpty()) {
                int x2 = comp.x2(), y2 = comp.y2(), z2 = comp.z2();
                tooltip.add(Text.translatable("tooltip.poopsky.toilet_linker.info_2", dim2, x2, y2, z2).formatted(Formatting.GRAY));
            }
        }
    }
}
