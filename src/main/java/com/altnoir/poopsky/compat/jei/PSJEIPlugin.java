package com.altnoir.poopsky.compat.jei;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.block.PSBlocks;
import com.altnoir.poopsky.init.PFlyTypes;
import com.altnoir.poopsky.item.PSItems;
import com.altnoir.poopsky.init.PRecipes;
import com.altnoir.poopsky.item.p.FlyItem;
import com.altnoir.poopsky.recipe.BreedingBoxRecipe;
import com.altnoir.poopsky.recipe.FlyNestRecipe;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;

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

        registration.addRecipeCategories(
                new CompooperRecipeCategory(registration.getJeiHelpers(), arrow),
                new SieveRecipeCategory(registration.getJeiHelpers(), arrow),
                new FlyNestRecipeCategory(registration.getJeiHelpers(), arrow),
                new BreedingBoxRecipeCategory(registration.getJeiHelpers(), arrow));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        // 1. 注册不需要 level 的硬编码配方
        registration.addRecipes(CompooperRecipeCategory.TYPE, List.of(
                new CompooperRecipe(
                        Ingredient.of(Stream.empty()), new ItemStack(PSItems.MAGGOTS_SEEDS.get()), PSBlocks.URINE_COMPOOPER.get().defaultBlockState()),
                new CompooperRecipe(
                        Ingredient.of(Items.STICK), new ItemStack(Items.BLAZE_ROD), PSBlocks.LAVA_COMPOOPER.get().defaultBlockState()),
                new CompooperRecipe(
                        Ingredient.of(Items.STICK), new ItemStack(Items.BREEZE_ROD), PSBlocks.POWDER_SNOW_COMPOOPER.get().defaultBlockState())
        ));

        // 2. 获取 level 并进行非空判断
        var level = Minecraft.getInstance().level;
        if (level == null) {
            // 如果玩家还在主界面，直接返回。
            // 不用担心，当玩家进入游戏时，JEI 会自动重载配方并再次调用这个方法！
            return;
        }

        var recipeManager = level.getRecipeManager();

        // 3. 注册需要 level 读取的数据包配方
        registration.addRecipes(SieveRecipeCategory.TYPE,
                recipeManager.getAllRecipesFor(PRecipes.SIEVE_TYPE.get()).stream()
                        .map(RecipeHolder::value)
                        .toList());

        registration.addRecipes(FlyNestRecipeCategory.TYPE,
                recipeManager.getAllRecipesFor(PRecipes.FLY_NEST_TYPE.get()).stream()
                        .map(holder -> {
                            FlyNestRecipe recipe = holder.value();
                            return new FlyNestJeiRecipe(
                                    FlyItem.withType(PFlyTypes.byId(recipe.flyTypeId())),
                                    recipe.result()
                            );
                        })
                        .toList());

        registration.addRecipes(BreedingBoxRecipeCategory.TYPE,
                recipeManager.getAllRecipesFor(PRecipes.BREEDING_BOX_TYPE.get()).stream()
                        .map(holder -> {
                            BreedingBoxRecipe recipe = holder.value();
                            ItemStack resultFly = FlyItem.withType(PFlyTypes.byId(recipe.result()));
                            ItemStack parentFly1 = FlyItem.withType(PFlyTypes.byId(recipe.parent1()));
                            ItemStack parentFly2 = FlyItem.withType(PFlyTypes.byId(recipe.parent2()));
                            return new BreedingBoxJeiRecipe(
                                    parentFly1,
                                    parentFly2,
                                    new ItemStack(PSItems.POOP.get()),
                                    resultFly,
                                    parentFly1,
                                    parentFly2,
                                    recipe.chance()
                            );
                        })
                        .toList());
    }

    @Override
    public void onRuntimeAvailable(IJeiRuntime jeiRuntime) {
        // 现在的 onRuntimeAvailable 可以保持为空。
        // 这个方法主要是让你拿到 IJeiRuntime 实例，用来在代码里动态控制 JEI 侧边栏（比如动态隐藏某些物品），而不是用来注册配方的。
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(PSBlocks.COMPOOPER.get()), CompooperRecipeCategory.TYPE);
        registration.addRecipeCatalyst(new ItemStack(PSBlocks.SIEVE.get()), SieveRecipeCategory.TYPE);
        registration.addRecipeCatalyst(new ItemStack(PSBlocks.FLY_NEST.get()), FlyNestRecipeCategory.TYPE);
        registration.addRecipeCatalyst(new ItemStack(PSBlocks.BREEDING_BOX.get()), BreedingBoxRecipeCategory.TYPE);
    }
}
