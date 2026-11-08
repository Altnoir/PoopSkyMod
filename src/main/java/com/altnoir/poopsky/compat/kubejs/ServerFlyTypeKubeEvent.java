package com.altnoir.poopsky.compat.kubejs;

import dev.latvian.mods.kubejs.event.EventJS;

public final class ServerFlyTypeKubeEvent extends EventJS {
    public ServerFlyTypeBuilder register(String id) {
        return new ServerFlyTypeBuilder(PoFlyTypes.INSTANCE.registerServer(id));
    }
}