package com.altnoir.poopsky.init;

import com.altnoir.poopsky.PoopSky;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.JukeboxSong;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class PSoundEvents {
    public static final DeferredRegister<SoundEvent> SOUND_EVENT = DeferredRegister.create(BuiltInRegistries.SOUND_EVENT, PoopSky.MOD_ID);

    public static final Supplier<SoundEvent> FART = registerSoundEvent("fart");
    public static final Supplier<SoundEvent> BLOCK_POOP_BLOCK_SLIDE = registerSoundEvent("block.poop_block.slide");
    public static final Supplier<SoundEvent> BLOCK_POOLIME_POOP_BLOCK_SLIDE = registerSoundEvent("block.poolime_poop_block.slide");
    public static final Supplier<SoundEvent> BLOCK_COMPOOPER_MAGGOTS = registerSoundEvent("block.compooper.maggots");
    public static final Supplier<SoundEvent> ENTITY_VILLAGER_WORK_COMPOOPER = registerSoundEvent("entity.villager.work_compooper");
    public static final Supplier<SoundEvent> ENTITY_VILLAGER_WORK_TOILET = registerSoundEvent("entity.villager.work_toilet");
    public static final Supplier<SoundEvent> ENTITY_POOLIME_ATTACK = registerSoundEvent("entity.poolime.attack");
    public static final Supplier<SoundEvent> ENTITY_POOLIME_DEATH = registerSoundEvent("entity.poolime.death");
    public static final Supplier<SoundEvent> ENTITY_POOLIME_DEATH_SMALL = registerSoundEvent("entity.poolime.death_small");
    public static final Supplier<SoundEvent> ENTITY_POOLIME_HURT = registerSoundEvent("entity.poolime.hurt");
    public static final Supplier<SoundEvent> ENTITY_POOLIME_HURT_SMALL = registerSoundEvent("entity.poolime.hurt_small");
    public static final Supplier<SoundEvent> ENTITY_POOLIME_JUMP = registerSoundEvent("entity.poolime.jump");
    public static final Supplier<SoundEvent> ENTITY_POOLIME_JUMP_SMALL = registerSoundEvent("entity.poolime.jump_small");
    public static final Supplier<SoundEvent> ENTITY_POOLIME_SQUISH = registerSoundEvent("entity.poolime.squish");
    public static final Supplier<SoundEvent> ENTITY_POOLIME_SQUISH_SMALL = registerSoundEvent("entity.poolime.squish_small");
    public static final Supplier<SoundEvent> ENTITY_POP_PRIMED = registerSoundEvent("entity.pop.primed");
    public static final Supplier<SoundEvent> ENTITY_FLY_AMBIENT = registerSoundEvent("entity.fly.ambient");
    public static final Supplier<SoundEvent> ENTITY_FLY_HURT = registerSoundEvent("entity.fly.hurt");
    public static final Supplier<SoundEvent> ENTITY_FLY_DEATH = registerSoundEvent("entity.fly.death");
    public static final Supplier<SoundEvent> ENTITY_FLY_CAPTURE = registerSoundEvent("entity.fly.capture");

    public static final Supplier<SoundEvent> LAWRENCE = registerSoundEvent("lawrence");
    public static final ResourceKey<JukeboxSong> LAWRENCE_KEY = registerJukeboxSong("lawrence");
    public static final Supplier<SoundEvent> LIGHT_DANCE = registerSoundEvent("light_dance");
    public static final ResourceKey<JukeboxSong> LIGHT_DANCE_KEY = registerJukeboxSong("light_dance");
    public static final Supplier<SoundEvent> MOON_BOWL = registerSoundEvent("moon_bowl");
    public static final ResourceKey<JukeboxSong> MOON_BOWL_KEY = registerJukeboxSong("moon_bowl");

    public static final Supplier<SoundEvent> ITEM_TOILET_LINKER_BOOP = registerSoundEvent("item.toilet_linker.boop");
    public static final Supplier<SoundEvent> ITEM_TOILET_LINKER_SUCCESS = registerSoundEvent("item.toilet_linker.success");

    public static ResourceKey<JukeboxSong> registerJukeboxSong(String name) {
        ResourceLocation id = PoopSky.loc(name);
        return ResourceKey.create(Registries.JUKEBOX_SONG, id);
    }
    public static Supplier<SoundEvent> registerSoundEvent(String name) {
        ResourceLocation id = PoopSky.loc(name);
        return SOUND_EVENT.register(name, () -> SoundEvent.createVariableRangeEvent(id));
    }
    public static void register(IEventBus eventBus) {
        SOUND_EVENT.register(eventBus);
    }
}
