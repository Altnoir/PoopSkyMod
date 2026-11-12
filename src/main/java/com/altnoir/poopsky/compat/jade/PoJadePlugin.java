package com.altnoir.poopsky.compat.jade;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.content.block.entity.IgneousExtruderBlockEntity;
import com.altnoir.poopsky.content.block.p.IgneousExtruderBlock;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import snownee.jade.api.*;
import snownee.jade.api.config.IPluginConfig;
import snownee.jade.api.ui.IElementHelper;

import java.util.List;

@WailaPlugin
public final class PoJadePlugin implements IWailaPlugin, IBlockComponentProvider {
    private static final ResourceLocation UID = PoopSky.loc("igneous_extruder_output");

    @Override
    public void registerClient(IWailaClientRegistration registration) {
        registration.registerBlockComponent(this, IgneousExtruderBlock.class);
    }

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
        if (!(accessor.getBlockEntity() instanceof IgneousExtruderBlockEntity extruder)) {
            return;
        }
        ItemStack result = extruder.getDisplayStack();
        if (!result.isEmpty()) {
            IElementHelper helper = IElementHelper.get();
            tooltip.add(List.of(helper.smallItem(result), helper.text(result.getHoverName())));
        }
    }

    @Override
    public ResourceLocation getUid() {
        return UID;
    }
}
