package inputdevice;

/**
 * Provides access to Keyboard, Mouse and Virtual Input.
 *
 * @author Christian Scheller
 * @author Benjamin Meyer
 */
public class InputDeviceLocator {

    private static Keyboard keyboardService;

    private static Mouse mouseService;

    private static VirtualButtons virtualInputService;

    /**
     * Return the provided Virtual Buttons service.
     * @return Virtual Buttons service.
     */
    public static VirtualButtons getVirtualButtons() {
        assert (virtualInputService != null);

        return virtualInputService;
    }

    /**
     * Return the provided Keyboard service.
     * @return The Keyboard service
     */
    public static Keyboard getKeyboard() {
        assert (keyboardService != null);

        return keyboardService;
    }

    /**
     * Return the provided Mouse service.
     * @return The Mouse service
     */
    public static Mouse getMouse() {
        assert (mouseService != null);

        return mouseService;
    }

    /**
     * Sets the provided Keyboard service.
     * @param service The Keyboard service
     */
    public static void provideKeyboard(Keyboard service) {
        InputDeviceLocator.keyboardService = service;
    }

    /**
     * Sets the provided Mouse service.
     * @param service The Mouse service
     */
    public static void provideMouse(Mouse service) {
        InputDeviceLocator.mouseService = service;
    }

    /**
     * Sets the provided Virtual Buttons service.
     * @param service The Virtual Buttons service
     */
    public static void provideVirtualButton(VirtualButtons service) {
        InputDeviceLocator.virtualInputService = service;
    }

}
