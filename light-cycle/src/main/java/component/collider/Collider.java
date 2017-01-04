package component.collider;

import component.Component;
import component.behaviour.Behaviour;
import gameobject.GameObject;

public abstract class Collider extends Component {

    @Override
    public final String typeName() {
        return Collider.class.getSimpleName();
    }
}
