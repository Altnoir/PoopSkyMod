package com.altnoir.poopsky.init;

import com.mojang.serialization.Codec;
import net.minecraft.nbt.NbtOps;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.IEventBus;

/** 玩家状态回移到 Forge 持久 NBT，明确保留原版的死亡复制规则。 */
public final class PoAttachments {
    public static final Key<Long> POOP_TIME = new Key<>("poop_time", Codec.LONG, 0L, false);
    public static final Key<Boolean> SEEN_INTRO = new Key<>("seen_intro", Codec.BOOL, false, true);
    public static final Key<Boolean> SEEN_POEM = new Key<>("seen_poem", Codec.BOOL, false, true);
    private PoAttachments() {}
    public static void register(IEventBus bus) { MinecraftForge.EVENT_BUS.addListener(PoAttachments::copy); }
    private static void copy(PlayerEvent.Clone event) {
        SEEN_INTRO.set(event.getEntity(), SEEN_INTRO.get(event.getOriginal()));
        SEEN_POEM.set(event.getEntity(), SEEN_POEM.get(event.getOriginal()));
        if (!event.isWasDeath()) POOP_TIME.set(event.getEntity(), POOP_TIME.get(event.getOriginal()));
    }
    public record Key<T>(String name, Codec<T> codec, T defaultValue, boolean copyOnDeath) {
        public Key<T> get() { return this; }
        private String tagName() { return "poopsky:" + name; }
        public T get(Player player) {
            var tag = player.getPersistentData();
            return tag.contains(tagName()) ? codec.parse(NbtOps.INSTANCE, tag.get(tagName())).result().orElse(defaultValue) : defaultValue;
        }
        public void set(Player player, T value) {
            codec.encodeStart(NbtOps.INSTANCE, value).result().ifPresent(tag -> player.getPersistentData().put(tagName(), tag));
        }
    }
}
