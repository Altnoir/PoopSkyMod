package com.altnoir.poopsky.game.gamediscs;

import com.altnoir.poopsky.game.ServerGame;
import com.altnoir.poopsky.game.controls.Button;
import com.altnoir.poopsky.game.util.GameStage;
import com.altnoir.poopsky.init.PoSoundEvents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;

import java.util.ArrayList;
import java.util.List;

public class ServerFlappyBirdGame extends ServerGame {
    private double birdX = 20;
    private double birdY = 30;
    private double birdVY;
    private final List<double[]> pipes = new ArrayList<>();
    private double groundX;
    private int pipeSpawnTimer;

    @Override
    public void prepare() {
        super.prepare();
        birdX = 20;
        birdY = 30;
        birdVY = 0;
        pipes.clear();
        groundX = 0;
        pipeSpawnTimer = 0;
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
        if (birdY < 0 || birdY + 8 >= HEIGHT - 16) {
            stage = GameStage.DIED;
            playSound(PoSoundEvents.GAME_OVER.get(), 0.9F, 2.0F);
            return;
        }
        if (pipeSpawnTimer <= 0) {
            int holeSize = random.nextInt(24, 28);
            int hole = random.nextInt(5, HEIGHT - holeSize - 21);
            pipes.add(new double[]{WIDTH, hole, holeSize});
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
