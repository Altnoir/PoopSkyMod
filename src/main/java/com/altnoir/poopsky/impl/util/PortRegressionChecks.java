package com.altnoir.poopsky.impl.util;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.content.entity.p.PoopBall;
import com.altnoir.poopsky.content.entity.p.SeaPoopBall;
import com.altnoir.poopsky.content.entity.p.WitherPoopBall;
import com.altnoir.poopsky.init.PoEntityType;
import com.altnoir.poopsky.init.PoItems;
import com.mojang.authlib.GameProfile;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.RecordItem;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.JukeboxBlockEntity;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.UUID;

/** 仅显式启用本地回归测试时注册，不在普通游戏中运行或修改玩家存档。 */
@Mod.EventBusSubscriber(modid = PoopSky.MOD_ID)
public final class PortRegressionChecks {
    private PortRegressionChecks() {}

    @SubscribeEvent
    public static void register(RegisterCommandsEvent event) {
        if (!Boolean.getBoolean("poopsky.portRegression")) return;
        event.getDispatcher().register(Commands.literal("poopsky_port_check")
                .requires(source -> source.hasPermission(4))
                .executes(context -> {
                    run(context.getSource().getLevel());
                    context.getSource().sendSuccess(() -> Component.literal("PORT_REGRESSION_OK"), false);
                    return 1;
                }));
    }

    private static void require(boolean success, String name) {
        if (!success) throw new IllegalStateException("移植回归检查失败：" + name);
        PoopSky.LOGGER.info("PORT_CHECK_PASS {}", name);
    }

    private static void run(ServerLevel level) {
        FakePlayer player = new FakePlayer(level, new GameProfile(
                UUID.fromString("54d17de8-a1ad-4431-913d-137258118388"), "PoPortCheck"));
        int largeStacks = 0;
        for (Item item : PoItems.getAllItems()) {
            ItemStack probe = new ItemStack(item);
            if (probe.getMaxStackSize() != 88) continue;
            largeStacks++;
            player.getInventory().clearContent();
            player.getInventory().add(new ItemStack(item, 64));
            player.getInventory().add(new ItemStack(item, 24));
            require(player.getInventory().getItem(0).getCount() == 88
                            && player.getInventory().getItem(1).isEmpty(),
                    "背包合并88 " + BuiltInRegistries.ITEM.getKey(item));
            SimpleContainer chest = new SimpleContainer(27);
            Slot slot = new Slot(chest, 0, 0, 0);
            slot.safeInsert(new ItemStack(item, 64));
            require(slot.safeInsert(new ItemStack(item, 25)).getCount() == 1
                            && slot.getItem().getCount() == 88,
                    "槽位合并88且不超过上限 " + BuiltInRegistries.ITEM.getKey(item));
            var menu = ChestMenu.threeRows(1, player.getInventory(), chest);
            player.getInventory().clearContent();
            menu.quickMoveStack(player, 0);
            require(chest.isEmpty() && player.getInventory().countItem(item) == 88,
                    "快捷移动数量保留 " + BuiltInRegistries.ITEM.getKey(item));
            require(player.getInventory().items.stream().anyMatch(s -> s.getCount() == 88),
                    "快捷移动不拆成64 " + BuiltInRegistries.ITEM.getKey(item));
            net.minecraft.nbt.ListTag saved = player.getInventory().save(new net.minecraft.nbt.ListTag());
            player.getInventory().clearContent();
            player.getInventory().load(saved);
            require(player.getInventory().countItem(item) == 88,
                    "背包保存读取88 " + BuiltInRegistries.ITEM.getKey(item));
        }
        require(largeStacks >= 25, "原作88堆叠物品覆盖");
        SimpleContainer ordinary = new SimpleContainer(3);
        require(new Slot(ordinary, 0, 0, 0).getMaxStackSize(new ItemStack(Items.STONE)) == 64,
                "普通方块仍为64");
        require(new Slot(ordinary, 1, 0, 0).getMaxStackSize(new ItemStack(Items.EGG)) == 16,
                "鸡蛋仍为16");
        require(new Slot(ordinary, 2, 0, 0).getMaxStackSize(new ItemStack(Items.IRON_SWORD)) == 1,
                "工具仍为1");
        ordinary.setItem(0, new ItemStack(PoItems.POOP.get(), 88));
        require(ordinary.getItem(0).getCount() == 88, "普通容器不截断88");

        var poop = new PoopBall(level, 0, 110, 0);
        var sea = new SeaPoopBall(level, player);
        var wither = new WitherPoopBall(level, 0, 110, 0);
        require(poop.getType() == PoEntityType.POOP_BALL.get()
                        && poop.getItem().is(PoItems.POOP_BALL.get()), "粪球类型及默认物品");
        require(sea.getType() == PoEntityType.SEA_POOP_BALL.get()
                        && sea.getItem().is(PoItems.SEA_POOP_BALL.get()), "海洋粪球类型及默认物品");
        require(wither.getType() == PoEntityType.WITHER_POOP_BALL.get()
                        && wither.getItem().is(PoItems.WITHER_POOP_BALL.get()), "凋零粪球类型及默认物品");
        require(PoEntityType.POOP_BALL.get().create(level) instanceof PoopBall
                        && PoEntityType.SEA_POOP_BALL.get().create(level) instanceof SeaPoopBall
                        && PoEntityType.WITHER_POOP_BALL.get().create(level) instanceof WitherPoopBall,
                "投射物网络创建工厂");
        require(poop.getAddEntityPacket() != null && sea.getAddEntityPacket() != null
                        && wither.getAddEntityPacket() != null, "投射物生成数据包");
        checkDroppedMerge(level);
        checkRecord(level, PoItems.LAWRENCE_MUSIC_DISC.get(), 276);
        checkRecord(level, PoItems.LIGHT_DANCE_MUSIC_DISC.get(), 212);
        checkRecord(level, PoItems.MOON_BOWL_MUSIC_DISC.get(), 245);
        checkRecord(level, PoItems.THEME_MUSIC_DISC.get(), 132);
    }

    private static void checkDroppedMerge(ServerLevel level) {
        var a = new net.minecraft.world.entity.item.ItemEntity(level, 14.5, 110, 14.5,
                new ItemStack(PoItems.POOP.get(), 80), 0, 0, 0);
        var b = new net.minecraft.world.entity.item.ItemEntity(level, 14.5, 110, 14.5,
                new ItemStack(PoItems.POOP.get(), 8), 0, 0, 0);
        a.getItem().getOrCreateTag().putBoolean("PortRegression", true);
        b.getItem().getOrCreateTag().putBoolean("PortRegression", true);
        a.setNoGravity(true);
        b.setNoGravity(true);
        level.addFreshEntity(a);
        level.addFreshEntity(b);
        try {
            for (int tick = 0; tick < 45; tick++) {
                if (!a.isRemoved()) a.tick();
                if (!b.isRemoved()) b.tick();
            }
            require((a.isRemoved() && b.getItem().getCount() == 88)
                            || (b.isRemoved() && a.getItem().getCount() == 88),
                    "掉落物80加8合并为88");
        } finally {
            a.discard();
            b.discard();
        }
    }

    private static void checkRecord(ServerLevel level, Item item, int seconds) {
        RecordItem record = (RecordItem) item;
        require(record.getLengthInTicks() == seconds * 20, "唱片时长 " + BuiltInRegistries.ITEM.getKey(item));
        // 不放置方块：仅对内存中的唱片机推进完整播放周期。
        BlockPos pos = new BlockPos(0, 110, 0);
        var state = Blocks.JUKEBOX.defaultBlockState();
        var jukebox = new JukeboxBlockEntity(pos, state);
        jukebox.setLevel(level);
        jukebox.setRecordWithoutPlaying(new ItemStack(record));
        jukebox.startPlaying();
        for (int tick = 0; tick < seconds * 20; tick++) {
            JukeboxBlockEntity.playRecordTick(level, pos, state, jukebox);
            if (!jukebox.isRecordPlaying()) throw new IllegalStateException("唱片提前停止：" + item);
        }
        require(jukebox.isRecordPlaying(), "唱片全程不提前停止 " + BuiltInRegistries.ITEM.getKey(item));
        for (int tick = 0; tick < 25; tick++) {
            JukeboxBlockEntity.playRecordTick(level, pos, state, jukebox);
        }
        require(!jukebox.isRecordPlaying(), "唱片正常结束 " + BuiltInRegistries.ITEM.getKey(item));
    }
}
