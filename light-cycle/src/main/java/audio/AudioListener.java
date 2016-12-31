package audio;

import ch.fhnw.util.math.Mat4;
import ch.fhnw.util.math.Vec3;

import java.util.ArrayList;

public class AudioListener {

    private Mat4 transform = Mat4.ID;
    private Vec3 velocity = Vec3.ZERO;

    public Mat4 getTransform() {
        return transform;
    }

    public void setTransform(Mat4 transform) {
        this.transform = transform;
    }

    public Vec3 getVelocity() {
        return velocity;
    }

    public void setVelocity(Vec3 velocity) {
        this.velocity = velocity;
    }
}
