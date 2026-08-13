package com.altnoir.poopsky.client.games.util;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.phys.Vec2;
import com.altnoir.poopsky.client.games.gamediscs.BlocktrisGame;

import java.util.List;
import java.util.function.Supplier;

public class BlocktrisPiece {
    private final List<List<Vec2>> variants;
    private final BlocktrisGame game;
    private int x;
    private int y;
    private int rotation;
    private int color;
    private final Grid grid;
    private List<Vec2> altPositions = List.of(
            new Vec2(0, 1),
            new Vec2(-1, 1),
            new Vec2(1, 1),
            new Vec2(-1, 0),
            new Vec2(1, 0),
            new Vec2(0, -1),
            new Vec2(-1, -1),
            new Vec2(1, -1)
    );

    public static final Supplier<List<List<Vec2>>> TRIANGLE = () -> List.of(
            List.of(
                    new Vec2(0, 0),
                    new Vec2(0, -1),
                    new Vec2(1, 0),
                    new Vec2(0, 1)
            ),
            List.of(
                    new Vec2(0, 0),
                    new Vec2(-1, 0),
                    new Vec2(1, 0),
                    new Vec2(0, 1)
            ),
            List.of(
                    new Vec2(0, 0),
                    new Vec2(0, -1),
                    new Vec2(-1, 0),
                    new Vec2(0, 1)
            ),
            List.of(
                    new Vec2(0, 0),
                    new Vec2(0, -1),
                    new Vec2(1, 0),
                    new Vec2(-1, 0)
            )
    );

    public static final Supplier<List<List<Vec2>>> J = () -> List.of(
            List.of(
                    new Vec2(0, -1),
                    new Vec2(0, 0),
                    new Vec2(0, 1),
                    new Vec2(-1, 1)
            ),
            List.of(
                    new Vec2(-1, -1),
                    new Vec2(-1, 0),
                    new Vec2(0, 0),
                    new Vec2(1, 0)
            ),
            List.of(
                    new Vec2(0, -1),
                    new Vec2(0, 0),
                    new Vec2(0, 1),
                    new Vec2(1, -1)
            ),
            List.of(
                    new Vec2(-1, 0),
                    new Vec2(0, 0),
                    new Vec2(1, 0),
                    new Vec2(1, 1)
            )
    );
    public static final Supplier<List<List<Vec2>>> LINE = () -> List.of(
            List.of(
                    new Vec2(0, -1),
                    new Vec2(0, 0),
                    new Vec2(0, 1),
                    new Vec2(0, 2)
            ),
            List.of(
                    new Vec2(-1, 0),
                    new Vec2(0, 0),
                    new Vec2(1, 0),
                    new Vec2(2, 0)
            )
    );
    public static final Supplier<List<List<Vec2>>> Z = () -> List.of(
            List.of(
                    new Vec2(-1, 0),
                    new Vec2(0, 0),
                    new Vec2(-1, 1),
                    new Vec2(0, -1)
            ),
            List.of(
                    new Vec2(-1, 0),
                    new Vec2(0, 0),
                    new Vec2(0, 1),
                    new Vec2(1, 1)
            )
    );
    public static final Supplier<List<List<Vec2>>> SQUARE = () -> List.of(
            List.of(
                    new Vec2(0, 0),
                    new Vec2(1, 0),
                    new Vec2(0, 1),
                    new Vec2(1, 1)
            )
    );
    public static final Supplier<List<List<Vec2>>> L = () -> List.of(
            List.of(
                    new Vec2(0, -1),
                    new Vec2(0, 0),
                    new Vec2(0, 1),
                    new Vec2(1, 1)
            ),
            List.of(
                    new Vec2(-1, 1),
                    new Vec2(-1, 0),
                    new Vec2(0, 0),
                    new Vec2(1, 0)
            ),
            List.of(
                    new Vec2(0, -1),
                    new Vec2(0, 0),
                    new Vec2(0, 1),
                    new Vec2(-1, -1)
            ),
            List.of(
                    new Vec2(-1, 0),
                    new Vec2(0, 0),
                    new Vec2(1, 0),
                    new Vec2(1, -1)
            )
    );
    public static final Supplier<List<List<Vec2>>> S = () -> List.of(
            List.of(
                    new Vec2(0, -1),
                    new Vec2(0, 0),
                    new Vec2(1, 0),
                    new Vec2( 1, 1)
            ),
            List.of(
                    new Vec2(0, -1),
                    new Vec2(0, 0),
                    new Vec2(1, -1),
                    new Vec2(-1, 0)
            )
    );

    public static final List<Supplier<List<List<Vec2>>>> PIECES = List.of(
            TRIANGLE,
            J,
            LINE,
            Z,
            SQUARE,
            L,
            S
    );

    public BlocktrisPiece(List<List<Vec2>> variants, int x, int y, int color, BlocktrisGame game) {
        this.variants = variants;
        this.x = x;
        this.y = y;
        this.color = color;
        this.grid = game.grid;
        this.game = game;
    }

    public List<Vec2> current() {
        return variants.get(rotation);
    }

    public void setPos(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public boolean isTouching() {
        for (Vec2 part : current()) {
            if (part.x + x >= grid.width() || part.x + x < 0 || part.y + y >= grid.height() || part.y + y < 0) {
                return true;
            }
            if (grid.get((int)part.x + x, (int)part.y + y) != 0) {
                return true;
            }
        }
        return false;
    }

    public void rotate() {
        rotation += 1;
        if (rotation >= variants.size()) {
            rotation = 0;
        }
        if (isTouching()) {
            int oldX = x;
            int oldY = y;
            int i = 0;
            while (true) {
                x = oldX;
                y = oldY;
                x += (int) altPositions.get(i).x;
                y += (int) altPositions.get(i).y;
                if (!isTouching()) {
                    break;
                }
                i++;
                if (i >= altPositions.size()) {
                    x = oldX;
                    y = oldY;
                    rotation -= 1;
                    if (rotation < 0) {
                        rotation = variants.size() - 1;
                    }
                    break;
                }
            }
        }
    }
    public boolean move(int x, int y) {
        boolean flag = false;
        this.x += x;
        if (isTouching()) {
            this.x -= x;
            flag = true;
        }
        this.y += y;
        if (isTouching()) {
            this.y -= y;
            flag = true;
        }
        return flag;
    }

    public void hardDrop() {
        while (true) {
            if (move(0, 1)) {
                return;
            }
        }
    }

    public void place() {
        for (Vec2 part : current()) {
            grid.set(x + (int)part.x, y + (int)part.y, color + 1);
        }
    }

    public void tick() {
        this.y += 1;
    }

    public void render(GuiGraphics graphics, int posX, int posY) {
        grid.getImages().setImage(color + 1);
        for (Vec2 part : current()) {
            grid.getImages().render(graphics, posX + (x + (int)part.x) * grid.tileSize(), posY + (y + (int)part.y) * grid.tileSize());
        }
    }

    public void renderCentered(GuiGraphics graphics, int posX, int posY) {
        grid.getImages().setImage(color + 1);
        Vec2 smallest = null;
        Vec2 biggest = null;
        for (int i = 0; i < current().size(); i++) {
            Vec2 part = current().get(i);
            if (smallest == null) {
                smallest = part;
            }
            if (biggest == null) {
                biggest = part;
            }
            smallest = new Vec2(Math.min(smallest.x, part.x), Math.min(smallest.y, part.y));
            biggest = new Vec2(Math.max(biggest.x, part.x), Math.max(biggest.y, part.y));
        }
        Vec2 addition = smallest.add(biggest.negated()).scale(0.5f).add(biggest).negated();
        for (Vec2 part : current()) {
            grid.getImages().render(graphics, posX + (int)((x + part.x + addition.x) * grid.tileSize()), posY + (int)((y + (int)part.y + addition.y) * grid.tileSize()));
        }
    }

    public void setRotation(int direction) {
        rotation = direction;
        if (rotation >= variants.size()) {
            rotation = variants.size() - 1;
        }
        if (rotation < 0) {
            rotation = 0;
        }
    }
}
