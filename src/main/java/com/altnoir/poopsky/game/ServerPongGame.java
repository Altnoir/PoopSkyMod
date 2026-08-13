package com.altnoir.poopsky.game;

import com.altnoir.poopsky.game.client.util.GameStage;
import com.altnoir.poopsky.init.PoSoundEvents;
import net.minecraft.nbt.CompoundTag;

public class ServerPongGame extends ServerGame {
    private double playerY = 70;
    private double opponentY = 70;
    private double ballX = 110;
    private double ballY = 78;
    private double ballVX = 2;
    private double ballVY = 2;
    private int opponentScore;
    private int ballTimer = 60;
    private float ballSpeed = 4.0F;

    @Override
    public void prepare() {
        playerY = 70;
        opponentY = 70;
        ballX = 110;
        ballY = 78;
        opponentScore = 0;
        ballTimer = 60;
        ballSpeed = 4.0F;
        resetBall();
        score = 0;
        stage = GameStage.START;
        ticks = 1;
    }

    private void resetBall() {
        ballX = 110;
        ballY = 78;
        ballSpeed = 4.0F;
        ballVX = random.nextBoolean() ? ballSpeed : -ballSpeed;
        ballVY = random.nextBoolean() ? ballSpeed : -ballSpeed;
        ballTimer = 60;
    }

    @Override
    protected void gameTick() {
        if (ticks % 20 == 0) {
            ballSpeed += 0.1F;
        }
        if (upDown) {
            playerY -= 3;
        }
        if (downDown) {
            playerY += 3;
        }
        playerY = Math.min(Math.max(playerY, 0), GAME_HEIGHT - 20);

        if (ballY < opponentY + 10) {
            opponentY -= 3;
        }
        if (ballY > opponentY + 10) {
            opponentY += 3;
        }
        opponentY = Math.min(Math.max(opponentY, 0), GAME_HEIGHT - 20);

        if (ballTimer > 0) {
            ballTimer--;
            return;
        }

        ballX += ballVX;
        ballY += ballVY;
        if (ballY <= 0 || ballY >= GAME_HEIGHT - 4) {
            ballVY = -ballVY;
            playSound(PoSoundEvents.JUMP.get(), 0.8F, 0.8F);
        }
        if (ballX <= 15 && ballY + 4 >= playerY && ballY <= playerY + 20) {
            ballVX = Math.abs(ballVX);
            playSound(PoSoundEvents.JUMP.get(), 1.0F, 1.0F);
        }
        if (ballX + 4 >= GAME_WIDTH - 15 && ballY + 4 >= opponentY && ballY <= opponentY + 20) {
            ballVX = -Math.abs(ballVX);
            playSound(PoSoundEvents.JUMP.get(), 1.0F, 1.0F);
        }
        if (ballX < 0) {
            opponentScore++;
            resetBall();
        }
        if (ballX + 4 > GAME_WIDTH) {
            score++;
            playSound(PoSoundEvents.POINT.get(), 1.0F, 0.7F);
            resetBall();
        }
        if (score >= 10) {
            stage = GameStage.WON;
            playSound(PoSoundEvents.NEW_BEST.get(), 1.5F, 2.0F);
        } else if (opponentScore >= 10) {
            stage = GameStage.DIED;
            playSound(PoSoundEvents.GAME_OVER.get(), 0.9F, 2.0F);
        }
    }

    @Override
    public CompoundTag writeSnapshot() {
        CompoundTag tag = super.writeSnapshot();
        tag.putDouble("playerY", playerY);
        tag.putDouble("opponentY", opponentY);
        tag.putDouble("ballX", ballX);
        tag.putDouble("ballY", ballY);
        tag.putInt("opponentScore", opponentScore);
        tag.putInt("ballTimer", ballTimer);
        return tag;
    }
}
