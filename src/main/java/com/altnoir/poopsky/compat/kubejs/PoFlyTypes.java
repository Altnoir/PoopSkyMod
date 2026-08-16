package com.altnoir.poopsky.compat.kubejs;

import com.altnoir.poopsky.content.FlyType;
import com.altnoir.poopsky.content.FlyTypeDefinition;

import java.util.*;

public final class PoFlyTypes {
    public static final PoFlyTypes INSTANCE = new PoFlyTypes();

    private final Map<String, FlyTypeBuilder> builders = new LinkedHashMap<>();
    private final Set<String> startupIds = new HashSet<>();
    private volatile List<FlyTypeDefinition> storedDefinitions = List.of();

    private PoFlyTypes() {
    }

    public FlyTypeBuilder register(String id) {
        return registerInternal(id);
    }

    public FlyTypeBuilder registerStartup(String id) {
        FlyTypeBuilder builder = registerInternal(id);
        startupIds.add(id);
        return builder;
    }

    public FlyTypeBuilder registerServer(String id) {
        return registerInternal(id);
    }

    private FlyTypeBuilder registerInternal(String id) {
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

    public Collection<FlyTypeDefinition> definitions() {
        return builders().stream().map(FlyTypeBuilder::toDefinition).toList();
    }

    public void store(Collection<FlyTypeDefinition> definitions) {
        this.storedDefinitions = List.copyOf(definitions);
    }

    public List<FlyTypeDefinition> storedDefinitions() {
        return storedDefinitions;
    }

    public void clear() {
        builders.keySet().removeIf(id -> !startupIds.contains(id));
    }
}