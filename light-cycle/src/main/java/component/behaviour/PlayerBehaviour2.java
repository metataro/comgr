package component.behaviour;

import inputdevice.Input;
import inputdevice.Input.Buttons;
import inputdevice.InputDeviceLocator;
import org.lwjgl.glfw.GLFW;

import java.util.HashSet;

public class PlayerBehaviour2 extends Behaviour {

    private final HashSet<String> buttonsCurrentlyPressed = new HashSet<>();

    @Override
    public void update(float deltaTime) {
        this.handleControls(deltaTime);
    }

    /**
     * @param deltaTime Time passed.
     * @return Get current velocity of the player depending on deltaTime.
     */
    private float getVelocity(final float deltaTime) {
        float velocity = deltaTime * 20;
        if (InputDeviceLocator.getKeyboard().isKeyDown(GLFW.GLFW_KEY_LEFT_ALT)) {
            velocity *= 5;
        }
        return velocity;
    }

    /**
     * Handle Input that controls the player.
     *
     * @param deltaTime Time passed.
     */
    private void handleControls(final float deltaTime) {
        this.handleSteering(getVelocity(deltaTime));
    }

    // TODO move somehow to Input class if we keep this kind of steering behaviour
    private boolean isButtonNewlyPressed(final boolean down, final String button) {
        if (down) {
            if (this.buttonsCurrentlyPressed.contains(button)) {
                // button was already pressed last update
                return false;
            } else {
                // button is first time pressed this update
                this.buttonsCurrentlyPressed.add(button);
                return true;
            }
        } else {
            // button is not pressed
            this.buttonsCurrentlyPressed.remove(button);
            return false;
        }
    }

    /**
     * Handle Input that controls the player's vehicle.
     *
     * @param velocity Current velocity.
     */
    private void handleSteering(final float velocity) {

        //if (Input.getButton(Buttons.FORWARD)) {
            getGameObject().transform.translateForward(velocity);
        //}

        //if (Input.getButton(Buttons.BACKWARD)) {
        //    getGameObject().transform.translateBackward(velocity);
        //}

        if (this.isButtonNewlyPressed(InputDeviceLocator.getKeyboard().isKeyDown(GLFW.GLFW_KEY_A), "Left")) {
            getGameObject().transform.rotateLeft(90);
        }

        if (this.isButtonNewlyPressed(InputDeviceLocator.getKeyboard().isKeyDown(GLFW.GLFW_KEY_D), "Right")) {
            getGameObject().transform.rotateRight(90);
        }

        // if (Input.getButton("Speed")) gameObject.transform.translate(0, velocity, 0);
        // if (Input.getButton("Speed")) gameObject.transform.translate(0, -velocity, 0);

    }

}
