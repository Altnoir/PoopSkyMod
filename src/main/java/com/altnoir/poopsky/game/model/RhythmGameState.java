package com.altnoir.poopsky.game.model;

import com.altnoir.poopsky.game.rhythm.RhythmSong;
import com.altnoir.poopsky.game.rhythm.RhythmSongFactory;
import com.altnoir.poopsky.game.rhythm.RhythmSongs;
import net.minecraft.nbt.CompoundTag;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 节奏游戏纯逻辑状态：歌曲时钟、音符推进、按键判定（重合窗口）、
 * 连击/计分/生命（红心），以及快照读写。服务端权威判定，客户端仅读取渲染。
 */
public final class RhythmGameState {
    public static final int LANES = 4;
    /** 三档命中窗口（tick）：PERFECT ≤ 2、GREAT ≤ 4、GOOD ≤ 6，均 +1 分，仅宽松度不同。 */
    public static final int PERFECT_WINDOW_TICKS = 2;
    public static final int GREAT_WINDOW_TICKS = 4;
    public static final int GOOD_WINDOW_TICKS = 6;
    /** 初始红心数，MISS 扣一颗，归零即失败。 */
    public static final int MAX_HEARTS = 3;

    public static final int JUDGMENT_NONE = 0;
    public static final int JUDGMENT_PERFECT = 1;
    public static final int JUDGMENT_GREAT = 2;
    public static final int JUDGMENT_GOOD = 3;
    public static final int JUDGMENT_MISS = 4;

    /** 各档命中获得的节奏积分：GOOD=1、GREAT=2、PERFECT=4。 */
    public static final int GOOD_POINTS = 1;
    public static final int GREAT_POINTS = 2;
    public static final int PERFECT_POINTS = 4;
    /** 每累计多少节奏积分换算为 1 奖励积分（奖励积分即街机奖励，结算时累加且不清零）。 */
    public static final int REWARD_POINTS_PER = 20;

    private RhythmSong song;
    private int songIndex;
    private List<RhythmSong.Note> notes = List.of();
    private boolean[] judged = new boolean[0];
    private int judgedCount;
    private int songTick;
    private int hits;
    private int misses;
    private int combo;
    private int maxCombo;
    private int hearts;
    /** 节奏积分：仅当前对局临时计算，开局/重置清零。 */
    private int rhythmPoints;

    public void prepare(Random random) {
        songIndex = RhythmSongFactory.pickIndex(random);
        song = RhythmSongs.ALL.get(songIndex);
        notes = song.notes();
        judged = new boolean[notes.size()];
        judgedCount = 0;
        songTick = 0;
        hits = 0;
        misses = 0;
        combo = 0;
        maxCombo = 0;
        hearts = MAX_HEARTS;
        rhythmPoints = 0;
    }

    /** 玩家按下某轨道按键。命中时返回本次命中的全部音符（同轨道同拍的和弦一并命中），未命中返回空列表。 */
    public List<RhythmSong.Note> pressLane(int lane) {
        if (lane < 0 || lane >= LANES || judgedCount >= notes.size()) {
            return List.of();
        }
        int best = -1;
        int bestOffset = Integer.MAX_VALUE;
        for (int i = 0; i < notes.size(); i++) {
            if (judged[i]) {
                continue;
            }
            RhythmSong.Note note = notes.get(i);
            if (note.lane() != lane) {
                continue;
            }
            int offset = note.hitTick() - songTick;
            if (Math.abs(offset) > GOOD_WINDOW_TICKS) {
                continue;
            }
            if (Math.abs(offset) < Math.abs(bestOffset)) {
                best = i;
                bestOffset = offset;
            }
        }
        if (best == -1) {
            return List.of();
        }
        // 同轨道同拍的其他音符（和弦）一并命中：一次按键同时判定多个音
        int chordTick = notes.get(best).hitTick();
        List<RhythmSong.Note> hitNotes = new ArrayList<>();
        for (int i = 0; i < notes.size(); i++) {
            if (judged[i]) {
                continue;
            }
            RhythmSong.Note note = notes.get(i);
            if (note.lane() != lane || note.hitTick() != chordTick) {
                continue;
            }
            judged[i] = true;
            judgedCount++;
            hits++;
            combo++;
            maxCombo = Math.max(maxCombo, combo);
            rhythmPoints += pointsOf(tierOf(note.hitTick() - songTick));
            hitNotes.add(note);
        }
        return hitNotes;
    }

    private static int tierOf(int offset) {
        int distance = Math.abs(offset);
        if (distance <= PERFECT_WINDOW_TICKS) {
            return JUDGMENT_PERFECT;
        }
        if (distance <= GREAT_WINDOW_TICKS) {
            return JUDGMENT_GREAT;
        }
        return JUDGMENT_GOOD;
    }

    private static int pointsOf(int judgment) {
        return switch (judgment) {
            case JUDGMENT_PERFECT -> PERFECT_POINTS;
            case JUDGMENT_GREAT -> GREAT_POINTS;
            default -> GOOD_POINTS;
        };
    }

    /** 每游戏 tick 推进时钟，并把越过判定窗口仍未被击中的音符记为 MISS。 */
    public void tick() {
        songTick++;
        for (int i = 0; i < notes.size(); i++) {
            if (judged[i]) {
                continue;
            }
            if (notes.get(i).hitTick() + GOOD_WINDOW_TICKS < songTick) {
                judged[i] = true;
                judgedCount++;
                misses++;
                combo = 0;
                hearts--;
            }
        }
    }

    public RhythmSong getSong() {
        return song;
    }

    public int getSongIndex() {
        return songIndex;
    }

    public List<RhythmSong.Note> getNotes() {
        return notes;
    }

    public int getSongTick() {
        return songTick;
    }

    public int getHits() {
        return hits;
    }

    public int getMisses() {
        return misses;
    }

    public int getCombo() {
        return combo;
    }

    public int getMaxCombo() {
        return maxCombo;
    }

    public int getHearts() {
        return hearts;
    }

    public int getRhythmPoints() {
        return rhythmPoints;
    }

    /** 奖励积分 = 节奏积分 / 20（向下取整），结算时由街机累加为奖励且不清零。 */
    public int getRewardPoints() {
        return rhythmPoints / REWARD_POINTS_PER;
    }

    public boolean isSongFinished() {
        return judgedCount >= notes.size();
    }

    public boolean isDead() {
        return hearts <= 0;
    }

    public void writeSnapshot(CompoundTag tag) {
        tag.putInt("songIndex", songIndex);
        tag.putInt("songTick", songTick);
        tag.putInt("hits", hits);
        tag.putInt("misses", misses);
        tag.putInt("combo", combo);
        tag.putInt("maxCombo", maxCombo);
        tag.putInt("hearts", hearts);
        tag.putInt("rhythmPoints", rhythmPoints);
    }

    public void applySnapshot(CompoundTag tag) {
        songIndex = tag.getInt("songIndex");
        song = RhythmSongs.ALL.get(songIndex);
        notes = song.notes();
        songTick = tag.getInt("songTick");
        hits = tag.getInt("hits");
        misses = tag.getInt("misses");
        combo = tag.getInt("combo");
        maxCombo = tag.getInt("maxCombo");
        hearts = tag.getInt("hearts");
        rhythmPoints = tag.getInt("rhythmPoints");
    }
}
