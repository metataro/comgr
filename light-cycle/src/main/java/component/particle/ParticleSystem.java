package component.particle;

import ch.fhnw.ether.render.shader.builtin.UnshadedTriangleShader;
import ch.fhnw.ether.scene.mesh.DefaultMesh;
import ch.fhnw.ether.scene.mesh.IMesh;
import ch.fhnw.ether.scene.mesh.MeshUtilities;
import ch.fhnw.ether.scene.mesh.geometry.DefaultGeometry;
import ch.fhnw.ether.scene.mesh.material.ColorMaterial;
import ch.fhnw.ether.scene.mesh.material.IMaterial;
import ch.fhnw.util.color.RGBA;
import ch.fhnw.util.math.Mat4;
import ch.fhnw.util.math.Vec3;
import component.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ParticleSystem extends Component {

    private final Random r = new Random();

    private ArrayList<Particle> particles = new ArrayList<>();

    public void emmitParticles(RGBA color, float minSize, float maxSize, int count) {
        for (int i = 0; i < count; i++) {
            particles.add(createParticle(color, minSize, maxSize));
        }
    }

    public void update(float deltaTime) {
        particles.forEach(p -> p.update(deltaTime));
    }

    private Particle createParticle(RGBA color, float minSize, float maxSize) {
        float size = minSize + r.nextFloat() * maxSize;
        float[] vertices = {
                0, 0, 0,
                size, 0, size,
                0, 0, size,
                0, 0, 0,
                0, 0, size,
                size, 0, size
        };
        IMesh particleMesh = new DefaultMesh(IMesh.Primitive.TRIANGLES, new ColorMaterial(color), DefaultGeometry.createV(vertices));
        getGameObject().getScene().getRenderManager().addMesh(particleMesh);
        Vec3 velocity = new Vec3(0.5f - r.nextFloat(), 1, 0.5f - r.nextFloat()).normalize().scale(r.nextFloat() * 0.25f);
        Vec3 rotation = new Vec3(r.nextFloat(), r.nextFloat(), r.nextFloat()).scale(r.nextFloat() * 10);
        Vec3 position = getTransform().getPosition();
        return new Particle(Mat4.translate(position.x, position.y < 0 ? 0 : position.y, position.z), velocity, rotation, particleMesh);
    }

    @Override
    public void destroy() {
        particles.forEach(p -> getGameObject().getScene().getRenderManager().removeMesh(p.getMesh()));
        particles.clear();
    }
}
