package audio;

import ch.fhnw.util.math.Vec3;
import org.lwjgl.openal.AL10;

import static org.lwjgl.openal.AL10.*;

public class AudioSource {

    public static final float DEFAULT_ROLLOFF_FACTOR = 1;
    public static final float DEFAULT_REFERENCE_DISTANCE = 6;
    public static final float DEFAULT_MAX_DISTANCE = 50;

    private int sourceId;

    private final AudioListener listener;

    public AudioSource(AudioListener listener) {
        this(listener, DEFAULT_ROLLOFF_FACTOR, DEFAULT_REFERENCE_DISTANCE, DEFAULT_MAX_DISTANCE);
    }

    public AudioSource(AudioListener listener, float rollOffFactor, float referenceDistance, float maxDistance) {
        sourceId = alGenSources();
        this.listener = listener;
        alSourcef(sourceId, AL10.AL_ROLLOFF_FACTOR, rollOffFactor);
        alSourcef(sourceId, AL10.AL_REFERENCE_DISTANCE, referenceDistance);
        alSourcef(sourceId, AL10.AL_MAX_DISTANCE, maxDistance);
    }

    public void play(AudioBuffer buffer) {
        stop();
        alSourcei(sourceId, AL10.AL_BUFFER, buffer.getBufferId());
        alSourcePlay(sourceId);
    }

    public void delete() {
        stop();
        alDeleteSources(sourceId);
    }

    public void pause() {
        alSourcePause(sourceId);
    }

    public void resume() {
        alSourcePlay(sourceId);
    }

    public void stop() {
        alSourceStop(sourceId);
    }

    public void setGain(float gain) {
        alSourcef(sourceId, AL10.AL_GAIN, gain);
    }

    public void setPitch(float pitch) {
        alSourcef(sourceId, AL10.AL_PITCH, pitch);
    }

    public void setPosition(Vec3 position) {
        position = listener.getTransform().transform(position);
        alSource3f(sourceId, AL10.AL_POSITION, position.x, position.y, position.z);
    }

    public void setVelocity(Vec3 velocity) {
        velocity = velocity.subtract(listener.getVelocity());
        alSource3f(sourceId, AL10.AL_VELOCITY, velocity.x, velocity.y, velocity.z);
    }

    public void setLooping(boolean loop) {
        alSourcei(sourceId, AL10.AL_LOOPING, loop ? AL10.AL_TRUE : AL10.AL_FALSE);
    }

    public void setRollOffFactor(float rollOffFactor) {
        alSourcef(sourceId, AL10.AL_ROLLOFF_FACTOR, rollOffFactor);
    }

    public void setReferenceDistance(float referenceDistance) {
        alSourcef(sourceId, AL10.AL_REFERENCE_DISTANCE, referenceDistance);
    }
    public void setMaxDistance(float maxDistance) {
        alSourcef(sourceId, AL10.AL_MAX_DISTANCE, maxDistance);
    }

    public boolean isPlaying() {
        return AL10.alGetSourcei(sourceId, AL10.AL_SOURCE_STATE) == AL10.AL_PLAYING;
    }

    public int getCurrentlyPlayingBufferId() {
        return AL10.alGetSourcei(sourceId, AL10.AL_BUFFER);
    }
}
