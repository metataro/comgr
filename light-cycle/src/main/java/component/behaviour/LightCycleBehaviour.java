package component.behaviour;

import audio.AudioBuffer;
import audio.AudioMaster;
import ch.fhnw.util.math.Mat4;
import component.audio.AudioSourceComponent;
import event.Event;
import gameobject.GameObject;
import main.LightCycle;

import java.util.List;
import java.util.Optional;

public class LightCycleBehaviour extends Behaviour {

    private AudioBuffer tronEngine;
    private AudioBuffer explosion;

    @Override
    public void init() {
        tronEngine = AudioMaster.createAudioBufferFromWAV(LightCycle.class.getResource("/tronengine.wav"));
        explosion = AudioMaster.createAudioBufferFromWAV(LightCycle.class.getResource("/explosion.wav"));
        AudioSourceComponent audio = getGameObject().addComponent(AudioSourceComponent.class);
        audio.setAudioSource(getGameObject().getScene().getAudioController().createAudioSources());
        audio.setLooping(true);
        audio.setGain(0.5f);
        audio.play(tronEngine);
    }

    @Override
    public void onCollision(GameObject other) {
        Optional<AudioSourceComponent> audio = getGameObject().getComponent(AudioSourceComponent.class);
        if (audio.isPresent() && !other.getComponent(Behaviour.class).toString().contains("PowerUpBehaviour")) {
            audio.get().setLooping(false);
            audio.get().play(explosion);
        }
    }

}
