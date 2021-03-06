package component.behaviour;

import java.util.HashSet;

import inputdevice.Input;

/**
 * @autor benikm91
 */
public class LookAroundBehaviour extends Behaviour {

    private int degree = 90;

    private boolean turnedLeft;
    private boolean turnedRight;

    private String turnLeftButton;
    private String turnRightButton;

    public void setButtons(String left, String right) {
        this.turnLeftButton = left;
        this.turnRightButton = right;
    }

    @Override
    public void update(float deltaTime) {

        final boolean turnLeftRequest = Input.getButton(turnLeftButton);
        final boolean turnRightRequest= Input.getButton(turnRightButton);

        // turn left
        if (turnLeftRequest && !turnedLeft) {
            getGameObject().transform.rotateLeft(degree);
            turnedLeft = true;
        } else if (!turnLeftRequest && turnedLeft) {
            getGameObject().transform.rotateRight(degree);
            turnedLeft = false;
        }

        // turn right
        if (turnRightRequest && !turnedRight) {
            getGameObject().transform.rotateRight(degree);
            turnedRight = true;
        } else if (!turnRightRequest && turnedRight) {
            getGameObject().transform.rotateLeft(degree);
            turnedRight = false;
        }

    }

}
