package com.altnoir.poopsky.game.render;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.game.client.ClientGame;
import com.altnoir.poopsky.game.client.graphics.BlocktrisPiece;
import com.altnoir.poopsky.game.client.graphics.Grid;
import com.altnoir.poopsky.game.client.graphics.MultiImage;
import com.altnoir.poopsky.game.model.BlocktrisGameState;
import com.altnoir.poopsky.init.PoKeyBoardInput;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public class ClientBlocktrisGame extends ClientGame {
    private static final int TILE_SIZE = 8;
    private static final int GRID_WIDTH = BlocktrisGameState.GRID_WIDTH;
    private static final int GRID_HEIGHT = BlocktrisGameState.VISIBLE_HEIGHT;
    private static final int BOARD_X = 72;
    private static final int NEXT_CELL_X = 23;
    private static final int NEXT_LABEL_X = 188;

    public Grid grid = createGrid();
    private final BlocktrisGameState state = new BlocktrisGameState();
    private BlocktrisPiece piece = new BlocktrisPiece(0, 4, 1, 0, grid);
    private final List<BlocktrisPiece> nexts = new ArrayList<>();

    public ClientBlocktrisGame() {
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
        state.applySnapshot(tag);

        grid = createGrid();
        for (int y = 0; y < GRID_HEIGHT; y++) {
            for (int x = 0; x < GRID_WIDTH; x++) {
                grid.set(x, y, state.getVisibleGrid(x, y));
            }
        }

        int type = state.getCurrentType();
        piece = new BlocktrisPiece(type, state.getCurrentX(), state.getCurrentY(), type, grid);
        piece.setRotation(state.getCurrentRotation());

        nexts.clear();
        int[] nextTypes = state.nextTypes();
        for (int i = 0; i < nextTypes.length; i++) {
            BlocktrisPiece next = new BlocktrisPiece(nextTypes[i], NEXT_CELL_X, 4 + i * 3, nextTypes[i], grid);
            next.setRotation(1);
            nexts.add(next);
        }
    }

    @Override
    protected void renderGame(GuiGraphics graphics, int posX, int posY) {
        grid.render(graphics, posX + BOARD_X, posY);
        piece.render(graphics, posX + BOARD_X, posY);

        for (int i = 0; i < nexts.size(); i++) {
            BlocktrisPiece next = nexts.get(i);
            next.setRotation(1);
            next.setPos(NEXT_CELL_X, 4 + i * 3);
            next.renderCentered(graphics, posX, posY);
        }

        Font font = Minecraft.getInstance().font;
        Component keyName1 = PoKeyBoardInput.ARCADE_BUTTON1.getTranslatedKeyMessage();
        Component keyName2 = PoKeyBoardInput.ARCADE_BUTTON2.getTranslatedKeyMessage();

        Component text = Component.translatable("gui.gamingconsole.next");
        Component textBotton1 = Component.translatable("gui.gamingconsole.blocktris.botton_1", keyName1);
        Component textBotton2 = Component.translatable("gui.gamingconsole.blocktris.botton_2", keyName2);

        int textX = NEXT_LABEL_X + posX - font.width(text.getVisualOrderText()) / 2;
        graphics.drawString(font, text, textX, 10 + posY, 0x555555, false);
        graphics.drawString(font, textBotton1, textX - 7, 100 + posY, 0x555555, false);
        graphics.drawString(font, textBotton2, textX - 7, 110 + posY, 0x555555, false);
    }

    @Override
    public ResourceLocation getBackground() {
        return PoopSky.loc("textures/games/background/blocktris_background.png");
    }

    @Override
    public boolean scoreText() {
        return false;
    }

}
