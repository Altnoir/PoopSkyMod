package com.altnoir.poopsky.init;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.inventory.BreedingBoxMenu;
import com.altnoir.poopsky.inventory.FlyNestMenu;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class PMenuTypes {
    public static final DeferredRegister<MenuType<?>> MENU_TYPES =
            DeferredRegister.create(BuiltInRegistries.MENU, PoopSky.MOD_ID);

    public static final DeferredHolder<MenuType<?>, MenuType<FlyNestMenu>> FLY_NEST = MENU_TYPES.register(
            "fly_nest", () -> new MenuType<>(FlyNestMenu::new, FeatureFlags.DEFAULT_FLAGS));

    public static final DeferredHolder<MenuType<?>, MenuType<BreedingBoxMenu>> BREEDING_BOX = MENU_TYPES.register(
            "breeding_box", () -> new MenuType<>(BreedingBoxMenu::new, FeatureFlags.DEFAULT_FLAGS));

    public static void register(IEventBus eventBus) {
        MENU_TYPES.register(eventBus);
    }
}
