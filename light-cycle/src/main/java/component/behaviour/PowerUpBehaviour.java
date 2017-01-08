package component.behaviour;

import audio.AudioBuffer;
import audio.AudioMaster;
import component.audio.AudioSourceComponent;
import event.Event;
import main.LightCycle;

import java.util.Optional;

public class PowerUpBehaviour extends Behaviour {

    private AudioBuffer powerupsound;


    @Override
    public void init() {
        powerupsound = AudioMaster.createAudioBufferFromWAV(LightCycle.class.getResource("/fanfare.wav"));
        AudioSourceComponent audio = getGameObject().addComponent(AudioSourceComponent.class);
        audio.setAudioSource(getGameObject().getScene().getAudioController().createAudioSources());
        
    }

    @Override
    public void onCollision(Event.CollisionEvent collisionEvent) {
        Optional<AudioSourceComponent> audio = getGameObject().getComponent(AudioSourceComponent.class);
        if (audio.isPresent()) {
            audio.get().setLooping(false);
            audio.get().play(powerupsound);
        }
    }

}
