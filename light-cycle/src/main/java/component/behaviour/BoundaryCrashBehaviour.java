package component.behaviour;

import ch.fhnw.util.color.RGBA;
import component.MeshGroup;
import component.particle.ParticleSystem;

public class BoundaryCrashBehaviour extends Behaviour {

    private float maxDistanceToCenter = 100f;
    private LightCycleBehaviour lightCycleBehaviour;

    public float getMaxDistanceToCenter() {
        return this.maxDistanceToCenter;
    }

    public void setMaxDistanceToCenter(float maxDistanceToCenter) {
        this.maxDistanceToCenter = maxDistanceToCenter;
    }

    @Override
    public void update(float deltaTime) {
        if(this.lightCycleBehaviour != null) {

            // Calculate distance, crash if too far
            if(this.getGameObject().getTransform().getPosition().length() > this.maxDistanceToCenter) {
                lightCycleBehaviour.onCollision(getGameObject());
                // Player is too far away, dies
                //this.playerBehaviour.setAlive(false);

                // Explosion
                //this.getGameObject().removeComponent(MeshGroup.class);
                //this.getGameObject().getComponent(ParticleSystem.class).ifPresent(p -> p.emmitParticles(RGBA.BLACK, 0.1f, 0.5f,100));
            }
        }
    }

    public void setPlayerBehaviour(LightCycleBehaviour lightCycleBehaviour) {
        // TODO: BAD hack, but it looks like only the most recent behaviour on each game object is accessible - WTF?
        this.lightCycleBehaviour = lightCycleBehaviour;
    }
}
