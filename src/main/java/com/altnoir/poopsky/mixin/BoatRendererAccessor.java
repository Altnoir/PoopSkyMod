package com.altnoir.poopsky.mixin;

import com.mojang.datafixers.util.Pair;
import net.minecraft.client.model.ListModel;
import net.minecraft.client.renderer.entity.BoatRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.vehicle.Boat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(BoatRenderer.class)
public interface BoatRendererAccessor {
    @Accessor("boatResources")
    Map<Boat.Type, Pair<ResourceLocation, ListModel<Boat>>> poopsky$getBoatResources();

    @Mutable
    @Accessor("boatResources")
    void poopsky$setBoatResources(Map<Boat.Type, Pair<ResourceLocation, ListModel<Boat>>> resources);
}
