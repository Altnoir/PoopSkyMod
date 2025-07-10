package com.altnoir.poopsky.event;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.block.PSBlocks;
import com.altnoir.poopsky.block.ToiletBlocks;
import com.altnoir.poopsky.entity.model.ToiletPlugModel;
import com.altnoir.poopsky.item.PSItems;
import com.altnoir.poopsky.villager.PSVillagers;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.MerchantOffer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.event.village.VillagerTradesEvent;

import java.util.List;
import java.util.Optional;

@EventBusSubscriber(modid = PoopSky.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class PSEventBusEvents {
    @SubscribeEvent
    public static void registerLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(ToiletPlugModel.LAYER_LOCATION, ToiletPlugModel::createBodyLayer);
    }
}
