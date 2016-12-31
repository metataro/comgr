package inputdevice;

import ch.fhnw.ether.view.IWindow;

import java.awt.event.KeyEvent;

public class GLFWKeyboard implements Keyboard {

    private class KeyState {
        boolean down;
        int mods;
        int scancode;
        boolean repeat;
    }

    private static final int MAX_KEYCODE = 348;

    private final KeyState[] keyStates = new KeyState[MAX_KEYCODE + 1];

    public GLFWKeyboard() {
        for (int i = 0; i <= MAX_KEYCODE; i++) {
            keyStates[i] = new KeyState();
        }
    }

    @Override
    public synchronized boolean isAnyKeyDown() {
        for (int key = 0; key < MAX_KEYCODE; key++) {
            if (isKeyDown(key)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public synchronized boolean isKeyDown(int key) {
        if (key < 0 || key > MAX_KEYCODE) return false;
        return keyStates[key].down;
    }

    @Override
    public synchronized void keyPressed(IWindow window, int mods, int key, int scancode, boolean repeat) {
        if (key < 0 || key > MAX_KEYCODE) return;
        keyStates[key].down = true;
        keyStates[key].mods = mods;
        keyStates[key].scancode = scancode;
        keyStates[key].repeat = repeat;
    }

    @Override
    public synchronized void keyReleased(IWindow window, int mods, int key, int scancode) {
        if (key < 0 || key > MAX_KEYCODE) return;
        keyStates[key].down = false;
        keyStates[key].mods = mods;
        keyStates[key].scancode = scancode;
        keyStates[key].repeat = false;
    }
}
