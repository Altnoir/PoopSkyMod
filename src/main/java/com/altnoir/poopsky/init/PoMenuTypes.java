package com.altnoir.poopsky.init;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.client.inventory.*;
import com.altnoir.poopsky.impl.registrate.PoRegistrate;
import com.tterrag.registrate.builders.MenuBuilder;
import com.tterrag.registrate.util.entry.MenuEntry;

public final class PoMenuTypes {
    private static final PoRegistrate REGISTRATE = PoopSky.registrate();

    public static final MenuEntry<FlyBarrelMenu> FLY_BARREL = REGISTRATE.menu("fly_barrel",
            (MenuBuilder.MenuFactory<FlyBarrelMenu>) FlyBarrelMenu::new, () -> FlyBarrelScreen::new).register();

    public static final MenuEntry<BreedingChestMenu> BREEDING_CHEST = REGISTRATE.menu("breeding_chest",
            (MenuBuilder.MenuFactory<BreedingChestMenu>) BreedingChestMenu::new, () -> BreedingChestScreen::new).register();

    public static final MenuEntry<PortableToiletMenu> PORTABLE_TOILET = REGISTRATE.menu("portable_toilet",
            PortableToiletMenu::new, () -> PortableToiletScreen::new).register();

    public static final MenuEntry<FlushToiletMenu> FLUSH_TOILET = REGISTRATE.menu("flush_toilet",
            (MenuBuilder.MenuFactory<FlushToiletMenu>) FlushToiletMenu::new, () -> FlushToiletScreen::new).register();

    private PoMenuTypes() {
    }

    public static void register() {
    }
}
