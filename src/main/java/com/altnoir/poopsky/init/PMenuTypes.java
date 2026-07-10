package com.altnoir.poopsky.init;

import com.altnoir.poopsky.client.inventory.BreedingChestMenu;
import com.altnoir.poopsky.client.inventory.BreedingChestScreen;
import com.altnoir.poopsky.client.inventory.FlyBarrelMenu;
import com.altnoir.poopsky.client.inventory.FlyBarrelScreen;
import com.tterrag.registrate.builders.MenuBuilder;
import com.tterrag.registrate.util.entry.MenuEntry;
import net.neoforged.bus.api.IEventBus;

public final class PMenuTypes {
    public static final MenuEntry<FlyBarrelMenu> FLY_BARREL = PRegistries.REGISTRATE
            .menu("fly_barrel", (MenuBuilder.MenuFactory<FlyBarrelMenu>) FlyBarrelMenu::new, () -> FlyBarrelScreen::new)
            .register();

    public static final MenuEntry<BreedingChestMenu> BREEDING_CHEST = PRegistries.REGISTRATE
            .menu("breeding_chest", (MenuBuilder.MenuFactory<BreedingChestMenu>) BreedingChestMenu::new, () -> BreedingChestScreen::new)
            .register();

    private PMenuTypes() {
    }

    public static void register(IEventBus eventBus) {
    }
}
