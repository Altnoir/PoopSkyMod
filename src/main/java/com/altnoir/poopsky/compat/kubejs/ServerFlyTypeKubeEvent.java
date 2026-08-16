package com.altnoir.poopsky.compat.kubejs;

import dev.latvian.mods.kubejs.event.KubeEvent;

public final class ServerFlyTypeKubeEvent implements KubeEvent {
    public ServerFlyTypeBuilder register(String id) {
        return new ServerFlyTypeBuilder(PoFlyTypes.INSTANCE.registerServer(id));
    }
}