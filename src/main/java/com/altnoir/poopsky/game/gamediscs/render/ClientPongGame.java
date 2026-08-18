package com.altnoir.poopsky.game.gamediscs.render;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.game.client.ClientGame;
import com.altnoir.poopsky.game.client.graphics.MultiImage;
import com.altnoir.poopsky.game.client.graphics.Sprite;
import com.altnoir.poopsky.game.model.PongGameState;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec2;

public class ClientPongGame extends ClientGame {
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
    private final Sprite numberRenderer = new Sprite(
            new Vec2(0, 0),
            new Vec2(8, 12),
            new MultiImage(PoopSky.loc("textures/games/sprite/numbers.png"), 8, 120, 10)
    );
    private CompoundTag lastSnapshot;
    private double previousPlayerY;
    private double previousOpponentY;
    private double previousBallX;
    private double previousBallY;
    private boolean hasPrevious;

    public ClientPongGame() {
        super();
    }

    @Override
    public void applySnapshot(CompoundTag tag) {
        if (tag == null) {
            return;
        }
        if (lastSnapshot != null && tag.equals(lastSnapshot)) {
            return;
        }

        double newPlayerY = tag.getDouble("playerY");
        double newOpponentY = tag.getDouble("opponentY");
        double newBallX = tag.getDouble("ballX");
        double newBallY = tag.getDouble("ballY");
        int newBallTimer = tag.getInt("ballTimer");

        boolean continuous = hasPrevious
                && newBallTimer <= state.getBallTimer()
                && Math.abs(newBallX - state.getBallX()) <= 12
                && Math.abs(newBallY - state.getBallY()) <= 12;
        if (!continuous) {
            previousPlayerY = newPlayerY;
            previousOpponentY = newOpponentY;
            previousBallX = newBallX;
            previousBallY = newBallY;
        } else {
            previousPlayerY = state.getPlayerY();
            previousOpponentY = state.getOpponentY();
            previousBallX = state.getBallX();
            previousBallY = state.getBallY();
        }

        super.applySnapshot(tag);
        state.applySnapshot(tag);
        lastSnapshot = tag.copy();
        hasPrevious = true;
    }

    @Override
    protected void renderGame(GuiGraphics graphics, int posX, int posY, float partialTick) {
        double delta = Mth.clamp(partialTick, 0.0F, 1.0F);
        player.setY((float) (previousPlayerY + (state.getPlayerY() - previousPlayerY) * delta));
        opponent.setY((float) (previousOpponentY + (state.getOpponentY() - previousOpponentY) * delta));
        ball.setPos(new Vec2(
                (float) (previousBallX + (state.getBallX() - previousBallX) * delta),
                (float) (previousBallY + (state.getBallY() - previousBallY) * delta)
        ));
        player.render(graphics, posX, posY);
        opponent.render(graphics, posX, posY);
        ball.render(graphics, posX, posY);

        if (getScore() < 10) {
            if (numberRenderer.getImage() instanceof MultiImage image) {
                image.setImage(getScore());
            }
        } else {
            if (numberRenderer.getImage() instanceof MultiImage image) {
                image.setImage(1);
            }
            numberRenderer.setPos(new Vec2((float) WIDTH / 2 - numberRenderer.getWidth() * 2 - 4 * 2, 4));
            numberRenderer.render(graphics, posX, posY);
            if (numberRenderer.getImage() instanceof MultiImage image) {
                image.setImage(0);
            }
        }
        numberRenderer.setPos(new Vec2((float) WIDTH / 2 - numberRenderer.getWidth() - 4, 4));
        numberRenderer.render(graphics, posX, posY);

        if (state.getOpponentScore() < 10) {
            if (numberRenderer.getImage() instanceof MultiImage image) {
                image.setImage(state.getOpponentScore());
            }
            numberRenderer.setPos(new Vec2((float) WIDTH / 2 + 4, 4));
            numberRenderer.render(graphics, posX, posY);
        } else {
            if (numberRenderer.getImage() instanceof MultiImage image) {
                image.setImage(1);
            }
            numberRenderer.setPos(new Vec2((float) WIDTH / 2 + 4, 4));
            numberRenderer.render(graphics, posX, posY);
            if (numberRenderer.getImage() instanceof MultiImage image) {
                image.setImage(0);
            }
            numberRenderer.setPos(new Vec2((float) WIDTH / 2 + numberRenderer.getWidth() + 4 * 2, 4));
            numberRenderer.render(graphics, posX, posY);
        }
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
