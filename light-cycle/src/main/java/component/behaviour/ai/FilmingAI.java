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
            velocity = velocity.add(new Vec3(r.nextFloat() - 0.5f, 0, r.nextFloat() - 0.5f).normalize().scale(0.01f));
        } else {
            velocity = velocity.add(diffXZ.normalize().scale(0.01f));
        }

        if (velocity.length() > 0.8f) velocity = velocity.scale(0.5f);

        double temp = getTransform().getLocalForward().dot(diffXZ.normalize());

        if (temp > 1) temp = 1;
        else if (temp < -1) temp = -1;
        float angle = (float) Math.acos(temp);

        getTransform().rotate(MathUtilities.RADIANS_TO_DEGREES * angle, 0, 1, 0);

    }

    public void setTarget(final Transform target) {
        this.target = target;
    }


}
