package com.altnoir.poopsky.sound;

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

public class PSSoundEvents {
    public static final DeferredRegister<SoundEvent> SOUND_EVENT = DeferredRegister.create(BuiltInRegistries.SOUND_EVENT, PoopSky.MOD_ID);

    public static final Supplier<SoundEvent> FART = registerSoundEvent("fart");
    public static final Supplier<SoundEvent> ENTITY_VILLAGER_WORK_TOILET = registerSoundEvent("entity.villager.work_toilet");
    public static final Supplier<SoundEvent> LAWRENCE = registerSoundEvent("lawrence");
    public static final ResourceKey<JukeboxSong> LAWRENCE_KEY = registerJukeboxSong("lawrence");

    public static ResourceKey<JukeboxSong> registerJukeboxSong(String name) {
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(PoopSky.MOD_ID, name);
        return ResourceKey.create(Registries.JUKEBOX_SONG, id);
    }
    public static Supplier<SoundEvent> registerSoundEvent(String name) {
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(PoopSky.MOD_ID, name);
        return SOUND_EVENT.register(name, () -> SoundEvent.createVariableRangeEvent(id));
    }
    public static void register(IEventBus eventBus) {
        SOUND_EVENT.register(eventBus);
    }
}
