package com.altnoir.poopsky.impl.registrate;

import com.altnoir.poopsky.PoopSky;
import com.tterrag.registrate.AbstractRegistrate;
import com.tterrag.registrate.util.entry.RegistryEntry;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.effect.MobEffect;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;

import java.util.Optional;
import java.util.function.Supplier;

public class PoRegistrate extends AbstractRegistrate<PoRegistrate> {
    protected PoRegistrate(String modid) {
        super(modid);
    }

    public static PoRegistrate create(String modId) {
        var ret = new PoRegistrate(modId);
        Optional<IEventBus> modEventBus = ModList.get().getModContainerById(modId).map(ModContainer::getEventBus);
        modEventBus.ifPresentOrElse(ret::registerEventListeners, () -> {
            PoopSky.LOGGER.error("Failed to register eventListeners for mod {}", modId);
        });
        return ret;
    }
}
