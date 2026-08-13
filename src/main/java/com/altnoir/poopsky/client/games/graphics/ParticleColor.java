package com.altnoir.poopsky.client.games.graphics;

import java.util.Random;

public enum ParticleColor {
    WHITE,
    ORANGE,
    MAGENTA,
    LIGHT_BLUE,
    YELLOW,
    LIME,
    PINK,
    GRAY,
    LIGHT_GRAY,
    CYAN,
    PURPLE,
    BLUE,
    BROWN,
    GREEN,
    RED,
    BLACK;

    public static ParticleColor random(Random random) {
        return switch (random.nextInt(0, 16)) {
            case 1 -> ORANGE;
            case 2 -> MAGENTA;
            case 3 -> LIGHT_BLUE;
            case 4 -> YELLOW;
            case 5 -> LIME;
            case 6 -> PINK;
            case 7 -> GRAY;
            case 8 -> LIGHT_GRAY;
            case 9 -> CYAN;
            case 10 -> PURPLE;
            case 11 -> BLUE;
            case 12 -> BROWN;
            case 13 -> GREEN;
            case 14 -> RED;
            case 15 -> BLACK;
            default -> WHITE;
        };
    }

    public int value() {
        return switch (this) {
            case WHITE -> 0;
            case ORANGE -> 1;
            case MAGENTA -> 2;
            case LIGHT_BLUE -> 3;
            case YELLOW -> 4;
            case LIME -> 5;
            case PINK -> 6;
            case GRAY -> 7;
            case LIGHT_GRAY -> 8;
            case CYAN -> 9;
            case PURPLE -> 10;
            case BLUE -> 11;
            case BROWN -> 12;
            case GREEN -> 13;
            case RED -> 14;
            case BLACK -> 15;
        };
    }
}
