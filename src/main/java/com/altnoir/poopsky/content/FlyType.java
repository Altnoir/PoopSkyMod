package com.altnoir.poopsky.content;

import com.altnoir.poopsky.content.item.PFlyTypes;
import net.minecraft.network.chat.Component;

import java.util.*;

public class FlyType {
    public static final List<String> FLY_TYPES = Arrays.stream(PFlyTypes.values())
            .map(PFlyTypes::id).toList();

    public static Map<String, Type> getAll() {
        List<String> ids = FlyTypeManager.INSTANCE.getFlyTypes();
        LinkedHashMap<String, Type> map = new LinkedHashMap<>();
        for (String id : ids) {
            map.put(id, byId(id));
        }
        return Collections.unmodifiableMap(map);
    }

    public static int size() {
        return FlyTypeManager.INSTANCE.size();
    }

    public static int getIndex(String id) {
        return FlyTypeManager.INSTANCE.getIndex(id);
    }

    public static Type byId(String id) {
        if (id == null || !FlyTypeManager.INSTANCE.isValid(id)) return PFlyTypes.NORMAL.get();
        return new Type(id);
    }

    public record Type(String id) {
        public Component getDisplayName() {
            return Component.translatable("fly_type.poopsky." + id);
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof Type(String id1) && this.id.equals(id1);
        }

        @Override
        public String toString() {
            return "FlyType[" + id + "]";
        }
    }
}