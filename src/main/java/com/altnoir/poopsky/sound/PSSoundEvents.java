package com.altnoir.poopsky.sound;

import com.altnoir.poopsky.PoopSky;
import net.minecraft.block.jukebox.JukeboxSong;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public class PSSoundEvents {
    public static final SoundEvent FART = registerSoundEvent("fart");
    public static final SoundEvent ENTITY_VILLAGER_WORK_TOILET = registerSoundEvent("entity.villager.work_toilet");
    public static final SoundEvent LAWRENCE = registerSoundEvent("lawrence");
    public static final RegistryKey<JukeboxSong> LAWRENCE_KEY = RegistryKey.of(RegistryKeys.JUKEBOX_SONG, Identifier.of(PoopSky.MOD_ID, "lawrence"));
    private static SoundEvent registerSoundEvent(String name) {
        Identifier id = Identifier.of(PoopSky.MOD_ID, name);
        return Registry.register(Registries.SOUND_EVENT, id, SoundEvent.of(id));
    }
    public static void registerSounds() {
        PoopSky.LOGGER.info("Registering sounds for " + PoopSky.MOD_ID);
    }
}
