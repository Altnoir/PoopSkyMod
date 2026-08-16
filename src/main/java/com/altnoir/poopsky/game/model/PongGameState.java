package com.altnoir.poopsky.game.model;

import com.altnoir.poopsky.game.Game;
import net.minecraft.nbt.CompoundTag;

import java.util.Random;

public final class PongGameState {
    private static final int PADDLE_HEIGHT = 20;
    private static final int BALL_SIZE = 4;
    private static final double PLAYER_X = 10;
    private static final double OPPONENT_X = Game.WIDTH - 15;
    private static final double INITIAL_BALL_X = 110;
    private static final double INITIAL_BALL_Y = 78;
    private static final int INITIAL_BALL_TIMER = 60;
    private static final float INITIAL_BALL_SPEED = 3.0F;

    private double playerY = 70;
    private double opponentY = 70;
    private double ballX = INITIAL_BALL_X;
    private double ballY = INITIAL_BALL_Y;
    private double ballVX = 2;
    private double ballVY = 2;
    private int opponentScore;
    private int ballTimer = INITIAL_BALL_TIMER;
    private float ballSpeed = INITIAL_BALL_SPEED;

    public void prepare(Random random) {
        playerY = 70;
        opponentY = 70;
        ballX = INITIAL_BALL_X;
        ballY = INITIAL_BALL_Y;
        opponentScore = 0;
        ballTimer = INITIAL_BALL_TIMER;
        ballSpeed = INITIAL_BALL_SPEED;
        resetBall(random);
    }

    public TickResult tick(int gameTicks, boolean up, boolean down, Random random) {
        if (gameTicks % 20 == 0) {
            ballSpeed += 0.1F;
        }
        if (up) {
            playerY -= 3;
        }
        if (down) {
            playerY += 3;
        }
        playerY = Math.clamp(playerY, 0, Game.HEIGHT - PADDLE_HEIGHT);

        if (ballY < opponentY + 10) {
            opponentY -= 3;
        }
        if (ballY > opponentY + 10) {
            opponentY += 3;
        }
        opponentY = Math.clamp(opponentY, 0, Game.HEIGHT - PADDLE_HEIGHT);

        if (ballTimer > 0) {
            ballTimer--;
            return new TickResult(false, false, false, false, false);
        }

        ballX += ballVX;
        ballY += ballVY;

        boolean wallBounce = false;
        boolean playerBounce = false;
        boolean opponentBounce = false;
        if (ballY <= 0 || ballY >= Game.HEIGHT - BALL_SIZE) {
            ballVY = -ballVY;
            wallBounce = true;
        }
        if (ballX <= PLAYER_X + 5 && ballY + BALL_SIZE >= playerY && ballY <= playerY + PADDLE_HEIGHT) {
            ballVX = Math.abs(ballVX);
            playerBounce = true;
        }
        if (ballX + BALL_SIZE >= OPPONENT_X && ballY + BALL_SIZE >= opponentY && ballY <= opponentY + PADDLE_HEIGHT) {
            ballVX = -Math.abs(ballVX);
            opponentBounce = true;
        }

        if (ballX < 0) {
            opponentScore++;
            resetBall(random);
            return new TickResult(wallBounce, playerBounce, opponentBounce, false, true);
        }
        if (ballX + BALL_SIZE > Game.WIDTH) {
            resetBall(random);
            return new TickResult(wallBounce, playerBounce, opponentBounce, true, false);
        }

        return new TickResult(wallBounce, playerBounce, opponentBounce, false, false);
    }

    public double getPlayerY() {
        return playerY;
    }

    public double getOpponentY() {
        return opponentY;
    }

    public double getBallX() {
        return ballX;
    }

    public double getBallY() {
        return ballY;
    }

    public int getOpponentScore() {
        return opponentScore;
    }

    public int getBallTimer() {
        return ballTimer;
    }

    public void writeSnapshot(CompoundTag tag) {
        tag.putDouble("playerY", playerY);
        tag.putDouble("opponentY", opponentY);
        tag.putDouble("ballX", ballX);
        tag.putDouble("ballY", ballY);
        tag.putInt("opponentScore", opponentScore);
        tag.putInt("ballTimer", ballTimer);
    }

    public void applySnapshot(CompoundTag tag) {
        playerY = tag.getDouble("playerY");
        opponentY = tag.getDouble("opponentY");
        ballX = tag.getDouble("ballX");
        ballY = tag.getDouble("ballY");
        opponentScore = tag.getInt("opponentScore");
        ballTimer = tag.getInt("ballTimer");
        ballVX = 2;
        ballVY = 2;
        ballSpeed = INITIAL_BALL_SPEED;
    }

    private void resetBall(Random random) {
        ballX = INITIAL_BALL_X;
        ballY = INITIAL_BALL_Y;
        ballSpeed = INITIAL_BALL_SPEED;
        ballVX = random.nextBoolean() ? ballSpeed : -ballSpeed;
        ballVY = random.nextBoolean() ? ballSpeed : -ballSpeed;
        ballTimer = INITIAL_BALL_TIMER;
    }

    public record TickResult(
            boolean wallBounce,
            boolean playerBounce,
            boolean opponentBounce,
            boolean playerScored,
            boolean opponentScored
    ) {
    }
}
