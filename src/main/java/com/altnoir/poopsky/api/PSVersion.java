package com.altnoir.poopsky.api;

import com.mojang.logging.LogUtils;
import org.slf4j.Logger;

public final class PSVersion {
    private static final Logger LOGGER = LogUtils.getLogger();

    public static final String MOD_VERSION = getModVersion();
    public static final String MINECRAFT_VERSION = "1.21.1";
    public static final String NEO_FORGE_VERSION = "21.1.219";

    public static final int MC_MAJOR = 1;
    public static final int MC_MINOR = 21;
    public static final int MC_PATCH = 1;
    public static final int NEO_MAJOR = 21;
    public static final int NEO_MINOR = 1;
    public static final int NEO_BUILD = 219;

    private PSVersion() {
    }

    private static String getModVersion() {
        try {
            var props = new java.util.Properties();
            props.load(PSVersion.class.getResourceAsStream("/poopsky.version"));
            return props.getProperty("mod_version", "UNKNOWN");
        } catch (Exception e) {
            LOGGER.warn("Failed to load mod version from poopsky.version, using fallback");
            return "${mod_version}";
        }
    }

    public static boolean isAtLeastMC(int major, int minor) {
        if (MC_MAJOR != major) return MC_MAJOR > major;
        return MC_MINOR >= minor;
    }

    public static boolean isAtLeastNeo(int major, int minor) {
        if (NEO_MAJOR != major) return NEO_MAJOR > major;
        return NEO_MINOR >= minor;
    }

    public static boolean isMCVersion(int major, int minor, int patch) {
        return MC_MAJOR == major && MC_MINOR == minor && MC_PATCH == patch;
    }

    public static void logVersionInfo() {
        LOGGER.info("PoopSky v{} for Minecraft {} / NeoForge {}", MOD_VERSION, MINECRAFT_VERSION, NEO_FORGE_VERSION);
    }
}