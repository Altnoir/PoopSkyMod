package com.altnoir.poopsky.compat.kubejs;

import dev.latvian.mods.kubejs.event.KubeEvent;

public final class CustomBossKubeEvent implements KubeEvent {
    public CustomBossBuilder register(String id) {
        return PoCustomBosses.INSTANCE.register(id);
    }
}
