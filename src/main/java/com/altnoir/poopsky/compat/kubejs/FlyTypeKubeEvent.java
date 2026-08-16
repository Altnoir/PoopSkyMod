package com.altnoir.poopsky.compat.kubejs;

import dev.latvian.mods.kubejs.event.KubeEvent;

public final class FlyTypeKubeEvent implements KubeEvent {
    public FlyTypeBuilder register(String id) {
        return PoopSkyFlyTypes.INSTANCE.register(id);
    }
}
