package com.altnoir.poopsky.Fluid;

import com.altnoir.poopsky.Fluid.UrineFluid;
import com.altnoir.poopsky.PoopSky;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.BucketItem;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class PSFluids {
    public static final FlowableFluid URINE = (FlowableFluid) registerFluid("urine", new UrineFluid.Still());
    public static final FlowableFluid FLOWING_URINE = (FlowableFluid) registerFluid("flowing_urine", new UrineFluid.Flowing());

    public static final Item URINE_BUCKET = registerBucketItem("urine_bucket", URINE);

    private static Fluid registerFluid(String name, FlowableFluid fluid) {
        return Registry.register(Registries.FLUID, Identifier.of(PoopSky.MOD_ID, name), fluid);
    }

    private static Item registerBucketItem(String name, Fluid fluid) {
        return Registry.register(Registries.ITEM, Identifier.of(PoopSky.MOD_ID, name),
                new BucketItem(fluid, new Item.Settings().recipeRemainder(Items.BUCKET).maxCount(1)));
    }
    public static void registerModFluids() {
        PoopSky.LOGGER.info("Registering Fluids for " + PoopSky.MOD_ID);
    }
}
