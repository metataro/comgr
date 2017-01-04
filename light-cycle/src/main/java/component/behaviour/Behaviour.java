package component.behaviour;

import component.Component;
import event.Event;

public abstract class Behaviour extends Component {

    public void update(float deltaTime) {
    }


    public void onCollision(Event.CollisionEvent collisionEvent) {
    }

    @Override
    public final String typeName() {
        return Behaviour.class.getSimpleName();
    }
}
