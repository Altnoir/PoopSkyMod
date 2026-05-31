package com.altnoir.poopsky.event;

import com.altnoir.poopsky.Config;
import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.block.PSBlocks;
import com.altnoir.poopsky.block.ToiletBlocks;
import com.altnoir.poopsky.effect.PSEffects;
import com.altnoir.poopsky.effect.PSPotions;
import com.altnoir.poopsky.item.PSItems;
import com.altnoir.poopsky.villager.PSVillagerTrades;
import com.altnoir.poopsky.villager.PSVillagers;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.MerchantOffer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.brewing.RegisterBrewingRecipesEvent;
import net.neoforged.neoforge.event.entity.living.FinalizeSpawnEvent;
import net.neoforged.neoforge.event.village.VillagerTradesEvent;

import java.util.List;
import java.util.Optional;

@EventBusSubscriber(modid = PoopSky.MOD_ID)
public class PSGameEvents {
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
}
