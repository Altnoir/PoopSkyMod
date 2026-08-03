package com.altnoir.poopsky.impl.registrate;

import com.tterrag.registrate.AbstractRegistrate;

public class PoRegistrate extends AbstractRegistrate<PoRegistrate> {
    protected PoRegistrate(String modid) {
        super(modid);
    }

    public static PoRegistrate create(String modId) {
        return new PoRegistrate(modId);
    }
}