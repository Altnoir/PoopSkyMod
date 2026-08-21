package com.altnoir.poopsky.compat.kubejs;

import dev.latvian.mods.kubejs.event.EventGroup;
import dev.latvian.mods.kubejs.event.EventHandler;

public interface PoopSkyStartupEvents {
    EventGroup GROUP = EventGroup.of("PoopSkyStartupEvents");
    EventHandler FLY_TYPE = GROUP.startup("flytype", () -> FlyTypeKubeEvent.class);
}