package com.altnoir.poopsky.compat.jei;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.block.PSBlocks;
import com.altnoir.poopsky.item.PSItems;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.List;

@JeiPlugin
public class CompooperJEIPlugin implements IModPlugin {
    @Override
    public ResourceLocation getPluginUid() {
        return ResourceLocation.fromNamespaceAndPath(PoopSky.MOD_ID, "jei_plugin");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        IGuiHelper guiHelper = registration.getJeiHelpers().getGuiHelper();
        registration.addRecipeCategories(new CompooperRecipeCategory(
                guiHelper.createDrawableItemStack(new ItemStack(PSBlocks.COMPOOPER.get()))
        ));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        registration.addRecipes(CompooperRecipeCategory.TYPE, List.of(
                new CompooperRecipe(
                        Ingredient.of(Items.AIR), new ItemStack(PSItems.MAGGOTS_SEEDS.get()), new ItemStack(PSItems.POOP_BUCKET.get())
                ),
                new CompooperRecipe(
                        Ingredient.of(Items.STICK), new ItemStack(Items.BLAZE_ROD), new ItemStack(Items.LAVA_BUCKET)
                ),
                new CompooperRecipe(
                        Ingredient.of(Items.STICK), new ItemStack(Items.BREEZE_ROD), new ItemStack(Items.POWDER_SNOW_BUCKET)
                )
        ));
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(PSBlocks.COMPOOPER.get()), CompooperRecipeCategory.TYPE);
    }
}
