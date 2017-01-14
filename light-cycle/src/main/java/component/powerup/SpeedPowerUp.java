package component.powerup;

import component.behaviour.Behaviour;
import component.behaviour.PlayerBehaviour;
import gameobject.GameObject;

public class SpeedPowerUp extends PowerUp {

    private float boostTime;

    public float getBoostTime() {
        return boostTime;
    }

    public void setBoostTime(float boostTime) {
        this.boostTime = boostTime;
    }

    @Override
    public void apply(GameObject gameObject) {
        gameObject.getComponent(Behaviour.class).ifPresent(b -> {
            if (b instanceof PlayerBehaviour)
                ((PlayerBehaviour)b).addBoostTime(boostTime);
        });
    }
}
