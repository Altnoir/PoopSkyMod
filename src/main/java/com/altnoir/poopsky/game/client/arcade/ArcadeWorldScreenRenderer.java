package com.altnoir.poopsky.game.client.arcade;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.content.item.p.GameDiscItem;
import com.altnoir.poopsky.game.client.ArcadeControlSession;
import com.altnoir.poopsky.game.client.ClientGame;
import com.altnoir.poopsky.game.client.ClientGameTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.RenderFrameEvent;

import java.util.HashMap;
import java.util.Map;

public final class ArcadeWorldScreenRenderer {
    private static final int TEXTURE_WIDTH = 448;
    private static final int TEXTURE_HEIGHT = 320;

    private static final Map<BlockPos, ScreenState> SCREENS = new HashMap<>();

    private ArcadeWorldScreenRenderer() {
    }

    public static void onRenderFrame(RenderFrameEvent.Pre event) {
        renderFrame();
    }

    public static void onLoggingOut(ClientPlayerNetworkEvent.LoggingOut event) {
        ArcadeControlSession.clear();
        onClientStop();
    }

    public static Identifier getScreenTexture(BlockPos pos, ItemStack cartridge) {
        if (cartridge.isEmpty() || !(cartridge.getItem() instanceof GameDiscItem disc)) {
            ScreenState state = SCREENS.remove(pos);
            if (state != null) {
                state.close();
            }
            return null;
        }

        ScreenState state = getOrCreate(pos, disc);
        state.ensureResources();
        return state.textureLocation;
    }

    public static void applyRemoteSnapshot(BlockPos pos, ItemStack cartridge, CompoundTag snapshot) {
        if (cartridge.isEmpty() || !(cartridge.getItem() instanceof GameDiscItem disc)) {
            ScreenState state = SCREENS.remove(pos);
            if (state != null) {
                state.close();
            }
            return;
        }

        ScreenState state = getOrCreate(pos, disc);
        state.snapshot = snapshot;
    }

    public static void onClientStop() {
        SCREENS.values().forEach(ScreenState::close);
        SCREENS.clear();
    }

    private static void renderFrame() {
        if (Minecraft.getInstance().level == null) {
            return;
        }

        for (ScreenState state : SCREENS.values()) {
            if (state.cartridgeClientGame == null || state.snapshot == null || state.snapshot.isEmpty()) {
                continue;
            }
            state.ensureResources();
            renderState(state);
        }
    }

    private static ScreenState getOrCreate(BlockPos pos, GameDiscItem disc) {
        ScreenState state = SCREENS.get(pos);
        if (state != null && state.cartridge == disc) {
            return state;
        }

        if (state != null) {
            state.close();
        }
        state = new ScreenState(pos, disc);
        SCREENS.put(pos, state);
        return state;
    }

    private static void renderState(ScreenState state) {
        if (state.cartridgeClientGame == null) {
            return;
        }
        state.cartridgeClientGame.applySnapshot(state.snapshot);
    }

    private static final class ScreenState {
        private final BlockPos pos;
        private final Identifier textureLocation;
        private GameDiscItem cartridge;
        private ClientGame cartridgeClientGame;
        private CompoundTag snapshot;
        private DynamicTexture texture;

        private ScreenState(BlockPos pos, GameDiscItem cartridge) {
            this.pos = pos;
            this.cartridge = cartridge;
            this.textureLocation = PoopSky.loc(texturePath(pos));
            this.cartridgeClientGame = ClientGameTypes.newGameFor(cartridge);
            this.cartridgeClientGame.setArcadeMachine(pos);
        }

        private void ensureResources() {
            if (texture == null) {
                texture = new DynamicTexture("poopsky arcade", TEXTURE_WIDTH, TEXTURE_HEIGHT, false);
                Minecraft.getInstance().getTextureManager().register(textureLocation, texture);
            }
        }

        private void close() {
            if (texture != null) {
                texture.close();
                texture = null;
            }
        }

        private static String texturePath(BlockPos pos) {
            return "dynamic/arcade_" + pos.getX() + "_" + pos.getY() + "_" + pos.getZ();
        }
    }
}
