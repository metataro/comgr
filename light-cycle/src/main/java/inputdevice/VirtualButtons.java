package inputdevice;

/**
 * Represents Virtual Button Input.
 * @author Benjamin Meyer
 */
public interface VirtualButtons {

    /**
     * @param buttonName Name of the virtual button to check.
     * @return True, if virtual button is pressed.
     */
    boolean isButtonDown(String buttonName);

}
