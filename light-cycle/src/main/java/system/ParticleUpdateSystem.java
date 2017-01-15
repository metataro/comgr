package system;

import component.particle.ParticleSystem;

public class ParticleUpdateSystem extends System {

    @Override
    protected void processSystem(float deltaTime) {
        scene.getComponentManager().getComponents(ParticleSystem.class).forEach(p -> {
            p.update(deltaTime);
        });
    }
}
