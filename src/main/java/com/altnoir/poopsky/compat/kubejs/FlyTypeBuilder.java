package com.altnoir.poopsky.compat.kubejs;

import com.altnoir.poopsky.PoopSky;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class FlyTypeBuilder {
    private final String id;
    private ResourceLocation texture;
    private String displayName;
    private String flyBarrelResult;
    private int flyBarrelCount = 1;
    private final List<BreedingRecipe> breedingRecipes = new ArrayList<>();

    FlyTypeBuilder(String id) {
        this.id = id;
        this.texture = PoopSky.loc("item/fly_" + id);
    }

    public String id() {
        return id;
    }

    public ResourceLocation texture() {
        return texture;
    }

    public String displayName() {
        return displayName;
    }

    public boolean hasFlyBarrel() {
        return flyBarrelResult != null;
    }

    public String flyBarrelResult() {
        return flyBarrelResult;
    }

    public int flyBarrelCount() {
        return flyBarrelCount;
    }

    public List<BreedingRecipe> breedingRecipes() {
        return breedingRecipes;
    }

    public FlyTypeBuilder texture(String texture) {
        ResourceLocation parsed = ResourceLocation.tryParse(texture);
        if (parsed == null) {
            throw new IllegalArgumentException("Invalid texture id: " + texture);
        }
        this.texture = parsed;
        return this;
    }

    public FlyTypeBuilder name(String displayName) {
        this.displayName = displayName;
        return this;
    }

    public FlyTypeBuilder flyBarrel(String result) {
        return flyBarrel(result, 1);
    }

    public FlyTypeBuilder flyBarrel(String result, int count) {
        if (count < 1 || count > 99) {
            throw new IllegalArgumentException("flyBarrel count must be between 1 and 99");
        }
        ResourceLocation parsed = ResourceLocation.tryParse(result);
        if (parsed == null) {
            throw new IllegalArgumentException("Invalid item id: " + result);
        }
        this.flyBarrelResult = parsed.toString();
        this.flyBarrelCount = count;
        return this;
    }

    public FlyTypeBuilder breeding(String parent1, String parent2) {
        return breeding(parent1, parent2, 0.2F);
    }

    public FlyTypeBuilder breeding(String parent1, String parent2, float chance) {
        if (chance < 0.0F || chance > 1.0F) {
            throw new IllegalArgumentException("breeding chance must be between 0.0 and 1.0");
        }
        this.breedingRecipes.add(new BreedingRecipe(parent1.toLowerCase(Locale.ROOT), parent2.toLowerCase(Locale.ROOT), chance));
        return this;
    }

    public record BreedingRecipe(String parent1, String parent2, float chance) {
    }
}