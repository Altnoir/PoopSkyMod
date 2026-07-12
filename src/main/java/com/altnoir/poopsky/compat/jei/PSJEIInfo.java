package com.altnoir.poopsky.compat.jei;

import com.altnoir.poopsky.content.item.PFlyTypes;
import com.altnoir.poopsky.content.item.p.FlyItem;
import com.altnoir.poopsky.init.PoBlocks;
import com.altnoir.poopsky.init.PoItems;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.network.chat.Component;

public class PSJEIInfo {
    public static void register(IRecipeRegistration registration) {
        registration.addIngredientInfo(
                PoItems.FOLIUM_SENNAE.get(),
                Component.translatable("jei.poopsky.folium_senna")
        );
        registration.addIngredientInfo(
                PoItems.SAPLING_POOP_BALL.get(),
                Component.translatable("jei.poopsky.sapling_poop_ball")
        );
        registration.addIngredientInfo(
                PoItems.SEA_POOP_BALL.get(),
                Component.translatable("jei.poopsky.sea_poop_ball")
        );
        registration.addIngredientInfo(
                PoItems.MAGGOTS_SEEDS.get(),
                Component.translatable("jei.poopsky.maggots_seeds")
        );
        registration.addIngredientInfo(
                FlyItem.withType(PFlyTypes.WHITE.get()),
                VanillaTypes.ITEM_STACK,
                Component.translatable("jei.poopsky.fly_desc.white")
        );
        registration.addIngredientInfo(
                FlyItem.withType(PFlyTypes.BLACK.get()),
                VanillaTypes.ITEM_STACK,
                Component.translatable("jei.poopsky.fly_desc.black")
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
        registration.addIngredientInfo(
                FlyItem.withType(PFlyTypes.DRAGON_FRUIT.get()),
                VanillaTypes.ITEM_STACK,
                Component.translatable("jei.poopsky.fly_desc.dragon_fruit")
        );
        registration.addIngredientInfo(
                PoBlocks.BREEDING_CHEST.get(),
                Component.translatable("jei.poopsky.breeding_chest_desc")
        );
    }
}