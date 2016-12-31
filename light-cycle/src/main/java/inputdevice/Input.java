package inputdevice;

/**
 * "Adapter" for Input stuff.
 *
 * @author Benjamin Meyer
 */
public final class Input {

    /**
     * Global button constants.
     */
    public static final class Buttons {

        public static final String SPEED = "Speed";
        public static final String FORWARD = "Forward";
        public static final String BACKWARD = "Backward";
        public static final String LEFT = "Left";
        public static final String RIGHT = "Right";

        /** Make static class. */
        private Buttons() { }

    }

    /**
     * @param buttonName Name of the button which to check for.
     * @return True if button is pressed.
     */
    public static boolean getButton(String buttonName) {
        return InputDeviceLocator.getVirtualButtons().isButtonDown(buttonName);
    }

    /** Make static class. */
    private Input() { }

}
