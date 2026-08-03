package com.altnoir.poopsky.mixin;

import com.altnoir.poopsky.impl.creative.PoSectionedCreativeModeTab;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Collection;

@Mixin(CreativeModeInventoryScreen.class)
public abstract class CreativeModeInventoryScreenMixin {
    @Redirect(
            method = {"selectTab", "tryRefreshInvalidatedTabs"},
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/item/CreativeModeTab;getDisplayItems()Ljava/util/Collection;"
            )
    )
    private Collection<ItemStack> poopsky$useSectionedLayout(CreativeModeTab tab) {
        if (tab instanceof PoSectionedCreativeModeTab sectionedTab) {
            return sectionedTab.layoutItems();
        }
        return tab.getDisplayItems();
    }
}
