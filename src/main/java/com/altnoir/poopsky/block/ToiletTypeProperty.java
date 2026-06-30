package com.altnoir.poopsky.block;

import com.altnoir.poopsky.init.PToiletTypes;
import net.minecraft.world.level.block.state.properties.Property;

import java.util.*;

public class ToiletTypeProperty extends Property<ToiletType> {
    private final Set<ToiletType.Category> categories;

    public ToiletTypeProperty(String name, ToiletType.Category... categories) {
        super(name, ToiletType.class);
        PToiletTypes.bootstrap();
        this.categories = Set.of(categories);
    }

    @Override
    public Collection<ToiletType> getPossibleValues() {
        List<ToiletType> all = new ArrayList<>();
        for (ToiletType.Category cat : categories) {
            all.addAll(ToiletType.getByCategory(cat).values());
        }
        return Collections.unmodifiableList(all);
    }

    @Override
    public String getName(ToiletType value) {
        return value.id();
    }

    @Override
    public Optional<ToiletType> getValue(String name) {
        for (ToiletType.Category cat : categories) {
            ToiletType found = ToiletType.getByCategory(cat).get(name);
            if (found != null) return Optional.of(found);
        }
        return Optional.empty();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj instanceof ToiletTypeProperty other) {
            return this.categories.equals(other.categories) && this.getName().equals(other.getName());
        }
        return false;
    }

    @Override
    public int generateHashCode() {
        return this.getName().hashCode() * 31 + this.categories.hashCode();
    }
}
