package component.powerup;

import component.behaviour.Behaviour;
import component.behaviour.PlayerBehaviour;
import gameobject.GameObject;

public class DestroyOtherTrailPowerUp extends PowerUp {


    @Override
    public void apply(GameObject gameObject) {
    	gameObject.getScene().getComponentManager().getComponents(Behaviour.class).forEach(b -> {
            if (b instanceof PlayerBehaviour){
            	if(!gameObject.equals(b.getGameObject())){
            		((PlayerBehaviour)b).destroyTrail();
            	}
            }
        });
    	
    }
}
