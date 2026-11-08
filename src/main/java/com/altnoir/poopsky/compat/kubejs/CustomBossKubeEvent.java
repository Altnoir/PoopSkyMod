package com.altnoir.poopsky.compat.kubejs;

import dev.latvian.mods.kubejs.event.EventJS;

public final class CustomBossKubeEvent extends EventJS {
    public CustomBossBuilder register(String id) {
        return PoCustomBosses.INSTANCE.register(id);
    }
}
