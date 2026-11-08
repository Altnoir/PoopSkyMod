package com.altnoir.poopsky.compat.kubejs;

import dev.latvian.mods.kubejs.event.EventJS;

public final class FlyTypeKubeEvent extends EventJS {
    public FlyTypeBuilder register(String id) {
        return PoFlyTypes.INSTANCE.registerStartup(id);
    }
}