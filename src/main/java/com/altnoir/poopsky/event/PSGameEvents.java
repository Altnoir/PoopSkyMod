package com.altnoir.poopsky.event;

import com.altnoir.poopsky.Config;
import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.block.PSBlocks;
import com.altnoir.poopsky.block.ToiletBlocks;
import com.altnoir.poopsky.effect.PSEffects;
import com.altnoir.poopsky.effect.PSPotions;
import com.altnoir.poopsky.event.asm.ASMHooks;
import com.altnoir.poopsky.item.PSItems;
import com.altnoir.poopsky.villager.PSVillagerTrades;
import com.altnoir.poopsky.worldgen.PSVoidChunkGenerator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import net.minecraft.client.gui.screens.worldselection.WorldCreationUiState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.features.TreeFeatures;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.XoroshiroRandomSource;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.presets.WorldPreset;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.neoforge.event.brewing.RegisterBrewingRecipesEvent;
import net.neoforged.neoforge.event.entity.living.FinalizeSpawnEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.event.village.VillagerTradesEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.Locale;

@EventBusSubscriber(modid = PoopSky.MOD_ID)
public class PSGameEvents {
    public static Holder<WorldPreset> originalDefaultWorldPreset;

    @SubscribeEvent
    public static void onBrewingRecipeRegistry(RegisterBrewingRecipesEvent event) {
        PotionBrewing.Builder builder = event.getBuilder();

        builder.addMix(Potions.AWKWARD, PSItems.FOLIUM_SENNAE.get(), PSPotions.FECAL_INCONTINENCE_POTION);
        builder.addMix(PSPotions.FECAL_INCONTINENCE_POTION, Items.REDSTONE, PSPotions.LONG_FECAL_INCONTINENCE_POTION);
        builder.addMix(PSPotions.FECAL_INCONTINENCE_POTION, Items.GLOWSTONE_DUST, PSPotions.STRONG_FECAL_INCONTINENCE_POTION);
        builder.addMix(PSPotions.FECAL_INCONTINENCE_POTION, Items.GLOWSTONE, PSPotions.SUPER_FECAL_INCONTINENCE_POTION);
    }

    @SubscribeEvent
    public static void registerTrades(VillagerTradesEvent event) {
        PSVillagerTrades.registerTrades(event.getType(), event.getTrades());
    }

    @SubscribeEvent
    public static void onFinalizeSpawn(FinalizeSpawnEvent event) {
        if (event.getLevel().isClientSide() || !Config.desperateWorld) return;
        Mob mob = event.getEntity();

        if (mob.hasEffect(PSEffects.FECAL_INCONTINENCE)) return;
        mob.addEffect(new MobEffectInstance(PSEffects.FECAL_INCONTINENCE, MobEffectInstance.INFINITE_DURATION, 3));
    }

    @SubscribeEvent
    public static void onScreenOpen(ScreenEvent.Opening event) {
        if (event.getNewScreen() instanceof CreateWorldScreen screen) {
            var uiState = screen.getUiState();
            var originalPreset = uiState.getWorldType().preset();

            if (originalPreset != null) {
                if (originalDefaultWorldPreset == null) {
                    originalDefaultWorldPreset = originalPreset;
                }
                if (originalDefaultWorldPreset.unwrapKey().equals(originalPreset.unwrapKey())) {
                    var voidWorldPreset = uiState.getSettings().worldgenLoadContext().registryOrThrow(Registries.WORLD_PRESET).getHolder(ASMHooks.overrideDefaultWorldPreset()).orElse(null);
                    uiState.setWorldType(new WorldCreationUiState.WorldTypeEntry(voidWorldPreset));
                }
            }
        }
    }

    @SubscribeEvent
    public static void createSpawnTree(LevelEvent.CreateSpawnPosition event) {
        if (event.getLevel() instanceof ServerLevel level && level.getChunkSource().getGenerator() instanceof PSVoidChunkGenerator) {
            var rand = new XoroshiroRandomSource(level.getSeed());
            var pos = new BlockPos.MutableBlockPos(rand.nextIntBetweenInclusive(-200, 200), 87, rand.nextIntBetweenInclusive(-200, 200));

            level.setBlock(pos, ToiletBlocks.OAK_TOILET.get().defaultBlockState(),2);

            event.setCanceled(true);
            event.getSettings().setSpawn(level.getHeightmapPos(Heightmap.Types.WORLD_SURFACE_WG, pos), 90.0F);
            level.getGameRules().getRule(GameRules.RULE_SPAWN_RADIUS).set(0, level.getServer());
        }
    }
}
