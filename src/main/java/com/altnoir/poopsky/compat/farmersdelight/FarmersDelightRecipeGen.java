package com.altnoir.poopsky.compat.farmersdelight;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.init.PoItems;
import com.tterrag.registrate.providers.RegistrateRecipeProvider;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import vectorwing.farmersdelight.client.recipebook.CookingPotRecipeBookTab;
import vectorwing.farmersdelight.common.registry.ModItems;
import vectorwing.farmersdelight.common.tag.CommonTags;
import vectorwing.farmersdelight.data.builder.CookingPotRecipeBuilder;
import vectorwing.farmersdelight.data.builder.CuttingBoardRecipeBuilder;

public class FarmersDelightRecipeGen {

    public static void buildRecipes(RecipeOutput output) {
        CookingPotRecipeBuilder.cookingPotRecipe(PoItems.POOP_VEGETABLE_STICKS.get(), 1, 100, 0.35F)
                .addIngredient(CommonTags.Items.CROPS_CABBAGE)
                .addIngredient(PoItems.POOP.get())
                .setRecipeBookTab(CookingPotRecipeBookTab.MEALS)
                .unlockedBy(getItemName(PoItems.POOP.get()), has(PoItems.POOP.get()))
                .save(output, PoopSky.loc("cooking/poop_vegetable_sticks"));

        CookingPotRecipeBuilder.cookingPotRecipe(PoItems.POOP_SOUP.get(), 1, 200, 1.0F, Items.BOWL)
                .addIngredient(PoItems.POOP.get())
                .addIngredient(PoItems.MAGGOTS_SEEDS.get())
                .addIngredient(PoItems.URINE_BOTTLE.get())
                .setRecipeBookTab(CookingPotRecipeBookTab.MEALS)
                .unlockedBy(getItemName(PoItems.POOP.get()), has(PoItems.POOP.get()))
                .save(output, PoopSky.loc("cooking/poop_soup"));

        CookingPotRecipeBuilder.cookingPotRecipe(PoItems.POOP_DUMPLINGS.get(), 1, 80, 0.35F)
                .addIngredient(ItemTags.LEAVES)
                .addIngredient(PoItems.POOP_BALL.get())
                .setRecipeBookTab(CookingPotRecipeBookTab.MEALS)
                .unlockedBy("has_poop_ball", has(PoItems.POOP_BALL.get()))
                .save(output, PoopSky.loc("cooking/poop_dumplings"));

        CuttingBoardRecipeBuilder.cuttingRecipe(
                        Ingredient.of(PoItems.POOBURGER_MEAT.get()),
                        Ingredient.of(CommonTags.Items.TOOLS_KNIFE),
                        PoItems.POOP_PASTA.get(), 2)
                .unlockedBy(getItemName(PoItems.POOBURGER_MEAT.get()), has(PoItems.POOBURGER_MEAT.get()))
                .save(output, PoopSky.loc("cutting/poop_pasta"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, ModItems.ORGANIC_COMPOST.get())
                .requires(ItemTags.DIRT)
                .requires(PoItems.POOP.get())
                .requires(PoItems.POOP.get())
                .requires(PoItems.POOP.get())
                .requires(PoItems.POOP.get())
                .unlockedBy(getItemName(PoItems.POOP.get()), has(PoItems.POOP.get()))
                .save(output, PoopSky.loc("organic_compost_from_poop"));
    }

    public static String getItemName(ItemLike itemLike) {
        return RegistrateRecipeProvider.getItemName(itemLike);
    }

    private static Criterion<InventoryChangeTrigger.TriggerInstance> has(ItemLike itemLike) {
        return RegistrateRecipeProvider.has(itemLike);
    }
}
