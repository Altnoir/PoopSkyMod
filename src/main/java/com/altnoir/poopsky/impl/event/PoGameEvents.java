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
import com.altnoir.poopsky.fabric.FabricatedTags;
import com.altnoir.poopsky.fabric.port.event.LevelEvents;
import com.altnoir.poopsky.fabric.port.event.entity.EntityMountEvent;
import com.altnoir.poopsky.fabric.port.event.entity.EntityTickEvents;
import com.altnoir.poopsky.fabric.port.event.entity.FinalizeSpawnEvent;
import com.altnoir.poopsky.fabric.port.event.entity.MobEffectEvents;
import com.altnoir.poopsky.fabric.port.util.EffectApplicableResult;
import com.altnoir.poopsky.impl.PoAnimationSavedData;
import com.altnoir.poopsky.impl.network.PoAnimation;
import com.altnoir.poopsky.impl.util.ToiletUtil;
import com.altnoir.poopsky.init.*;
import com.altnoir.poopsky.worldgen.PoVoidChunkGenerator;
import com.altnoir.poopsky.worldgen.structure.PoopIslandStructure;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.registry.FabricBrewingRecipeRegistryBuilder;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.PackType;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.*;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.storage.ServerLevelData;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public class PoGameEvents {
    public static void registerGame() {
        UseBlockCallback.EVENT.register(PoGameEvents::onRightClickBlock);
        UseItemCallback.EVENT.register(PoGameEvents::onRightClickItem);
        FabricBrewingRecipeRegistryBuilder.BUILD.register(PoGameEvents::onBrewingRecipeRegistry);
        onVillagerTrades();
        EntityMountEvent.EVENT.register(PoGameEvents::onEntityDismount);
        MobEffectEvents.APPLICABLE.register(PoGameEvents::onMobEffectApplicable);
        onAddReloadListener();
        EntityTickEvents.PRE.register(PoGameEvents::onEntityTick);
        FinalizeSpawnEvent.EVENT.register(PoGameEvents::onFinalizeSpawn);
        LevelEvents.CREATE_SPAWN_POSITION.register(PoGameEvents::onCreateSpawnToilet);
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> onPlayerLoggedIn(handler.player));
        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) ->
                ToiletUtil.clearPendingEndToiletTeleport(handler.player));
        ServerTickEvents.END_SERVER_TICK.register(PoGameEvents::onServerTick);
    }

    public static InteractionResult onRightClickBlock(Player player, Level level, InteractionHand hand, BlockHitResult hitResult) {
        var pos = hitResult.getBlockPos();
        var heldItem = player.getItemInHand(hand);

        if (!(heldItem.getItem() instanceof BottleItem) && !heldItem.is(FabricatedTags.Items.BUCKETS_EMPTY)) return InteractionResult.PASS;
        if (!(level.getBlockState(pos).getBlock() instanceof AbstractToiletBlock abstractToiletBlock)) return InteractionResult.PASS;
        if (abstractToiletBlock instanceof BaseToiletLavaBlock && level.getBlockState(pos).getValue(BaseToiletLavaBlock.LAVA))
            return InteractionResult.PASS;

        if (!level.isClientSide) {
            SoundEvent sound;
            Item item;
            if (heldItem.is(FabricatedTags.Items.BUCKETS_EMPTY)) {
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

        return InteractionResult.SUCCESS;
    }

    public static InteractionResultHolder<ItemStack> onRightClickItem(Player player, Level level, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (!stack.isEmpty() && stack.getItem() instanceof BottleItem) {
            BlockHitResult blockhitresult = Item.getPlayerPOVHitResult(level, player, ClipContext.Fluid.SOURCE_ONLY);
            BlockPos blockpos = blockhitresult.getBlockPos();

            if (blockhitresult.getType() == HitResult.Type.BLOCK && level.mayInteract(player, blockpos) && level.getFluidState(blockpos).is(PoFluids.URINE.get())) {
                if (!level.isClientSide) {
                    level.playSound(null, blockpos, SoundEvents.BOTTLE_FILL, SoundSource.NEUTRAL, 1.0F, 0.6F);
                    level.gameEvent(player, GameEvent.FLUID_PICKUP, blockpos);

                    ItemStack itemStack = ItemUtils.createFilledResult(stack, player, new ItemStack(PoItems.URINE_BOTTLE.get()));
                    player.setItemInHand(hand, itemStack);
                }

                return InteractionResultHolder.success(stack);
            }
        }
        return InteractionResultHolder.pass(stack);
    }

    public static void onBrewingRecipeRegistry(PotionBrewing.Builder builder) {
        builder.addMix(Potions.AWKWARD, PoItems.FOLIUM_SENNAE.get(), PoPotions.FECAL_INCONTINENCE_POTION);
        builder.addMix(PoPotions.FECAL_INCONTINENCE_POTION, Items.REDSTONE, PoPotions.LONG_FECAL_INCONTINENCE_POTION);
        builder.addMix(PoPotions.FECAL_INCONTINENCE_POTION, Items.GLOWSTONE_DUST, PoPotions.STRONG_FECAL_INCONTINENCE_POTION);
        builder.addMix(PoPotions.FECAL_INCONTINENCE_POTION, Items.GLOWSTONE, PoPotions.SUPER_FECAL_INCONTINENCE_POTION);

        builder.addMix(Potions.AWKWARD, PoItems.KING_OF_DRAGON_FRUIT.get(), PoPotions.ON_THE_VGE_POTION);
        builder.addMix(PoPotions.ON_THE_VGE_POTION, Items.REDSTONE, PoPotions.LONG_ON_THE_VGE_POTION);
        builder.addMix(PoPotions.ON_THE_VGE_POTION, Items.GLOWSTONE_DUST, PoPotions.STRONG_ON_THE_VGE_POTION);
    }

    public static void onVillagerTrades() {
        PVillagerTrades.registerTrades();
    }

    public static InteractionResult onEntityDismount(Entity entityMounting, Entity entityBeingMounted, Level level, boolean isMounting) {
        if (!isMounting && entityBeingMounted instanceof ToiletPlugEntity &&
                entityMounting instanceof Player player && player.isShiftKeyDown()) {
            return InteractionResult.FAIL;
        }
        return InteractionResult.PASS;
    }

    public static EffectApplicableResult onMobEffectApplicable(LivingEntity entity, MobEffectInstance effectInstance) {
        if (OMEN_EFFECTS.contains(effectInstance.getEffect()) && entity.hasEffect(PoEffects.holder(PoEffects.OMENER))) {
            if (!effectInstance.is(MobEffects.CONFUSION) && !entity.hasEffect(MobEffects.REGENERATION)) {
                int amplifier = entity.getEffect(PoEffects.holder(PoEffects.OMENER)).getAmplifier();
                entity.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 40, amplifier + 1));
            }
            return EffectApplicableResult.DO_NOT_APPLY;
        }
        return EffectApplicableResult.DEFAULT;
    }

    private static final Set<Holder<MobEffect>> OMEN_EFFECTS = Set.of(
            MobEffects.POISON,
            MobEffects.WITHER,
            MobEffects.CONFUSION
    );

    public static void onAddReloadListener() {
        ResourceManagerHelper.get(PackType.SERVER_DATA).registerReloadListener(FlyTypeManager.INSTANCE);
        ResourceManagerHelper.get(PackType.SERVER_DATA).registerReloadListener(ToiletTypeManager.INSTANCE);
    }

    public static InteractionResult onEntityTick(Entity entity) {
        if (!(entity instanceof Villager villager) || entity.level().isClientSide || entity.tickCount % 10 != 0) return InteractionResult.PASS;

        PVillagerBehaviors.tickPoopTemptation(villager);
        return InteractionResult.SUCCESS;
    }

    public static void onFinalizeSpawn(Entity entity, ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType spawnType, @Nullable SpawnGroupData spawnGroupData) {
        if (level.isClientSide() || !Config.desperateWorld) return;
        Mob mob = (Mob) entity;

        if (mob.hasEffect(PoEffects.holder(PoEffects.FECAL_INCONTINENCE))) return;
        mob.addEffect(new MobEffectInstance(PoEffects.holder(PoEffects.FECAL_INCONTINENCE), MobEffectInstance.INFINITE_DURATION, 3));
    }

    public static boolean onCreateSpawnToilet(LevelAccessor levelAccessor, ServerLevelData settings) {
        if (levelAccessor instanceof ServerLevel level && level.getChunkSource().getGenerator() instanceof PoVoidChunkGenerator) {
            BlockPos pos = PoVoidChunkGenerator.defaultSpawnPosition(level.getSeed());

            level.setBlock(pos, Config.skyFlushToilet
                    ? PoBlocks.FLUSH_TOILET.get().defaultBlockState()
                    : PoBlocks.WOODEN_TOILET.get().defaultBlockState(), 2);

            BlockPos spawn = level.getHeightmapPos(Heightmap.Types.WORLD_SURFACE_WG, pos);
            settings.setSpawn(spawn, 90.0F);
            level.getGameRules().getRule(GameRules.RULE_SPAWN_RADIUS).set(0, level.getServer());

            PoopIslandStructure.registerGuaranteedSpawn(level.getSeed(), spawn);
            BlockPos islandCenter = PoopIslandStructure.getGuaranteedSpawnIslandCenter(level.getSeed(), spawn);
            ChunkPos islandChunk = new ChunkPos(islandCenter);
            level.getChunk(islandChunk.x, islandChunk.z);
            return true;
        }
        return false;
    }

    public static void onPlayerLoggedIn(ServerPlayer player) {
        ServerLevel overworld = player.getServer().overworld();
        if (!(overworld.getChunkSource().getGenerator() instanceof PoVoidChunkGenerator)) return;

        boolean firstJoin = PoAnimationSavedData.get(overworld)
                .markPlayed(PoAnimation.INTRO, player.getUUID(), player.getGameProfile().getName());
        if (firstJoin && "zh_cn".equalsIgnoreCase(player.clientInformation().language())) {
            player.sendSystemMessage(Component.literal(
                    "温馨提示：如果您正在直播或录制，可在资源包中启用空中厕所的“认知滤网”资源包"
            ));
        }
    }

    public static void onServerTick(net.minecraft.server.MinecraftServer server) {
        TimeBellItem.tickPending(server);
        TimeBellItem.freezeTick(server);
    }
}
