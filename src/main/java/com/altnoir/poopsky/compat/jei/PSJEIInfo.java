package com.altnoir.poopsky.compat.jei;

import com.altnoir.poopsky.item.PFlyTypes;
import com.altnoir.poopsky.item.p.FlyItem;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.network.chat.Component;

public class PSJEIInfo {
    public static void register(IRecipeRegistration registration) {
        registration.addIngredientInfo(
                FlyItem.withType(PFlyTypes.WHITE.get()),
                VanillaTypes.ITEM_STACK,
                Component.translatable("jei.poopsky.fly_desc.white")
        );
        registration.addIngredientInfo(
                FlyItem.withType(PFlyTypes.NORMAL.get()),
                VanillaTypes.ITEM_STACK,
                Component.translatable("jei.poopsky.fly_desc.normal")
        );
        registration.addIngredientInfo(
                FlyItem.withType(PFlyTypes.RED.get()),
                VanillaTypes.ITEM_STACK,
                Component.translatable("jei.poopsky.fly_desc.red")
        );
        registration.addIngredientInfo(
                FlyItem.withType(PFlyTypes.GREEN.get()),
                VanillaTypes.ITEM_STACK,
                Component.translatable("jei.poopsky.fly_desc.green")
        );
        registration.addIngredientInfo(
                FlyItem.withType(PFlyTypes.BLUE.get()),
                VanillaTypes.ITEM_STACK,
                Component.translatable("jei.poopsky.fly_desc.blue")
        );
    }
}