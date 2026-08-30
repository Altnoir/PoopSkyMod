package com.altnoir.poopsky.game.rhythm;

import java.util.List;

/**
 * 内置歌曲常量表。谱面即旋律（音符盒音色），当前为占位节奏型，
 * 请按实际编曲手写替换 lightDance() 内的 note(...) 序列。
 */
public final class RhythmSongs {
    /**
     * ライトダンス：BPM 128。
     */
    public static final RhythmSong LIGHT_DANCE = lightDance();

    public static final List<RhythmSong> ALL = List.of(LIGHT_DANCE);

    private RhythmSongs() {
    }

    private static RhythmSong lightDance() {
        RhythmSong.Builder b = RhythmSong.builder("light_dance", 138);

        // 写法示例：
        //   b.note(12).harp().up().delay(2);    // 音高 12 + 竖琴 + 上箭头，一拍
        //   b.note(7).bass().down().delay(1);   // 音高 7 + 贝斯 + 下箭头，八分音符
        //   b.delay(100);
        b.delay(10);
        int[] melody = {6, 6, 6, 10, 10, 10, 18, 18, 18};
        for (int i = 0; i < 48; i++) {
            if (i % 2 == 0) {
                b.note(melody[i % melody.length] - 7).snare().up();
            }
            b.note(melody[i % melody.length]).basedrum().bass().right();
            b.delay(2);
        }

        return b.build();
    }
}
