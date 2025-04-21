package com.altnoir.poopsky.lib;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.mixin.BlockSetTypeMixin;
import net.minecraft.block.BlockSetType;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;

public class PoopBlockSetType {
    public static final BlockSetType POOP = register("poop",
            true,  // 是否可用手开启
            true,  // 是否可被风弹开启
            true,  // 是否可用箭激活按钮
            BlockSetType.ActivationRule.EVERYTHING,
            BlockSoundGroup.MUD,
            SoundEvents.BLOCK_HONEY_BLOCK_BREAK,  // 门关闭音效（示例）
            SoundEvents.BLOCK_MUD_HIT,  // 门开启音效（示例）
            SoundEvents.BLOCK_MUD_FALL,   // 活板门关闭（示例）
            SoundEvents.BLOCK_MUD_BREAK,   // 活板门开启（示例）
            SoundEvents.BLOCK_HONEY_BLOCK_STEP,    // 压力板关闭
            SoundEvents.BLOCK_HONEY_BLOCK_PLACE ,  // 压力板开启
            SoundEvents.BLOCK_HONEY_BLOCK_STEP,   // 按钮关闭
            SoundEvents.BLOCK_HONEY_BLOCK_PLACE    // 按钮开启
    );
    private static BlockSetType register(String name, boolean handOpenable, boolean windChargeOpenable, boolean arrowsCanActivate, BlockSetType.ActivationRule activationRule, BlockSoundGroup soundGroup, SoundEvent doorClose, SoundEvent doorOpen, SoundEvent trapdoorClose, SoundEvent trapdoorOpen, SoundEvent pressurePlateOff, SoundEvent pressurePlateOn, SoundEvent buttonOff, SoundEvent buttonOn) {
        BlockSetType type = BlockSetTypeMixin.invokeConstructor(
                PoopSky.MOD_ID + ":" + name,
                handOpenable,
                windChargeOpenable,
                arrowsCanActivate,
                activationRule,
                soundGroup,
                doorClose,
                doorOpen,
                trapdoorClose,
                trapdoorOpen,
                pressurePlateOff,
                pressurePlateOn,
                buttonOff,
                buttonOn
        );
        return BlockSetTypeMixin.invokeRegister(type);
    }
}
