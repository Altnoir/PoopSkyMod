package com.altnoir.poopsky.impl;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.properties.BlockSetType;

public class PoBlockSetType {
    public static final BlockSetType POOP = BlockSetType.register(
            new BlockSetType("poop",
                    true,
                    true,
                    true,
                    BlockSetType.PressurePlateSensitivity.EVERYTHING,
                    SoundType.MUD,
                    SoundEvents.HONEY_BLOCK_BREAK,  // 门关闭音效（示例）
                    SoundEvents.MUD_HIT,  // 门开启音效（示例）
                    SoundEvents.MUD_FALL,   // 活板门关闭（示例）
                    SoundEvents.MUD_BREAK,   // 活板门开启（示例）
                    SoundEvents.HONEY_BLOCK_STEP,    // 压力板关闭
                    SoundEvents.HONEY_BLOCK_PLACE,  // 压力板开启
                    SoundEvents.HONEY_BLOCK_STEP,   // 按钮关闭
                    SoundEvents.HONEY_BLOCK_PLACE    // 按钮开启
            )
    );
}
