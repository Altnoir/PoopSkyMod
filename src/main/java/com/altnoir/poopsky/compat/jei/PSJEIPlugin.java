package com.altnoir.poopsky.compat.jei;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.block.PSBlocks;
import com.altnoir.poopsky.item.PSItems;
import com.altnoir.poopsky.recipe.PSRecipes;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.List;
import java.util.stream.Stream;

@JeiPlugin
public class PSJEIPlugin implements IModPlugin {
    private static final ResourceLocation PS_JEI_TEXTURE = PoopSky.loc("textures/gui/jei/enr_jei.png");

    @Override
    public ResourceLocation getPluginUid() {
        return PoopSky.loc("jei_plugin");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        var helper = registration.getJeiHelpers().getGuiHelper();
        var arrow = helper.createDrawable(PS_JEI_TEXTURE, 0, 18, 22, 15);
        var plus = helper.createDrawable(PS_JEI_TEXTURE, 22, 18, 8, 8);

        registration.addRecipeCategories(
                new CompooperRecipeCategory(registration.getJeiHelpers(), arrow),
                new SieveRecipeCategory(registration.getJeiHelpers(), arrow));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        registration.addRecipes(CompooperRecipeCategory.TYPE, List.of(
                new CompooperRecipe(
                        Ingredient.of(Stream.empty()), new ItemStack(PSItems.MAGGOTS_SEEDS.get()), PSBlocks.URINE_COMPOOPER.get().defaultBlockState()),
                new CompooperRecipe(
                        Ingredient.of(Items.STICK), new ItemStack(Items.BLAZE_ROD), PSBlocks.LAVA_COMPOOPER.get().defaultBlockState()),
                new CompooperRecipe(
                        Ingredient.of(Items.STICK), new ItemStack(Items.BREEZE_ROD), PSBlocks.POWER_SNOW_COMPOOPER.get().defaultBlockState())
        ));
        var level = net.minecraft.client.Minecraft.getInstance().level;
        if (level != null) {
            registration.addRecipes(SieveRecipeCategory.TYPE,
                    level.getRecipeManager().getAllRecipesFor(PSRecipes.SIEVE_TYPE.get()).stream()
                            .map(net.minecraft.world.item.crafting.RecipeHolder::value)
                            .toList());
        }
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(PSBlocks.COMPOOPER.get()), CompooperRecipeCategory.TYPE);
        registration.addRecipeCatalyst(new ItemStack(PSBlocks.SIEVE.get()), SieveRecipeCategory.TYPE);
    }
}
