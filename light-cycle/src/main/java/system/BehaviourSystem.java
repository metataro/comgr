package system;

import component.behaviour.Behaviour;
import event.Event;

import java.util.Optional;

public class BehaviourSystem extends System {

    @Override
    public void process(float deltaTime) {
        for(Behaviour behaviour : scene.getComponentManager().getComponents(Behaviour.class)) {
            behaviour.update(deltaTime);
        }
    }

    @Override
    public void receive(Event event) {
        // on init: behaviours.init()
        if (event instanceof Event.CollisionEvent) {
            Event.CollisionEvent collisionEvent = (Event.CollisionEvent) event;
            Optional<Behaviour> b1 = collisionEvent.collider1.getGameObject().getComponent(Behaviour.class);
            Optional<Behaviour> b2 = collisionEvent.collider2.getGameObject().getComponent(Behaviour.class);
            if (b1.isPresent()) {
               b1.get().onCollision(collisionEvent);
            }
            if (b2.isPresent()) {
                b2.get().onCollision(collisionEvent);
            }
        }
    }
}
