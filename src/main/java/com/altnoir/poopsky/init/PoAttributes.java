package com.altnoir.poopsky.init;

import com.altnoir.poopsky.PoopSky;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraftforge.event.entity.EntityAttributeModificationEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class PoAttributes {
    private static final DeferredRegister<Attribute> REGISTRY = DeferredRegister.create(ForgeRegistries.ATTRIBUTES, PoopSky.MOD_ID);
    public static final RegistryObject<Attribute> SCALE = REGISTRY.register("scale", () -> new RangedAttribute("attribute.name.poopsky.scale", 1, 0.0625, 16).setSyncable(true));
    public static final RegistryObject<Attribute> WATER_MOVEMENT_EFFICIENCY = REGISTRY.register("water_movement_efficiency", () -> new RangedAttribute("attribute.name.poopsky.water_movement_efficiency", 0, 0, 1).setSyncable(true));
    public static final RegistryObject<Attribute> MOVEMENT_EFFICIENCY = REGISTRY.register("movement_efficiency", () -> new RangedAttribute("attribute.name.poopsky.movement_efficiency", 0, 0, 1).setSyncable(true));
    public static void register(IEventBus bus) { REGISTRY.register(bus); bus.addListener(PoAttributes::add); }
    private static void add(EntityAttributeModificationEvent event) {
        for (var type : event.getTypes()) { event.add(type, SCALE.get()); event.add(type, WATER_MOVEMENT_EFFICIENCY.get()); event.add(type, MOVEMENT_EFFICIENCY.get()); }
    }
    private PoAttributes() {}
}
