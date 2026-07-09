package com.altnoir.poopsky.impl.registrate;

import com.tterrag.registrate.AbstractRegistrate;
import com.tterrag.registrate.builders.NoConfigBuilder;
import net.minecraft.world.item.CreativeModeTab;

import java.util.function.Consumer;

public class PoRegistrate extends AbstractRegistrate<PoRegistrate> {
    /**
     * Construct a new Registrate for the given mod ID.
     *
     * @param modid The mod ID for which objects will be registered
     */
    protected PoRegistrate(String modid) {
        super(modid);
    }

    public static PoRegistrate create(String modId) {
        return new PoRegistrate(modId);
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
