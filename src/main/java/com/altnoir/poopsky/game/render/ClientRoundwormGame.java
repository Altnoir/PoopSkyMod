package com.altnoir.poopsky.game.render;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.game.client.ClientGame;
import com.altnoir.poopsky.game.client.graphics.MultiImage;
import com.altnoir.poopsky.game.client.graphics.Sprite;
import com.altnoir.poopsky.game.model.RoundwormGameState;
import com.altnoir.poopsky.game.util.VecUtil;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.world.phys.Vec2;

import java.util.List;

public class ClientRoundwormGame extends ClientGame {
    private final MultiImage HEAD = new MultiImage(
            PoopSky.loc("textures/games/sprite/roundworm_head.png"), 8, 32, 4);
    private final MultiImage TAIL = new MultiImage(
            PoopSky.loc("textures/games/sprite/roundworm_tail.png"), 8, 32, 4);
    private final MultiImage CONNECTION = new MultiImage(
            PoopSky.loc("textures/games/sprite/roundworm_connection.png"), 8, 32, 4);
    private static final Identifier BODY = PoopSky.loc("textures/games/sprite/roundworm_body.png");
    private static final Identifier SHIT = PoopSky.loc("textures/games/sprite/shit.png");

    private static final Vec2 GAME_POS = new Vec2(0, 0);
    private static final int TILE_SIZE = 8;

    private final RoundwormGameState state = new RoundwormGameState();
    private final Sprite roundwormRenderer = new Sprite(Vec2.ZERO, new Vec2(TILE_SIZE, TILE_SIZE), BODY);
    private final Sprite shit = new Sprite(Vec2.ZERO, new Vec2(TILE_SIZE, TILE_SIZE), SHIT);

    @Override
    public void applySnapshot(CompoundTag tag) {
        super.applySnapshot(tag);
        state.applySnapshot(tag);
    }

    @Override
    protected void renderGame(GuiGraphicsExtractor graphics, int posX, int posY) {
        List<Vec2> roundworm = state.body();
        shit.setPos(calcPos(state.shit()));
        shit.render(graphics, posX, posY);

        for (int i = roundworm.size() - 1; i >= 0; i--) {
            if (i == 0) {
                roundwormRenderer.setImage(TAIL.setImage(VecUtil.get4DirectionTo(roundworm.get(0), roundworm.get(1))));
            } else if (i == roundworm.size() - 1) {
                roundwormRenderer.setImage(HEAD.setImage(VecUtil.get4DirectionTo(roundworm.get(roundworm.size() - 2), roundworm.getLast())));
            } else {
                roundwormRenderer.setImage(BODY);
            }

            Vec2 part = roundworm.get(i);
            roundwormRenderer.setPos(calcPos(part));
            roundwormRenderer.render(graphics, posX, posY);

            if (i + 1 < roundworm.size()) {
                roundwormRenderer.setPos(calcPos(part.add(VecUtil.getFrom(VecUtil.get4DirectionTo(part, roundworm.get(i + 1))).scale(0.5F))));
                roundwormRenderer.setImage(CONNECTION.setImage(VecUtil.get4DirectionTo(part, roundworm.get(i + 1))));
                roundwormRenderer.render(graphics, posX, posY);
            }
        }
    }

    private Vec2 calcPos(Vec2 tile) {
        return tile.scale(TILE_SIZE).add(GAME_POS);
    }

    @Override
    public Identifier getBackground() {
        return PoopSky.loc("textures/games/background/roundworm_background.png");
    }
}

