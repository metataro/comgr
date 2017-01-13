package system;

import component.behaviour.Behaviour;
import event.Event;

import java.util.Optional;

public class BehaviourSystem extends System {

    @Override
    public void processSystem(float deltaTime) {
        for(Behaviour behaviour : scene.getComponentManager().getComponents(Behaviour.class)) {
            behaviour.update(deltaTime);
        }
        processAllPending(event -> {
            if (event instanceof Event.CollisionEvent) {
                Event.CollisionEvent collisionEvent = (Event.CollisionEvent) event;
                Optional<Behaviour> b1 = collisionEvent.collider1.getGameObject().getComponent(Behaviour.class);
                Optional<Behaviour> b2 = collisionEvent.collider2.getGameObject().getComponent(Behaviour.class);
                b1.ifPresent(behaviour -> behaviour.onCollision(collisionEvent.collider2.getGameObject()));
                b2.ifPresent(behaviour -> behaviour.onCollision(collisionEvent.collider1.getGameObject()));
            }
        });
    }

    @Override
    protected void processEvent(Event event) {

    }
}
