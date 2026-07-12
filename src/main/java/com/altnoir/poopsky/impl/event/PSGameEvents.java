package com.altnoir.poopsky.impl.event;

import com.altnoir.poopsky.Config;
import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.content.block.abs.AbstractToiletBlock;
import com.altnoir.poopsky.content.block.p.BaseToiletLavaBlock;
import com.altnoir.poopsky.init.*;
import com.altnoir.poopsky.content.villager.PVillagerBehaviors;
import com.altnoir.poopsky.content.villager.PVillagerTrades;
import com.altnoir.poopsky.content.worldgen.PoVoidChunkGenerator;
import com.altnoir.poopsky.content.worldgen.structure.PoopIslandStructure;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
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
import net.minecraft.world.level.levelgen.XoroshiroRandomSource;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.event.brewing.RegisterBrewingRecipesEvent;
import net.neoforged.neoforge.event.entity.living.FinalizeSpawnEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.neoforge.event.village.VillagerTradesEvent;

@EventBusSubscriber(modid = PoopSky.MOD_ID)
public class PSGameEvents {
    @SubscribeEvent
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

    @SubscribeEvent
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

    @SubscribeEvent
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

    @SubscribeEvent
    public static void registerTrades(VillagerTradesEvent event) {
        PVillagerTrades.registerTrades(event.getType(), event.getTrades());
    }

    @SubscribeEvent
    public static void onEntityTick(EntityTickEvent.Pre event) {
        Entity entity = event.getEntity();
        if (!(entity instanceof Villager villager) || entity.level().isClientSide || entity.tickCount % 10 != 0) return;

        PVillagerBehaviors.tickPoopTemptation(villager);
    }

    @SubscribeEvent
    public static void onFinalizeSpawn(FinalizeSpawnEvent event) {
        if (event.getLevel().isClientSide() || !Config.desperateWorld) return;
        Mob mob = event.getEntity();

        if (mob.hasEffect(PoEffects.FECAL_INCONTINENCE)) return;
        mob.addEffect(new MobEffectInstance(PoEffects.FECAL_INCONTINENCE, MobEffectInstance.INFINITE_DURATION, 3));
    }

    @SubscribeEvent
    public static void createSpawnToilet(LevelEvent.CreateSpawnPosition event) {
        if (event.getLevel() instanceof ServerLevel level && level.getChunkSource().getGenerator() instanceof PoVoidChunkGenerator) {
            var rand = new XoroshiroRandomSource(level.getSeed());
            var pos = new BlockPos.MutableBlockPos(rand.nextIntBetweenInclusive(-200, 200), 87, rand.nextIntBetweenInclusive(-200, 200));

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
}