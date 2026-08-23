package com.altnoir.poopsky.game.model;

import com.altnoir.poopsky.game.danmaku.Boss;
import com.altnoir.poopsky.game.danmaku.BossFactory;
import net.minecraft.nbt.CompoundTag;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public final class TouhouGameState {
    public static final int PLAY_WIDTH = 144;
    public static final int PLAY_HEIGHT = 160;
    public static final int PLAYER_SIZE = 8;
    public static final int PLAYER_HITBOX_SIZE = 2;
    public static final int BOSS_SIZE = 16;
    public static final int ENEMY_BULLET_SIZE = 6;
    public static final int PLAYER_BULLET_SIZE = 4;
    public static final int PLAYER_SPEED = 1;
    public static final int PLAYER_BULLET_SPEED = 4;
    public static final float BOSS_BULLET_DRAG = 0.995F;
    public static final int PLAYER_SHOOT_INTERVAL = 8;
    public static final int PLAYER_SHOOT_INTERVAL_FAST = 4;
    public static final int POWERUP_SIZE = 8;
    public static final int POWERUP_SPEED = 2;
    public static final int POWERUP_LIFE = 600;
    public static final float POWERUP_SPAWN_CHANCE = 0.15F;
    public static final int START_BOSS_HP = 80;
    public static final int SCORE_PER_BOSS = 10;
    public static final int BOSS_RESPAWN_DELAY = 40;
    public static final int BOSS_APPEAR_DURATION = 20;
    public static final float BOSS_SCALE_MIN = 0.1F;

    private float playerX;
    private float playerY;
    private float bossX;
    private float bossY;
    private int bossHp;
    private int bossMaxHp;
    private int wave;
    private int score;
    private int shootCooldown;
    private int bossSpawnTimer;
    private int bossAppearTimer;
    private int bossHitTicks;
    private float bossScale = 1.0F;
    private boolean hasSpeedBoost;
    private boolean hasDoubleShot;
    private Boss boss;
    private final List<Bullet> enemyBullets = new ArrayList<>();
    private final List<Bullet> playerBullets = new ArrayList<>();
    private final List<PowerUp> powerUps = new ArrayList<>();

    public void prepare(Random random) {
        playerX = PLAY_WIDTH / 2.0F - PLAYER_SIZE / 2.0F;
        playerY = PLAY_HEIGHT - 28.0F;
        score = 0;
        wave = 0;
        shootCooldown = 0;
        hasSpeedBoost = false;
        hasDoubleShot = false;
        enemyBullets.clear();
        playerBullets.clear();
        powerUps.clear();
        spawnBoss(random);
    }

    public TickResult tick(boolean up, boolean down, boolean left, boolean right, boolean shoot, Random random) {
        if (up) playerY -= PLAYER_SPEED;
        if (down) playerY += PLAYER_SPEED;
        if (left) playerX -= PLAYER_SPEED;
        if (right) playerX += PLAYER_SPEED;
        playerX = Math.clamp(playerX, 0, PLAY_WIDTH - PLAYER_SIZE);
        playerY = Math.clamp(playerY, 0, PLAY_HEIGHT - PLAYER_SIZE);

        if (bossHitTicks > 0) {
            bossHitTicks--;
        }

        boolean shot = false;
        boolean bossHit = false;
        boolean bossKilled = false;
        boolean powerUpPickup = false;

        if (shoot && shootCooldown <= 0) {
            int shootInterval = hasSpeedBoost ? PLAYER_SHOOT_INTERVAL_FAST : PLAYER_SHOOT_INTERVAL;
            float bulletX = playerX + PLAYER_SIZE / 2.0F - PLAYER_BULLET_SIZE / 2.0F;
            float bulletY = playerY - PLAYER_BULLET_SIZE;
            if (hasDoubleShot) {
                playerBullets.add(new Bullet(
                        bulletX - PLAYER_BULLET_SIZE,
                        bulletY,
                        0, -PLAYER_BULLET_SPEED,
                        120, 0, 0, 0
                ));
                playerBullets.add(new Bullet(
                        bulletX + PLAYER_BULLET_SIZE,
                        bulletY,
                        0, -PLAYER_BULLET_SPEED,
                        120, 0, 0, 0
                ));
            } else {
                playerBullets.add(new Bullet(
                        bulletX,
                        bulletY,
                        0, -PLAYER_BULLET_SPEED,
                        120, 0, 0, 0
                ));
            }
            shootCooldown = shootInterval;
            shot = true;
        }
        if (shootCooldown > 0) shootCooldown--;

        if (bossSpawnTimer > 0) {
            bossSpawnTimer--;
            if (bossSpawnTimer == 0) {
                spawnBoss(random);
            }
        }

        if (bossSpawnTimer == 0) {
            if (bossAppearTimer < BOSS_APPEAR_DURATION) {
                bossAppearTimer++;
            }
            bossScale = Math.min(1.0F, BOSS_SCALE_MIN + (bossAppearTimer / (float) BOSS_APPEAR_DURATION) * (1.0F - BOSS_SCALE_MIN));

            if (boss != null && bossAppearTimer >= BOSS_APPEAR_DURATION) {
                boss.tick(this, random);
            }
        }

        updateEnemyBullets();
        updatePlayerBullets();
        updatePowerUps();
        powerUpPickup = tryPickupPowerUp();

        if (bossSpawnTimer == 0) {
            for (int i = playerBullets.size() - 1; i >= 0; i--) {
                Bullet bullet = playerBullets.get(i);
                if (intersects(bullet.x, bullet.y, PLAYER_BULLET_SIZE, PLAYER_BULLET_SIZE,
                        bossX, bossY, BOSS_SIZE, BOSS_SIZE)) {
                    playerBullets.remove(i);
                    bossHp--;
                    bossHit = true;
                    bossHitTicks = 1;
                    trySpawnPowerUp(random);
                    if (bossHp <= 0) {
                        bossKilled = true;
                        score += SCORE_PER_BOSS;
                        wave++;
                        enemyBullets.clear();
                        bossSpawnTimer = BOSS_RESPAWN_DELAY;
                        bossAppearTimer = 0;
                        bossScale = 0.0F;
                    }
                    break;
                }
            }
        }

        float playerCenterX = playerX + PLAYER_SIZE / 2.0F;
        float playerCenterY = playerY + PLAYER_SIZE / 2.0F;
        float hitboxMinX = playerCenterX - PLAYER_HITBOX_SIZE / 2.0F;
        float hitboxMinY = playerCenterY - PLAYER_HITBOX_SIZE / 2.0F;
        for (Bullet bullet : enemyBullets) {
            if (intersects(bullet.x, bullet.y, ENEMY_BULLET_SIZE, ENEMY_BULLET_SIZE,
                    hitboxMinX, hitboxMinY, PLAYER_HITBOX_SIZE, PLAYER_HITBOX_SIZE)) {
                return new TickResult(shot, bossHit, bossKilled, true, powerUpPickup);
            }
        }

        return new TickResult(shot, bossHit, bossKilled, false, powerUpPickup);
    }

    public void spawnEnemyBullet(float x, float y, float vx, float vy, int life, int maxBounces, float acceleration) {
        spawnEnemyBullet(x, y, vx, vy, life, maxBounces, acceleration, false);
    }

    public void spawnEnemyBullet(float x, float y, float vx, float vy, int life, int maxBounces, float acceleration, boolean removeOnBossHit) {
        enemyBullets.add(new Bullet(x, y, vx, vy, life, 0, maxBounces, acceleration, removeOnBossHit));
    }

    public void spawnCircle(float centerX, float centerY, int count, float speed, int maxBounces, float angleOffsetDegrees) {
        double offset = Math.toRadians(angleOffsetDegrees);
        for (int i = 0; i < count; i++) {
            double angle = Math.PI * 2 * i / count + offset;
            float vx = (float) (Math.cos(angle) * speed);
            float vy = (float) (Math.sin(angle) * speed);
            spawnEnemyBullet(
                    centerX - ENEMY_BULLET_SIZE / 2.0F,
                    centerY - ENEMY_BULLET_SIZE / 2.0F,
                    vx, vy,
                    600,
                    maxBounces,
                    0.0F
            );
        }
    }

    public void spawnArc(float centerX, float centerY, int count, float speed, int maxBounces, float centerAngleDegrees, float spreadDegrees) {
        double center = Math.toRadians(centerAngleDegrees);
        double spread = Math.toRadians(spreadDegrees);
        for (int i = 0; i < count; i++) {
            double t = count <= 1 ? 0.0 : (i / (double) (count - 1) - 0.5);
            double angle = center + t * spread;
            float vx = (float) (Math.cos(angle) * speed);
            float vy = (float) (Math.sin(angle) * speed);
            spawnEnemyBullet(
                    centerX - ENEMY_BULLET_SIZE / 2.0F,
                    centerY - ENEMY_BULLET_SIZE / 2.0F,
                    vx, vy,
                    600,
                    maxBounces,
                    0.0F
            );
        }
    }

    public float getBossCenterX() {
        return bossX + BOSS_SIZE / 2.0F;
    }

    public float getBossCenterY() {
        return bossY + BOSS_SIZE / 2.0F;
    }

    private void updateEnemyBullets() {
        for (int i = enemyBullets.size() - 1; i >= 0; i--) {
            Bullet bullet = enemyBullets.get(i);
            float x = bullet.x + bullet.vx;
            float y = bullet.y + bullet.vy;
            float vx = bullet.vx;
            float vy = bullet.vy;
            int bounces = bullet.bounces;

            if (x <= 0) {
                x = 0;
                if (Math.abs(vy) < 0.001F || bullet.maxBounces <= 0 || bounces >= bullet.maxBounces) {
                    enemyBullets.remove(i);
                    continue;
                }
                vx = Math.abs(vx);
                bounces++;
            } else if (x >= PLAY_WIDTH - ENEMY_BULLET_SIZE) {
                x = PLAY_WIDTH - ENEMY_BULLET_SIZE;
                if (Math.abs(vy) < 0.001F || bullet.maxBounces <= 0 || bounces >= bullet.maxBounces) {
                    enemyBullets.remove(i);
                    continue;
                }
                vx = -Math.abs(vx);
                bounces++;
            }

            if (y <= 0) {
                y = 0;
                if (bullet.maxBounces <= 0 || bounces >= bullet.maxBounces) {
                    enemyBullets.remove(i);
                    continue;
                }
                vy = Math.abs(vy);
                bounces++;
            } else if (y >= PLAY_HEIGHT) {
                enemyBullets.remove(i);
                continue;
            }

            if (bullet.removeOnBossHit && intersects(x, y, ENEMY_BULLET_SIZE, ENEMY_BULLET_SIZE,
                    bossX, bossY, BOSS_SIZE, BOSS_SIZE)) {
                enemyBullets.remove(i);
                continue;
            }

            if (bullet.acceleration > 0) {
                vx *= (1.0F + bullet.acceleration);
                vy *= (1.0F + bullet.acceleration);
            } else {
                vx *= BOSS_BULLET_DRAG;
                vy *= BOSS_BULLET_DRAG;
            }

            if (bullet.life <= 0) {
                enemyBullets.remove(i);
            } else {
                enemyBullets.set(i, new Bullet(x, y, vx, vy, bullet.life - 1, bounces, bullet.maxBounces, bullet.acceleration, bullet.removeOnBossHit));
            }
        }
    }

    private void updatePlayerBullets() {
        for (int i = playerBullets.size() - 1; i >= 0; i--) {
            Bullet bullet = playerBullets.get(i);
            float x = bullet.x + bullet.vx;
            float y = bullet.y + bullet.vy;
            if (y < -PLAYER_BULLET_SIZE || y > PLAY_HEIGHT || x < -PLAYER_BULLET_SIZE || x > PLAY_WIDTH) {
                playerBullets.remove(i);
            } else {
                playerBullets.set(i, new Bullet(x, y, bullet.vx, bullet.vy, bullet.life - 1, 0, 0, 0));
            }
        }
    }

    private void updatePowerUps() {
        for (int i = powerUps.size() - 1; i >= 0; i--) {
            PowerUp powerUp = powerUps.get(i);
            float x = powerUp.x + powerUp.vx;
            float y = powerUp.y + powerUp.vy;
            float vx = powerUp.vx;
            float vy = powerUp.vy;

            if (x <= 0) {
                x = 0;
                vx = Math.abs(vx);
            } else if (x >= PLAY_WIDTH - POWERUP_SIZE) {
                x = PLAY_WIDTH - POWERUP_SIZE;
                vx = -Math.abs(vx);
            }
            if (y <= 0) {
                y = 0;
                vy = Math.abs(vy);
            } else if (y >= PLAY_HEIGHT) {
                powerUps.remove(i);
                continue;
            }

            if (powerUp.life <= 0) {
                powerUps.remove(i);
            } else {
                powerUps.set(i, new PowerUp(x, y, vx, vy, powerUp.type, powerUp.life - 1));
            }
        }
    }

    private boolean tryPickupPowerUp() {
        for (int i = powerUps.size() - 1; i >= 0; i--) {
            PowerUp powerUp = powerUps.get(i);
            if (intersects(powerUp.x, powerUp.y, POWERUP_SIZE, POWERUP_SIZE,
                    playerX, playerY, PLAYER_SIZE, PLAYER_SIZE)) {
                powerUps.remove(i);
                if (powerUp.type == PowerUpType.SPEED) {
                    hasSpeedBoost = true;
                } else {
                    hasDoubleShot = true;
                }
                return true;
            }
        }
        return false;
    }

    private void trySpawnPowerUp(Random random) {
        if (random.nextFloat() > POWERUP_SPAWN_CHANCE) {
            return;
        }

        boolean speedOnField = powerUps.stream().anyMatch(powerUp -> powerUp.type == PowerUpType.SPEED);
        boolean doubleOnField = powerUps.stream().anyMatch(powerUp -> powerUp.type == PowerUpType.DOUBLE);
        List<PowerUpType> available = new ArrayList<>();
        if (!hasSpeedBoost && !speedOnField) {
            available.add(PowerUpType.SPEED);
        }
        if (!hasDoubleShot && !doubleOnField) {
            available.add(PowerUpType.DOUBLE);
        }
        if (available.isEmpty()) {
            return;
        }

        PowerUpType type = available.get(random.nextInt(available.size()));
        float x = random.nextInt(PLAY_WIDTH - POWERUP_SIZE + 1);
        float y = 20 + random.nextInt(PLAY_HEIGHT - POWERUP_SIZE - 40);
        float vx = (random.nextFloat() * 2.0F - 1.0F) * POWERUP_SPEED;
        float vy = (random.nextFloat() * 2.0F - 1.0F) * POWERUP_SPEED;
        powerUps.add(new PowerUp(x, y, vx, vy, type, POWERUP_LIFE));
    }


    private void spawnBoss(Random random) {
        bossMaxHp = START_BOSS_HP + wave * 5;
        bossHp = bossMaxHp;
        bossX = PLAY_WIDTH / 2.0F - BOSS_SIZE / 2.0F;
        bossY = 12;
        bossSpawnTimer = 0;
        bossAppearTimer = 0;
        bossScale = BOSS_SCALE_MIN;
        boss = BossFactory.createRandomBoss(random, wave);
    }

    private static boolean intersects(float ax, float ay, float aw, float ah,
                                      float bx, float by, float bw, float bh) {
        return ax < bx + bw && ax + aw > bx && ay < by + bh && ay + ah > by;
    }

    public float getPlayerX() {
        return playerX;
    }

    public float getPlayerY() {
        return playerY;
    }

    public float getBossX() {
        return bossX;
    }

    public float getBossY() {
        return bossY;
    }

    public float getBossScale() {
        return bossScale;
    }

    public float getBossHitScale() {
        return bossHitTicks == 1 ? 0.8F : 1.0F;
    }

    public int getBossSpawnTimer() {
        return bossSpawnTimer;
    }

    public int getBossHp() {
        return bossHp;
    }

    public int getBossMaxHp() {
        return bossMaxHp;
    }

    public int getWave() {
        return wave;
    }

    public int getScore() {
        return score;
    }

    public boolean hasSpeedBoost() {
        return hasSpeedBoost;
    }

    public boolean hasDoubleShot() {
        return hasDoubleShot;
    }

    public List<PowerUp> getPowerUps() {
        return powerUps;
    }

    public List<Bullet> getEnemyBullets() {
        return enemyBullets;
    }

    public List<Bullet> getPlayerBullets() {
        return playerBullets;
    }

    public void writeSnapshot(CompoundTag tag) {
        tag.putFloat("playerX", playerX);
        tag.putFloat("playerY", playerY);
        tag.putFloat("bossX", bossX);
        tag.putFloat("bossY", bossY);
        tag.putInt("bossHp", bossHp);
        tag.putInt("bossMaxHp", bossMaxHp);
        tag.putInt("wave", wave);
        tag.putInt("score", score);
        tag.putInt("shootCooldown", shootCooldown);
        tag.putInt("bossSpawnTimer", bossSpawnTimer);
        tag.putInt("bossAppearTimer", bossAppearTimer);
        tag.putInt("bossHitTicks", bossHitTicks);
        tag.putFloat("bossScale", bossScale);
        tag.putBoolean("hasSpeedBoost", hasSpeedBoost);
        tag.putBoolean("hasDoubleShot", hasDoubleShot);

        tag.putInt("powerUpCount", powerUps.size());
        for (int i = 0; i < powerUps.size(); i++) {
            PowerUp powerUp = powerUps.get(i);
            tag.putFloat("powerUpX" + i, powerUp.x);
            tag.putFloat("powerUpY" + i, powerUp.y);
            tag.putFloat("powerUpVX" + i, powerUp.vx);
            tag.putFloat("powerUpVY" + i, powerUp.vy);
            tag.putString("powerUpType" + i, powerUp.type.name());
            tag.putInt("powerUpLife" + i, powerUp.life);
        }

        tag.putInt("enemyBulletCount", enemyBullets.size());
        for (int i = 0; i < enemyBullets.size(); i++) {
            Bullet bullet = enemyBullets.get(i);
            tag.putFloat("enemyBulletX" + i, bullet.x);
            tag.putFloat("enemyBulletY" + i, bullet.y);
            tag.putFloat("enemyBulletVX" + i, bullet.vx);
            tag.putFloat("enemyBulletVY" + i, bullet.vy);
            tag.putInt("enemyBulletLife" + i, bullet.life);
            tag.putInt("enemyBulletBounces" + i, bullet.bounces);
            tag.putInt("enemyBulletMaxBounces" + i, bullet.maxBounces);
            tag.putFloat("enemyBulletAcceleration" + i, bullet.acceleration);
            tag.putBoolean("enemyBulletRemoveOnBossHit" + i, bullet.removeOnBossHit);
        }

        tag.putInt("playerBulletCount", playerBullets.size());
        for (int i = 0; i < playerBullets.size(); i++) {
            Bullet bullet = playerBullets.get(i);
            tag.putFloat("playerBulletX" + i, bullet.x);
            tag.putFloat("playerBulletY" + i, bullet.y);
            tag.putFloat("playerBulletVX" + i, bullet.vx);
            tag.putFloat("playerBulletVY" + i, bullet.vy);
            tag.putInt("playerBulletLife" + i, bullet.life);
            tag.putInt("playerBulletBounces" + i, bullet.bounces);
            tag.putInt("playerBulletMaxBounces" + i, bullet.maxBounces);
            tag.putFloat("playerBulletAcceleration" + i, bullet.acceleration);
        }
    }

    public void applySnapshot(CompoundTag tag) {
        playerX = tag.getFloat("playerX");
        playerY = tag.getFloat("playerY");
        bossX = tag.getFloat("bossX");
        bossY = tag.getFloat("bossY");
        bossHp = tag.getInt("bossHp");
        bossMaxHp = tag.getInt("bossMaxHp");
        wave = tag.getInt("wave");
        score = tag.getInt("score");
        shootCooldown = tag.getInt("shootCooldown");
        bossSpawnTimer = tag.getInt("bossSpawnTimer");
        bossAppearTimer = tag.getInt("bossAppearTimer");
        bossHitTicks = tag.getInt("bossHitTicks");
        bossScale = tag.getFloat("bossScale");
        hasSpeedBoost = tag.getBoolean("hasSpeedBoost");
        hasDoubleShot = tag.getBoolean("hasDoubleShot");

        powerUps.clear();
        int powerUpCount = tag.getInt("powerUpCount");
        for (int i = 0; i < powerUpCount; i++) {
            powerUps.add(new PowerUp(
                    tag.getFloat("powerUpX" + i),
                    tag.getFloat("powerUpY" + i),
                    tag.getFloat("powerUpVX" + i),
                    tag.getFloat("powerUpVY" + i),
                    PowerUpType.valueOf(tag.getString("powerUpType" + i)),
                    tag.getInt("powerUpLife" + i)
            ));
        }

        enemyBullets.clear();
        int enemyCount = tag.getInt("enemyBulletCount");
        for (int i = 0; i < enemyCount; i++) {
            enemyBullets.add(new Bullet(
                    tag.getFloat("enemyBulletX" + i),
                    tag.getFloat("enemyBulletY" + i),
                    tag.getFloat("enemyBulletVX" + i),
                    tag.getFloat("enemyBulletVY" + i),
                    tag.getInt("enemyBulletLife" + i),
                    tag.getInt("enemyBulletBounces" + i),
                    tag.getInt("enemyBulletMaxBounces" + i),
                    tag.getFloat("enemyBulletAcceleration" + i),
                    tag.getBoolean("enemyBulletRemoveOnBossHit" + i)
            ));
        }

        playerBullets.clear();
        int playerCount = tag.getInt("playerBulletCount");
        for (int i = 0; i < playerCount; i++) {
            playerBullets.add(new Bullet(
                    tag.getFloat("playerBulletX" + i),
                    tag.getFloat("playerBulletY" + i),
                    tag.getFloat("playerBulletVX" + i),
                    tag.getFloat("playerBulletVY" + i),
                    tag.getInt("playerBulletLife" + i),
                    tag.getInt("playerBulletBounces" + i),
                    tag.getInt("playerBulletMaxBounces" + i),
                    tag.getFloat("playerBulletAcceleration" + i)
            ));
        }
    }

    public enum PowerUpType {
        SPEED,
        DOUBLE
    }

    public record PowerUp(float x, float y, float vx, float vy, PowerUpType type, int life) {
    }

    public record Bullet(float x, float y, float vx, float vy, int life, int bounces, int maxBounces, float acceleration, boolean removeOnBossHit) {
        public Bullet(float x, float y, float vx, float vy, int life, int bounces, int maxBounces, float acceleration) {
            this(x, y, vx, vy, life, bounces, maxBounces, acceleration, false);
        }
    }

    public record TickResult(boolean shot, boolean bossHit, boolean bossKilled, boolean playerHit, boolean powerUpPickup) {
    }
}
