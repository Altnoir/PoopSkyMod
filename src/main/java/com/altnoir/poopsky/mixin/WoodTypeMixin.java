package com.altnoir.poopsky.mixin;

import net.minecraft.block.BlockSetType;
import net.minecraft.block.WoodType;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(WoodType.class)
public interface WoodTypeMixin {
    @Invoker("<init>")
    static WoodType invokeConstructor(String name, BlockSetType setType, BlockSoundGroup soundType, BlockSoundGroup hangingSignSoundType, SoundEvent fenceGateClose, SoundEvent fenceGateOpen) {
        throw new AssertionError();
    }

    @Invoker("register")
    static WoodType invokeRegister(WoodType woodType) {
        throw new AssertionError();
    }
}
