package com.altnoir.poopsky.client.games.controls;

import com.altnoir.poopsky.client.games.util.Game;
public class Controls {
    // Stores state of all buttons
    private boolean up;
    private boolean down;
    private boolean left;
    private boolean right;
    private boolean button1;
    private boolean button2;
    private boolean was_up;
    private boolean was_down;
    private boolean was_left;
    private boolean was_right;
    private boolean was_b1;
    private boolean was_b2;

    // Stores the game
    private final Game game;
    public Controls(Game game) {
        this.game = game;
    }

    /**
     * Sets button's state, and calls key change event in the game
     * @param button Button that is affected
     * @param isDown True means pressed, false means not pressed
     */
    public void setButton(Button button, boolean isDown) {
        // Determines if the event should be called (if the button was already pressed, the event will not be called)
        boolean callEvent = false;
        switch (button) {
            // If the button is [button], changes the [button]'s state to [isDown],
            // and sets callEvent to true if the button wasn't in the state before
            case UP -> {
                if (!up && isDown) {
                    callEvent = true;
                    was_up = true;
                }
                up = isDown;
            }
            case DOWN -> {
                if (!down && isDown) {
                    callEvent = true;
                    was_down = true;
                }
                down = isDown;
            }
            case LEFT -> {
                if (!left && isDown) {
                    callEvent = true;
                    was_left = true;
                }
                left = isDown;
            }
            case RIGHT -> {
                if (!right && isDown) {
                    callEvent = true;
                    was_right = true;
                }
                right = isDown;
            }
            case BUTTON1 -> {
                if (!button1 && isDown) {
                    callEvent = true;
                    was_b1 = true;
                }
                button1 = isDown;
            }
            case BUTTON2 -> {
                if (!button2 && isDown) {
                    callEvent = true;
                    was_b2 = true;
                }
                button2 = isDown;
            }
        }
        // Calls the key changing event in game, if callEvent is true (calls buttonDown(), or buttonUp(), based on the isDown)
        if (isDown) {
            if (callEvent) {
                game.buttonDown(button);
            }
        }
        else {
            game.buttonUp(button);
        }
    }

    /**
     * @param button Button to check for
     * @return true if the button is down, false otherwise
     */
    public boolean isButtonDown(Button button) {
        return switch (button) {
            case UP -> up;
            case DOWN -> down;
            case LEFT -> left;
            case RIGHT -> right;
            case BUTTON1 -> button1;
            case BUTTON2 -> button2;
        };
    }

    public boolean wasButtonDown(Button button) {
        boolean toReturn = false;
        switch (button) {
            case UP -> {
                toReturn = was_up;
                was_up = false;
            }
            case DOWN -> {
                toReturn = was_down;
                was_down = false;
            }
            case LEFT -> {
                toReturn = was_left;
                was_left = false;
            }
            case RIGHT -> {
                toReturn = was_right;
                was_right = false;
            }
            case BUTTON1 -> {
                toReturn = was_b1;
                was_b1 = false;
            }
            case BUTTON2 -> {
                toReturn = was_b2;
                was_b2 = false;
            }
        }
        return !toReturn;
    }
}
