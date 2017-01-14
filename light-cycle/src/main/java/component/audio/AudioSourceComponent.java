package component.audio;

import audio.AudioBuffer;
import audio.AudioSource;
import ch.fhnw.util.math.Vec3;
import component.Component;


public class AudioSourceComponent extends Component{

    private AudioSource[] audioSources;

    public AudioSource[] getAudioSource() {
        return audioSources;
    }

    public void setAudioSource(AudioSource[] audioSources) {
        this.audioSources = audioSources;
    }

    public void setPosition(Vec3 position) {
        for (AudioSource source : audioSources) {
            source.setPosition(position);
        }
    }

    public void setVelocity(Vec3 velocity) {
        for (AudioSource source : audioSources) {
            source.setVelocity(velocity);
        }
    }

    public void setLooping(boolean loop) {
        for (AudioSource source : audioSources) {
            source.setLooping(loop);
        }
    }

    public void setPitch(float pitch) {
        for (AudioSource source : audioSources) {
            source.setPitch(pitch);
        }
    }

    public void setGain(float gain) {
        for (AudioSource source : audioSources) {
            source.setGain(gain);
        }
    }

    public void setRollOffFactor(float rollOffFactor) {
        for (AudioSource source : audioSources) {
            source.setRollOffFactor(rollOffFactor);
        }
    }

    public void setReferenceDistance(float referenceDistance) {
        for (AudioSource source : audioSources) {
            source.setReferenceDistance(referenceDistance);
        }
    }

    public void setMaxDistance(float maxDistance) {
        for (AudioSource source : audioSources) {
            source.setMaxDistance(maxDistance);
        }
    }

    public void play(AudioBuffer buffer) {
        for (AudioSource source : audioSources) {
            source.play(buffer);
        }
    }

    public boolean isBufferPlaying(AudioBuffer buffer) {
        return audioSources[0].getCurrentlyPlayingBufferId() == buffer.getBufferId();
    }

    public boolean isPlaying() {
        return audioSources[0].isPlaying();
    }

    public void pause() {
        for (AudioSource source : audioSources) {
            source.pause();
        }
    }

    public void resume() {
        for (AudioSource source : audioSources) {
            source.resume();
        }
    }

    public void stop() {
        for (AudioSource source : audioSources) {
            source.stop();
        }
    }

    @Override
    public void destroy() {
        super.destroy();
        for (AudioSource source : audioSources) {
            source.delete();
        }
    }
}
