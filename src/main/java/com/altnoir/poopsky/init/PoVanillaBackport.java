package com.altnoir.poopsky.init;

import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import java.util.function.Supplier;

/** Vanilla 1.21 entries directly referenced by Hotfix2; keep their original registry IDs. */
public final class PoVanillaBackport {
    private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, "minecraft");
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, "minecraft");
    private static final DeferredRegister<net.minecraft.core.particles.ParticleType<?>> PARTICLES = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, "minecraft");
    private static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, "minecraft");
    public static final SoundType POLISHED_TUFF_SOUND = soundType("polished_tuff");
    public static final SoundType TUFF_BRICKS_SOUND = soundType("tuff_bricks");
    public static final SoundType HEAVY_CORE_SOUND = soundType("heavy_core");
    public static final RegistryObject<Block> POLISHED_TUFF = block("polished_tuff", () -> new Block(BlockBehaviour.Properties.copy(Blocks.TUFF).sound(POLISHED_TUFF_SOUND)));
    public static final RegistryObject<Block> TUFF_BRICKS = block("tuff_bricks", () -> new Block(BlockBehaviour.Properties.copy(Blocks.TUFF).sound(TUFF_BRICKS_SOUND)));
    public static final RegistryObject<Block> CHISELED_COPPER = copper("chiseled_copper", WeatheringCopper.WeatherState.UNAFFECTED, Blocks.COPPER_BLOCK);
    public static final RegistryObject<Block> EXPOSED_CHISELED_COPPER = copper("exposed_chiseled_copper", WeatheringCopper.WeatherState.EXPOSED, Blocks.EXPOSED_COPPER);
    public static final RegistryObject<Block> WEATHERED_CHISELED_COPPER = copper("weathered_chiseled_copper", WeatheringCopper.WeatherState.WEATHERED, Blocks.WEATHERED_COPPER);
    public static final RegistryObject<Block> OXIDIZED_CHISELED_COPPER = copper("oxidized_chiseled_copper", WeatheringCopper.WeatherState.OXIDIZED, Blocks.OXIDIZED_COPPER);
    public static final RegistryObject<Block> WAXED_CHISELED_COPPER = block("waxed_chiseled_copper", () -> new Block(BlockBehaviour.Properties.copy(Blocks.WAXED_COPPER_BLOCK)));
    public static final RegistryObject<Block> WAXED_EXPOSED_CHISELED_COPPER = block("waxed_exposed_chiseled_copper", () -> new Block(BlockBehaviour.Properties.copy(Blocks.WAXED_EXPOSED_COPPER)));
    public static final RegistryObject<Block> WAXED_WEATHERED_CHISELED_COPPER = block("waxed_weathered_chiseled_copper", () -> new Block(BlockBehaviour.Properties.copy(Blocks.WAXED_WEATHERED_COPPER)));
    public static final RegistryObject<Block> WAXED_OXIDIZED_CHISELED_COPPER = block("waxed_oxidized_chiseled_copper", () -> new Block(BlockBehaviour.Properties.copy(Blocks.WAXED_OXIDIZED_COPPER)));
    public static final RegistryObject<Block> HEAVY_CORE = block("heavy_core", () -> new com.altnoir.poopsky.content.block.p.PoHeavyCoreBlock(BlockBehaviour.Properties.of().strength(10, 1200).requiresCorrectToolForDrops().sound(HEAVY_CORE_SOUND)));
    public static final RegistryObject<Item> BREEZE_ROD = ITEMS.register("breeze_rod", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> ARMADILLO_SCUTE = ITEMS.register("armadillo_scute", () -> new Item(new Item.Properties()));
    public static final RegistryObject<SimpleParticleType> DUST_PLUME = PARTICLES.register("dust_plume", () -> new SimpleParticleType(false));
    private static SoundType soundType(String name) {
        SoundEvent[] sounds = new SoundEvent[5];
        String[] kinds = {"break", "step", "place", "hit", "fall"};
        for (int i = 0; i < sounds.length; i++) {
            String id = "block." + name + "." + kinds[i];
            SoundEvent sound = SoundEvent.createVariableRangeEvent(new ResourceLocation("minecraft", id));
            sounds[i] = sound;
            SOUNDS.register(id, () -> sound);
        }
        return new SoundType(1, 1, sounds[0], sounds[1], sounds[2], sounds[3], sounds[4]);
    }
    public static java.util.List<Block> copperStages() {
        return java.util.List.of(CHISELED_COPPER.get(), EXPOSED_CHISELED_COPPER.get(), WEATHERED_CHISELED_COPPER.get(), OXIDIZED_CHISELED_COPPER.get());
    }
    public static java.util.List<Block> waxedCopperStages() {
        return java.util.List.of(WAXED_CHISELED_COPPER.get(), WAXED_EXPOSED_CHISELED_COPPER.get(), WAXED_WEATHERED_CHISELED_COPPER.get(), WAXED_OXIDIZED_CHISELED_COPPER.get());
    }
    private static RegistryObject<Block> copper(String name, WeatheringCopper.WeatherState age, Block original) {
        return block(name, () -> new WeatheringCopperFullBlock(age, BlockBehaviour.Properties.copy(original)) {
            @Override public boolean isRandomlyTicking(net.minecraft.world.level.block.state.BlockState state) { return age != WeatheringCopper.WeatherState.OXIDIZED; }
            @Override public java.util.Optional<net.minecraft.world.level.block.state.BlockState> getNext(net.minecraft.world.level.block.state.BlockState state) {
                Block next = switch (getAge()) { case UNAFFECTED -> EXPOSED_CHISELED_COPPER.get(); case EXPOSED -> WEATHERED_CHISELED_COPPER.get(); case WEATHERED -> OXIDIZED_CHISELED_COPPER.get(); case OXIDIZED -> null; };
                return java.util.Optional.ofNullable(next).map(Block::defaultBlockState);
            }
        });
    }
    private static RegistryObject<Block> block(String name, Supplier<Block> factory) {
        RegistryObject<Block> block = BLOCKS.register(name, factory);
        ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
        return block;
    }
    public static void register(IEventBus bus) { BLOCKS.register(bus); ITEMS.register(bus); SOUNDS.register(bus); PARTICLES.register(bus); }
    private PoVanillaBackport() {}
}
