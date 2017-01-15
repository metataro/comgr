package component.particle;

import ch.fhnw.ether.scene.mesh.IMesh;
import ch.fhnw.util.math.Mat4;
import ch.fhnw.util.math.Vec3;

public class Particle {

    private static final float g = 9.81f;

    private Mat4 transform;

    private Vec3 velocity;

    private Vec3 rotation;

    private IMesh mesh;

    public Particle(Mat4 transform, Vec3 velocity, Vec3 rotation, IMesh mesh) {
        this.transform = transform;
        this.velocity = velocity;
        this.rotation = rotation;
        this.mesh = mesh;
    }

    public IMesh getMesh() {
        return mesh;
    }

    public void update(float deltaTime) {
        if (transform.m13 > -0.5f) {
            velocity = velocity.add(new Vec3(0, deltaTime*deltaTime*-(g/2), 0));
            transform = Mat4.multiply(
                    Mat4.translate(velocity),
                    transform,
                    Mat4.rotate(90 * rotation.z * deltaTime, 1, 0, 0),
                    Mat4.rotate(90 * rotation.y * deltaTime, 0, 1, 0),
                    Mat4.rotate(90 * rotation.x * deltaTime, 0, 0, 1)
            );
            mesh.setTransform(transform);
        } else if (transform.m13 < -0.5f) {
            transform = transform.preMultiply(Mat4.translate(new Vec3(0, -0.5f - transform.m13, 0)));
            mesh.setTransform(transform);
        }
    }
}
