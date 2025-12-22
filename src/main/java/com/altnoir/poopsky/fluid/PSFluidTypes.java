package com.altnoir.poopsky.fluid;

import com.altnoir.poopsky.PoopSky;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.neoforged.neoforge.common.SoundActions;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public class PSFluidTypes {
    public static final DeferredRegister<FluidType> FLUID_TYPES = DeferredRegister.create(NeoForgeRegistries.Keys.FLUID_TYPES, PoopSky.MOD_ID);

    public static final ResourceLocation POOP_STILL_TEXTURE = ResourceLocation.fromNamespaceAndPath(PoopSky.MOD_ID, "block/poop_liquid");
    public static final ResourceLocation POOP_FLOWING_TEXTURE = ResourceLocation.fromNamespaceAndPath(PoopSky.MOD_ID, "block/poop_liquid_flowing");
    public static final ResourceLocation POOP_OVERLAY_TEXTURE = ResourceLocation.fromNamespaceAndPath(PoopSky.MOD_ID, "block/poop_liquid");

    public static final Supplier<FluidType> POOP_FLUID_TYPE = FLUID_TYPES.register("poop", 
            () -> new FluidType(FluidType.Properties.create()
                    .descriptionId("block.poopsky.poop_liquid")
                    .fallDistanceModifier(0F)
                    .canExtinguish(true)
                    .canConvertToSource(true)
                    .supportsBoating(true)
                    .sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL_LAVA)
                    .sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY_LAVA)
                    .density(3000)
                    .viscosity(6000)
                    .lightLevel(7)
            ));
}