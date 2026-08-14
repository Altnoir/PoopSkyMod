package com.altnoir.poopsky.game.client.gamediscs;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.game.client.graphics.AnimatedImage;
import com.altnoir.poopsky.game.client.ClientGame;
import com.altnoir.poopsky.game.util.Sprite;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec2;

import java.util.ArrayList;
import java.util.List;

public class ClientFlappyBirdGame extends ClientGame {
    private Sprite bird = new Sprite(new Vec2(20, 30), new Vec2(10, 8),
            new AnimatedImage(PoopSky.loc("textures/games/sprite/bird.png"), 10, 32, 4, 2));
    private final List<Sprite> pipes = new ArrayList<>();
    private final Sprite ground = new Sprite(new Vec2(0, HEIGHT - 16), new Vec2(156, 16),
            PoopSky.loc("textures/games/sprite/ground.png"));
    private int pipeSpawnTimer;

    public ClientFlappyBirdGame() {
        super();
    }

    @Override
    public void applySnapshot(CompoundTag tag) {
        super.applySnapshot(tag);
        bird.setPos(new Vec2((float) tag.getDouble("birdX"), (float) tag.getDouble("birdY")));
        bird.setVelocity(new Vec2(0, (float) tag.getDouble("birdVY")));
        ground.setX((float) tag.getDouble("groundX"));
        pipeSpawnTimer = tag.getInt("pipeSpawnTimer");

        pipes.clear();
        ListTag pipeTags = tag.getList("pipes", 10);
        for (int i = 0; i < pipeTags.size(); i++) {
            CompoundTag pipeTag = pipeTags.getCompound(i);
            float x = (float) pipeTag.getDouble("x");
            float hole = (float) pipeTag.getDouble("hole");
            float holeSize = (float) pipeTag.getDouble("holeSize");
            pipes.add(new Sprite(new Vec2(x, hole - 64), new Vec2(16, 64),
                    PoopSky.loc("textures/games/sprite/pipe_top.png")));
            pipes.add(new Sprite(new Vec2(x, hole + holeSize), new Vec2(16, 64),
                    PoopSky.loc("textures/games/sprite/pipe_bottom.png")));
        }
    }

    @Override
    public void render(GuiGraphics graphics, int posX, int posY) {
        super.render(graphics, posX, posY);

        bird.render(graphics, posX, posY);
        for (Sprite pipe : pipes) {
            pipe.render(graphics, posX, posY);
        }
        ground.render(graphics, posX, posY);

        renderOverlay(graphics, posX, posY);
    }

    @Override
    public ResourceLocation getBackground() {
        return PoopSky.loc("textures/games/background/flappy_bird_background.png");
    }

    @Override
    public int scoreColor() {
        return 0xFFFF00;
    }

}
