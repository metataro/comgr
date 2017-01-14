package system;

import component.behaviour.Behaviour;
import component.collider.Collider;
import event.Event;

import java.util.Optional;

public class BehaviourSystem extends System {

    @Override
    public void processSystem(float deltaTime) {
        for(Behaviour behaviour : scene.getComponentManager().getComponents(Behaviour.class)) {
            behaviour.update(deltaTime);
        }
    }

    @Override
    protected void processEvent(Event event) {
        if (event instanceof Event.CollisionEvent) {
            Event.CollisionEvent collisionEvent = (Event.CollisionEvent) event;
            Optional<Behaviour> b1 = collisionEvent.collider1.getGameObject().getComponent(Behaviour.class);
            Optional<Behaviour> b2 = collisionEvent.collider2.getGameObject().getComponent(Behaviour.class);
            b1.ifPresent(behaviour -> handleCollision(behaviour, collisionEvent.collider2));
            b2.ifPresent(behaviour -> handleCollision(behaviour, collisionEvent.collider1));
        }
    }

    private void handleCollision(Behaviour behaviour, Collider otherCollider) {
        if (otherCollider.isTrigger()) {
            java.lang.System.out.println("Collided with trigger!");
            behaviour.onTrigger(otherCollider.getGameObject());
        } else {
            behaviour.onCollision(otherCollider.getGameObject());
        }
    }
}
