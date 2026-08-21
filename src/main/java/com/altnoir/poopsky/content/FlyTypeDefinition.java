package com.altnoir.poopsky.content;

import com.altnoir.poopsky.PoopSky;
import net.minecraft.resources.Identifier;

public record FlyTypeDefinition(String id, Identifier texture, String displayName) {
    public static FlyTypeDefinition defaultOf(String id) {
        String texturePath = "normal".equals(id) ? "fly" : "fly_" + id;
        return new FlyTypeDefinition(id, PoopSky.loc("item/" + texturePath), null);
    }

    public String modelId() {
        return "normal".equals(id) ? "fly" : "fly_" + id;
    }
}
