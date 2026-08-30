package com.altnoir.poopsky.game.rhythm;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * 数据驱动的节奏游戏歌曲（关卡）。BPM 自定义，每个音符带音符盒音高、音色（可多个叠加）与方向箭头；
 * 玩家按对方向箭头命中时，服务端用该音符的音符盒音色+音高播放（类似 POINT 的触发时机）。
 * 无外部音频，判定与声音同源。
 * <p>
 * 链式写法示例（游标式，最小时间单位 = 最小拍 stepsPerBeat，默认每拍 2 格 = 八分音符）：
 * <pre>
 * RhythmSong.builder("light_dance", 128)
 *         .note(12)                 // 音高 12（音符盒 0-24，12 = 中央），默认竖琴 + 左箭头
 *         .bass().down()            // 可选修饰：换音色/方向，顺序随意
 *         .delay(2)                 // 前进 2 格（一拍）
 *         .note(7).banjo().up()     // 音高 7 + 班卓琴 + 上箭头
 *         .delay(1)
 *         .note(19).chime().right()
 *         .build();
 * </pre>
 * 规则：
 * <ul>
 *   <li>方向作用于整条链（同链所有音符一起变向，后写覆盖）：note(A).basedrum().note(B).bass().right() → 两个都右</li>
 *   <li>音色在单个音符上叠加：note(A).basedrum().bass() → 命中时同时响鼓与贝斯</li>
 *   <li>同一条链中未修饰的前置 note() 会被新音符取代（一条链只保留最后一个音符）</li>
 *   <li>和弦 = 同链多个带修饰的 note()，或多条语句同拍</li>
 * </ul>
 * 轨道：0=左 1=上 2=下 3=右。
 */
public final class RhythmSong {
    /** 每分钟 tick 数（20 TPS × 60s）。 */
    private static final double TICKS_PER_MINUTE = 1200.0;
    private static final SoundEvent DEFAULT_INSTRUMENT = SoundEvents.NOTE_BLOCK_HARP.value();

    private final String id;
    private final int bpm;
    private final List<Note> notes;

    private RhythmSong(String id, int bpm, List<Note> notes) {
        this.id = id;
        this.bpm = bpm;
        this.notes = notes;
    }

    public String id() {
        return id;
    }

    public int bpm() {
        return bpm;
    }

    public List<Note> notes() {
        return notes;
    }

    public static Builder builder(String id, int bpm) {
        return new Builder(id, bpm);
    }

    public record Note(int lane, int hitTick, List<SoundEvent> instruments, int pitch) {
    }

    public static final class Builder {
        private final String id;
        private final int bpm;
        private final List<PendingNote> pending = new ArrayList<>();
        /** 当前链：本次 Builder.note() 起创建的音符，方向方法作用于整条链。 */
        private final List<PendingNote> chain = new ArrayList<>();
        private int stepsPerBeat = 2;
        private int cursor;

        private Builder(String id, int bpm) {
            this.id = id;
            this.bpm = bpm;
        }

        /** 设置每拍的细分数（最小拍单位）。默认 2（八分音符）；BPM 128 下 4 格/拍约 2.3 tick/格，过密不建议。 */
        public Builder stepsPerBeat(int stepsPerBeat) {
            if (stepsPerBeat < 1) {
                throw new IllegalArgumentException("stepsPerBeat must be >= 1");
            }
            this.stepsPerBeat = stepsPerBeat;
            return this;
        }

        /** 在当前游标放置一个音符并开始一条新链（默认竖琴音色 + 左箭头），返回可继续修饰的链。
         *  音高超出 0-24 会自动钳制到最近有效值（音符盒音频上限 2.0 倍音高，超出也无法更高）。 */
        public NoteBuilder note(int pitch) {
            PendingNote pendingNote = new PendingNote(tickAtCursor(), Math.clamp(pitch, 0, 24));
            adopt(pendingNote);
            chain.clear();
            chain.add(pendingNote);
            return new NoteBuilder(this, pendingNote);
        }

        private void adopt(PendingNote pendingNote) {
            if (!pending.isEmpty() && !pending.get(pending.size() - 1).modified) {
                PendingNote removed = pending.remove(pending.size() - 1);
                chain.remove(removed);
            }
            pending.add(pendingNote);
        }

        /** 前进 n 个最小拍单位（如 delay(1) = 一个八分音符时值）。 */
        public Builder delay(int steps) {
            if (steps < 0) {
                throw new IllegalArgumentException("delay must be >= 0");
            }
            cursor += steps;
            return this;
        }

        private int tickAtCursor() {
            double beat = (double) cursor / stepsPerBeat;
            return (int) Math.round(beat * TICKS_PER_MINUTE / bpm);
        }

        public RhythmSong build() {
            List<Note> result = pending.stream()
                    .map(p -> new Note(
                            p.lane,
                            p.hitTick,
                            p.instruments.isEmpty() ? List.of(DEFAULT_INSTRUMENT) : List.copyOf(p.instruments),
                            p.pitch))
                    .sorted(Comparator.comparingInt(Note::hitTick))
                    .toList();
            return new RhythmSong(id, bpm, List.copyOf(result));
        }
    }

    /** 音符修饰链：方向与音色均为可选，默认左箭头 + 竖琴。修饰方法返回本链，可继续修饰或接 note/delay。 */
    public static final class NoteBuilder {
        private final Builder builder;
        private final PendingNote note;

        private NoteBuilder(Builder builder, PendingNote note) {
            this.builder = builder;
            this.note = note;
        }

        /** 在当前游标再放置一个音符（同链和弦，延续当前链；未修饰的前置音符会被取代）。 */
        public NoteBuilder note(int pitch) {
            PendingNote pendingNote = new PendingNote(builder.tickAtCursor(), Math.clamp(pitch, 0, 24));
            builder.adopt(pendingNote);
            builder.chain.add(pendingNote);
            return new NoteBuilder(builder, pendingNote);
        }

        /** 前进 n 个最小拍单位，返回 Builder 以便继续编写后续音符。 */
        public Builder delay(int steps) {
            return builder.delay(steps);
        }

        public NoteBuilder left() {
            return setDirection(0);
        }

        public NoteBuilder up() {
            return setDirection(1);
        }

        public NoteBuilder down() {
            return setDirection(2);
        }

        public NoteBuilder right() {
            return setDirection(3);
        }

        /** 方向作用于整条链（同链所有音符一起变向），后写覆盖。 */
        private NoteBuilder setDirection(int lane) {
            for (PendingNote p : builder.chain) {
                p.lane = lane;
                p.modified = true;
            }
            return this;
        }

        private NoteBuilder addInstrument(SoundEvent instrument) {
            note.instruments.add(instrument);
            note.modified = true;
            return this;
        }

        public NoteBuilder harp() {
            return addInstrument(SoundEvents.NOTE_BLOCK_HARP.value());
        }

        public NoteBuilder bass() {
            return addInstrument(SoundEvents.NOTE_BLOCK_BASS.value());
        }

        public NoteBuilder banjo() {
            return addInstrument(SoundEvents.NOTE_BLOCK_BANJO.value());
        }

        public NoteBuilder bell() {
            return addInstrument(SoundEvents.NOTE_BLOCK_BELL.value());
        }

        public NoteBuilder chime() {
            return addInstrument(SoundEvents.NOTE_BLOCK_CHIME.value());
        }

        public NoteBuilder flute() {
            return addInstrument(SoundEvents.NOTE_BLOCK_FLUTE.value());
        }

        public NoteBuilder guitar() {
            return addInstrument(SoundEvents.NOTE_BLOCK_GUITAR.value());
        }

        public NoteBuilder bit() {
            return addInstrument(SoundEvents.NOTE_BLOCK_BIT.value());
        }

        public NoteBuilder cowBell() {
            return addInstrument(SoundEvents.NOTE_BLOCK_COW_BELL.value());
        }

        public NoteBuilder didgeridoo() {
            return addInstrument(SoundEvents.NOTE_BLOCK_DIDGERIDOO.value());
        }

        public NoteBuilder xylophone() {
            return addInstrument(SoundEvents.NOTE_BLOCK_XYLOPHONE.value());
        }

        public NoteBuilder ironXylophone() {
            return addInstrument(SoundEvents.NOTE_BLOCK_IRON_XYLOPHONE.value());
        }

        public NoteBuilder basedrum() {
            return addInstrument(SoundEvents.NOTE_BLOCK_BASEDRUM.value());
        }

        public NoteBuilder snare() {
            return addInstrument(SoundEvents.NOTE_BLOCK_SNARE.value());
        }

        public NoteBuilder hat() {
            return addInstrument(SoundEvents.NOTE_BLOCK_HAT.value());
        }

        public NoteBuilder pling() {
            return addInstrument(SoundEvents.NOTE_BLOCK_PLING.value());
        }
    }

    private static final class PendingNote {
        int lane = 0;
        /** 是否已有显式修饰（方向/音色）；未修饰的前置音符会被后续 note() 取代。 */
        boolean modified = false;
        final int hitTick;
        final int pitch;
        final List<SoundEvent> instruments = new ArrayList<>();

        private PendingNote(int hitTick, int pitch) {
            this.hitTick = hitTick;
            this.pitch = pitch;
        }
    }
}
