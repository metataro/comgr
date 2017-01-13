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

        public static final String P1_SPEED = "p1_speed";
        public static final String P1_LEFT =  "p1_left";
        public static final String P1_RIGHT = "p1_right";
        public static final String P1_LOOK_LEFT = "p1_look_left";
        public static final String P1_LOOK_RIGHT = "p1_look_right";

        public static final String P2_SPEED = "p2_speed";
        public static final String P2_LEFT =  "p2_left";
        public static final String P2_RIGHT = "p2_right";
        public static final String P2_LOOK_LEFT = "p2_look_left";
        public static final String P2_LOOK_RIGHT = "p2_look_right";

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
