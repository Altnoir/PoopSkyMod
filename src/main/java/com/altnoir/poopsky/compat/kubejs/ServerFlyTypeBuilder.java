package com.altnoir.poopsky.compat.kubejs;

public final class ServerFlyTypeBuilder {
    private final FlyTypeBuilder delegate;

    ServerFlyTypeBuilder(FlyTypeBuilder delegate) {
        this.delegate = delegate;
    }

    public ServerFlyTypeBuilder flyBarrel(String result) {
        delegate.flyBarrel(result);
        return this;
    }

    public ServerFlyTypeBuilder flyBarrel(String result, int count) {
        delegate.flyBarrel(result, count);
        return this;
    }

    public ServerFlyTypeBuilder breeding(String parent1, String parent2) {
        delegate.breeding(parent1, parent2);
        return this;
    }

    public ServerFlyTypeBuilder breeding(String parent1, String parent2, float chance) {
        delegate.breeding(parent1, parent2, chance);
        return this;
    }
}
