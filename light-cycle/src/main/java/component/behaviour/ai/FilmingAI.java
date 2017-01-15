package component.behaviour.ai;

import org.jetbrains.annotations.Contract;

import java.util.Random;

import ch.fhnw.util.math.MathUtilities;
import ch.fhnw.util.math.Vec3;
import component.Transform;
import component.behaviour.Behaviour;

/**
 * @autor benikm91
 */
public class FilmingAI extends Behaviour {

    private Transform target;
    private Vec3 velocity = new Vec3(0, 0, 0);
    private boolean left = true;

    @Override
    public void update(float deltaTime) {

        getTransform().translate(velocity);

        Vec3 diffXZ =
                new Vec3(target.getLocalPosition().x, 0, target.getLocalPosition().z)
                .subtract
                (new Vec3(this.getTransform().getLocalPosition().x, 0, this.getTransform().getLocalPosition().z));

        Random r = new Random();
        int randomInt = r.nextInt(50);
        if (diffXZ.length() <= randomInt) {
            if (randomInt == 1) {
                left = !left;
            }
            Vec3 dir = this.getTransform().getLocalLeft();
            velocity = velocity.add(dir.normalize().scale(0.01f));
        } else {
            velocity = velocity.add(diffXZ.normalize().scale(0.01f));
        }

        velocity = velocity.scale(0.99f);

        double temp = diffXZ.normalize().dot(getTransform().getLocalForward());

        if (temp > 1) temp = 1;
        else if (temp < -1) temp = -1;
        float angle = (float) Math.acos(temp);

        double cross = getTransform().getLocalForward().cross(diffXZ.normalize()).y;
        getTransform().rotate(MathUtilities.RADIANS_TO_DEGREES * angle, 0, (cross > 0) ? 1 : -1, 0);

    }

    public void setTarget(final Transform target) {
        this.target = target;
    }


}
