package com.altnoir.poopsky.compat.jei;

import com.altnoir.poopsky.content.item.p.FlyItem;
import com.altnoir.poopsky.init.FlyTypes;
import com.altnoir.poopsky.init.PoBlocks;
import com.altnoir.poopsky.init.PoItems;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Items;

public class PSJEIInfo {
    public static void register(IRecipeRegistration registration) {
        registration.addIngredientInfo(
                PoItems.FOLIUM_SENNAE.asStack(),
                VanillaTypes.ITEM_STACK,
                Component.translatable("jei.poopsky.folium_senna")
        );
        registration.addIngredientInfo(
                PoItems.SAPLING_POOP_BALL.asStack(),
                VanillaTypes.ITEM_STACK,
                Component.translatable("jei.poopsky.sapling_poop_ball")
        );
        registration.addIngredientInfo(
                PoItems.SEA_POOP_BALL.asStack(),
                VanillaTypes.ITEM_STACK,
                Component.translatable("jei.poopsky.sea_poop_ball")
        );
        registration.addIngredientInfo(
                PoItems.MAGGOTS_SEEDS.asStack(),
                VanillaTypes.ITEM_STACK,
                Component.translatable("jei.poopsky.maggots_seeds")
        );
        registration.addIngredientInfo(
                Items.CACTUS.getDefaultInstance(),
                VanillaTypes.ITEM_STACK,
                Component.translatable("jei.poopsky.cactus")
        );
        registration.addIngredientInfo(
                Items.SUGAR_CANE.getDefaultInstance(),
                VanillaTypes.ITEM_STACK,
                Component.translatable("jei.poopsky.sugar_cane")
        );
        registration.addIngredientInfo(
                FlyItem.withType(FlyTypes.WHITE.get()),
                VanillaTypes.ITEM_STACK,
                Component.translatable("jei.poopsky.fly_desc.white")
        );
        registration.addIngredientInfo(
                FlyItem.withType(FlyTypes.BLACK.get()),
                VanillaTypes.ITEM_STACK,
                Component.translatable("jei.poopsky.fly_desc.black")
        );
        registration.addIngredientInfo(
                FlyItem.withType(FlyTypes.RED.get()),
                VanillaTypes.ITEM_STACK,
                Component.translatable("jei.poopsky.fly_desc.red")
        );
        registration.addIngredientInfo(
                FlyItem.withType(FlyTypes.GREEN.get()),
                VanillaTypes.ITEM_STACK,
                Component.translatable("jei.poopsky.fly_desc.green")
        );
        registration.addIngredientInfo(
                FlyItem.withType(FlyTypes.BLUE.get()),
                VanillaTypes.ITEM_STACK,
                Component.translatable("jei.poopsky.fly_desc.blue")
        );
        registration.addIngredientInfo(
                FlyItem.withType(FlyTypes.DRAGON_FRUIT.get()),
                VanillaTypes.ITEM_STACK,
                Component.translatable("jei.poopsky.fly_desc.dragon_fruit")
        );
        registration.addIngredientInfo(
                PoBlocks.POOP_FARMLAND.asStack(),
                VanillaTypes.ITEM_STACK,
                Component.translatable("jei.poopsky.poop_farmland")
        );
        registration.addIngredientInfo(
                PoBlocks.BREEDING_CHEST.asStack(),
                VanillaTypes.ITEM_STACK,
                Component.translatable("jei.poopsky.breeding_chest_desc")
        );
        registration.addIngredientInfo(
                PoItems.UREA.asStack(),
                VanillaTypes.ITEM_STACK,
                Component.translatable("jei.poopsky.urea")
        );
        registration.addIngredientInfo(
                PoItems.SALTPETER_SHARD.asStack(),
                VanillaTypes.ITEM_STACK,
                Component.translatable("jei.poopsky.saltpeter_shard")
        );
        registration.addIngredientInfo(
                PoBlocks.SALTPETER_CLUSTER.asStack(),
                VanillaTypes.ITEM_STACK,
                Component.translatable("jei.poopsky.saltpeter_cluster")
        );
    }
}
