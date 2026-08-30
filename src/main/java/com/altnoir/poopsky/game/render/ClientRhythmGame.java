package com.altnoir.poopsky.game.render;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.game.Button;
import com.altnoir.poopsky.game.GameStage;
import com.altnoir.poopsky.game.client.ClientGame;
import com.altnoir.poopsky.game.client.graphics.Image;
import com.altnoir.poopsky.game.model.RhythmGameState;
import com.altnoir.poopsky.game.rhythm.RhythmSong;
import com.altnoir.poopsky.game.rhythm.RhythmSongs;
import com.altnoir.poopsky.init.PoKeyBoardInput;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

/**
 * 客户端节奏游戏：中央 112×160 游戏框（4 条轨道）内渲染下落箭头与底部空箭头，
 * 两侧面板显示节奏积分/奖励积分/生命/连击等数据。旋律由服务端用音符盒音色演奏，
 * 客户端无需播放音频。
 * <p>
 * 判定展示为纯客户端：客户端依据本地歌曲时钟与按键（onButtonDown 钩子）做本地判定，
 * 即时渲染判定文字与命中动效，可同时出现多个；分数/生命等结算仍由服务端权威判定。
 * 布局常量集中在文件顶部，贴图坐标与背景不符时只需调整 FRAME_X / HOLLOW_CENTER_Y 等。
 */
public class ClientRhythmGame extends ClientGame {
    private static final ResourceLocation ARROW_TEX = PoopSky.loc("textures/games/sprite/arrow.png");
    private static final ResourceLocation ARROW_EMPTY_TEX = PoopSky.loc("textures/games/sprite/arrow_empty.png");
    private static final ResourceLocation HEART_TEX = PoopSky.loc("textures/games/sprite/heart.png");

    /** 轨道左右间隔（每格宽度）：只调这一个即可，轨道组自动居中于画布。 */
    private static final int LANE_WIDTH = 16;
    /** 轨道组左边缘（4 格宽，自动居中于 224 画布，中心固定 112）。 */
    private static final int FRAME_X = (WIDTH - RhythmGameState.LANES * LANE_WIDTH) / 2;
    /** 8×8 箭头放大倍数 → 显示 20×20。 */
    private static final float ARROW_SCALE = 2.0F;
    private static final int ARROW_DISPLAY = (int) (8 * ARROW_SCALE);
    /** 底部空箭头中心 Y（命中瞬间实心箭头与之完全重合）。 */
    private static final float HOLLOW_CENTER_Y = 138.0F;
    /** 下落速度：每 tick 像素（6.0 ≈ 全程约 25 tick / 1.25 秒）。 */
    private static final float PX_PER_TICK = 6.0F;
    /** 音符从多远开始渲染：与速度联动，音符在屏幕外上方入屏。 */
    private static final int NOTE_SPAWN_TICKS = (int) ((HOLLOW_CENTER_Y + ARROW_DISPLAY) / PX_PER_TICK);
    private static final int JUDGMENT_TEXT_TICKS = 12;
    /** 命中动效时长：箭头瞬移到空箭头位置后放大淡出。 */
    private static final int HIT_EFFECT_TICKS = 4;
    /** 命中动效放大倍率（在正常显示基础上额外放大）。 */
    private static final float HIT_EFFECT_GROW = 1.25F;
    /** 判定文字起始缩放：放大出现（无透明度淡化，避免淡出抽搐）。 */
    private static final float JUDGMENT_TEXT_MIN_SCALE = 0.6F;
    private static final float TICK_NANOS = 50_000_000.0F;

    private final RhythmGameState state = new RhythmGameState();
    private final Image[] filledArrows = new Image[RhythmGameState.LANES];
    private final Image[] hollowArrows = new Image[RhythmGameState.LANES];
    private final Image heartRed = new Image(HEART_TEX, 8, 16, 0, 0, 8, 8);
    private final Image heartGray = new Image(HEART_TEX, 8, 16, 0, 8, 8, 8);
    private long snapshotNanos;
    /** 上一帧渲染时间（用于按真实帧时间推进判定展示年龄，与 tick 时钟解耦）。 */
    private long lastFrameNanos;
    /** 客户端本地判定：命中的音符（隐藏/瞬移感）。 */
    private boolean[] clientHit = new boolean[0];
    /** 客户端本地判定：漏掉的音符（仅显示 MISS 文字）。 */
    private boolean[] clientMissed = new boolean[0];
    /** 客户端本地判定展示队列（文字 + 命中动效），独立于服务器权威结算。 */
    private final List<ClientJudgment> displayJudgments = new ArrayList<>();

    public ClientRhythmGame() {
        for (int lane = 0; lane < RhythmGameState.LANES; lane++) {
            filledArrows[lane] = new Image(ARROW_TEX, 8, 32, 0, lane * 8, 8, 8);
            hollowArrows[lane] = new Image(ARROW_EMPTY_TEX, 8, 32, 0, lane * 8, 8, 8);
        }
    }

    @Override
    public void applySnapshot(CompoundTag tag) {
        super.applySnapshot(tag);
        state.applySnapshot(tag);
        snapshotNanos = System.nanoTime();
        if (getStage() == GameStage.START) {
            resetClientState();
        }
    }

    private void resetClientState() {
        int size = state.getNotes().size();
        clientHit = new boolean[size];
        clientMissed = new boolean[size];
        displayJudgments.clear();
    }

    private void ensureClientArrays() {
        if (state.getNotes().size() != clientHit.length) {
            resetClientState();
        }
    }

    /** 本地按键判定（纯客户端展示）：按下按键时即时判定并记录，同拍多次按键可同时出现多个判定。 */
    @Override
    public void onButtonDown(Button button) {
        if (getStage() != GameStage.PLAYING) {
            return;
        }
        int lane = laneFor(button);
        if (lane < 0) {
            return;
        }
        ensureClientArrays();
        int currentTick = (int) Math.floor(computeCurrentTick());
        List<RhythmSong.Note> notes = state.getNotes();
        int best = -1;
        int bestOffset = Integer.MAX_VALUE;
        for (int i = 0; i < notes.size(); i++) {
            if (clientHit[i] || clientMissed[i]) {
                continue;
            }
            RhythmSong.Note note = notes.get(i);
            if (note.lane() != lane) {
                continue;
            }
            int offset = note.hitTick() - currentTick;
            if (Math.abs(offset) > RhythmGameState.GOOD_WINDOW_TICKS) {
                continue;
            }
            if (Math.abs(offset) < Math.abs(bestOffset)) {
                best = i;
                bestOffset = offset;
            }
        }
        if (best < 0) {
            return;
        }
        // 同轨道同拍的和弦音符一并命中（隐藏全部），显示一次判定
        int chordTick = notes.get(best).hitTick();
        for (int i = 0; i < notes.size(); i++) {
            if (clientHit[i] || clientMissed[i]) {
                continue;
            }
            RhythmSong.Note note = notes.get(i);
            if (note.lane() == lane && note.hitTick() == chordTick) {
                clientHit[i] = true;
            }
        }
        displayJudgments.add(new ClientJudgment(tierOf(bestOffset), lane, currentTick));
    }

    /** 渲染时补漏：越过判定窗口仍未命中的音符记为 MISS（纯展示）。 */
    private void detectMisses(double currentTick) {
        ensureClientArrays();
        List<RhythmSong.Note> notes = state.getNotes();
        for (int i = 0; i < notes.size(); i++) {
            if (clientHit[i] || clientMissed[i]) {
                continue;
            }
            if (notes.get(i).hitTick() + RhythmGameState.GOOD_WINDOW_TICKS < currentTick) {
                clientMissed[i] = true;
                displayJudgments.add(new ClientJudgment(
                        RhythmGameState.JUDGMENT_MISS,
                        notes.get(i).lane(),
                        notes.get(i).hitTick() + RhythmGameState.GOOD_WINDOW_TICKS
                ));
            }
        }
    }

    @Override
    protected void renderGame(GuiGraphics graphics, int posX, int posY, float partialTick) {
        double currentTick = computeCurrentTick();
        renderHollowArrows(graphics, posX, posY);
        if (getStage() == GameStage.PLAYING) {
            detectMisses(currentTick);
            renderNotes(graphics, posX, posY, currentTick);
            renderDisplayJudgments(graphics, posX, posY);
        }
        renderKeyHints(graphics, posX, posY);
        renderPanels(graphics, posX, posY);
    }

    /** 快照间外推（镜像 ClientTouhouGame）：最多外推 1 tick，暂停/断流时画面自然冻结。 */
    private double computeCurrentTick() {
        double tickDelta = getStage() == GameStage.PLAYING
                ? Math.min((System.nanoTime() - snapshotNanos) / TICK_NANOS, 1.0)
                : 0.0;
        return state.getSongTick() + tickDelta;
    }

    private void renderHollowArrows(GuiGraphics graphics, int posX, int posY) {
        for (int lane = 0; lane < RhythmGameState.LANES; lane++) {
            hollowArrows[lane].renderScaled(graphics,
                    posX + laneCenterX(lane) - ARROW_DISPLAY / 2,
                    posY + (int) HOLLOW_CENTER_Y - ARROW_DISPLAY / 2,
                    ARROW_SCALE);
        }
    }

    private void renderNotes(GuiGraphics graphics, int posX, int posY, double currentTick) {
        List<RhythmSong.Note> notes = state.getNotes();
        for (int i = 0; i < notes.size(); i++) {
            if (i < clientHit.length && clientHit[i]) {
                continue;
            }
            RhythmSong.Note note = notes.get(i);
            double delta = note.hitTick() - currentTick;
            if (delta <= 0 || delta > NOTE_SPAWN_TICKS) {
                continue;
            }
            int lane = note.lane();
            filledArrows[lane].renderScaled(graphics,
                    posX + laneCenterX(lane) - ARROW_DISPLAY / 2,
                    posY + (int) (HOLLOW_CENTER_Y - delta * PX_PER_TICK) - ARROW_DISPLAY / 2,
                    ARROW_SCALE);
        }
    }

    /** 推进判定展示年龄并渲染命中动效（箭头瞬移到空箭头位置，放大并淡出）。
     *  判定文字由 renderPanels 在生命值下方渲染（放大出现，无透明度）。 */
    private void renderDisplayJudgments(GuiGraphics graphics, int posX, int posY) {
        long now = System.nanoTime();
        if (!Minecraft.getInstance().isPaused() && lastFrameNanos != 0) {
            // 按真实帧间隔换算 tick（20 TPS），推进所有判定记录的展示年龄
            float deltaTicks = (float) ((now - lastFrameNanos) / TICK_NANOS);
            for (ClientJudgment judgment : displayJudgments) {
                judgment.shownAge += deltaTicks;
            }
        }
        lastFrameNanos = now;
        displayJudgments.removeIf(judgment -> judgment.shownAge >= JUDGMENT_TEXT_TICKS);

        for (ClientJudgment judgment : displayJudgments) {
            double age = judgment.shownAge;
            if (!isHitJudgment(judgment.type) || age >= HIT_EFFECT_TICKS) {
                continue;
            }
            float progress = (float) (age / HIT_EFFECT_TICKS);
            float scale = ARROW_SCALE * (HIT_EFFECT_GROW * progress);
            float displaySize = 8 * scale;
            int x = posX + laneCenterX(judgment.lane) - (int) (displaySize / 2);
            int y = posY + (int) HOLLOW_CENTER_Y - (int) (displaySize / 2);
            graphics.setColor(1.0F, 1.0F, 1.0F, 1.0F - progress);
            filledArrows[judgment.lane].renderScaled(graphics, x, y, scale);
            graphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
        }
    }

    private static boolean isHitJudgment(int judgment) {
        return judgment == RhythmGameState.JUDGMENT_PERFECT
                || judgment == RhythmGameState.JUDGMENT_GREAT
                || judgment == RhythmGameState.JUDGMENT_GOOD;
    }

    private static int tierOf(int offset) {
        int distance = Math.abs(offset);
        if (distance <= RhythmGameState.PERFECT_WINDOW_TICKS) {
            return RhythmGameState.JUDGMENT_PERFECT;
        }
        if (distance <= RhythmGameState.GREAT_WINDOW_TICKS) {
            return RhythmGameState.JUDGMENT_GREAT;
        }
        return RhythmGameState.JUDGMENT_GOOD;
    }

    private static int laneFor(Button button) {
        return switch (button) {
            case LEFT -> 0;
            case UP -> 1;
            case DOWN -> 2;
            case RIGHT -> 3;
            default -> -1;
        };
    }

    private void renderKeyHints(GuiGraphics graphics, int posX, int posY) {
        Font font = Minecraft.getInstance().font;
        KeyMapping[] mappings = {
                PoKeyBoardInput.ARCADE_LEFT,
                PoKeyBoardInput.ARCADE_UP,
                PoKeyBoardInput.ARCADE_DOWN,
                PoKeyBoardInput.ARCADE_RIGHT
        };
        for (int lane = 0; lane < RhythmGameState.LANES; lane++) {
            Component key = PoKeyBoardInput.getLocalizedKeyMessage(mappings[lane]);
            graphics.drawString(font, key,
                    posX + laneCenterX(lane) - font.width(key) / 2,
                    posY + 4, 0xAAAAAA, false);
        }
    }

    private void renderPanels(GuiGraphics graphics, int posX, int posY) {
        Font font = Minecraft.getInstance().font;
        int leftX = posX + 4;
        int rightX = posX + WIDTH - 4;

        // 左侧：节奏积分（仅本局临时，开局/重置清零）
        graphics.drawString(font, Component.translatable("gui.gamingconsole.rhythm.rhythm_score"), leftX, posY + 4, 0x555555, false);
        graphics.drawString(font, String.valueOf(state.getRhythmPoints()), leftX, posY + 14, 0xFFD700, false);

        // 左侧：奖励积分（每 20 节奏积分 = 1，结算累加不清零）
        graphics.drawString(font, Component.translatable("gui.gamingconsole.rhythm.reward"), leftX, posY + 30, 0x555555, false);
        graphics.drawString(font, String.valueOf(getScore()), leftX, posY + 40, 0xFFAA00, false);

        // 左侧：生命（红心/灰心）
        graphics.drawString(font, Component.translatable("gui.gamingconsole.rhythm.lives"), leftX, posY + 56, 0x555555, false);
        for (int i = 0; i < RhythmGameState.MAX_HEARTS; i++) {
            Image heart = i < state.getHearts() ? heartRed : heartGray;
            heart.renderScaled(graphics, leftX + i * 11, posY + 66, 1.25F);
        }

        // 左侧：判定文字（生命值下方，固定位置放大出现，无透明度淡化）
        if (getStage() == GameStage.PLAYING) {
            int panelCenterX = posX + 28;
            int judgmentY = posY + 80;
            for (ClientJudgment judgment : displayJudgments) {
                if (judgmentY > posY + HEIGHT - 16) {
                    break;
                }
                Component text = switch (judgment.type) {
                    case RhythmGameState.JUDGMENT_PERFECT -> Component.translatable("gui.gamingconsole.rhythm.perfect");
                    case RhythmGameState.JUDGMENT_GREAT -> Component.translatable("gui.gamingconsole.rhythm.great");
                    case RhythmGameState.JUDGMENT_GOOD -> Component.translatable("gui.gamingconsole.rhythm.good");
                    default -> Component.translatable("gui.gamingconsole.rhythm.miss");
                };
                int baseColor = switch (judgment.type) {
                    case RhythmGameState.JUDGMENT_PERFECT -> 0xFFD700;
                    case RhythmGameState.JUDGMENT_GREAT -> 0x55FF55;
                    case RhythmGameState.JUDGMENT_GOOD -> 0x55AAFF;
                    default -> 0xFF5555;
                };
                // 放大出现：0.6x 起步，前 2/3 生命周期放大到 1.0x，随后直接消失
                float progress = (float) Math.min(judgment.shownAge / JUDGMENT_TEXT_TICKS, 1.0);
                float scale = JUDGMENT_TEXT_MIN_SCALE
                        + (1.0F - JUDGMENT_TEXT_MIN_SCALE) * Math.min(progress * 1.5F, 1.0F);
                graphics.pose().pushPose();
                graphics.pose().translate(panelCenterX - font.width(text) * scale / 2.0F, judgmentY, 0.0F);
                graphics.pose().scale(scale, scale, 1.0F);
                graphics.drawString(font, text, 0, 0, baseColor | 0xFF000000, false);
                graphics.pose().popPose();
                judgmentY += font.lineHeight + 2;
            }
        }

        // 左侧：歌曲名
        graphics.drawString(font, Component.translatable(songTitleKey()), leftX, posY + HEIGHT - 12, 0x555555, false);

        // 右侧：连击
        Component comboLabel = Component.translatable("gui.gamingconsole.rhythm.combo");
        drawRight(graphics, font, comboLabel, rightX, posY + 6, 0x555555);
        drawRight(graphics, font, Component.literal(String.valueOf(state.getCombo())), rightX, posY + 16, 0xFFAA00);

        // 右侧：最大连击
        Component maxComboLabel = Component.translatable("gui.gamingconsole.rhythm.max_combo");
        drawRight(graphics, font, maxComboLabel, rightX, posY + 36, 0x555555);
        drawRight(graphics, font, Component.literal(String.valueOf(state.getMaxCombo())), rightX, posY + 46, 0x87CEEB);
    }

    private void drawRight(GuiGraphics graphics, Font font, Component text, int rightX, int y, int color) {
        graphics.drawString(font, text, rightX - font.width(text), y, color, false);
    }

    private String songTitleKey() {
        RhythmSong song = state.getSong();
        if (song == null) {
            song = RhythmSongs.ALL.get(0);
        }
        return "gamedisks.rhythm.song." + song.id();
    }

    private static int laneCenterX(int lane) {
        return FRAME_X + LANE_WIDTH / 2 + lane * LANE_WIDTH;
    }

    @Override
    public ResourceLocation getBackground() {
        return PoopSky.loc("textures/games/background/rhythm_background.png");
    }

    @Override
    public boolean showScore() {
        return false;
    }

    @Override
    public boolean requiresPerFrameRender() {
        return true;
    }

    /** 一次本地判定展示记录（判定类型 / 轨道 / 发生 tick）。 */
    private static final class ClientJudgment {
        final int type;
        final int lane;
        final int tick;
        /** 展示年龄（tick）：逐帧平滑推进，避免时钟跳变导致渲染抽搐。 */
        float shownAge;

        private ClientJudgment(int type, int lane, int tick) {
            this.type = type;
            this.lane = lane;
            this.tick = tick;
        }
    }
}
