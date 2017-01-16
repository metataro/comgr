package component.behaviour;

import audio.AudioBuffer;
import audio.AudioMaster;
import ch.fhnw.ether.image.IGPUImage;
import ch.fhnw.ether.platform.Platform;
import ch.fhnw.ether.scene.mesh.IMesh;
import ch.fhnw.util.color.RGBA;
import ch.fhnw.util.math.Mat4;
import ch.fhnw.util.math.Vec2;
import ch.fhnw.util.math.Vec3;
import component.Mesh;
import component.Transform;
import component.audio.AudioSourceComponent;
import component.powerup.DestroyOtherTrailPowerUp;
import component.powerup.DestroyTrailPowerUp;
import component.powerup.PowerUp;
import component.powerup.SpeedPowerUp;
import event.Event;
import gameobject.GameObject;
import main.LightCycle;

import java.io.IOException;
import java.util.Optional;
import java.util.Random;

public class PowerUpBehaviour extends Behaviour {

    private AudioBuffer sound;
    private GameObject notification;
    private int time = 0;;
    
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
		time++;
    	Vec3 position = getTransform().getLocalPosition();
		float targetY = minY + Math.abs(maxY - minY) * (float)Math.sin(modifier) / 2;
		modifier += bumpSpeed*deltaTime;
		getTransform().translate(0, targetY - position.y, 0);
		int i = LightCycle.getIndexPowerUp(getGameObject());
		GameObject mirror = LightCycle.getMirrorPowerUp(i);
		mirror.getTransform().translate(0,-(targetY - position.y),0);
		if(time == 100 && notification != null){
			notification.destroy();
		}
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
		        int w = Platform.get().getMonitors()[0].getWidth()/2;
	
		    	IGPUImage k = null;
		        try {
		        	if(((LightCycleBehaviour)otherBehaviour.get()).playerBehaviour.name.contains("1")){
		        		 Optional<PowerUp> p = getGameObject().getComponent(PowerUp.class);
		        		if(p.isPresent() && p.get() instanceof SpeedPowerUp){
		        			k = IGPUImage.read(LightCycle.class.getResource("/textures/speedp1.png"));
		        		} else if(p.isPresent() && p.get() instanceof DestroyTrailPowerUp){
		        			k = IGPUImage.read(LightCycle.class.getResource("/textures/destroyylp1.png"));
		        		} else if(p.isPresent() && p.get() instanceof DestroyOtherTrailPowerUp){
		        			k = IGPUImage.read(LightCycle.class.getResource("/textures/destroyelp1.png"));;
		        		}
		        	}else{
		        		 Optional<PowerUp> p = getGameObject().getComponent(PowerUp.class);
			        		if(p.isPresent() && p.get() instanceof SpeedPowerUp){
			        			k = IGPUImage.read(LightCycle.class.getResource("/textures/speedp2.png"));
			        		} else if(p.isPresent() && p.get() instanceof DestroyTrailPowerUp){
			        			k = IGPUImage.read(LightCycle.class.getResource("/textures/destroyylp2.png"));
			        		} else if(p.isPresent() && p.get() instanceof DestroyOtherTrailPowerUp){
			        			k = IGPUImage.read(LightCycle.class.getResource("/textures/destroyelp2.png"));;
			        		}
		        	}
		        	
		        } catch (IOException e) {
		            e.printStackTrace();
		        }
		    	IMesh raw = LightCycle.createPanel(new Vec2(50,0), new Vec2(w-100,(w-100)/3),k, RGBA.BLACK  );
		    	notification = getGameObject().getScene().createGameObject(); 
		    	notification.getTransform().setLocal(Mat4.translate(0,-0.5f,0));
		    	notification.addComponent(Mesh.class).setMesh(raw);
		    	time = 0;
	        
	        }
	        
	        movePowerUp(getGameObject());
    }

	public void movePowerUp(GameObject go) {
		int i = LightCycle.getIndexPowerUp(go);
		GameObject mirror = LightCycle.getMirrorPowerUp(i);
		Random r = new Random(); 
		int extend = LightCycle.playAreaExtends;
		int rx = r.nextInt(2*extend)-extend;
		int ry = r.nextInt(2*extend)-extend;
		go.getTransform().setLocal(Mat4.translate(rx, 0, ry));
		mirror.getTransform().setLocal(Mat4.translate(rx, -1, ry));
	}
}
