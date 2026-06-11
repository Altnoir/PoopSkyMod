package com.altnoir.poopsky.event;

import com.altnoir.poopsky.Config;
import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.block.AllToiletBlocks;
import com.altnoir.poopsky.block.abs.AbstractToiletBlock;
import com.altnoir.poopsky.block.p.ToiletLavaBlock;
import com.altnoir.poopsky.init.PEffects;
import com.altnoir.poopsky.init.PFluids;
import com.altnoir.poopsky.init.PPotions;
import com.altnoir.poopsky.item.PSItems;
import com.altnoir.poopsky.villager.PSVillagerTrades;
import com.altnoir.poopsky.worldgen.PSVoidChunkGenerator;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.alchemy.Potions;
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
import net.neoforged.neoforge.event.village.VillagerTradesEvent;

@EventBusSubscriber(modid = PoopSky.MOD_ID)
public class PSGameEvents {
    @SubscribeEvent
    public static void onBrewingRecipeRegistry(RegisterBrewingRecipesEvent event) {
        PotionBrewing.Builder builder = event.getBuilder();

        builder.addMix(Potions.AWKWARD, PSItems.FOLIUM_SENNAE.get(), PPotions.FECAL_INCONTINENCE_POTION);
        builder.addMix(PPotions.FECAL_INCONTINENCE_POTION, Items.REDSTONE, PPotions.LONG_FECAL_INCONTINENCE_POTION);
        builder.addMix(PPotions.FECAL_INCONTINENCE_POTION, Items.GLOWSTONE_DUST, PPotions.STRONG_FECAL_INCONTINENCE_POTION);
        builder.addMix(PPotions.FECAL_INCONTINENCE_POTION, Items.GLOWSTONE, PPotions.SUPER_FECAL_INCONTINENCE_POTION);
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
        if (abstractToiletBlock instanceof ToiletLavaBlock && level.getBlockState(pos).getValue(ToiletLavaBlock.LAVA))
            return;

        if (!level.isClientSide) {
            SoundEvent sound;
            Item item;
            if (heldItem.is(Tags.Items.BUCKETS_EMPTY)) {
                sound = SoundEvents.BUCKET_FILL;
                item = PSItems.URINE_BUCKET.get();
            } else {
                sound = SoundEvents.BOTTLE_FILL;
                item = PSItems.URINE_BOTTLE.get();
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

            if (blockhitresult.getType() == HitResult.Type.BLOCK && level.mayInteract(player, blockpos) && level.getFluidState(blockpos).is(PFluids.POOP.get())) {
                if (!level.isClientSide) {
                    level.playSound(null, blockpos, SoundEvents.BOTTLE_FILL, SoundSource.NEUTRAL, 1.0F, 0.6F);
                    level.gameEvent(player, GameEvent.FLUID_PICKUP, blockpos);

                    ItemStack itemStack = ItemUtils.createFilledResult(stack, player, new ItemStack(PSItems.URINE_BOTTLE.get()));
                    player.setItemInHand(event.getHand(), itemStack);
                }

                event.setCancellationResult(InteractionResult.SUCCESS);
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public static void registerTrades(VillagerTradesEvent event) {
        PSVillagerTrades.registerTrades(event.getType(), event.getTrades());
    }

    @SubscribeEvent
    public static void onFinalizeSpawn(FinalizeSpawnEvent event) {
        if (event.getLevel().isClientSide() || !Config.desperateWorld) return;
        Mob mob = event.getEntity();

        if (mob.hasEffect(PEffects.FECAL_INCONTINENCE)) return;
        mob.addEffect(new MobEffectInstance(PEffects.FECAL_INCONTINENCE, MobEffectInstance.INFINITE_DURATION, 3));
    }

    @SubscribeEvent
    public static void createSpawnToilet(LevelEvent.CreateSpawnPosition event) {
        if (event.getLevel() instanceof ServerLevel level && level.getChunkSource().getGenerator() instanceof PSVoidChunkGenerator) {
            var rand = new XoroshiroRandomSource(level.getSeed());
            var pos = new BlockPos.MutableBlockPos(rand.nextIntBetweenInclusive(-200, 200), 87, rand.nextIntBetweenInclusive(-200, 200));

            level.setBlock(pos, AllToiletBlocks.OAK_TOILET.get().defaultBlockState(), 2);

            event.setCanceled(true);
            event.getSettings().setSpawn(level.getHeightmapPos(Heightmap.Types.WORLD_SURFACE_WG, pos), 90.0F);
            level.getGameRules().getRule(GameRules.RULE_SPAWN_RADIUS).set(0, level.getServer());
        }
    }
}
