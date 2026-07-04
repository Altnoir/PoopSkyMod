package com.altnoir.poopsky.common.event;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.common.entity.p.FlyEntity;
import com.altnoir.poopsky.common.entity.p.PoolimeEntity;
import com.altnoir.poopsky.common.entity.p.ToiletPlugEntity;
import com.altnoir.poopsky.init.PBlockEntityType;
import com.altnoir.poopsky.init.PEffects;
import com.altnoir.poopsky.init.PEntityType;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.EntityMountEvent;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;

import java.util.Set;

@EventBusSubscriber(modid = PoopSky.MOD_ID)
public class PSModEvents {
    @SubscribeEvent
    public static void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(PEntityType.POOLIME.get(), PoolimeEntity.createAttributes().build());
        event.put(PEntityType.FLY.get(), FlyEntity.createAttributes().build());
    }

    @SubscribeEvent
    public static void registerSpawnPlacements(RegisterSpawnPlacementsEvent event) {
        event.register(PEntityType.POOLIME.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                PoolimeEntity::checkPoolimeSpawnRules, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(PEntityType.FLY.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                FlyEntity::checkFlySpawnRules, RegisterSpawnPlacementsEvent.Operation.REPLACE);
    }

    @SubscribeEvent
    public static void onEntityDismount(EntityMountEvent event) {
        if (event.isDismounting() && event.getEntityBeingMounted() instanceof ToiletPlugEntity &&
                event.getEntity() instanceof Player player && player.isShiftKeyDown()) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void CapabilitiesRegister(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                PBlockEntityType.SIEVE_BLOCK_ENTITY.get(),
                (blockEntity, direction) -> {
                    if (direction == null || direction == Direction.DOWN) {
                        return blockEntity.getBottomHandler();
                    }
                    return blockEntity.getTopSideHandler();
                }
        );
        event.registerBlockEntity(
                Capabilities.FluidHandler.BLOCK,
                PBlockEntityType.TOILET_BLOCK_ENTITY.get(),
                (blockEntity, side) -> blockEntity.fluidTank
        );
        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                PBlockEntityType.FLY_BARREL.get(),
                (blockEntity, direction) -> {
                    if (direction == null || direction == Direction.DOWN) {
                        return blockEntity.getBottomHandler();
                    }
                    return blockEntity.getTopSideHandler();
                }
        );
        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                PBlockEntityType.BREEDING_CHEST.get(),
                (blockEntity, direction) -> {
                    if (direction == null || direction == Direction.DOWN) {
                        return blockEntity.getBottomHandler();
                    }
                    return blockEntity.getTopSideHandler();
                }
        );
    }

    @SubscribeEvent
    public static void onMobEffectApplicable(MobEffectEvent.Applicable event) {
        LivingEntity entity = event.getEntity();
        MobEffectInstance effectInstance = event.getEffectInstance();

        if (OMEN_EFFECTS.contains(effectInstance.getEffect()) && entity.hasEffect(PEffects.OMENER)) {
            if (!effectInstance.is(MobEffects.CONFUSION) && !entity.hasEffect(MobEffects.REGENERATION)) {
                int amplifier = entity.getEffect(PEffects.OMENER).getAmplifier();
                entity.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 40, amplifier + 1));
            }
            event.setResult(MobEffectEvent.Applicable.Result.DO_NOT_APPLY);
        }
    }

    private static final Set<Holder<MobEffect>> OMEN_EFFECTS = Set.of(
            MobEffects.POISON,
            MobEffects.WITHER,
            MobEffects.CONFUSION
    );
}