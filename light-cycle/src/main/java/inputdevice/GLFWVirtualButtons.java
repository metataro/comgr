package inputdevice;

import java.util.Map;

public class GLFWVirtualButtons implements VirtualButtons {

    private final Map<String, Integer> keyMapping;

    public GLFWVirtualButtons(Map<String, Integer> keyMapping) {
        this.keyMapping = keyMapping;
    }

    @Override
    public synchronized boolean isButtonDown(String buttonName) {
        assert(keyMapping.containsKey(buttonName));

        return InputDeviceLocator.getKeyboard().isKeyDown(keyMapping.get(buttonName));
    }

}
