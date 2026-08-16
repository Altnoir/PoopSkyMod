package com.altnoir.poopsky.compat.kubejs;

import dev.latvian.mods.kubejs.event.EventGroup;
import dev.latvian.mods.kubejs.event.EventHandler;

public interface PoopSkyServerEvents {
    EventGroup GROUP = EventGroup.of("PoopSkyServerEvents");
    EventHandler FLY_TYPE = GROUP.server("flytype", () -> ServerFlyTypeKubeEvent.class);
}