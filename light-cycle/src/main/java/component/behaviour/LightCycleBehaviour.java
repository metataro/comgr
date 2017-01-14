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
    private boolean collided = false;
    private PlayerBehaviour playerBehaviour;

    public void setPlayerBehaviour(PlayerBehaviour playerBehaviour) {
        this.playerBehaviour = playerBehaviour;
    }

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
        if (audio.isPresent() && !collided) {
            audio.get().setLooping(false);
            audio.get().play(explosion);
            collided = true;
            if (playerBehaviour != null) {
                playerBehaviour.setAlive(false);
            }
            //getTransform().getParent().getGameObject().getComponent(Behaviour.class).ifPresent(b -> b.onCollision(other));
        }
    }

}
