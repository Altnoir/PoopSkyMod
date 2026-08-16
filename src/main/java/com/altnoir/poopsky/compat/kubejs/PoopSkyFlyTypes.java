package com.altnoir.poopsky.compat.kubejs;

import com.altnoir.poopsky.content.FlyType;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public final class PoopSkyFlyTypes {
    public static final PoopSkyFlyTypes INSTANCE = new PoopSkyFlyTypes();

    private final Map<String, FlyTypeBuilder> builders = new LinkedHashMap<>();

    private PoopSkyFlyTypes() {
    }

    public FlyTypeBuilder register(String id) {
        String normalized = id.toLowerCase(Locale.ROOT);
        if (!normalized.matches("[a-z0-9_.-]+")) {
            throw new IllegalArgumentException("Invalid fly type id: " + id);
        }
        if (FlyType.FLY_TYPES.contains(normalized)) {
            throw new IllegalArgumentException("Fly type already exists: " + normalized);
        }
        return builders.computeIfAbsent(normalized, FlyTypeBuilder::new);
    }

    public Collection<FlyTypeBuilder> builders() {
        return List.copyOf(builders.values());
    }

    public void clear() {
        builders.clear();
    }
}