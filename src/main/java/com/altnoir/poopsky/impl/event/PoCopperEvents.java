package com.altnoir.poopsky.impl.event;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.init.PoVanillaBackport;
import net.minecraftforge.common.ToolActions;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = PoopSky.MOD_ID)
public final class PoCopperEvents {
    /** Keep vanilla bootstrap from touching deferred entries before registry events finish. */
    public static boolean ready;
    @SubscribeEvent public static void waxOff(BlockEvent.BlockToolModificationEvent event) {
        if (!ready) return;
        if (event.getToolAction() == ToolActions.AXE_WAX_OFF) {
            int i = PoVanillaBackport.waxedCopperStages().indexOf(event.getState().getBlock());
            if (i >= 0) event.setFinalState(PoVanillaBackport.copperStages().get(i).withPropertiesOf(event.getState()));
        } else if (event.getToolAction() == ToolActions.AXE_SCRAPE) {
            int i = PoVanillaBackport.copperStages().indexOf(event.getState().getBlock());
            if (i > 0) event.setFinalState(PoVanillaBackport.copperStages().get(i - 1).withPropertiesOf(event.getState()));
        }
    }
    private PoCopperEvents() {}
}
