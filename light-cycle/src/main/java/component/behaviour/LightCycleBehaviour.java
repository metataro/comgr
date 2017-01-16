package component.behaviour;

import audio.AudioBuffer;
import audio.AudioMaster;
import ch.fhnw.util.color.RGBA;
import ch.fhnw.util.math.Mat4;
import component.MeshGroup;
import component.audio.AudioSourceComponent;
import component.particle.ParticleSystem;
import component.powerup.PowerUp;
import event.Event;
import gameobject.GameObject;
import main.LightCycle;

import java.util.List;
import java.util.Optional;

public class LightCycleBehaviour extends Behaviour {

    private AudioBuffer tronEngine;
    private AudioBuffer explosion;
    private boolean collided = false;
    public PlayerBehaviour playerBehaviour;

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
    public void update(float deltaTime) {
        if (playerBehaviour != null && !playerBehaviour.isAlive()) {
            getGameObject().getComponent(AudioSourceComponent.class).ifPresent(a -> {
                if (a.isBufferPlaying(tronEngine)) {
                    a.setLooping(false);
                    a.stop();
                }
            });
        }
    }

    @Override
    public void onCollision(GameObject other) {
        Optional<AudioSourceComponent> audio = getGameObject().getComponent(AudioSourceComponent.class);
        if (!collided) {
            audio.ifPresent(a -> {
                a.setGain(1f);
                a.setLooping(false);
                a.play(explosion);
            });
            collided = true;
            if (playerBehaviour != null) {
                playerBehaviour.setAlive(false);
            }
            getGameObject().removeComponent(MeshGroup.class);
            getGameObject().getComponent(ParticleSystem.class).ifPresent(p -> p.emmitParticles(RGBA.BLACK, 0.1f, 0.5f,100));
        }
    }

    @Override
    public void onTrigger(GameObject other) {
        other.getComponent(PowerUp.class).ifPresent(powerUp -> powerUp.apply(playerBehaviour.getGameObject()));
    }
}
