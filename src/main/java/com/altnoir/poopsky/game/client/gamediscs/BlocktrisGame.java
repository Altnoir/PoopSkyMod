package com.altnoir.poopsky.game.client.gamediscs;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.game.client.graphics.MultiImage;
import com.altnoir.poopsky.game.client.util.BlocktrisPiece;
import com.altnoir.poopsky.game.client.util.Game;
import com.altnoir.poopsky.game.client.util.Grid;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public class BlocktrisGame extends Game {
    private static final int TILE_SIZE = 8;
    private static final int GRID_WIDTH = 10;
    private static final int GRID_HEIGHT = 20;
    private static final int BOARD_X = 72;
    private static final int NEXT_CELL_X = 22;
    private static final int NEXT_LABEL_X = 176;

    public Grid grid = createGrid();
    private BlocktrisPiece piece = new BlocktrisPiece(BlocktrisPiece.PIECES.get(0).get(), 4, 1, 0, this);
    private final List<BlocktrisPiece> nexts = new ArrayList<>();

    public BlocktrisGame() {
        super();
    }

    private static Grid createGrid() {
        return new Grid(
                GRID_WIDTH,
                GRID_HEIGHT,
                TILE_SIZE,
                new MultiImage(PoopSky.loc("textures/games/sprite/cubes.png"), 8, 64, 8)
        );
    }

    @Override
    public void applySnapshot(CompoundTag tag) {
        super.applySnapshot(tag);

        grid = createGrid();
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
        graphics.drawString(font, text, NEXT_LABEL_X + posX - font.width(text.getVisualOrderText()) / 2, 6 + posY, 0x555555, false);

        renderOverlay(graphics, posX, posY);
    }

    @Override
    public ResourceLocation getBackground() {
        return PoopSky.loc("textures/games/background/blocktris_bakground.png");
    }

    @Override
    public boolean scoreText() {
        return false;
    }

}
