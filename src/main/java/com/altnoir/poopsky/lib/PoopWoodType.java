package com.altnoir.poopsky.lib;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.mixin.WoodTypeMixin;
import net.minecraft.block.BlockSetType;
import net.minecraft.block.WoodType;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;

public class PoopWoodType {
    public static final WoodType POOP = register(
            "poop",
            PoopBlockSetType.POOP,
            BlockSoundGroup.MUD,   // 主音效组
            BlockSoundGroup.HANGING_SIGN, // 悬挂告示牌音效（需自定义）
            SoundEvents.BLOCK_MUD_STEP, // 栅栏门关闭音效
            SoundEvents.BLOCK_MUD_HIT  // 栅栏门开启音效
    );

    private static WoodType register(String name, BlockSetType setType, BlockSoundGroup soundType, BlockSoundGroup hangingSignSoundType, SoundEvent fenceClose, SoundEvent fenceOpen) {
        WoodType type = WoodTypeMixin.invokeConstructor(
                PoopSky.MOD_ID + ":" + name,
                setType,
                soundType,
                hangingSignSoundType,
                fenceClose,
                fenceOpen
        );
        return WoodTypeMixin.invokeRegister(type);
    }
}
