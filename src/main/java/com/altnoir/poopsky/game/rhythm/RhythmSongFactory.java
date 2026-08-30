package com.altnoir.poopsky.game.rhythm;

import java.util.Random;

/**
 * 歌曲选择工厂（镜像 BossFactory）。每局随机从内置歌曲中选一首；
 * 后续扩展多曲关卡（一局连打）时在此按权重/顺序调整。
 */
public final class RhythmSongFactory {
    private RhythmSongFactory() {
    }

    public static int pickIndex(Random random) {
        return random.nextInt(RhythmSongs.ALL.size());
    }

    public static RhythmSong pick(Random random) {
        return RhythmSongs.ALL.get(pickIndex(random));
    }
}
