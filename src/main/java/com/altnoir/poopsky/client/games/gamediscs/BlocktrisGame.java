package com.altnoir.poopsky.client.games.gamediscs;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.client.games.controls.Button;
import com.altnoir.poopsky.client.games.graphics.BasicParticleRenderer;
import com.altnoir.poopsky.client.games.graphics.MultiImage;
import com.altnoir.poopsky.client.games.graphics.ParticleColor;
import com.altnoir.poopsky.client.games.util.*;
import com.altnoir.poopsky.init.PoSoundEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec2;

import java.util.ArrayList;
import java.util.List;

public class BlocktrisGame extends Game {
    private static final int TILE_SIZE = 8;
    private static final int GRID_WIDTH = 10;
    private static final int GRID_HEIGHT = 20;
    private static final int BOARD_X = 72;
    private static final int NEXT_CELL_X = 23;
    private static final int NEXT_LABEL_X = 188;

    public Grid grid;
    private BlocktrisPiece piece;
    private final List<BlocktrisPiece> nexts = new ArrayList<>();
    private int placementCooldown = 0;

    public BlocktrisGame() {
        super();
        grid = new Grid(
                GRID_WIDTH,
                GRID_HEIGHT,
                TILE_SIZE,
                new MultiImage(
                        PoopSky.loc("textures/games/sprite/cubes.png"),
                        8, 64, 8));

        int type = random.nextInt(7);
        piece = new BlocktrisPiece(
                BlocktrisPiece.PIECES.get(type).get(),
                4, 1,
                type,
                this
        );
    }

    @Override
    public void applySnapshot(CompoundTag tag) {
        super.applySnapshot(tag);

        ListTag rows = tag.getList("grid", 11);
        for (int y = 0; y < rows.size(); y++) {
            int[] row = rows.getIntArray(y);
            for (int x = 0; x < row.length; x++) {
                grid.set(x, y, row[x]);
            }
        }

        int type = tag.getInt("pieceType");
        piece = new BlocktrisPiece(BlocktrisPiece.PIECES.get(type).get(), tag.getInt("pieceX"), tag.getInt("pieceY"), type, this);
        piece.setRotation(tag.getInt("pieceRot"));

        nexts.clear();
        int[] nextTypes = tag.getIntArray("nextTypes");
        for (int i = 0; i < nextTypes.length; i++) {
            BlocktrisPiece next = new BlocktrisPiece(BlocktrisPiece.PIECES.get(nextTypes[i]).get(), NEXT_CELL_X, 4 + i * 3, nextTypes[i], this);
            next.setRotation(1);
            nexts.add(next);
        }
        placementCooldown = tag.getInt("placementCooldown");
    }

    @Override
    public void prepare() {
        super.prepare();

        grid = new Grid(
                GRID_WIDTH,
                GRID_HEIGHT,
                TILE_SIZE,
                new MultiImage(
                        PoopSky.loc("textures/games/sprite/cubes.png"),
                        8, 64, 8));

        int type = random.nextInt(0, 7);
        piece = new BlocktrisPiece(
                BlocktrisPiece.PIECES.get(type).get(),
                4, 1,
                type,
                this
        );
        nexts.clear();
    }

    @Override
    public void start() {
        super.start();
        for (int i = 0; i < 3; i++) {
            int type = random.nextInt(0, 7);
            nexts.add(
                    new BlocktrisPiece(
                            BlocktrisPiece.PIECES.get(type).get(),
                            4, 1,
                            type,
                            this
                    )
            );
        }
        placementCooldown = 0;
    }

    @Override
    public void gameTick() {
        if (piece.move(0, 1)) {
            placePiece();
        }
    }

    @Override
    public int gameTickDuration() {
        return (int) (10f / ((float) score / 50f + 1f));
    }

    @Override
    public void render(GuiGraphics graphics, int posX, int posY) {
        super.render(graphics, posX, posY);

        grid.render(graphics, posX + BOARD_X, posY);
        piece.render(graphics, posX + BOARD_X, posY);

        for (int i = 0; i < nexts.size(); i++) {
            BlocktrisPiece next = nexts.get(i);
            next.setRotation(1);
            next.setPos(NEXT_CELL_X, 4 + i * 3);
            next.renderCentered(graphics, posX, posY);
        }

        Font font = Minecraft.getInstance().font;
        Component text = Component.translatable("gui.gamingconsole.next");
        graphics.drawString(font, text, NEXT_LABEL_X + posX - font.width(text.getVisualOrderText()) / 2, 12 + posY, 0x555555, false);

        renderParticles(graphics, posX, posY);

        renderOverlay(graphics, posX, posY);
    }

    @Override
    public void buttonDown(Button button) {
        super.buttonDown(button);
        if (stage == GameStage.PLAYING && ticks > 5) {
            if (button == Button.UP) {
                piece.rotate();
                soundPlayer.play(PoSoundEvents.SWING.get(), 1.5f, 0.5f);
                placementCooldown = 10;
            }
            if (button == Button.LEFT) {
                piece.move(-1, 0);
                soundPlayer.play(PoSoundEvents.SHOOT.get(), 2.5f, 0.1f);
                placementCooldown = 10;
            }
            if (button == Button.RIGHT) {
                piece.move(1, 0);
                soundPlayer.play(PoSoundEvents.SHOOT.get(), 2.5f, 0.1f);
                placementCooldown = 10;
            }
            if (button == Button.DOWN) {
                soundPlayer.play(PoSoundEvents.SHOOT.get(), 2.5f, 0.1f);
                if (piece.move(0, 1)) {
                    placePiece();
                } else {
                    placementCooldown = 10;
                }
            }
            if (button == Button.BUTTON1) {
                soundPlayer.play(PoSoundEvents.EXPLOSION.get(), 0.7f, 0.5f);
                piece.hardDrop();
                placementCooldown = 0;
                placePiece();
                placementCooldown = 10;
            }
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (stage == GameStage.PLAYING && ticks % 2 == 0) {
            if (placementCooldown <= 0) {
                if (controls.isButtonDown(Button.LEFT) && controls.wasButtonDown(Button.LEFT)) {
                    piece.move(-1, 0);
                    soundPlayer.play(PoSoundEvents.SHOOT.get(), 2.5f, 0.1f);
                }
                if (controls.isButtonDown(Button.RIGHT) && controls.wasButtonDown(Button.RIGHT)) {
                    piece.move(1, 0);
                    soundPlayer.play(PoSoundEvents.SHOOT.get(), 2.5f, 0.1f);
                }
                if (controls.isButtonDown(Button.DOWN) && controls.wasButtonDown(Button.DOWN)) {
                    soundPlayer.play(PoSoundEvents.SHOOT.get(), 2.5f, 0.1f);
                    if (piece.move(0, 1)) {
                        placePiece();
                    }
                }
            }
        }
        placementCooldown--;
        if (placementCooldown < 0) {
            placementCooldown = 0;
        }
    }

    private void placePiece() {
        if (placementCooldown <= 0) {
            piece.place();

            int combo = 0;

            int y = grid.height() - 1;
            while (y >= 0) {
                boolean isFull = true;
                for (int x = 0; x < grid.width(); x++) {
                    if (grid.get(x, y) == 0) {
                        isFull = false;
                    }
                }
                if (isFull) {
                    combo++;
                    for (int line = y - 1; line >= 0; line--) {
                        for (int x = 0; x < grid.width(); x++) {
                            if (line == y - 1) {
                                spawnParticleExplosion(
                                        () ->
                                                new BasicParticleRenderer(ParticleColor.random(random)),
                                        new Vec2(BOARD_X + x * TILE_SIZE, y * TILE_SIZE),
                                        4,
                                        3,
                                        5,
                                        ParticleLevel.RUNNING_GAME
                                );
                            }
                            grid.set(x, line + 1, grid.get(x, line));
                        }
                    }
                    y++;
                }
                y--;
            }

            score += combo * combo;
            if (combo > 0) {
                soundPlayer.playPoint();
            }

            piece = getNext();
            piece.setPos(4, 1);
            if (piece.isTouching()) {
                die();
            }
        }
    }

    private BlocktrisPiece getNext() {
        BlocktrisPiece toReturn = nexts.getFirst();

        nexts.removeFirst();
        int type = random.nextInt(0, 7);
        nexts.add(
                new BlocktrisPiece(
                        BlocktrisPiece.PIECES.get(type).get(),
                        4, 1,
                        type,
                        this
                )
        );
        toReturn.setPos(4, 1);
        toReturn.setRotation(0);

        return toReturn;
    }

    @Override
    public ResourceLocation getBackground() {
        return PoopSky.loc("textures/games/background/blocktris_bakground.png");
    }

    @Override
    public boolean showScoreBox() {
        return false;
    }

    @Override
    public boolean scoreText() {
        return false;
    }

    @Override
    public Component getName() {
        return Component.translatable("gamediscs.blocktris");
    }

    @Override
    public ResourceLocation getIcon() {
        return PoopSky.loc("textures/item/game_disc_blocktris.png");
    }
}
