package com.altnoir.poopsky.compat.jei;

import com.altnoir.poopsky.PTags;
import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.block.PBlocks;
import com.altnoir.poopsky.block.p.CompooperBlock;
import com.altnoir.poopsky.compat.PSMods;
import com.altnoir.poopsky.compat.jei.create.FanDigestingCategory;
import com.altnoir.poopsky.init.PRecipes;
import com.altnoir.poopsky.item.PItems;
import com.simibubi.create.compat.jei.category.CreateRecipeCategory;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@JeiPlugin
public class PSJEIPlugin implements IModPlugin {
    private static final ResourceLocation PS_JEI_TEXTURE = PoopSky.loc("textures/gui/jei/enr_jei.png");
    private final List<CreateRecipeCategory<?>> createCategories = new ArrayList<>();

    @Override
    public ResourceLocation getPluginUid() {
        return PoopSky.loc("jei_plugin");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        var helper = registration.getJeiHelpers().getGuiHelper();
        var arrow = helper.createDrawable(PS_JEI_TEXTURE, 0, 18, 22, 15);

        registration.addRecipeCategories(
                new CompooperRecipeCategory(registration.getJeiHelpers(), arrow),
                new SieveRecipeCategory(registration.getJeiHelpers(), arrow));

        if (PSMods.CREATE.isLoaded()) {
            createCategories.clear();
            createCategories.add(FanDigestingCategory.create());
            registration.addRecipeCategories(createCategories.toArray(IRecipeCategory[]::new));
        }
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        registration.addRecipes(CompooperRecipeCategory.TYPE, List.of(
                new CompooperRecipe(
                        Ingredient.of(Stream.empty()), new ItemStack(PItems.MAGGOTS_SEEDS.get()), PBlocks.URINE_COMPOOPER.get().defaultBlockState()
                ),
                new CompooperRecipe(
                        Ingredient.of(PTags.Items.CAN_COMPOSTABLE), new ItemStack(PItems.SAPLING_POOP_BALL.get()),
                        PBlocks.COMPOOPER.get().defaultBlockState().setValue(CompooperBlock.POOP_LEVEL, CompooperBlock.READY)
                ),
                new CompooperRecipe(
                        Ingredient.of(Items.STICK), new ItemStack(Items.BLAZE_ROD), PBlocks.LAVA_COMPOOPER.get().defaultBlockState()
                ),
                new CompooperRecipe(
                        Ingredient.of(Items.STICK), new ItemStack(Items.BREEZE_ROD), PBlocks.POWDER_SNOW_COMPOOPER.get().defaultBlockState()
                )
        ));
        var level = net.minecraft.client.Minecraft.getInstance().level;
        if (level != null) {
            registration.addRecipes(SieveRecipeCategory.TYPE, level.getRecipeManager().getAllRecipesFor(PRecipes.SIEVE_TYPE.get()));
        }

        if (PSMods.CREATE.isLoaded()) {
            createCategories.forEach(category -> category.registerRecipes(registration));
        }
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(PBlocks.COMPOOPER.get()), CompooperRecipeCategory.TYPE);
        registration.addRecipeCatalyst(new ItemStack(PBlocks.SIEVE.get()), SieveRecipeCategory.TYPE);

        if (PSMods.CREATE.isLoaded()) {
            createCategories.forEach(category -> category.registerCatalysts(registration));
        }
    }
}