package com.altnoir.poopsky.impl.event;

import com.altnoir.poopsky.Config;
import com.altnoir.poopsky.content.FlyTypeManager;
import com.altnoir.poopsky.content.ToiletTypeManager;
import com.altnoir.poopsky.content.block.abs.AbstractToiletBlock;
import com.altnoir.poopsky.content.block.p.BaseToiletLavaBlock;
import com.altnoir.poopsky.content.entity.p.ToiletPlugEntity;
import com.altnoir.poopsky.content.item.p.TimeBellItem;
import com.altnoir.poopsky.content.villager.PVillagerBehaviors;
import com.altnoir.poopsky.content.villager.PVillagerTrades;
import com.altnoir.poopsky.impl.IntroSavedData;
import com.altnoir.poopsky.impl.command.PoCommands;
import com.altnoir.poopsky.init.*;
import com.altnoir.poopsky.worldgen.PoVoidChunkGenerator;
import com.altnoir.poopsky.worldgen.structure.PoopIslandStructure;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.event.brewing.RegisterBrewingRecipesEvent;
import net.neoforged.neoforge.event.entity.EntityMountEvent;
import net.neoforged.neoforge.event.entity.living.FinalizeSpawnEvent;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.neoforged.neoforge.event.village.VillagerTradesEvent;

import java.util.Set;

public class PoGameEvents {
    public static void registerGame(IEventBus gameEventBus) {
        gameEventBus.addListener(PoGameEvents::onRightClickBlock);
        gameEventBus.addListener(PoGameEvents::onRightClickItem);
        gameEventBus.addListener(PoGameEvents::onBrewingRecipeRegistry);
        gameEventBus.addListener(PoGameEvents::onVillagerTrades);
        gameEventBus.addListener(PoGameEvents::onEntityDismount);
        gameEventBus.addListener(PoGameEvents::onMobEffectApplicable);
        gameEventBus.addListener(PoGameEvents::onAddReloadListener);
        gameEventBus.addListener(PoGameEvents::onEntityTick);
        gameEventBus.addListener(PoGameEvents::onFinalizeSpawn);
        gameEventBus.addListener(PoGameEvents::onCreateSpawnToilet);
        gameEventBus.addListener(PoGameEvents::onPlayerLoggedIn);
        gameEventBus.addListener(PoCommands::register);
        gameEventBus.addListener(PoGameEvents::onServerTick);
    }

    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        var level = event.getLevel();
        var player = event.getEntity();
        var pos = event.getPos();
        var hand = event.getHand();
        var heldItem = player.getItemInHand(hand);

        if (!(heldItem.getItem() instanceof BottleItem) && !heldItem.is(Tags.Items.BUCKETS_EMPTY)) return;
        if (!(level.getBlockState(pos).getBlock() instanceof AbstractToiletBlock abstractToiletBlock)) return;
        if (abstractToiletBlock instanceof BaseToiletLavaBlock && level.getBlockState(pos).getValue(BaseToiletLavaBlock.LAVA))
            return;

        if (!level.isClientSide) {
            SoundEvent sound;
            Item item;
            if (heldItem.is(Tags.Items.BUCKETS_EMPTY)) {
                sound = SoundEvents.BUCKET_FILL;
                item = PoItems.URINE_BUCKET.get();
            } else {
                sound = SoundEvents.BOTTLE_FILL;
                item = PoItems.URINE_BOTTLE.get();
            }
            level.playSound(null, pos, sound, SoundSource.PLAYERS, 1.0F, 0.6F);
            level.gameEvent(player, GameEvent.FLUID_PICKUP, pos);

            var result = ItemUtils.createFilledResult(heldItem, player, new ItemStack(item));

            player.setItemInHand(hand, result);
        }

        event.setCancellationResult(InteractionResult.SUCCESS);
        event.setCanceled(true);
    }

    public static void onRightClickItem(PlayerInteractEvent.RightClickItem event) {
        ItemStack stack = event.getItemStack();
        Player player = event.getEntity();
        Level level = event.getLevel();

        if (!stack.isEmpty() && stack.getItem() instanceof BottleItem) {
            BlockHitResult blockhitresult = Item.getPlayerPOVHitResult(level, player, ClipContext.Fluid.SOURCE_ONLY);
            BlockPos blockpos = blockhitresult.getBlockPos();

            if (blockhitresult.getType() == HitResult.Type.BLOCK && level.mayInteract(player, blockpos) && level.getFluidState(blockpos).is(PoFluids.URINE.get())) {
                if (!level.isClientSide) {
                    level.playSound(null, blockpos, SoundEvents.BOTTLE_FILL, SoundSource.NEUTRAL, 1.0F, 0.6F);
                    level.gameEvent(player, GameEvent.FLUID_PICKUP, blockpos);

                    ItemStack itemStack = ItemUtils.createFilledResult(stack, player, new ItemStack(PoItems.URINE_BOTTLE.get()));
                    player.setItemInHand(event.getHand(), itemStack);
                }

                event.setCancellationResult(InteractionResult.SUCCESS);
                event.setCanceled(true);
            }
        }
    }

    public static void onBrewingRecipeRegistry(RegisterBrewingRecipesEvent event) {
        PotionBrewing.Builder builder = event.getBuilder();

        builder.addMix(Potions.AWKWARD, PoItems.FOLIUM_SENNAE.get(), PoPotions.FECAL_INCONTINENCE_POTION);
        builder.addMix(PoPotions.FECAL_INCONTINENCE_POTION, Items.REDSTONE, PoPotions.LONG_FECAL_INCONTINENCE_POTION);
        builder.addMix(PoPotions.FECAL_INCONTINENCE_POTION, Items.GLOWSTONE_DUST, PoPotions.STRONG_FECAL_INCONTINENCE_POTION);
        builder.addMix(PoPotions.FECAL_INCONTINENCE_POTION, Items.GLOWSTONE, PoPotions.SUPER_FECAL_INCONTINENCE_POTION);

        builder.addMix(Potions.AWKWARD, PoItems.KING_OF_DRAGON_FRUIT.get(), PoPotions.ON_THE_VGE_POTION);
        builder.addMix(PoPotions.ON_THE_VGE_POTION, Items.REDSTONE, PoPotions.LONG_ON_THE_VGE_POTION);
        builder.addMix(PoPotions.ON_THE_VGE_POTION, Items.GLOWSTONE_DUST, PoPotions.STRONG_ON_THE_VGE_POTION);
    }

    public static void onVillagerTrades(VillagerTradesEvent event) {
        PVillagerTrades.registerTrades(event.getType(), event.getTrades());
    }

    public static void onEntityDismount(EntityMountEvent event) {
        if (event.isDismounting() && event.getEntityBeingMounted() instanceof ToiletPlugEntity &&
                event.getEntity() instanceof Player player && player.isShiftKeyDown()) {
            event.setCanceled(true);
        }
    }

    public static void onMobEffectApplicable(MobEffectEvent.Applicable event) {
        LivingEntity entity = event.getEntity();
        MobEffectInstance effectInstance = event.getEffectInstance();

        if (OMEN_EFFECTS.contains(effectInstance.getEffect()) && entity.hasEffect(PoEffects.OMENER)) {
            if (!effectInstance.is(MobEffects.CONFUSION) && !entity.hasEffect(MobEffects.REGENERATION)) {
                int amplifier = entity.getEffect(PoEffects.OMENER).getAmplifier();
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

    public static void onAddReloadListener(AddReloadListenerEvent event) {
        event.addListener(FlyTypeManager.INSTANCE);
        event.addListener(ToiletTypeManager.INSTANCE);
    }

    public static void onEntityTick(EntityTickEvent.Pre event) {
        Entity entity = event.getEntity();
        if (!(entity instanceof Villager villager) || entity.level().isClientSide || entity.tickCount % 10 != 0) return;

        PVillagerBehaviors.tickPoopTemptation(villager);
    }

    public static void onFinalizeSpawn(FinalizeSpawnEvent event) {
        if (event.getLevel().isClientSide() || !Config.desperateWorld) return;
        Mob mob = event.getEntity();

        if (mob.hasEffect(PoEffects.FECAL_INCONTINENCE)) return;
        mob.addEffect(new MobEffectInstance(PoEffects.FECAL_INCONTINENCE, MobEffectInstance.INFINITE_DURATION, 3));
    }

    public static void onCreateSpawnToilet(LevelEvent.CreateSpawnPosition event) {
        if (event.getLevel() instanceof ServerLevel level && level.getChunkSource().getGenerator() instanceof PoVoidChunkGenerator) {
            BlockPos pos = PoVoidChunkGenerator.defaultSpawnPosition(level.getSeed());

            level.setBlock(pos, PoBlocks.WOODEN_TOILET.get().defaultBlockState(), 2);

            event.setCanceled(true);
            BlockPos spawn = level.getHeightmapPos(Heightmap.Types.WORLD_SURFACE_WG, pos);
            event.getSettings().setSpawn(spawn, 90.0F);
            level.getGameRules().getRule(GameRules.RULE_SPAWN_RADIUS).set(0, level.getServer());

            PoopIslandStructure.registerGuaranteedSpawn(level.getSeed(), spawn);
            BlockPos islandCenter = PoopIslandStructure.getGuaranteedSpawnIslandCenter(level.getSeed(), spawn);
            ChunkPos islandChunk = new ChunkPos(islandCenter);
            level.getChunk(islandChunk.x, islandChunk.z);
        }
    }

    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        ServerLevel overworld = player.getServer().overworld();
        if (!(overworld.getChunkSource().getGenerator() instanceof PoVoidChunkGenerator)) return;

        boolean firstJoin = IntroSavedData.get(overworld)
                .markPlayed(player.getUUID(), player.getGameProfile().getName());
        if (firstJoin && "zh_cn".equalsIgnoreCase(player.getLanguage())) {
            player.sendSystemMessage(Component.literal(
                    "温馨提示：如果您正在直播或录制，可在资源包中启用空中厕所的“认知滤网”资源包"
            ));
        }
    }

    public static void onServerTick(ServerTickEvent.Post event) {
        TimeBellItem.tickPending(event.getServer());
        TimeBellItem.freezeTick(event.getServer());
    }
}
