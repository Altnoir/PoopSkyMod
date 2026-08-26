package com.altnoir.poopsky.compat.kubejs;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public final class PoCustomBosses {
    public static final PoCustomBosses INSTANCE = new PoCustomBosses();

    private final Map<String, CustomBossBuilder> builders = new LinkedHashMap<>();
    private volatile List<CustomBossDefinition> definitions = List.of();

    private PoCustomBosses() {
    }

    public synchronized CustomBossBuilder register(String id) {
        String normalized = id.toLowerCase(Locale.ROOT);
        if (!normalized.matches("[a-z0-9_.-]+")) {
            throw new IllegalArgumentException("Invalid custom boss id: " + id);
        }
        return builders.computeIfAbsent(normalized, CustomBossBuilder::new);
    }

    public synchronized void clear() {
        builders.clear();
        definitions = List.of();
    }

    public synchronized void store() {
        definitions = builders.values().stream()
                .map(CustomBossBuilder::build)
                .toList();
    }

    public Collection<CustomBossDefinition> definitions() {
        return definitions;
    }
}
