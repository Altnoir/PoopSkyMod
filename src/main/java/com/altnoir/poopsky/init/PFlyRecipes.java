package com.altnoir.poopsky.init;

import com.altnoir.poopsky.item.PSItems;
import com.altnoir.poopsky.recipe.BreedingBoxRecipe;
import com.altnoir.poopsky.recipe.FlyNestRecipe;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.Optional;
import java.util.Random;

/**
 * 苍蝇杂交与产物查询工具类。
 * 所有数据从 RecipeManager 中读取 JSON 配方，不硬编码。
 */
public class PFlyRecipes {

    public record JeiHybridRecipe(PFlyTypes.FlyType parent1, PFlyTypes.FlyType parent2, PFlyTypes.FlyType result, float chance) {}

    /**
     * 获取苍蝇品种对应的产物 ItemStack。
     */
    public static ItemStack getProduct(Level level, PFlyTypes.FlyType type) {
        if (level == null) return ItemStack.EMPTY;
        return level.getRecipeManager()
                .getAllRecipesFor(PRecipes.FLY_NEST_TYPE.get())
                .stream()
                .filter(holder -> holder.value().matches(type.getSerializedName()))
                .findFirst()
                .map(holder -> holder.value().result().copy())
                .orElse(ItemStack.EMPTY);
    }

    /**
     * 杂交判定：查找配方；找到配方且概率通过 -> 返回新品种；
     * 找不到配方 -> 两个不同品种随机选一个返回，相同品种返回自身。
     */
    public static HybridResult tryHybrid(Level level, PFlyTypes.FlyType parent1, PFlyTypes.FlyType parent2) {
        if (level == null) return fallbackResult(parent1, parent2);

        // 查找匹配的杂交配方
        List<RecipeHolder<BreedingBoxRecipe>> recipes = level.getRecipeManager()
                .getAllRecipesFor(PRecipes.BREEDING_BOX_TYPE.get());

        Random random = new Random();
        for (var holder : recipes) {
            BreedingBoxRecipe recipe = holder.value();
            if (recipe.matches(parent1.getSerializedName(), parent2.getSerializedName())) {
                if (random.nextFloat() < recipe.chance()) {
                    return new HybridResult(PFlyTypes.byId(recipe.result()), true);
                }
                // 杂交失败 -> 返回父母中随机一个
                return new HybridResult(random.nextBoolean() ? parent1 : parent2, false);
            }
        }

        // 没有匹配配方
        return fallbackResult(parent1, parent2);
    }

    private static HybridResult fallbackResult(PFlyTypes.FlyType parent1, PFlyTypes.FlyType parent2) {
        if (parent1.equals(parent2)) {
            return new HybridResult(parent1, false);
        }
        return new HybridResult(new Random().nextBoolean() ? parent1 : parent2, false);
    }

    public record HybridResult(PFlyTypes.FlyType result, boolean isHybrid) {}
}

