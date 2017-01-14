package component.behaviour;

import audio.AudioBuffer;
import audio.AudioMaster;
import ch.fhnw.util.math.Mat4;
import component.Transform;
import component.audio.AudioSourceComponent;
import event.Event;
import gameobject.GameObject;
import main.LightCycle;

import java.util.Optional;
import java.util.Random;

public class PowerUpBehaviour extends Behaviour {

    private AudioBuffer powerupsound;
    


    @Override
    public void init() {
        powerupsound = AudioMaster.createAudioBufferFromWAV(LightCycle.class.getResource("/fanfare.wav"));
        AudioSourceComponent audio = getGameObject().addComponent(AudioSourceComponent.class);
        audio.setAudioSource(getGameObject().getScene().getAudioController().createAudioSources());
        
    }

    @Override
    public void onCollision(GameObject other) {
    	
	        Optional<AudioSourceComponent> audio = getGameObject().getComponent(AudioSourceComponent.class);
	        Optional<Behaviour> otherBehaviour = other.getComponent(Behaviour.class);
	        
	        if (audio.isPresent() && otherBehaviour.isPresent() && otherBehaviour.get() instanceof LightCycleBehaviour) {
	        	audio.get().setLooping(false);
	        	Transform t = audio.get().getTransform();
	        	AudioSourceComponent audioloc = new AudioSourceComponent();
	        	audioloc.setAudioSource(getGameObject().getScene().getAudioController().createAudioSources());
	        	audioloc.setPosition(t.getLocalPosition());
	        	audioloc.play(powerupsound);
	        }
	        movePowerUp(getGameObject());
    }

	public void movePowerUp(GameObject go) {
		Random r = new Random(); 
		int rx = r.nextInt(2000)-1000;
		int ry = r.nextInt(2000)-1000;
		go.getTransform().setLocal(Mat4.translate(rx, 0, ry));
	}

}
