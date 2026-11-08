package com.altnoir.poopsky.mixin;

import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.world.item.CreativeModeTab;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(CreativeModeInventoryScreen.class)
public interface CreativeScreenAccessor {
    @Accessor("selectedTab") static CreativeModeTab poopsky$getSelectedTab() { throw new AssertionError(); }
    @Accessor("scrollOffs") float poopsky$getScrollOffs();
}
