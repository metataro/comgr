package component.behaviour;

import org.jetbrains.annotations.Contract;

import ch.fhnw.util.math.Vec3;
import component.Transform;

/**
 * @autor benikm91
 */
public class FollowBehaviour extends Behaviour {

    private Transform target;
    private float speedLerp = 0.25f;
    private float rotationLerp = 0.25f;

    @Contract(pure = true)
    // TODO MOVE THIS INTO A UTIL CLASS! BUT WHERE?
    private float normAngle(float angle) {
        if (angle > 180)
            return -360 + angle;
        else if (angle < -180)
            return 360 + angle;
        else
            return angle;
    }

    @Override
    public void update(float deltaTime) {
        Vec3 currentPosition = this.getTransform().getPosition();
        Vec3 targetPosition = this.target.getPosition();
        Vec3 wayToGo = targetPosition.subtract(currentPosition);
        Vec3 currentRotation = this.getTransform().getLocalEulerAngles();
        Vec3 targetRotation = this.target.getLocalEulerAngles();

        Vec3 wayToGoRotation = targetRotation.subtract(currentRotation);

        this.getTransform().translate(wayToGo.scale(speedLerp));
        this.getTransform().getLocalEulerAngles().subtract(this.target.getLocalEulerAngles());

        this.getTransform().rotateLeft(normAngle(wayToGoRotation.x) * rotationLerp);
    }

    public void setTarget(final Transform target) {
        this.target = target;
    }

    public void setSpeedLerp(float speedLerp) {
        this.speedLerp = speedLerp;
    }

    public void setRotationLerp(float rotationLerp) {
        this.rotationLerp = rotationLerp;
    }

}
