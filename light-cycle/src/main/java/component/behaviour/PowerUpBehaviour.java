package component.behaviour;

import audio.AudioBuffer;
import audio.AudioMaster;
import ch.fhnw.util.math.Mat4;
import ch.fhnw.util.math.Vec3;
import component.Transform;
import component.audio.AudioSourceComponent;
import event.Event;
import gameobject.GameObject;
import main.LightCycle;

import java.util.Optional;
import java.util.Random;

public class PowerUpBehaviour extends Behaviour {

    private AudioBuffer sound;

    private float minY = 0.15f;//0.15f;
    private float maxY = 0.5f;//0.45f;
	private float bumpSpeed = 2f;
	private float modifier = 0;

	public AudioBuffer getSound() {
		return sound;
	}

	public void setSound(AudioBuffer sound) {
		this.sound = sound;
	}

	@Override
    public void init() {
        //powerupsound = AudioMaster.createAudioBufferFromWAV(LightCycle.class.getResource("/fanfare.wav"));
        AudioSourceComponent audio = getGameObject().addComponent(AudioSourceComponent.class);
        audio.setAudioSource(getGameObject().getScene().getAudioController().createAudioSources());
    }

	@Override
	public void update(float deltaTime) {
    	Vec3 position = getTransform().getLocalPosition();
		float targetY = minY + Math.abs(maxY - minY) * (float)Math.sin(modifier) / 2;
		modifier += bumpSpeed*deltaTime;
		getTransform().translate(0, targetY - position.y, 0);
		int i = LightCycle.getIndexPowerUp(getGameObject());
		GameObject mirror = LightCycle.getMirrorPowerUp(i);
		mirror.getTransform().translate(0,-(targetY - position.y),0);
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
	        	audioloc.play(sound);
	        }
	        movePowerUp(getGameObject());
    }

	public void movePowerUp(GameObject go) {
		int i = LightCycle.getIndexPowerUp(go);
		GameObject mirror = LightCycle.getMirrorPowerUp(i);
		Random r = new Random(); 
		int rx = r.nextInt(2000)-1000;
		int ry = r.nextInt(2000)-1000;
		go.getTransform().setLocal(Mat4.translate(rx, 0, ry));
		mirror.getTransform().setLocal(Mat4.translate(rx, -1, ry));
	}
}
