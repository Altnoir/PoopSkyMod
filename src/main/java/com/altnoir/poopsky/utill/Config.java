package com.altnoir.poopsky.utill;

import net.neoforged.neoforge.common.ModConfigSpec;

public class Config {
    public static ModConfigSpec COMMON_CONFIG;
    public static ModConfigSpec.IntValue MAX_SPEED;
    public static ModConfigSpec.BooleanValue ADVANCED_MODE;
    public static ModConfigSpec.BooleanValue AUTO_PERSPECTIVE;
    public static ModConfigSpec.BooleanValue SHIFT_TO_DISMOUNT;
    static {
        ModConfigSpec.Builder COMMON_BUILDER = new ModConfigSpec.Builder();
        COMMON_BUILDER.comment("General settings").push("general");
        MAX_SPEED = COMMON_BUILDER.comment("The speed value refers to the percentage of the default maximum speed.The default value is 100%").defineInRange("max_speed", 100, 0, Integer.MAX_VALUE);
        ADVANCED_MODE = COMMON_BUILDER.comment("When this value equals false, you don't need experience to get on and fly the broom.").define("advanced_mode",true);
        AUTO_PERSPECTIVE = COMMON_BUILDER.comment("When this value equals false, perspective will not be changed automatically when you get on the broom.").define("auto_perspective",true);
        SHIFT_TO_DISMOUNT = COMMON_BUILDER.comment("When this value equals false, you can bind the key 'shift' to descend the broom").define("shift_key_to_dismount",true);
        COMMON_BUILDER.pop();
        COMMON_CONFIG = COMMON_BUILDER.build();
    }
}
