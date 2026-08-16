package com.altnoir.poopsky.compat.kubejs;

import dev.latvian.mods.kubejs.event.EventGroup;
import dev.latvian.mods.kubejs.event.EventHandler;

public interface PoopSkyEvents {
    EventGroup GROUP = EventGroup.of("PoopSkyEvents");
    EventHandler FLY_TYPE = GROUP.server("flyType", () -> FlyTypeKubeEvent.class);
}
