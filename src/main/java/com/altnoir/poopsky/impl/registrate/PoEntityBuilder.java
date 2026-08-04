package com.altnoir.poopsky.impl.registrate;

import com.tterrag.registrate.builders.BuilderCallback;
import com.tterrag.registrate.builders.EntityBuilder;
import com.tterrag.registrate.util.nullness.NonNullConsumer;
import com.tterrag.registrate.util.nullness.NonNullSupplier;
import net.minecraft.SharedConstants;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

public class PoEntityBuilder<T extends Entity, P> extends EntityBuilder<T, P> {
    private final NonNullSupplier<EntityType.Builder<T>> entityTypeBuilder;
    private NonNullConsumer<EntityType.Builder<T>> builderCallback = builder -> {
    };

    protected PoEntityBuilder(PoRegistrate owner, P parent, String name, BuilderCallback callback, EntityType.EntityFactory<T> factory, MobCategory category) {
        super(owner, parent, name, callback, factory, category);
        this.entityTypeBuilder = () -> EntityType.Builder.of(factory, category);
    }

    static <T extends Entity, P> PoEntityBuilder<T, P> create(PoRegistrate owner, P parent, String name, BuilderCallback callback, EntityType.EntityFactory<T> factory, MobCategory category) {
        PoEntityBuilder<T, P> builder = new PoEntityBuilder<>(owner, parent, name, callback, factory, category);
        builder.defaultLang();
        return builder;
    }

    @Override
    public PoEntityBuilder<T, P> properties(NonNullConsumer<EntityType.Builder<T>> callback) {
        builderCallback = builderCallback.andThen(callback);
        return this;
    }

    @Override
    protected EntityType<T> createEntry() {
        EntityType.Builder<T> builder = entityTypeBuilder.get();
        builderCallback.accept(builder);

        boolean checkDataFixerSchema = SharedConstants.CHECK_DATA_FIXER_SCHEMA;
        SharedConstants.CHECK_DATA_FIXER_SCHEMA = false;
        try {
            return builder.build(getName());
        } finally {
            SharedConstants.CHECK_DATA_FIXER_SCHEMA = checkDataFixerSchema;
        }
    }

    @Override
    public PoRegistrate getOwner() {
        return (PoRegistrate) super.getOwner();
    }
}
