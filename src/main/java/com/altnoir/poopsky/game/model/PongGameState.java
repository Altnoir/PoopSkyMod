package com.altnoir.poopsky.game.model;

import com.altnoir.poopsky.game.Game;
import net.minecraft.nbt.CompoundTag;

import java.util.Random;

public final class PongGameState {
    private static final int PADDLE_HEIGHT = 20;
    private static final int BALL_SIZE = 4;
    private static final double PLAYER_X = 10;
    private static final double OPPONENT_X = Game.WIDTH - 15;
    private static final double PLAYER_SPEED = 4;
    private static final double OPPONENT_SPEED = 2.7;
    private static final double OPPONENT_DEAD_ZONE = 2;
    private static final double INITIAL_BALL_X = 110;
    private static final double INITIAL_BALL_Y = 78;
    private static final int INITIAL_BALL_TIMER = 60;
    private static final double INITIAL_BALL_SPEED = 3;

    private double playerY = 70;
    private double opponentY = 70;
    private double ballX = INITIAL_BALL_X;
    private double ballY = INITIAL_BALL_Y;
    private double ballVX = INITIAL_BALL_SPEED;
    private double ballVY = INITIAL_BALL_SPEED;
    private int opponentScore;
    private int ballTimer = INITIAL_BALL_TIMER;

    public void prepare(Random random) {
        playerY = 70;
        opponentY = 70;
        ballX = INITIAL_BALL_X;
        ballY = INITIAL_BALL_Y;
        opponentScore = 0;
        resetBall(random);
    }

    public TickResult tick(boolean up, boolean down, Random random) {
        if (up) {
            playerY -= PLAYER_SPEED;
        }
        if (down) {
            playerY += PLAYER_SPEED;
        }
        playerY = Math.clamp(playerY, 0, Game.HEIGHT - PADDLE_HEIGHT);

        updateOpponent();

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
            ballY = Math.clamp(ballY, 0, Game.HEIGHT - BALL_SIZE);
            ballVY = -ballVY;
            wallBounce = true;
        }
        if (ballVX < 0 && ballX <= PLAYER_X + 5 && ballX + BALL_SIZE >= PLAYER_X
                && ballY + BALL_SIZE >= playerY && ballY <= playerY + PADDLE_HEIGHT) {
            ballX = PLAYER_X + 5;
            ballVX = Math.abs(ballVX);
            playerBounce = true;
        }
        if (ballVX > 0 && ballX + BALL_SIZE >= OPPONENT_X && ballX <= OPPONENT_X + 5
                && ballY + BALL_SIZE >= opponentY && ballY <= opponentY + PADDLE_HEIGHT) {
            ballX = OPPONENT_X - BALL_SIZE;
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

    private void updateOpponent() {
        double targetY = ballVX > 0 && ballTimer == 0
                ? ballY + (double) BALL_SIZE / 2 - (double) PADDLE_HEIGHT / 2
                : (double) (Game.HEIGHT - PADDLE_HEIGHT) / 2;
        double delta = targetY - opponentY;

        if (Math.abs(delta) > OPPONENT_DEAD_ZONE) {
            opponentY += Math.copySign(Math.min(Math.abs(delta), OPPONENT_SPEED), delta);
        }
        opponentY = Math.clamp(opponentY, 0, Game.HEIGHT - PADDLE_HEIGHT);
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
        playerY = tag.getDoubleOr("playerY", 70.0);
        opponentY = tag.getDoubleOr("opponentY", 70.0);
        ballX = tag.getDoubleOr("ballX", INITIAL_BALL_X);
        ballY = tag.getDoubleOr("ballY", INITIAL_BALL_Y);
        opponentScore = tag.getIntOr("opponentScore", 0);
        ballTimer = tag.getIntOr("ballTimer", 0);
    }

    private void resetBall(Random random) {
        ballX = INITIAL_BALL_X;
        ballY = INITIAL_BALL_Y;
        ballVX = random.nextBoolean() ? INITIAL_BALL_SPEED : -INITIAL_BALL_SPEED;
        ballVY = random.nextBoolean() ? INITIAL_BALL_SPEED : -INITIAL_BALL_SPEED;
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
