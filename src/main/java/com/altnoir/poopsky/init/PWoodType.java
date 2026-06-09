package com.altnoir.poopsky.init;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.properties.WoodType;

public class PWoodType {
    public static final WoodType POOP = WoodType.register(
            new WoodType(
                    "poop",
                    PBlockSetType.POOP,
                    SoundType.MUD,
                    SoundType.BAMBOO_WOOD_HANGING_SIGN, // 悬挂告示牌音效
                    SoundEvents.MUD_STEP, // 栅栏门关闭音效
                    SoundEvents.MUD_HIT  // 栅栏门开启音效
            )
    );
}
