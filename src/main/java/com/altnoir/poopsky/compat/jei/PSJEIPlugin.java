package com.altnoir.poopsky.compat.jei;

import com.altnoir.poopsky.impl.PoTags;
import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.content.ToiletType;
import com.altnoir.poopsky.content.block.p.CompooperBlock;
import com.altnoir.poopsky.init.FlyTypes;
import com.altnoir.poopsky.content.item.p.FlyItem;
import com.altnoir.poopsky.content.recipe.ToiletShapedRecipe;
import com.altnoir.poopsky.compat.PoMods;
import com.altnoir.poopsky.compat.jei.create.FanDigestingCategory;
import com.altnoir.poopsky.init.PoBlocks;
import com.altnoir.poopsky.init.PoComponents;
import com.altnoir.poopsky.init.PoItems;
import com.altnoir.poopsky.init.PoRecipes;
import com.simibubi.create.compat.jei.category.CreateRecipeCategory;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.ingredients.subtypes.ISubtypeInterpreter;
import mezz.jei.api.ingredients.subtypes.UidContext;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.registration.*;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

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
        var plus = helper.createDrawable(PS_JEI_TEXTURE, 22, 18, 13, 13);

        registration.addRecipeCategories(
                new CompooperRecipeCategory(registration.getJeiHelpers(), arrow),
                new SieveRecipeCategory(registration.getJeiHelpers(), arrow),
                new FlyBarrelRecipeCategory(registration.getJeiHelpers(), arrow),
                new BreedingChestRecipeCategory(registration.getJeiHelpers(), arrow),
                new POPExplosionRecipeCategory(registration.getJeiHelpers(), arrow),
                new AnalPressingRecipeCategory(registration.getJeiHelpers(), arrow, plus));

        if (PoMods.CREATE.isLoaded()) {
            createCategories.clear();
            createCategories.add(FanDigestingCategory.create());
            registration.addRecipeCategories(createCategories.toArray(IRecipeCategory[]::new));
        }
    }

    @Override
    public void registerItemSubtypes(ISubtypeRegistration registration) {
        registration.registerSubtypeInterpreter(PoItems.FLY.get(), new ISubtypeInterpreter<>() {
            @Override
            public Object getSubtypeData(ItemStack itemStack, UidContext context) {
                return itemStack.get(PoComponents.FLY_TYPE.get());
            }

            @Override
            public @NotNull String getLegacyStringSubtypeInfo(ItemStack itemStack, UidContext context) {
                String flyType = itemStack.get(PoComponents.FLY_TYPE.get());
                return flyType != null ? flyType : "";
            }
        });

        ISubtypeInterpreter<ItemStack> toiletSubtypeInterpreter = new ISubtypeInterpreter<>() {
            @Override
            public Object getSubtypeData(ItemStack itemStack, UidContext context) {
                ToiletType toiletType = itemStack.get(PoComponents.TOILET_TYPE.get());
                return toiletType != null ? toiletType.id() : null;
            }

            @Override
            public @NotNull String getLegacyStringSubtypeInfo(ItemStack itemStack, UidContext context) {
                ToiletType toiletType = itemStack.get(PoComponents.TOILET_TYPE.get());
                return toiletType != null ? toiletType.id() : "";
            }
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
        registration.addRecipes(CompooperRecipeCategory.TYPE, List.of(
                new CompooperRecipe(
                        Ingredient.of(Stream.empty()), new ItemStack(PoItems.MAGGOTS_SEEDS.get()), PoBlocks.URINE_COMPOOPER.get().defaultBlockState()
                ),
                new CompooperRecipe(
                        Ingredient.of(PoTags.Items.CAN_COMPOSTABLE), new ItemStack(PoItems.SAPLING_POOP_BALL.get()),
                        PoBlocks.COMPOOPER.get().defaultBlockState().setValue(CompooperBlock.POOP_LEVEL, CompooperBlock.READY)
                ),
                new CompooperRecipe(
                        Ingredient.of(Items.STICK), new ItemStack(Items.BLAZE_ROD), PoBlocks.LAVA_COMPOOPER.get().defaultBlockState()
                ),
                new CompooperRecipe(
                        Ingredient.of(Items.STICK), new ItemStack(Items.BREEZE_ROD), PoBlocks.POWDER_SNOW_COMPOOPER.get().defaultBlockState()
                ),
                new CompooperRecipe(
                        Ingredient.of(PoItems.FLY.get()), FlyItem.withType(FlyTypes.BLUE.get()), PoBlocks.WATER_COMPOOPER.get().defaultBlockState()
                )
        ));

        Level level = Minecraft.getInstance().level;
        RecipeManager recipeManager = level.getRecipeManager();

        registration.addRecipes(SieveRecipeCategory.TYPE, recipeManager.getAllRecipesFor(PoRecipes.SIEVE.type().get()));
        registration.addRecipes(POPExplosionRecipeCategory.TYPE, recipeManager.getAllRecipesFor(PoRecipes.POP_EXPLOSION.type().get()));
        registration.addRecipes(AnalPressingRecipeCategory.TYPE, recipeManager.getAllRecipesFor(PoRecipes.ANAL_PRESSING.type().get()));
        registration.addRecipes(FlyBarrelRecipeCategory.TYPE, recipeManager.getAllRecipesFor(PoRecipes.FLY_BARREL.type().get()));
        registration.addRecipes(BreedingChestRecipeCategory.TYPE, recipeManager.getAllRecipesFor(PoRecipes.BREEDING_CHEST.type().get()));

        PSJEIInfo.register(registration);

        if (PoMods.CREATE.isLoaded()) {
            createCategories.forEach(category -> category.registerRecipes(registration));
        }
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(PoBlocks.COMPOOPER.get()), CompooperRecipeCategory.TYPE);
        registration.addRecipeCatalyst(new ItemStack(PoBlocks.SIEVE.get()), SieveRecipeCategory.TYPE);
        registration.addRecipeCatalyst(new ItemStack(PoBlocks.FLY_BARREL.get()), FlyBarrelRecipeCategory.TYPE);
        registration.addRecipeCatalyst(new ItemStack(PoBlocks.BREEDING_CHEST.get()), BreedingChestRecipeCategory.TYPE);
        registration.addRecipeCatalyst(new ItemStack(PoBlocks.POOP_TNT.get()), POPExplosionRecipeCategory.TYPE);
        registration.addRecipeCatalyst(new ItemStack(PoItems.KING_OF_DRAGON_FRUIT.get()), AnalPressingRecipeCategory.TYPE);
        registration.addRecipeCatalyst(new ItemStack(PoItems.DRAGON_BREATH_CHILI.get()), AnalPressingRecipeCategory.TYPE);

        if (PoMods.CREATE.isLoaded()) {
            createCategories.forEach(category -> category.registerCatalysts(registration));
        }
    }
}