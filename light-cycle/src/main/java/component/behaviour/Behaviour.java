package component.behaviour;

import component.Component;
import event.Event;
import gameobject.GameObject;

public abstract class Behaviour extends Component {

    public void update(float deltaTime) {
    }

    public void onCollision(GameObject other) {
    }

    @Override
    public final String typeName() {
        return Behaviour.class.getSimpleName();
    }
}
