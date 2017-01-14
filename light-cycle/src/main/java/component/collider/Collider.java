package component.collider;

import component.Component;
import component.behaviour.Behaviour;
import gameobject.GameObject;

public abstract class Collider extends Component {

    private boolean trigger;

    public boolean isTrigger() {
        return trigger;
    }

    public void setTrigger(boolean trigger) {
        this.trigger = trigger;
    }

    @Override
    public final String typeName() {
        return Collider.class.getSimpleName();
    }
}
