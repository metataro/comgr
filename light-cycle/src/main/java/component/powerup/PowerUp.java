package component.powerup;

import component.Component;
import component.behaviour.PlayerBehaviour;
import gameobject.GameObject;

public abstract class PowerUp extends Component {

    public abstract void apply(GameObject gameObject);

    @Override
    public final String typeName() {
        return PowerUp.class.getSimpleName();
    }
}
