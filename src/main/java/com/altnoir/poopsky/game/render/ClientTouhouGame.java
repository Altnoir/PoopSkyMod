package com.altnoir.poopsky.game.render;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.game.GameStage;
import com.altnoir.poopsky.game.client.ClientGame;
import com.altnoir.poopsky.game.client.graphics.Sprite;
import com.altnoir.poopsky.game.model.TouhouGameState;
import com.altnoir.poopsky.init.PoKeyBoardInput;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec2;

public class ClientTouhouGame extends ClientGame {
    private static final int GAME_OFFSET_X = 8;
    private static final ResourceLocation PLAYER_TEXTURE = PoopSky.loc("textures/games/sprite/touhou_player.png");
    private static final ResourceLocation PLAYER_BULLET_TEXTURE = PoopSky.loc("textures/games/sprite/touhou_player_bullet.png");
    private static final ResourceLocation SPEED_POWERUP_TEXTURE = PoopSky.loc("textures/games/sprite/touhou_player_speed.png");
    private static final ResourceLocation DOUBLE_POWERUP_TEXTURE = PoopSky.loc("textures/games/sprite/touhou_player_double.png");
    private static final ResourceLocation BOSS_TEXTURE = PoopSky.loc("textures/games/sprite/touhou_boss.png");
    private static final ResourceLocation BOSS_BULLET_TEXTURE = PoopSky.loc("textures/games/sprite/touhou_boss_bullet.png");

    private final TouhouGameState state = new TouhouGameState();
    private final Sprite player = new Sprite(Vec2.ZERO, new Vec2(TouhouGameState.PLAYER_SIZE, TouhouGameState.PLAYER_SIZE), PLAYER_TEXTURE);
    private final Sprite boss = new Sprite(Vec2.ZERO, new Vec2(TouhouGameState.BOSS_SIZE, TouhouGameState.BOSS_SIZE), BOSS_TEXTURE);
    private final Sprite bossBullet = new Sprite(Vec2.ZERO, new Vec2(TouhouGameState.BOSS_BULLET_SIZE, TouhouGameState.BOSS_BULLET_SIZE), BOSS_BULLET_TEXTURE);
    private final Sprite playerBullet = new Sprite(Vec2.ZERO, new Vec2(TouhouGameState.PLAYER_BULLET_SIZE, TouhouGameState.PLAYER_BULLET_SIZE), PLAYER_BULLET_TEXTURE);
    private final Sprite speedPowerUp = new Sprite(Vec2.ZERO, new Vec2(TouhouGameState.POWERUP_SIZE, TouhouGameState.POWERUP_SIZE), SPEED_POWERUP_TEXTURE);
    private final Sprite doublePowerUp = new Sprite(Vec2.ZERO, new Vec2(TouhouGameState.POWERUP_SIZE, TouhouGameState.POWERUP_SIZE), DOUBLE_POWERUP_TEXTURE);

    @Override
    public void applySnapshot(CompoundTag tag) {
        super.applySnapshot(tag);
        state.applySnapshot(tag);
    }

    @Override
    protected void renderGame(GuiGraphics graphics, int posX, int posY) {
        for (TouhouGameState.Bullet bullet : state.getEnemyBullets()) {
            bossBullet.setPos(new Vec2(GAME_OFFSET_X + bullet.x(), bullet.y()));
            bossBullet.render(graphics, posX, posY);
        }

        for (TouhouGameState.Bullet bullet : state.getPlayerBullets()) {
            playerBullet.setPos(new Vec2(GAME_OFFSET_X + bullet.x(), bullet.y()));
            playerBullet.render(graphics, posX, posY);
        }

        for (TouhouGameState.PowerUp powerUp : state.getPowerUps()) {
            Sprite powerUpSprite = powerUp.type() == TouhouGameState.PowerUpType.SPEED ? speedPowerUp : doublePowerUp;
            powerUpSprite.setPos(new Vec2(GAME_OFFSET_X + powerUp.x(), powerUp.y()));
            powerUpSprite.render(graphics, posX, posY);
        }

        player.setPos(new Vec2(GAME_OFFSET_X + state.getPlayerX(), state.getPlayerY()));
        player.render(graphics, posX, posY);

        if (getStage() == GameStage.PLAYING && state.getBossSpawnTimer() == 0) {
            float scale = state.getBossScale() * state.getBossHitScale();
            float scaledSize = TouhouGameState.BOSS_SIZE * scale;
            float offset = (TouhouGameState.BOSS_SIZE - scaledSize) / 2.0F;
            boss.setPos(new Vec2(
                    GAME_OFFSET_X + state.getBossX() + offset,
                    state.getBossY() + offset
            ));
            boss.renderScaled(graphics, posX, posY, scale);
        }

        renderInfo(graphics, posX, posY);
    }

    private void renderInfo(GuiGraphics graphics, int posX, int posY) {
        var font = Minecraft.getInstance().font;
        int infoX = posX + GAME_OFFSET_X + TouhouGameState.PLAY_WIDTH + 8;
        int infoY = posY + 8;

        graphics.drawString(font, "SCORE", infoX, infoY, 0xFFFFFF);
        graphics.drawString(font, String.valueOf(state.getScore()), infoX, infoY + 10, 0xFFD700);

        graphics.drawString(font, "WAVE", infoX, infoY + 30, 0xFFFFFF);
        graphics.drawString(font, String.valueOf(state.getWave() + 1), infoX, infoY + 40, 0x87CEEB);

        if (state.getBossSpawnTimer() > 0) {
            graphics.drawString(font, "NEXT BOSS", infoX, infoY + 60, 0xFFFFFF);
        } else {
            graphics.drawString(font, "BOSS HP", infoX, infoY + 60, 0xFFFFFF);
            int hp = Math.max(0, state.getBossHp());
            int maxHp = Math.max(1, state.getBossMaxHp());
            graphics.fill(infoX, infoY + 70, infoX + 56, infoY + 76, 0xFF333333);
            graphics.fill(infoX, infoY + 70, infoX + (int) (56.0F * hp / maxHp), infoY + 76, 0xFFCC2222);
        }

        Component shootHint = Component.translatable(
                "gui.gamingconsole.touhou.shoot",
                PoKeyBoardInput.ARCADE_BUTTON1.getTranslatedKeyMessage()
        );
        graphics.drawString(font, shootHint, infoX, infoY + 86, 0xFFFFFF);
        Component slowHint = Component.translatable(
                "gui.gamingconsole.touhou.slow",
                PoKeyBoardInput.ARCADE_BUTTON2.getTranslatedKeyMessage()
        );
        graphics.drawString(font, slowHint, infoX, infoY + 96, 0xFFFFFF);

        if (state.hasSpeedBoost()) {
            graphics.drawString(font, "SPEED UP", infoX, infoY + 110, 0x55FF55);
        }
        if (state.hasDoubleShot()) {
            graphics.drawString(font, "DOUBLE SHOT", infoX, infoY + 120, 0x55AAFF);
        }
    }

    @Override
    public ResourceLocation getBackground() {
        return PoopSky.loc("textures/games/background/touhou_background.png");
    }

    @Override
    public boolean showScore() {
        return false;
    }

    @Override
    public boolean requiresPerFrameRender() {
        return true;
    }
}
