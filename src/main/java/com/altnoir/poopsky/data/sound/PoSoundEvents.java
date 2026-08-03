package com.altnoir.poopsky.data.sound;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.impl.registrate.PoRegistrate;
import com.tterrag.registrate.util.entry.RegistryEntry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.JukeboxSong;

public class PoSoundEvents {
    private static final PoRegistrate REGISTRATE = PoopSky.registrate();

    public static final RegistryEntry<SoundEvent, SoundEvent> FART = registerSoundEvent("fart");
    public static final RegistryEntry<SoundEvent, SoundEvent> BLOCK_POOP_BLOCK_SLIDE = registerSoundEvent("block.poop_block.slide");
    public static final RegistryEntry<SoundEvent, SoundEvent> BLOCK_SALTPETER_CHIME = registerSoundEvent("block.saltpeter.chime");
    public static final RegistryEntry<SoundEvent, SoundEvent> BLOCK_COMPOOPER_MAGGOTS = registerSoundEvent("block.compooper.maggots");
    public static final RegistryEntry<SoundEvent, SoundEvent> BLOCK_COMPOOPER_BUCKET_FILL = registerSoundEvent("block.compooper.bucket_fill");
    public static final RegistryEntry<SoundEvent, SoundEvent> BLOCK_COMPOOPER_BUCKET_FILL_LAVA = registerSoundEvent("block.compooper.bucket_fill_lava");
    public static final RegistryEntry<SoundEvent, SoundEvent> BLOCK_FLY_BARREL_OPEN = registerSoundEvent("block.fly_barrel.open");
    public static final RegistryEntry<SoundEvent, SoundEvent> BLOCK_FLY_BARREL_CLOSE = registerSoundEvent("block.fly_barrel.close");
    public static final RegistryEntry<SoundEvent, SoundEvent> BLOCK_FLY_BARREL_WORK = registerSoundEvent("block.fly_barrel.work");
    public static final RegistryEntry<SoundEvent, SoundEvent> BLOCK_FLUSH_TOILET_OPEN = registerSoundEvent("block.flush_toilet.open");
    public static final RegistryEntry<SoundEvent, SoundEvent> BLOCK_FLUSH_TOILET_CLOSE = registerSoundEvent("block.flush_toilet.close");
    public static final RegistryEntry<SoundEvent, SoundEvent> BLOCK_BREEDING_CHEST_WORK = registerSoundEvent("block.breeding_chest.work");
    public static final RegistryEntry<SoundEvent, SoundEvent> BLOCK_TOILET_LAVA_EMPTY = registerSoundEvent("block.toilet.lava_empty");
    public static final RegistryEntry<SoundEvent, SoundEvent> ENTITY_VILLAGER_WORK_COMPOOPER = registerSoundEvent("entity.villager.work_compooper");
    public static final RegistryEntry<SoundEvent, SoundEvent> ENTITY_VILLAGER_WORK_TOILET = registerSoundEvent("entity.villager.work_toilet");
    public static final RegistryEntry<SoundEvent, SoundEvent> ENTITY_POOLIME_ATTACK = registerSoundEvent("entity.poolime.attack");
    public static final RegistryEntry<SoundEvent, SoundEvent> ENTITY_POOLIME_DEATH = registerSoundEvent("entity.poolime.death");
    public static final RegistryEntry<SoundEvent, SoundEvent> ENTITY_POOLIME_DEATH_SMALL = registerSoundEvent("entity.poolime.death_small");
    public static final RegistryEntry<SoundEvent, SoundEvent> ENTITY_POOLIME_HURT = registerSoundEvent("entity.poolime.hurt");
    public static final RegistryEntry<SoundEvent, SoundEvent> ENTITY_POOLIME_HURT_SMALL = registerSoundEvent("entity.poolime.hurt_small");
    public static final RegistryEntry<SoundEvent, SoundEvent> ENTITY_POOLIME_JUMP = registerSoundEvent("entity.poolime.jump");
    public static final RegistryEntry<SoundEvent, SoundEvent> ENTITY_POOLIME_JUMP_SMALL = registerSoundEvent("entity.poolime.jump_small");
    public static final RegistryEntry<SoundEvent, SoundEvent> ENTITY_POOLIME_SQUISH = registerSoundEvent("entity.poolime.squish");
    public static final RegistryEntry<SoundEvent, SoundEvent> ENTITY_POOLIME_SQUISH_SMALL = registerSoundEvent("entity.poolime.squish_small");
    public static final RegistryEntry<SoundEvent, SoundEvent> ENTITY_POOP_BALL_THROW = registerSoundEvent("entity.poop_ball.throw");
    public static final RegistryEntry<SoundEvent, SoundEvent> ENTITY_POP_PRIMED = registerSoundEvent("entity.pop.primed");
    public static final RegistryEntry<SoundEvent, SoundEvent> ENTITY_FLY_AMBIENT = registerSoundEvent("entity.fly.ambient");
    public static final RegistryEntry<SoundEvent, SoundEvent> ENTITY_FLY_HURT = registerSoundEvent("entity.fly.hurt");
    public static final RegistryEntry<SoundEvent, SoundEvent> ENTITY_FLY_DEATH = registerSoundEvent("entity.fly.death");
    public static final RegistryEntry<SoundEvent, SoundEvent> ENTITY_FLY_CAPTURE = registerSoundEvent("entity.fly.capture");
    public static final RegistryEntry<SoundEvent, SoundEvent> POOPSKY_INTRO = registerSoundEvent("poopsky_intro");
    public static final RegistryEntry<SoundEvent, SoundEvent> LAWRENCE = registerSoundEvent("lawrence");
    public static final ResourceKey<JukeboxSong> LAWRENCE_KEY = registerJukeboxSong("lawrence");
    public static final RegistryEntry<SoundEvent, SoundEvent> LIGHT_DANCE = registerSoundEvent("light_dance");
    public static final ResourceKey<JukeboxSong> LIGHT_DANCE_KEY = registerJukeboxSong("light_dance");
    public static final RegistryEntry<SoundEvent, SoundEvent> MOON_BOWL = registerSoundEvent("moon_bowl");
    public static final ResourceKey<JukeboxSong> MOON_BOWL_KEY = registerJukeboxSong("moon_bowl");

    public static final RegistryEntry<SoundEvent, SoundEvent> ITEM_TOILET_LINKER_BOOP = registerSoundEvent("item.toilet_linker.boop");
    public static final RegistryEntry<SoundEvent, SoundEvent> ITEM_TOILET_LINKER_SUCCESS = registerSoundEvent("item.toilet_linker.success");
    public static final RegistryEntry<SoundEvent, SoundEvent> ITEM_JINKELA_USE = registerSoundEvent("item.jinkela.use");
    public static final RegistryEntry<SoundEvent, SoundEvent> ITEM_TIME_BELL_OPEN = registerSoundEvent("item.time_bell.open");
    public static final RegistryEntry<SoundEvent, SoundEvent> ITEM_TIME_BELL_CLOSE = registerSoundEvent("item.time_bell.close");

    private static RegistryEntry<SoundEvent, SoundEvent> registerSoundEvent(String name) {
        ResourceLocation id = PoopSky.loc(name);
        return REGISTRATE.simple(name, Registries.SOUND_EVENT, () -> SoundEvent.createVariableRangeEvent(id));
    }

    public static ResourceKey<JukeboxSong> registerJukeboxSong(String name) {
        ResourceLocation id = PoopSky.loc(name);
        return ResourceKey.create(Registries.JUKEBOX_SONG, id);
    }

    public static void register() {
    }
}
