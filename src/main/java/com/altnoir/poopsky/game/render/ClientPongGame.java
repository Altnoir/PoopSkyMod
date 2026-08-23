package com.altnoir.poopsky.game.render;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.game.client.ClientGame;
import com.altnoir.poopsky.game.client.graphics.MultiImage;
import com.altnoir.poopsky.game.client.graphics.Sprite;
import com.altnoir.poopsky.game.model.PongGameState;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec2;

public class ClientPongGame extends ClientGame {
    private static final long DEFAULT_INTERPOLATION_TIME = 50_000_000L;
    private final PongGameState state = new PongGameState();
    private final Sprite player = new Sprite(
            new Vec2(10, (float) HEIGHT / 2 - 10),
            new Vec2(5, 20),
            ResourceLocation.fromNamespaceAndPath("minecraft", "textures/block/white_concrete.png")
    );
    private final Sprite opponent = new Sprite(
            new Vec2(WIDTH - 15, (float) HEIGHT / 2 - 10),
            new Vec2(5, 20),
            ResourceLocation.fromNamespaceAndPath("minecraft", "textures/block/white_concrete.png")
    );
    private final Sprite ball = new Sprite(
            new Vec2((float) WIDTH / 2 - 2, (float) HEIGHT / 2 - 2),
            new Vec2(4, 4),
            ResourceLocation.fromNamespaceAndPath("minecraft", "textures/block/white_concrete.png")
    );
    private final MultiImage numbers = new MultiImage(PoopSky.loc("textures/games/sprite/numbers.png"), 8, 120, 10);
    private final Sprite numberRenderer = new Sprite(new Vec2(0, 0), new Vec2(8, 12), numbers);
    private CompoundTag lastSnapshot;
    private double previousPlayerY;
    private double previousOpponentY;
    private double previousBallX;
    private double previousBallY;
    private long interpolationStartNanos;
    private long lastSnapshotNanos;
    private long interpolationTime = DEFAULT_INTERPOLATION_TIME;
    private boolean hasPrevious;

    @Override
    public void applySnapshot(CompoundTag tag) {
        if (tag == null || tag.equals(lastSnapshot)) {
            return;
        }

        double newPlayerY = tag.getDouble("playerY");
        double newOpponentY = tag.getDouble("opponentY");
        double newBallX = tag.getDouble("ballX");
        double newBallY = tag.getDouble("ballY");
        int newBallTimer = tag.getInt("ballTimer");

        previousPlayerY = hasPrevious ? state.getPlayerY() : newPlayerY;
        previousOpponentY = hasPrevious ? state.getOpponentY() : newOpponentY;

        boolean continuous = hasPrevious
                && newBallTimer <= state.getBallTimer()
                && Math.abs(newBallX - state.getBallX()) <= 12
                && Math.abs(newBallY - state.getBallY()) <= 12;
        previousBallX = continuous ? state.getBallX() : newBallX;
        previousBallY = continuous ? state.getBallY() : newBallY;

        long now = System.nanoTime();
        if (lastSnapshotNanos != 0) {
            interpolationTime = Math.clamp(now - lastSnapshotNanos, 25_000_000L, 100_000_000L);
        }
        lastSnapshotNanos = now;
        interpolationStartNanos = now;

        super.applySnapshot(tag);
        state.applySnapshot(tag);
        lastSnapshot = tag.copy();
        hasPrevious = true;
    }

    @Override
    protected void renderGame(GuiGraphics graphics, int posX, int posY, float partialTick) {
        double delta = Math.min((System.nanoTime() - interpolationStartNanos) / (double) interpolationTime, 1.0);
        player.setY((float) (previousPlayerY + (state.getPlayerY() - previousPlayerY) * delta));
        opponent.setY((float) (previousOpponentY + (state.getOpponentY() - previousOpponentY) * delta));
        ball.setPos(new Vec2(
                (float) (previousBallX + (state.getBallX() - previousBallX) * delta),
                (float) (previousBallY + (state.getBallY() - previousBallY) * delta)
        ));

        player.render(graphics, posX, posY);
        opponent.render(graphics, posX, posY);
        ball.render(graphics, posX, posY);
        renderScore(graphics, posX, posY, getScore(), true);
        renderScore(graphics, posX, posY, state.getOpponentScore(), false);
    }

    private void renderScore(GuiGraphics graphics, int posX, int posY, int score, boolean left) {
        float width = numberRenderer.getWidth();
        float x = left
                ? (float) WIDTH / 2 - (score >= 10 ? width * 2 + 8 : width + 4)
                : (float) WIDTH / 2 + 4;

        if (score >= 10) {
            numbers.setImage(score / 10);
            numberRenderer.setPos(new Vec2(x, 4));
            numberRenderer.render(graphics, posX, posY);
            x += width + 4;
        }

        numbers.setImage(score % 10);
        numberRenderer.setPos(new Vec2(x, 4));
        numberRenderer.render(graphics, posX, posY);
    }

    @Override
    public ResourceLocation getBackground() {
        return PoopSky.loc("textures/games/background/pong_background.png");
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