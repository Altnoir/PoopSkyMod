package com.altnoir.poopsky.impl.registrate;

import com.tterrag.registrate.AbstractRegistrate;
import com.tterrag.registrate.builders.NoConfigBuilder;
import net.minecraft.world.item.CreativeModeTab;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;

import java.util.Optional;
import java.util.function.Consumer;

public class PoRegistrate extends AbstractRegistrate<PoRegistrate> {
    private static final org.apache.logging.log4j.Logger log = org.apache.logging.log4j.LogManager.getLogger(PoRegistrate.class);

    protected PoRegistrate(String modid) {
        super(modid);
    }

    public static PoRegistrate create(String modId) {
        var ret = new PoRegistrate(modId);
        Optional<IEventBus> modEventBus = ModList.get().getModContainerById(modId).map(ModContainer::getEventBus);
        modEventBus.ifPresentOrElse(ret::registerEventListeners, () -> {
            log.fatal("Failed to register eventListeners for mod {}", modId);
        });
        return ret;
    }

    public NoConfigBuilder<CreativeModeTab, CreativeModeTab, PoRegistrate> creativeTab(Consumer<CreativeModeTab.Builder> config) {
        return creativeTab(self(), config);
    }

    public NoConfigBuilder<CreativeModeTab, CreativeModeTab, PoRegistrate> creativeTab(String name) {
        return creativeTab(self(), name);
    }

    public <P> NoConfigBuilder<CreativeModeTab, CreativeModeTab, P> creativeTab(P parent, Consumer<CreativeModeTab.Builder> config) {
        return creativeTab(parent, currentName(), config);
    }

    public <P> NoConfigBuilder<CreativeModeTab, CreativeModeTab, P> creativeTab(P parent, String name) {
        return creativeTab(parent, name, tab -> {
        });
    }

    public <P> NoConfigBuilder<CreativeModeTab, CreativeModeTab, P> creativeTab(P parent, String name, Consumer<CreativeModeTab.Builder> config) {
//        return this.generic(parent, name, Registries.CREATIVE_MODE_TAB, () -> {
//            var builder = CreativeModeTab.builder()
//                    .icon(() -> getAll(Registries.ITEM).stream().findFirst().map(ItemEntry::cast).map(ItemEntry::asStack).orElse(new ItemStack(Items.AIR)))
//                    .title(this.addLang("itemGroup", PoopSky.asResource(name), PoopSky.NAME + " : " + RegistrateLangProvider.toEnglishName(name)));
//            config.accept(builder);
//            return builder.build();
        return null;
    }
}