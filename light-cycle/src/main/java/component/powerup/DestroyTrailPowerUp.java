package component.powerup;

import component.behaviour.Behaviour;
import component.behaviour.PlayerBehaviour;
import gameobject.GameObject;

public class DestroyTrailPowerUp extends PowerUp {


    @Override
    public void apply(GameObject gameObject) {
        gameObject.getComponent(Behaviour.class).ifPresent(b -> {
            if (b instanceof PlayerBehaviour)
                ((PlayerBehaviour)b).destroyTrail();
        });
    }
}
