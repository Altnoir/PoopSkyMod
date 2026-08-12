package com.altnoir.poopsky.impl.util;

import com.altnoir.poopsky.init.PoWorldPreset;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.worldselection.WorldCreationUiState;
import net.minecraft.core.Holder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Optional;

public final class ClientUtil {
    private ClientUtil() {
    }

    public static void renderBlock(GuiGraphicsExtractor guiGraphics, BlockState state, float x, float y, float z, float scale) {
        guiGraphics.item(new ItemStack(state.getBlock()), Math.round(x), Math.round(y));
    }

    public static boolean isPoopSkyWorldType(WorldCreationUiState uiState) {
        return isPoopSkyWorldType(uiState.getWorldType());
    }

    public static boolean isPoopSkyWorldType(WorldCreationUiState.WorldTypeEntry worldType) {
        return Optional.ofNullable(worldType.preset())
                .flatMap(Holder::unwrapKey)
                .filter(PoWorldPreset.POOPSKY::equals)
                .isPresent();
    }
}
