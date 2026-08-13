package com.altnoir.poopsky.game;

import com.altnoir.poopsky.client.games.controls.Button;
import com.altnoir.poopsky.client.games.util.GameStage;
import com.altnoir.poopsky.content.item.p.GameDiscItem;
import com.altnoir.poopsky.init.PoItems;
import com.altnoir.poopsky.init.PoSoundEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntArrayTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.phys.Vec2;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public abstract class ServerGame {
    protected static final int GAME_WIDTH = 224;
    protected static final int GAME_HEIGHT = 160;

    public GameStage stage = GameStage.START;
    public int score;
    public int settledScore;
    public int ticks = 1;
    protected final Random random;
    protected boolean upDown;
    protected boolean downDown;
    protected boolean leftDown;
    protected boolean rightDown;
    protected boolean button1Down;
    protected boolean button2Down;
    private SoundEmitter soundEmitter = (event, pitch, volume) -> {
    };

    protected ServerGame() {
        this.random = new Random();
    }

    public void setSoundEmitter(SoundEmitter soundEmitter) {
        this.soundEmitter = soundEmitter;
    }

    protected void playSound(SoundEvent event, float pitch, float volume) {
        soundEmitter.play(event, pitch, volume);
    }

    public abstract void prepare();

    public void start() {
        stage = GameStage.PLAYING;
        ticks = 1;
    }

    public final void setButton(Button button, boolean pressed) {
        switch (button) {
            case UP -> upDown = pressed;
            case DOWN -> downDown = pressed;
            case LEFT -> leftDown = pressed;
            case RIGHT -> rightDown = pressed;
            case BUTTON1 -> button1Down = pressed;
            case BUTTON2 -> button2Down = pressed;
        }

        if (pressed) {
            buttonDown(button);
        } else {
            buttonUp(button);
        }
    }

    protected void buttonDown(Button button) {
        if (stage == GameStage.START || stage == GameStage.RETRY) {
            start();
        } else if (stage == GameStage.DIED || stage == GameStage.WON) {
            prepare();
        }
    }

    protected void buttonUp(Button button) {
    }

    public final void tick() {
        if (stage == GameStage.PLAYING && ticks % gameTickDuration() == 0) {
            gameTick();
        }
        extraTick();
        ticks++;
    }

    protected int gameTickDuration() {
        return 1;
    }

    protected abstract void gameTick();

    protected void extraTick() {
    }

    public CompoundTag writeSnapshot() {
        CompoundTag tag = new CompoundTag();
        tag.putString("stage", stage.name());
        tag.putInt("score", score);
        tag.putInt("settled_score", settledScore);
        tag.putInt("ticks", ticks);
        return tag;
    }

    public String getGameName() {
        if (this instanceof Pong) {
            return "PongGame";
        }
        if (this instanceof Slime) {
            return "SlimeGame";
        }
        if (this instanceof FlappyBird) {
            return "FlappyBirdGame";
        }
        if (this instanceof Blocktris) {
            return "BlocktrisGame";
        }
        return getClass().getSimpleName();
    }

    public static ServerGame create(GameDiscItem disc, BlockPos pos) {
        ServerGame game;
        if (disc == PoItems.GAME_DISC_FLAPPY_BIRD.get()) {
            game = new FlappyBird();
        } else if (disc == PoItems.GAME_DISC_SLIME.get()) {
            game = new Slime();
        } else if (disc == PoItems.GAME_DISC_BLOCKTRIS.get()) {
            game = new Blocktris();
        } else if (disc == PoItems.GAME_DISC_PONG.get()) {
            game = new Pong();
        } else {
            throw new IllegalArgumentException("Unknown arcade game " + disc);
        }
        game.prepare();
        return game;
    }

    @FunctionalInterface
    public interface SoundEmitter {
        void play(SoundEvent event, float pitch, float volume);
    }

    public static final class Pong extends ServerGame {
        private double playerY = 70;
        private double opponentY = 70;
        private double ballX = 110;
        private double ballY = 78;
        private double ballVX = 2;
        private double ballVY = 2;
        private int opponentScore;
        private int ballTimer = 60;
        private float ballSpeed = 4.0F;

        @Override
        public void prepare() {
            playerY = 70;
            opponentY = 70;
            ballX = 110;
            ballY = 78;
            opponentScore = 0;
            ballTimer = 60;
            ballSpeed = 4.0F;
            resetBall();
            score = 0;
            stage = GameStage.START;
            ticks = 1;
        }

        private void resetBall() {
            ballX = 110;
            ballY = 78;
            ballSpeed = 4.0F;
            ballVX = random.nextBoolean() ? ballSpeed : -ballSpeed;
            ballVY = random.nextBoolean() ? ballSpeed : -ballSpeed;
            ballTimer = 60;
        }

        @Override
        protected void gameTick() {
            if (ticks % 20 == 0) {
                ballSpeed += 0.1F;
            }
            if (upDown) {
                playerY -= 3;
            }
            if (downDown) {
                playerY += 3;
            }
            playerY = Math.min(Math.max(playerY, 0), GAME_HEIGHT - 20);

            if (ballY < opponentY + 10) {
                opponentY -= 3;
            }
            if (ballY > opponentY + 10) {
                opponentY += 3;
            }
            opponentY = Math.min(Math.max(opponentY, 0), GAME_HEIGHT - 20);

            if (ballTimer > 0) {
                ballTimer--;
                return;
            }

            ballX += ballVX;
            ballY += ballVY;
            if (ballY <= 0 || ballY >= GAME_HEIGHT - 4) {
                ballVY = -ballVY;
                playSound(PoSoundEvents.JUMP.get(), 0.8F, 0.8F);
            }
            if (ballX <= 15 && ballY + 4 >= playerY && ballY <= playerY + 20) {
                ballVX = Math.abs(ballVX);
                playSound(PoSoundEvents.JUMP.get(), 1.0F, 1.0F);
            }
            if (ballX + 4 >= GAME_WIDTH - 15 && ballY + 4 >= opponentY && ballY <= opponentY + 20) {
                ballVX = -Math.abs(ballVX);
                playSound(PoSoundEvents.JUMP.get(), 1.0F, 1.0F);
            }
            if (ballX < 0) {
                opponentScore++;
                resetBall();
            }
            if (ballX + 4 > GAME_WIDTH) {
                score++;
                playSound(PoSoundEvents.POINT.get(), 1.0F, 0.7F);
                resetBall();
            }
            if (score >= 10) {
                stage = GameStage.WON;
                playSound(PoSoundEvents.NEW_BEST.get(), 1.5F, 2.0F);
            } else if (opponentScore >= 10) {
                stage = GameStage.DIED;
                playSound(PoSoundEvents.GAME_OVER.get(), 0.9F, 2.0F);
            }
        }

        @Override
        public CompoundTag writeSnapshot() {
            CompoundTag tag = super.writeSnapshot();
            tag.putDouble("playerY", playerY);
            tag.putDouble("opponentY", opponentY);
            tag.putDouble("ballX", ballX);
            tag.putDouble("ballY", ballY);
            tag.putInt("opponentScore", opponentScore);
            tag.putInt("ballTimer", ballTimer);
            return tag;
        }
    }

    public static final class Slime extends ServerGame {
        private final List<Vec2> body = new ArrayList<>();
        private Vec2 apple = new Vec2(12, 8);
        private Vec2 direction = new Vec2(1, 0);
        private Vec2 nextDirection;

        private static final List<Vec2> BLOCKED = List.of(
                new Vec2(0, 0), new Vec2(1, 0), new Vec2(2, 0),
                new Vec2(3, 0), new Vec2(4, 0), new Vec2(5, 0), new Vec2(6, 0)
        );

        @Override
        public void prepare() {
            body.clear();
            body.add(new Vec2(5, 5));
            body.add(new Vec2(6, 5));
            body.add(new Vec2(7, 5));
            respawnApple();
            direction = new Vec2(1, 0);
            nextDirection = null;
            score = 0;
            stage = GameStage.START;
            ticks = 1;
        }

        private void respawnApple() {
            while (true) {
                apple = new Vec2(random.nextInt(28), random.nextInt(20));
                if (!BLOCKED.contains(apple) && !body.contains(apple)) {
                    return;
                }
            }
        }

        @Override
        protected int gameTickDuration() {
            return 5;
        }

        @Override
        protected void buttonDown(Button button) {
            super.buttonDown(button);
            if (stage != GameStage.PLAYING) {
                return;
            }
            Vec2 next = switch (button) {
                case UP -> new Vec2(0, -1);
                case DOWN -> new Vec2(0, 1);
                case LEFT -> new Vec2(-1, 0);
                case RIGHT -> new Vec2(1, 0);
                default -> null;
            };
            if (next != null && !next.equals(direction) && !next.equals(new Vec2(-direction.x, -direction.y))) {
                nextDirection = next;
                playSound(SoundEvents.SLIME_SQUISH, 0.1F, 0.5F);
            }
        }

        @Override
        protected void gameTick() {
            if (nextDirection != null) {
                direction = nextDirection;
                nextDirection = null;
            }
            Vec2 head = body.getLast();
            Vec2 newPos = new Vec2(head.x + direction.x, head.y + direction.y);
            if (newPos.x < 0 || newPos.x >= 28 || newPos.y < 0 || newPos.y >= 20
                    || body.contains(newPos) || BLOCKED.contains(newPos)) {
                stage = GameStage.DIED;
                playSound(PoSoundEvents.GAME_OVER.get(), 0.9F, 2.0F);
                return;
            }
            body.add(newPos);
            if (newPos.equals(apple)) {
                score++;
                playSound(SoundEvents.GENERIC_EAT, 0.8F, 0.8F);
                respawnApple();
            } else {
                body.removeFirst();
            }
        }

        @Override
        public CompoundTag writeSnapshot() {
            CompoundTag tag = super.writeSnapshot();
            int[] xs = new int[body.size()];
            int[] ys = new int[body.size()];
            for (int i = 0; i < body.size(); i++) {
                xs[i] = (int) body.get(i).x;
                ys[i] = (int) body.get(i).y;
            }
            tag.putIntArray("slimeX", xs);
            tag.putIntArray("slimeY", ys);
            tag.putInt("appleX", (int) apple.x);
            tag.putInt("appleY", (int) apple.y);
            tag.putInt("dirX", (int) direction.x);
            tag.putInt("dirY", (int) direction.y);
            return tag;
        }
    }

    public static final class FlappyBird extends ServerGame {
        private double birdX = 20;
        private double birdY = 30;
        private double birdVY;
        private final List<double[]> pipes = new ArrayList<>();
        private double groundX;
        private int pipeSpawnTimer;

        @Override
        public void prepare() {
            birdX = 20;
            birdY = 30;
            birdVY = 0;
            pipes.clear();
            groundX = 0;
            pipeSpawnTimer = 0;
            score = 0;
            stage = GameStage.START;
            ticks = 1;
        }

        @Override
        protected void buttonDown(Button button) {
            super.buttonDown(button);
            if (button.isActionButton()) {
                birdVY = -4.5;
                playSound(PoSoundEvents.JUMP.get(), 0.8F, 1.0F);
            }
        }

        @Override
        protected void gameTick() {
            birdY += birdVY;
            birdVY += 0.75;
            birdVY *= 0.9;
            if (birdY < 0 || birdY + 8 >= GAME_HEIGHT - 16) {
                stage = GameStage.DIED;
                playSound(PoSoundEvents.GAME_OVER.get(), 0.9F, 2.0F);
                return;
            }
            if (pipeSpawnTimer <= 0) {
                int holeSize = random.nextInt(24, 28);
                int hole = random.nextInt(5, GAME_HEIGHT - holeSize - 21);
                pipes.add(new double[]{GAME_WIDTH, hole, holeSize});
                pipeSpawnTimer = 30;
            }
            pipeSpawnTimer--;

            for (int i = 0; i < pipes.size(); i++) {
                double[] pipe = pipes.get(i);
                pipe[0] -= 2.5;
                if (birdX + 10 >= pipe[0] && birdX <= pipe[0] + 16
                        && (birdY < pipe[1] || birdY + 8 > pipe[1] + pipe[2])) {
                    stage = GameStage.DIED;
                    playSound(PoSoundEvents.GAME_OVER.get(), 0.9F, 2.0F);
                }
                if (pipe[0] + 16 < birdX && pipe[1] >= 0) {
                    score++;
                    playSound(PoSoundEvents.POINT.get(), 1.0F, 0.7F);
                    pipe[1] = -1;
                }
            }
            pipes.removeIf(pipe -> pipe[0] + 16 < 0);
        }

        @Override
        protected void extraTick() {
            if (stage != GameStage.DIED && stage != GameStage.WON) {
                groundX -= 2.5;
                if (groundX <= -16) {
                    groundX += 16;
                }
            }
        }

        @Override
        public CompoundTag writeSnapshot() {
            CompoundTag tag = super.writeSnapshot();
            tag.putDouble("birdX", birdX);
            tag.putDouble("birdY", birdY);
            tag.putDouble("birdVY", birdVY);
            tag.putDouble("groundX", groundX);
            tag.putInt("pipeSpawnTimer", pipeSpawnTimer);
            ListTag list = new ListTag();
            for (double[] pipe : pipes) {
                CompoundTag p = new CompoundTag();
                p.putDouble("x", pipe[0]);
                p.putDouble("hole", pipe[1]);
                p.putDouble("holeSize", pipe[2]);
                list.add(p);
            }
            tag.put("pipes", list);
            return tag;
        }
    }

    public static final class Blocktris extends ServerGame {
        private static final List<List<List<Vec2>>> PIECES = List.of(
                List.of(
                        List.of(new Vec2(0, 0), new Vec2(0, -1), new Vec2(1, 0), new Vec2(0, 1)),
                        List.of(new Vec2(0, 0), new Vec2(-1, 0), new Vec2(1, 0), new Vec2(0, 1)),
                        List.of(new Vec2(0, 0), new Vec2(0, -1), new Vec2(-1, 0), new Vec2(0, 1)),
                        List.of(new Vec2(0, 0), new Vec2(0, -1), new Vec2(1, 0), new Vec2(-1, 0))
                ),
                List.of(List.of(new Vec2(0, -1), new Vec2(0, 0), new Vec2(0, 1), new Vec2(-1, 1)),
                        List.of(new Vec2(-1, -1), new Vec2(-1, 0), new Vec2(0, 0), new Vec2(1, 0)),
                        List.of(new Vec2(0, -1), new Vec2(0, 0), new Vec2(0, 1), new Vec2(1, -1)),
                        List.of(new Vec2(-1, 0), new Vec2(0, 0), new Vec2(1, 0), new Vec2(1, 1))),
                List.of(List.of(new Vec2(0, -1), new Vec2(0, 0), new Vec2(0, 1), new Vec2(0, 2)),
                        List.of(new Vec2(-1, 0), new Vec2(0, 0), new Vec2(1, 0), new Vec2(2, 0))),
                List.of(List.of(new Vec2(-1, 0), new Vec2(0, 0), new Vec2(-1, 1), new Vec2(0, -1)),
                        List.of(new Vec2(-1, 0), new Vec2(0, 0), new Vec2(0, 1), new Vec2(1, 1))),
                List.of(List.of(new Vec2(0, 0), new Vec2(1, 0), new Vec2(0, 1), new Vec2(1, 1))),
                List.of(List.of(new Vec2(0, -1), new Vec2(0, 0), new Vec2(0, 1), new Vec2(1, 1)),
                        List.of(new Vec2(-1, 1), new Vec2(-1, 0), new Vec2(0, 0), new Vec2(1, 0)),
                        List.of(new Vec2(0, -1), new Vec2(0, 0), new Vec2(0, 1), new Vec2(-1, -1)),
                        List.of(new Vec2(-1, 0), new Vec2(0, 0), new Vec2(1, 0), new Vec2(1, -1))),
                List.of(List.of(new Vec2(0, -1), new Vec2(0, 0), new Vec2(1, 0), new Vec2(1, 1)),
                        List.of(new Vec2(0, -1), new Vec2(0, 0), new Vec2(1, -1), new Vec2(-1, 0)))
        );

        private int[][] grid = new int[10][20];
        private Piece piece;
        private final List<Piece> next = new ArrayList<>();
        private int placementCooldown;

        @Override
        public void prepare() {
            grid = new int[10][20];
            next.clear();
            piece = new Piece(random.nextInt(7));
            placementCooldown = 0;
            score = 0;
            stage = GameStage.START;
            ticks = 1;
        }

        @Override
        public void start() {
            super.start();
            for (int i = 0; i < 3; i++) {
                next.add(new Piece(random.nextInt(7)));
            }
        }

        @Override
        protected void buttonDown(Button button) {
            super.buttonDown(button);
            if (stage != GameStage.PLAYING) {
                return;
            }
            switch (button) {
                case UP -> {
                    piece.rotate();
                    playSound(PoSoundEvents.SWING.get(), 1.5F, 0.5F);
                }
                case LEFT -> {
                    piece.move(-1, 0);
                    playSound(PoSoundEvents.SHOOT.get(), 2.5F, 0.1F);
                    placementCooldown = 10;
                }
                case RIGHT -> {
                    piece.move(1, 0);
                    playSound(PoSoundEvents.SHOOT.get(), 2.5F, 0.1F);
                    placementCooldown = 10;
                }
                case DOWN -> {
                    if (piece.move(0, 1)) {
                        place();
                    } else {
                        playSound(PoSoundEvents.SHOOT.get(), 2.5F, 0.1F);
                        placementCooldown = 10;
                    }
                }
                case BUTTON1 -> {
                    piece.hardDrop();
                    playSound(PoSoundEvents.EXPLOSION.get(), 0.7F, 0.5F);
                    placementCooldown = 0;
                    place();
                    placementCooldown = 10;
                }
                default -> {
                }
            }
        }

        @Override
        protected void gameTick() {
            if (piece.move(0, 1)) {
                place();
            }
        }

        @Override
        protected int gameTickDuration() {
            return (int) (10f / ((float) score / 50f + 1f));
        }

        @Override
        protected void extraTick() {
            placementCooldown--;
            if (placementCooldown < 0) {
                placementCooldown = 0;
            }
            if (stage != GameStage.PLAYING || ticks % 2 != 0 || placementCooldown > 0) {
                return;
            }
            if (leftDown) {
                piece.move(-1, 0);
                playSound(PoSoundEvents.SHOOT.get(), 2.5F, 0.1F);
            }
            if (rightDown) {
                piece.move(1, 0);
                playSound(PoSoundEvents.SHOOT.get(), 2.5F, 0.1F);
            }
            if (downDown && piece.move(0, 1)) {
                playSound(PoSoundEvents.SHOOT.get(), 2.5F, 0.1F);
                place();
            }
        }

        private void place() {
            piece.place();
            for (int y = grid[0].length - 1; y >= 0; y--) {
                boolean full = true;
                for (int x = 0; x < grid.length; x++) {
                    if (grid[x][y] == 0) {
                        full = false;
                        break;
                    }
                }
                if (full) {
                    score++;
                    playSound(PoSoundEvents.POINT.get(), 1.0F, 0.7F);
                    for (int line = y; line > 0; line--) {
                        for (int x = 0; x < grid.length; x++) {
                            grid[x][line] = grid[x][line - 1];
                        }
                    }
                    y++;
                }
            }
            piece = next.removeFirst();
            next.add(new Piece(random.nextInt(7)));
            if (piece.touches()) {
                stage = GameStage.DIED;
                playSound(PoSoundEvents.GAME_OVER.get(), 0.9F, 2.0F);
            }
        }

        @Override
        public CompoundTag writeSnapshot() {
            CompoundTag tag = super.writeSnapshot();
            ListTag rows = new ListTag();
            for (int y = 0; y < grid[0].length; y++) {
                int[] row = new int[grid.length];
                for (int x = 0; x < grid.length; x++) {
                    row[x] = grid[x][y];
                }
                rows.add(new IntArrayTag(row));
            }
            tag.put("grid", rows);
            tag.putInt("pieceType", piece.type);
            tag.putInt("pieceX", piece.x);
            tag.putInt("pieceY", piece.y);
            tag.putInt("pieceRot", piece.rotation);
            int[] nextTypes = new int[next.size()];
            for (int i = 0; i < next.size(); i++) {
                nextTypes[i] = next.get(i).type;
            }
            tag.putIntArray("nextTypes", nextTypes);
            tag.putInt("placementCooldown", placementCooldown);
            return tag;
        }

        private final class Piece {
            private final int type;
            private final List<List<Vec2>> variants;
            private int x = 4;
            private int y = 1;
            private int rotation;

            private Piece(int type) {
                this.type = type;
                this.variants = PIECES.get(type);
            }

            private List<Vec2> cells() {
                return variants.get(rotation);
            }

            private boolean touches() {
                for (Vec2 cell : cells()) {
                    int cx = x + (int) cell.x;
                    int cy = y + (int) cell.y;
                    if (cx < 0 || cx >= 10 || cy < 0 || cy >= 20 || grid[cx][cy] != 0) {
                        return true;
                    }
                }
                return false;
            }

            private boolean move(int dx, int dy) {
                x += dx;
                boolean blocked = touches();
                if (blocked) {
                    x -= dx;
                }
                y += dy;
                if (touches()) {
                    y -= dy;
                    blocked = true;
                }
                return blocked;
            }

            private void rotate() {
                rotation = (rotation + 1) % variants.size();
                if (touches()) {
                    rotation = (rotation - 1 + variants.size()) % variants.size();
                }
            }

            private void hardDrop() {
                while (!move(0, 1)) {
                }
            }

            private void place() {
                for (Vec2 cell : cells()) {
                    grid[x + (int) cell.x][y + (int) cell.y] = type + 1;
                }
            }
        }
    }
}
