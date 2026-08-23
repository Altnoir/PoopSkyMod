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
    public static final int PLAYER_HITBOX_SIZE = 1;
    public static final int PLAYER_BULLET_SIZE = 4;
    public static final int PLAYER_SPEED = 2;
    public static final float FOCUS_SPEED_MULTIPLIER = 0.5F;
    public static final int PLAYER_BULLET_SPEED = 4;
    public static final int PLAYER_SHOOT_INTERVAL = 8;
    public static final int PLAYER_SHOOT_INTERVAL_FAST = 4;

    public static final int POWERUP_SIZE = 8;
    public static final int POWERUP_SPEED = 2;
    public static final int POWERUP_LIFE = 600;
    public static final float POWERUP_SPAWN_CHANCE = 0.15F;

    public static final int START_BOSS_HP = 80;
    public static final int BOSS_SIZE = 16;
    public static final float BOSS_SCALE_MIN = 0.1F;
    public static final int BOSS_RESPAWN_DELAY = 40;
    public static final int BOSS_APPEAR_DURATION = 20;
    public static final int BOSS_BULLET_SIZE = 6;
    public static final float BOSS_BULLET_DRAG = 0.99F;
    private static final int BULLET_DATA_SIZE = 9;

    public static final int SCORE_PER_BOSS = 5;

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
    private float bossScale = 1.0F;
    private boolean hasSpeedBoost;
    private boolean hasDoubleShot;
    private final List<ActiveBoss> bosses = new ArrayList<>();
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

    public TickResult tick(boolean up, boolean down, boolean left, boolean right, boolean shoot, boolean focus, Random random) {
        float playerSpeed = focus ? PLAYER_SPEED * FOCUS_SPEED_MULTIPLIER : PLAYER_SPEED;
        if (up) playerY -= playerSpeed;
        if (down) playerY += playerSpeed;
        if (left) playerX -= playerSpeed;
        if (right) playerX += playerSpeed;
        playerX = Math.clamp(playerX, 0, PLAY_WIDTH - PLAYER_SIZE);
        playerY = Math.clamp(playerY, 0, PLAY_HEIGHT - PLAYER_SIZE);

        for (ActiveBoss activeBoss : bosses) {
            if (activeBoss.hitTicks > 0) {
                activeBoss.hitTicks--;
            }
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

            if (bossAppearTimer >= BOSS_APPEAR_DURATION) {
                for (ActiveBoss activeBoss : bosses) {
                    if (activeBoss.boss == null) {
                        continue;
                    }
                    bossX = activeBoss.x;
                    bossY = activeBoss.y;
                    activeBoss.boss.tick(this, random);
                    activeBoss.x = bossX;
                    activeBoss.y = bossY;
                }
            }
        }

        updateEnemyBullets();
        updatePlayerBullets();
        updatePowerUps();
        powerUpPickup = tryPickupPowerUp();

        if (bossSpawnTimer == 0) {
            for (int i = playerBullets.size() - 1; i >= 0; i--) {
                Bullet bullet = playerBullets.get(i);
                ActiveBoss hitBoss = null;
                for (ActiveBoss activeBoss : bosses) {
                    if (intersects(bullet.x, bullet.y, PLAYER_BULLET_SIZE, PLAYER_BULLET_SIZE,
                            activeBoss.x, activeBoss.y, BOSS_SIZE, BOSS_SIZE)) {
                        hitBoss = activeBoss;
                        break;
                    }
                }

                if (hitBoss != null) {
                    removeUnordered(playerBullets, i);
                    hitBoss.hp--;
                    bossHp--;
                    bossHit = true;
                    hitBoss.hitTicks = 2;
                    trySpawnPowerUp(random);

                    if (hitBoss.hp <= 0) {
                        bosses.remove(hitBoss);
                    }

                    if (bosses.isEmpty()) {
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
            if (intersects(bullet.x, bullet.y, BOSS_BULLET_SIZE, BOSS_BULLET_SIZE,
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
        spawnArcRadians(centerX, centerY, count, speed, maxBounces,
                Math.toRadians(angleOffsetDegrees), Math.PI * 2.0 / count);
    }

    public void spawnArc(float centerX, float centerY, int count, float speed, int maxBounces, float centerAngleDegrees, float spreadDegrees) {
        double center = Math.toRadians(centerAngleDegrees);
        double spread = Math.toRadians(spreadDegrees);
        spawnArcRadians(centerX, centerY, count, speed, maxBounces,
                count <= 1 ? center : center - spread / 2.0, count <= 1 ? 0.0 : spread / (count - 1));
    }

    private void spawnArcRadians(float centerX, float centerY, int count, float speed, int maxBounces,
                                 double startAngle, double angleStep) {
        if (count <= 0) {
            return;
        }

        double cos = Math.cos(startAngle);
        double sin = Math.sin(startAngle);
        double stepCos = angleStep == 0.0 ? 1.0 : Math.cos(angleStep);
        double stepSin = angleStep == 0.0 ? 0.0 : Math.sin(angleStep);
        float x = centerX - BOSS_BULLET_SIZE / 2.0F;
        float y = centerY - BOSS_BULLET_SIZE / 2.0F;
        for (int i = 0; i < count; i++) {
            spawnEnemyBullet(x, y, (float) (cos * speed), (float) (sin * speed), 600, maxBounces, 0.0F);
            double nextCos = cos * stepCos - sin * stepSin;
            sin = sin * stepCos + cos * stepSin;
            cos = nextCos;
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
            bullet.x += bullet.vx;
            bullet.y += bullet.vy;

            if (bullet.x <= 0) {
                bullet.x = 0;
                if (Math.abs(bullet.vy) < 0.001F || bullet.maxBounces <= 0 || bullet.bounces >= bullet.maxBounces) {
                    removeUnordered(enemyBullets, i);
                    continue;
                }
                bullet.vx = Math.abs(bullet.vx);
                bullet.bounces++;
            } else if (bullet.x >= PLAY_WIDTH - BOSS_BULLET_SIZE) {
                bullet.x = PLAY_WIDTH - BOSS_BULLET_SIZE;
                if (Math.abs(bullet.vy) < 0.001F || bullet.maxBounces <= 0 || bullet.bounces >= bullet.maxBounces) {
                    removeUnordered(enemyBullets, i);
                    continue;
                }
                bullet.vx = -Math.abs(bullet.vx);
                bullet.bounces++;
            }

            if (bullet.y <= 0) {
                bullet.y = 0;
                if (bullet.maxBounces <= 0 || bullet.bounces >= bullet.maxBounces) {
                    removeUnordered(enemyBullets, i);
                    continue;
                }
                bullet.vy = Math.abs(bullet.vy);
                bullet.bounces++;
            } else if (bullet.y >= PLAY_HEIGHT) {
                removeUnordered(enemyBullets, i);
                continue;
            }

            if (bullet.removeOnBossHit) {
                boolean hitBoss = false;
                for (ActiveBoss activeBoss : bosses) {
                    if (intersects(bullet.x, bullet.y, BOSS_BULLET_SIZE, BOSS_BULLET_SIZE,
                            activeBoss.x, activeBoss.y, BOSS_SIZE, BOSS_SIZE)) {
                        hitBoss = true;
                        break;
                    }
                }
                if (hitBoss) {
                    removeUnordered(enemyBullets, i);
                    continue;
                }
            }

            if (bullet.acceleration > 0) {
                bullet.vx *= 1.0F + bullet.acceleration;
                bullet.vy *= 1.0F + bullet.acceleration;
            } else {
                bullet.vx *= BOSS_BULLET_DRAG;
                bullet.vy *= BOSS_BULLET_DRAG;
            }

            if (bullet.life <= 0) {
                removeUnordered(enemyBullets, i);
            } else {
                bullet.life--;
            }
        }
    }

    private void updatePlayerBullets() {
        for (int i = playerBullets.size() - 1; i >= 0; i--) {
            Bullet bullet = playerBullets.get(i);
            bullet.x += bullet.vx;
            bullet.y += bullet.vy;
            if (bullet.y < -PLAYER_BULLET_SIZE || bullet.y > PLAY_HEIGHT
                    || bullet.x < -PLAYER_BULLET_SIZE || bullet.x > PLAY_WIDTH) {
                removeUnordered(playerBullets, i);
            } else {
                bullet.life--;
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

        boolean speedAvailable = !hasSpeedBoost;
        boolean doubleAvailable = !hasDoubleShot;
        for (PowerUp powerUp : powerUps) {
            speedAvailable &= powerUp.type != PowerUpType.SPEED;
            doubleAvailable &= powerUp.type != PowerUpType.DOUBLE;
        }
        if (!speedAvailable && !doubleAvailable) {
            return;
        }

        PowerUpType type = speedAvailable && doubleAvailable
                ? (random.nextInt(2) == 0 ? PowerUpType.SPEED : PowerUpType.DOUBLE)
                : speedAvailable ? PowerUpType.SPEED : PowerUpType.DOUBLE;
        float x = random.nextInt(PLAY_WIDTH - POWERUP_SIZE + 1);
        float y = 20 + random.nextInt(PLAY_HEIGHT - POWERUP_SIZE - 40);
        float vx = (random.nextFloat() * 2.0F - 1.0F) * POWERUP_SPEED;
        float vy = (random.nextFloat() * 2.0F - 1.0F) * POWERUP_SPEED;
        powerUps.add(new PowerUp(x, y, vx, vy, type, POWERUP_LIFE));
    }


    private void spawnBoss(Random random) {
        bosses.clear();

        int count = bossCountForWave(wave);
        int baseMaxHp = START_BOSS_HP + wave * 5;
        float[][] positions = bossPositions(count);

        for (int i = 0; i < count; i++) {
            Boss boss = BossFactory.createRandomBoss(random, wave);
            bosses.add(new ActiveBoss(
                    boss,
                    positions[i][0],
                    positions[i][1],
                    baseMaxHp,
                    baseMaxHp
            ));
        }

        bossMaxHp = 0;
        bossHp = 0;
        for (ActiveBoss activeBoss : bosses) {
            bossMaxHp += activeBoss.maxHp;
            bossHp += activeBoss.hp;
        }

        bossX = bosses.getFirst().x;
        bossY = bosses.getFirst().y;
        bossSpawnTimer = 0;
        bossAppearTimer = 0;
        bossScale = BOSS_SCALE_MIN;
    }

    private static int bossCountForWave(int wave) {
        if (wave >= 9) {
            return 3;
        }
        if (wave >= 4) {
            return 2;
        }
        return 1;
    }

    private static float[][] bossPositions(int count) {
        float centerX = PLAY_WIDTH / 2.0F - BOSS_SIZE / 2.0F;
        float topY = 12.0F;
        float sideY = 30.0F;
        float sideOffset = 30.0F;

        if (count == 2) {
            return new float[][]{
                    {centerX - sideOffset, topY},
                    {centerX + sideOffset, topY}
            };
        }
        if (count == 3) {
            return new float[][]{
                    {centerX - sideOffset, sideY},
                    {centerX, topY},
                    {centerX + sideOffset, sideY}
            };
        }
        return new float[][]{{centerX, topY}};
    }

    private static boolean intersects(float ax, float ay, float aw, float ah,
                                      float bx, float by, float bw, float bh) {
        return ax < bx + bw && ax + aw > bx && ay < by + bh && ay + ah > by;
    }

    private static <T> void removeUnordered(List<T> list, int index) {
        int last = list.size() - 1;
        if (index != last) {
            list.set(index, list.get(last));
        }
        list.removeLast();
    }

    public float getPlayerX() {
        return playerX;
    }

    public float getPlayerY() {
        return playerY;
    }

    public List<ActiveBoss> getBosses() {
        return bosses;
    }

    public float getBossX() {
        return bossX;
    }

    public float getBossY() {
        return bossY;
    }

    public void setBossX(float bossX) {
        this.bossX = bossX;
    }

    public void setBossY(float bossY) {
        this.bossY = bossY;
    }

    public float getBossScale() {
        return bossScale;
    }

    public float getBossHitScale(ActiveBoss activeBoss) {
        return activeBoss.hitTicks > 0 ? 0.8F : 1.0F;
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
        tag.putFloat("bossScale", bossScale);
        tag.putBoolean("hasSpeedBoost", hasSpeedBoost);
        tag.putBoolean("hasDoubleShot", hasDoubleShot);

        tag.putInt("bossCount", bosses.size());
        for (int i = 0; i < bosses.size(); i++) {
            ActiveBoss activeBoss = bosses.get(i);
            tag.putFloat("bossX" + i, activeBoss.x);
            tag.putFloat("bossY" + i, activeBoss.y);
            tag.putInt("bossHp" + i, activeBoss.hp);
            tag.putInt("bossMaxHp" + i, activeBoss.maxHp);
            tag.putInt("bossHitTicks" + i, activeBoss.hitTicks);
        }

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

        tag.putIntArray("enemyBullets", writeBullets(enemyBullets));
        tag.putIntArray("playerBullets", writeBullets(playerBullets));
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
        bossScale = tag.getFloat("bossScale");
        hasSpeedBoost = tag.getBoolean("hasSpeedBoost");
        hasDoubleShot = tag.getBoolean("hasDoubleShot");

        bosses.clear();
        int bossCount = tag.getInt("bossCount");
        if (bossCount > 0) {
            for (int i = 0; i < bossCount; i++) {
                ActiveBoss activeBoss = new ActiveBoss(
                        null,
                        tag.getFloat("bossX" + i),
                        tag.getFloat("bossY" + i),
                        tag.getInt("bossHp" + i),
                        tag.getInt("bossMaxHp" + i)
                );
                activeBoss.hitTicks = tag.getInt("bossHitTicks" + i);
                bosses.add(activeBoss);
            }
        } else if (tag.contains("bossX")) {
            bosses.add(new ActiveBoss(
                    null,
                    tag.getFloat("bossX"),
                    tag.getFloat("bossY"),
                    tag.getInt("bossHp"),
                    tag.getInt("bossMaxHp")
            ));
        }

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

        readBullets(enemyBullets, tag.getIntArray("enemyBullets"));
        readBullets(playerBullets, tag.getIntArray("playerBullets"));
    }

    private static int[] writeBullets(List<Bullet> bullets) {
        int[] data = new int[bullets.size() * BULLET_DATA_SIZE];
        for (int i = 0; i < bullets.size(); i++) {
            bullets.get(i).write(data, i * BULLET_DATA_SIZE);
        }
        return data;
    }

    private static void readBullets(List<Bullet> bullets, int[] data) {
        int count = data.length / BULLET_DATA_SIZE;
        while (bullets.size() > count) {
            bullets.removeLast();
        }
        for (int i = 0; i < count; i++) {
            int offset = i * BULLET_DATA_SIZE;
            if (i < bullets.size()) {
                bullets.get(i).read(data, offset);
            } else {
                Bullet bullet = new Bullet();
                bullet.read(data, offset);
                bullets.add(bullet);
            }
        }
    }

    public enum PowerUpType {
        SPEED,
        DOUBLE
    }

    public record PowerUp(float x, float y, float vx, float vy, PowerUpType type, int life) {
    }

    public static final class ActiveBoss {
        public Boss boss;
        public float x;
        public float y;
        public int hp;
        public int maxHp;
        public int hitTicks;

        public ActiveBoss(Boss boss, float x, float y, int hp, int maxHp) {
            this.boss = boss;
            this.x = x;
            this.y = y;
            this.hp = hp;
            this.maxHp = maxHp;
        }
    }

    public static final class Bullet {
        public float x, y, vx, vy;
        private float acceleration;
        private int life, bounces, maxBounces;
        private boolean removeOnBossHit;

        private Bullet() {
        }

        public Bullet(float x, float y, float vx, float vy, int life, int bounces, int maxBounces, float acceleration) {
            this(x, y, vx, vy, life, bounces, maxBounces, acceleration, false);
        }

        public Bullet(float x, float y, float vx, float vy, int life, int bounces, int maxBounces,
                      float acceleration, boolean removeOnBossHit) {
            set(x, y, vx, vy, life, bounces, maxBounces, acceleration, removeOnBossHit);
        }

        private void set(float x, float y, float vx, float vy, int life, int bounces, int maxBounces,
                         float acceleration, boolean removeOnBossHit) {
            this.x = x;
            this.y = y;
            this.vx = vx;
            this.vy = vy;
            this.life = life;
            this.bounces = bounces;
            this.maxBounces = maxBounces;
            this.acceleration = acceleration;
            this.removeOnBossHit = removeOnBossHit;
        }

        private void write(int[] data, int offset) {
            data[offset] = Float.floatToRawIntBits(x);
            data[offset + 1] = Float.floatToRawIntBits(y);
            data[offset + 2] = Float.floatToRawIntBits(vx);
            data[offset + 3] = Float.floatToRawIntBits(vy);
            data[offset + 4] = life;
            data[offset + 5] = bounces;
            data[offset + 6] = maxBounces;
            data[offset + 7] = Float.floatToRawIntBits(acceleration);
            data[offset + 8] = removeOnBossHit ? 1 : 0;
        }

        private void read(int[] data, int offset) {
            set(Float.intBitsToFloat(data[offset]), Float.intBitsToFloat(data[offset + 1]),
                    Float.intBitsToFloat(data[offset + 2]), Float.intBitsToFloat(data[offset + 3]),
                    data[offset + 4], data[offset + 5], data[offset + 6], Float.intBitsToFloat(data[offset + 7]),
                    data[offset + 8] != 0);
        }
    }

    public record TickResult(boolean shot, boolean bossHit, boolean bossKilled, boolean playerHit,
                             boolean powerUpPickup) {
    }
}
