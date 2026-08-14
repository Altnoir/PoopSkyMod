package com.altnoir.poopsky.game.client.gamediscs;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.game.client.graphics.MultiImage;
import com.altnoir.poopsky.game.client.ClientGame;
import com.altnoir.poopsky.game.util.Sprite;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec2;

public class ClientPongGame extends ClientGame {
    private final Sprite player = new Sprite(
            new Vec2(10, (float) HEIGHT / 2 - 10),
            new Vec2(5, 20),
            ResourceLocation.fromNamespaceAndPath("minecraft", "textures/block/white_concrete.png")
    );
    private final Sprite oponent = new Sprite(
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

    private int oponentScore;

    public ClientPongGame() {
        super();
    }

    @Override
    public void applySnapshot(CompoundTag tag) {
        super.applySnapshot(tag);
        player.setY((float) tag.getDouble("playerY"));
        oponent.setY((float) tag.getDouble("opponentY"));
        ball.setPos(new Vec2((float) tag.getDouble("ballX"), (float) tag.getDouble("ballY")));
        oponentScore = tag.getInt("opponentScore");
    }

    @Override
    public void render(GuiGraphics graphics, int posX, int posY) {
        super.render(graphics, posX, posY);
        player.render(graphics, posX, posY);
        oponent.render(graphics, posX, posY);
        ball.render(graphics, posX, posY);

        if (score < 10) {
            if (numberRenderer.getImage() instanceof MultiImage image) {
                image.setImage(score);
            }
            numberRenderer.setPos(new Vec2((float) WIDTH / 2 - numberRenderer.getWidth() - 4, 4));
            numberRenderer.render(graphics, posX, posY);
        } else {
            if (numberRenderer.getImage() instanceof MultiImage image) {
                image.setImage(1);
            }
            numberRenderer.setPos(new Vec2((float) WIDTH / 2 - numberRenderer.getWidth() * 2 - 4 * 2, 4));
            numberRenderer.render(graphics, posX, posY);
            if (numberRenderer.getImage() instanceof MultiImage image) {
                image.setImage(0);
            }
            numberRenderer.setPos(new Vec2((float) WIDTH / 2 - numberRenderer.getWidth() - 4, 4));
            numberRenderer.render(graphics, posX, posY);
        }

        if (oponentScore < 10) {
            if (numberRenderer.getImage() instanceof MultiImage image) {
                image.setImage(oponentScore);
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

        renderOverlay(graphics, posX, posY);
    }

    @Override
    public ResourceLocation getBackground() {
        return PoopSky.loc("textures/games/background/pong_background.png");
    }

    @Override
    public boolean showScore() {
        return false;
    }

}
