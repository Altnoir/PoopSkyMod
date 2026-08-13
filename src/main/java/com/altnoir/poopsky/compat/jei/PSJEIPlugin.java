package com.altnoir.poopsky.compat.jei;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.content.ToiletType;
import com.altnoir.poopsky.content.recipe.ToiletShapedRecipe;
import com.altnoir.poopsky.init.PoBlocks;
import com.altnoir.poopsky.init.PoComponents;
import com.altnoir.poopsky.init.PoItems;
import com.altnoir.poopsky.init.PoRecipes;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.ingredients.subtypes.ISubtypeInterpreter;
import mezz.jei.api.registration.*;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

//import com.altnoir.poopsky.compat.PoMods;
//import com.altnoir.poopsky.compat.jei.create.FanDigestingCategory;
//import com.simibubi.create.compat.jei.category.CreateRecipeCategory;
//import mezz.jei.api.recipe.category.IRecipeCategory;

//import java.util.ArrayList;
//import java.util.List;

@JeiPlugin
public class PSJEIPlugin implements IModPlugin {
    private static final Identifier PS_JEI_TEXTURE = PoopSky.loc("textures/gui/jei/enr_jei.png");
//    private final List<CreateRecipeCategory<?>> createCategories = new ArrayList<>();

    @Override
    public @NotNull Identifier getPluginUid() {
        return PoopSky.loc("jei_plugin");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        var helper = registration.getJeiHelpers().getGuiHelper();
        var arrow = helper.createDrawable(PS_JEI_TEXTURE, 0, 18, 22, 15);
        var arrow2 = helper.createDrawable(PS_JEI_TEXTURE, 0, 33, 22, 15);
        var plus = helper.createDrawable(PS_JEI_TEXTURE, 22, 18, 13, 13);

        registration.addRecipeCategories(
                new CompooperRecipeCategory(registration.getJeiHelpers(), arrow),
                new SieveRecipeCategory(registration.getJeiHelpers(), arrow),
                new FlyBarrelRecipeCategory(registration.getJeiHelpers(), arrow),
                new BreedingChestRecipeCategory(registration.getJeiHelpers(), arrow),
                new POPExplosionRecipeCategory(registration.getJeiHelpers(), arrow,arrow2),
                new AnalPressingRecipeCategory(registration.getJeiHelpers(), arrow, plus));

/*        if (PoMods.CREATE.isLoaded()) {
            createCategories.clear();
            createCategories.add(FanDigestingCategory.create());
            registration.addRecipeCategories(createCategories.toArray(IRecipeCategory[]::new));
        }*/
    }

    @Override
    public void registerItemSubtypes(ISubtypeRegistration registration) {
        registration.registerSubtypeInterpreter(PoItems.FLY.get(), (itemStack, context) -> itemStack.get(PoComponents.FLY_TYPE.get()));

        ISubtypeInterpreter<ItemStack> toiletSubtypeInterpreter = (itemStack, context) -> {
            ToiletType toiletType = itemStack.get(PoComponents.TOILET_TYPE.get());
            return toiletType != null ? toiletType.id() : null;
        };
        registration.registerSubtypeInterpreter(PoBlocks.WOODEN_TOILET.asItem(), toiletSubtypeInterpreter);
        registration.registerSubtypeInterpreter(PoBlocks.HARD_TOILET.asItem(), toiletSubtypeInterpreter);
    }

    @Override
    public void registerVanillaCategoryExtensions(IVanillaCategoryExtensionRegistration registration) {
        registration.getCraftingCategory().addExtension(ToiletShapedRecipe.class, new ToiletCraftingExtension());
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        registration.addRecipes(CompooperRecipeCategory.TYPE, PSJeiRecipeCache.getAll(PoRecipes.COMPOOPER.type().get()));
        registration.addRecipes(SieveRecipeCategory.TYPE, PSJeiRecipeCache.getAll(PoRecipes.SIEVE.type().get()));
        registration.addRecipes(POPExplosionRecipeCategory.TYPE, PSJeiRecipeCache.getAll(PoRecipes.POP_EXPLOSION.type().get()));
        registration.addRecipes(AnalPressingRecipeCategory.TYPE, PSJeiRecipeCache.getAll(PoRecipes.ANAL_PRESSING.type().get()));
        registration.addRecipes(FlyBarrelRecipeCategory.TYPE, PSJeiRecipeCache.getAll(PoRecipes.FLY_BARREL.type().get()));
        registration.addRecipes(BreedingChestRecipeCategory.TYPE, PSJeiRecipeCache.getAll(PoRecipes.BREEDING_CHEST.type().get()));

        PSJEIInfo.register(registration);

//        if (PoMods.CREATE.isLoaded()) {
//            createCategories.forEach(category -> category.registerRecipes(registration));
//        }
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addCraftingStation(CompooperRecipeCategory.TYPE,
                PoBlocks.COMPOOPER.get(), PoBlocks.URINE_COMPOOPER.get(), PoBlocks.WATER_COMPOOPER.get(),
                PoBlocks.POWDER_SNOW_COMPOOPER.get(), PoBlocks.LAVA_COMPOOPER.get());
        registration.addCraftingStation(SieveRecipeCategory.TYPE, PoBlocks.SIEVE.get());
        registration.addCraftingStation(FlyBarrelRecipeCategory.TYPE, PoBlocks.FLY_BARREL.get());
        registration.addCraftingStation(BreedingChestRecipeCategory.TYPE, PoBlocks.BREEDING_CHEST.get());
        registration.addCraftingStation(POPExplosionRecipeCategory.TYPE, PoBlocks.POOP_TNT.get());
        registration.addCraftingStation(AnalPressingRecipeCategory.TYPE,
                PoItems.KING_OF_DRAGON_FRUIT.asStack(),
                PoItems.DRAGON_BREATH_CHILI.asStack());

//        if (PoMods.CREATE.isLoaded()) {
//            createCategories.forEach(category -> category.registerCatalysts(registration));
//        }
    }
}
