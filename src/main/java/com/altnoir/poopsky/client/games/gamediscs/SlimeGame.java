package com.altnoir.poopsky.client.games.gamediscs;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.client.games.controls.Button;
import com.altnoir.poopsky.client.games.graphics.BreakParticleRenderer;
import com.altnoir.poopsky.client.games.graphics.DirectionalImage;
import com.altnoir.poopsky.client.games.util.*;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.phys.Vec2;

import java.util.ArrayList;
import java.util.List;

public class SlimeGame extends Game {
    private final DirectionalImage HEAD = new DirectionalImage(
            PoopSky.loc("textures/games/sprite/slime_head.png"), 8, 32);
    private final DirectionalImage TAIL = new DirectionalImage(
            PoopSky.loc("textures/games/sprite/slime_tail.png"), 8, 32);
    private final DirectionalImage CONNECTION = new DirectionalImage(
            PoopSky.loc("textures/games/sprite/slime_connection.png"), 8, 32);
    private static final ResourceLocation BODY = PoopSky.loc("textures/games/sprite/slime_body.png");
    private static final ResourceLocation APPLE = PoopSky.loc("textures/games/sprite/apple.png");

    // Start position of the actual game field
    private static final Vec2 GAME_POS = new Vec2(0, 0);

    // Size of the tile
    private static final int TILE_SIZE = 8;

    // Dimensions of the game field (in tiles)
    private static final int GAME_WIDTH = 35;
    private static final int GAME_HEIGHT = 20;

    // List of blocked positions (in tiles)
    private static final List<Vec2> BLOCKED = List.of(
            new Vec2(0, 0),
            new Vec2(1, 0),
            new Vec2(2, 0),
            new Vec2(3, 0),
            new Vec2(4, 0),
            new Vec2(5, 0),
            new Vec2(6, 0)
    );

    // List of slime positions (in tiles)
    private List<Vec2> slime = new ArrayList<>();

    // Direction of moving (as a Vec2 relative position)
    private Vec2 direction = VecUtil.VEC_RIGHT;
    private Vec2 nextDirection;

    // Sprite used for rendering all slime parts
    private final Sprite slimeRenderer = new Sprite(Vec2.ZERO, new Vec2(TILE_SIZE, TILE_SIZE), BODY);

    // Apple Sprite
    private final Sprite apple = new Sprite(Vec2.ZERO, new Vec2(TILE_SIZE, TILE_SIZE), APPLE);

    public SlimeGame() {
        super();
    }

    @Override
    public void prepare() {
        // Calls prepare of super
        super.prepare();

        // Creates slime body
        slime = new ArrayList<>();
        slime.add(new Vec2(5, 5));
        slime.add(new Vec2(6, 5));
        slime.add(new Vec2(7, 5));

        // Moves apple to random position
        respawnApple();
        nextDirection = null;
    }

    @Override
    public void start() {
        // Calls start of super
        super.start();

        // Resets direction
        direction = VecUtil.VEC_RIGHT;
        nextDirection = null;
    }

    @Override
    public void gameTick() {
        // Calls game tick of super
        super.gameTick();

        if (nextDirection != null) {
            direction = nextDirection;
            nextDirection = null;
        }

        // Calculates new position of the slime
        Vec2 newPos = slime.getLast().add(direction);

        // If is outside the game field, player dies
        if (newPos.x >= GAME_WIDTH || newPos.x < 0 || newPos.y >= GAME_HEIGHT || newPos.y < 0) {
            die();
        }

        // If there is any part of the slime, player dies
        for (int i = 0; i < slime.size(); i++) {
            Vec2 part = slime.get(i);
            if (i != 0 && VecUtil.is(part, newPos)) {
                die();
            }
        }

        // If there is any blocked position, the player dies
        for (Vec2 block : BLOCKED) {
            if (VecUtil.is(block, newPos)) {
                die();
            }
        }

        if (stage == GameStage.PLAYING) {
            // New position is added to the slime body
            slime.add(newPos);

            // If there is no apple on the position, it removes tail part of the body
            if (!VecUtil.is(newPos, calcTile(apple.getPos()))) {
                slime.remove(slime.getFirst());
            } else {
                // If there is an apple, it respawns, and the score grows
                spawnParticleExplosion(() -> new BreakParticleRenderer(APPLE, 8, 8), apple.getCenterPos(), 15, 3, 5, ParticleLevel.GAME);
                respawnApple();
                score++;
                soundPlayer.play(SoundEvents.GENERIC_EAT);
            }
        }

    }

    @Override
    public void render(GuiGraphics graphics, int posX, int posY) {
        // Calls render of super
        super.render(graphics, posX, posY);

        // Renders apple
        apple.render(graphics, posX, posY);

        // Renders particles
        renderParticles(graphics, posX, posY);

        // Renders slime
        for (int i = slime.size() - 1; i >= 0; i--) {
            // Sets the image of the renderer to the corresponding slime part
            if (i == 0) {
                slimeRenderer.setImage(TAIL.setImage(VecUtil.get4DirectionTo(slime.get(0), slime.get(1))));
            } else if (i == slime.size() - 1) {
                slimeRenderer.setImage(HEAD.setImage(VecUtil.get4DirectionTo(slime.get(slime.size() - 2), slime.getLast())));
            } else {
                slimeRenderer.setImage(BODY);
            }

            Vec2 part = slime.get(i);
            // Renders the part on its position
            slimeRenderer.setPos(calcPos(part));
            slimeRenderer.render(graphics, posX, posY);

            // If the part is not head, it renders a connection
            if (i + 1 < slime.size()) {
                // Moves by 0.5 tiles to the previous part
                slimeRenderer.setPos(calcPos(part.add(VecUtil.getFrom(VecUtil.get4DirectionTo(part, slime.get(i + 1))).scale(0.5f))));

                // Sets the corresponding image based on rotation
                slimeRenderer.setImage(CONNECTION.setImage(VecUtil.get4DirectionTo(part, slime.get(i + 1))));

                // Renders the connection
                slimeRenderer.render(graphics, posX, posY);
            }
        }

        // Renders overlay
        renderOverlay(graphics, posX, posY);
    }

    /**
     * Moves apple to a random location that is not occupied by slime, or blocked position
     */
    private void respawnApple() {
        // Keeps moving to random position until the position is valid
        boolean valid = false;
        while (!valid) {
            // Goes to random position within the game field
            apple.setPos(calcPos(VecUtil.randomInt(Vec2.ZERO, new Vec2(GAME_WIDTH, GAME_HEIGHT), random)));

            // Sets valid to true
            valid = true;
            Vec2 applePos = calcTile(apple.getPos());

            // If there is slime or blocked position, it sets valid to false
            for (Vec2 vec2 : slime) {
                if (VecUtil.is(vec2, applePos)) {
                    valid = false;
                    break;
                }
            }
            for (Vec2 block : BLOCKED) {
                if (VecUtil.is(block, applePos)) {
                    valid = false;
                    break;
                }
            }
        }
    }

    /**
     * @param pos Position on screen
     * @return Tile position
     */
    private Vec2 calcTile(Vec2 pos) {
        return pos.add(GAME_POS.negated()).scale((float) 1 / TILE_SIZE);
    }

    /**
     * @param tile Tile position
     * @return Position on screen
     */
    private Vec2 calcPos(Vec2 tile) {
        return tile.scale(TILE_SIZE).add(GAME_POS);
    }

    @Override
    public void buttonDown(Button button) {
        // Calls button down of super
        super.buttonDown(button);

        // Changes the direction of the slime if a corresponding key was pressed
        if (stage == GameStage.PLAYING) {
            if (nextDirection != null) {
                return;
            }

            Vec2 newDirection = null;
            switch (button) {
                case UP -> newDirection = VecUtil.VEC_UP;
                case RIGHT -> newDirection = VecUtil.VEC_RIGHT;
                case DOWN -> newDirection = VecUtil.VEC_DOWN;
                case LEFT -> newDirection = VecUtil.VEC_LEFT;
            }
            if (newDirection != null && !VecUtil.is(newDirection, direction.negated()) && !VecUtil.is(newDirection, direction)) {
                nextDirection = newDirection;
                soundPlayer.play(SoundEvents.SLIME_SQUISH, 0.1f, 0.5f);
            }
        }
    }

    @Override
    public int gameTickDuration() {
        return 5;
    }

    @Override
    public ResourceLocation getBackground() {
        return PoopSky.loc("textures/games/background/slime_background.png");
    }

    @Override
    public Component getName() {
        return Component.translatable("gamediscs.slime");
    }

    @Override
    public ResourceLocation getIcon() {
        return PoopSky.loc("textures/item/game_disc_slime.png");
    }
}
