package com.altnoir.poopsky.game.client.arcade;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.content.item.p.GameDiskItem;
import com.altnoir.poopsky.game.Button;
import com.altnoir.poopsky.game.client.ArcadeControlSession;
import com.altnoir.poopsky.game.client.ClientGame;
import com.altnoir.poopsky.game.client.ClientGameTypes;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import com.mojang.blaze3d.vertex.VertexSorting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.RenderFrameEvent;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;

import java.util.HashMap;
import java.util.Map;

@OnlyIn(Dist.CLIENT)
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

    public static ResourceLocation getScreenTexture(BlockPos pos, ItemStack cartridge) {
        if (cartridge.isEmpty() || !(cartridge.getItem() instanceof GameDiskItem disc)) {
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
        if (cartridge.isEmpty() || !(cartridge.getItem() instanceof GameDiskItem disc)) {
            ScreenState state = SCREENS.remove(pos);
            if (state != null) {
                state.close();
            }
            return;
        }

        ScreenState state = getOrCreate(pos, disc);
        state.snapshot = snapshot;
        state.cartridgeClientGame.applySnapshot(snapshot);
    }

    /** 玩家按下街机按键（仅本地操作者）：转发给对应屏幕的客户端游戏做本地展示。 */
    public static void onButtonPressed(BlockPos pos, Button button) {
        ScreenState state = SCREENS.get(pos);
        if (state != null && state.cartridgeClientGame != null) {
            state.cartridgeClientGame.onButtonDown(button);
        }
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

    private static ScreenState getOrCreate(BlockPos pos, GameDiskItem disc) {
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
        RenderSystem.backupProjectionMatrix();
        Matrix4fStack modelViewStack = RenderSystem.getModelViewStack();
        modelViewStack.pushMatrix();
        modelViewStack.identity();
        modelViewStack.translation(0.0F, 0.0F, -2000.0F);
        RenderSystem.applyModelViewMatrix();

        state.target.setClearColor(0.0F, 0.0F, 0.0F, 1.0F);
        state.target.clear(false);
        state.target.bindWrite(true);
        RenderSystem.disableDepthTest();
        RenderSystem.setProjectionMatrix(
                new Matrix4f().setOrtho(0.0F, ClientGame.WIDTH, ClientGame.HEIGHT, 0.0F, 1000.0F, 3000.0F),
                VertexSorting.ORTHOGRAPHIC_Z
        );

        GuiGraphics graphics = new GuiGraphics(Minecraft.getInstance(), state.bufferSource);
        state.cartridgeClientGame.render(graphics, 0, 0);
        graphics.flush();

        NativeImage pixels = state.texture.getPixels();
        if (pixels != null) {
            state.target.bindRead();
            pixels.downloadTexture(0, true);
            state.target.unbindRead();
            state.texture.upload();
        }

        modelViewStack.popMatrix();
        RenderSystem.applyModelViewMatrix();
        RenderSystem.restoreProjectionMatrix();
        Minecraft.getInstance().getMainRenderTarget().bindWrite(true);
    }

    private static final class ScreenState {
        private final BlockPos pos;
        private final ResourceLocation textureLocation;
        private GameDiskItem cartridge;
        private ClientGame cartridgeClientGame;
        private CompoundTag snapshot;
        private RenderTarget target;
        private DynamicTexture texture;
        private ByteBufferBuilder bufferBuilder;
        private MultiBufferSource.BufferSource bufferSource;

        private ScreenState(BlockPos pos, GameDiskItem cartridge) {
            this.pos = pos;
            this.cartridge = cartridge;
            this.textureLocation = PoopSky.loc(texturePath(pos));
            this.cartridgeClientGame = ClientGameTypes.newGameFor(cartridge);
            this.cartridgeClientGame.setArcadeMachine(pos);
        }

        private void ensureResources() {
            if (target == null) {
                target = new RenderTarget(false) {
                };
                target.resize(TEXTURE_WIDTH, TEXTURE_HEIGHT, false);
            }
            if (texture == null) {
                texture = new DynamicTexture(TEXTURE_WIDTH, TEXTURE_HEIGHT, false);
                Minecraft.getInstance().getTextureManager().register(textureLocation, texture);
            }
            if (bufferBuilder == null) {
                bufferBuilder = new ByteBufferBuilder(786432);
                bufferSource = MultiBufferSource.immediate(bufferBuilder);
            }
        }

        private void close() {
            if (cartridgeClientGame != null) {
                cartridgeClientGame.onRemoved();
            }
            if (bufferBuilder != null) {
                bufferBuilder.close();
                bufferBuilder = null;
                bufferSource = null;
            }
            if (texture != null) {
                texture.close();
                texture = null;
            }
            if (target != null) {
                target.destroyBuffers();
                target = null;
            }
        }

        private static String texturePath(BlockPos pos) {
            return "dynamic/arcade_" + pos.getX() + "_" + pos.getY() + "_" + pos.getZ();
        }
    }
}
