package audio;

import ch.fhnw.util.math.Vec3;

import java.util.ArrayList;

public class AudioController {

    private final int listenerCount;

    private final AudioListener[] audioListeners;

    public AudioController(int listenerCount) {
        this.listenerCount = listenerCount;
        this.audioListeners = new AudioListener[listenerCount];
        for (int i = 0; i < audioListeners.length; i++) {
            audioListeners[i] = new AudioListener();
        }
    }

    public AudioSource[] createAudioSources() {
        AudioSource[] audioSources = new AudioSource[listenerCount];
        for (int i = 0; i < audioSources.length; i++){
            audioSources[i] = new AudioSource(audioListeners[i]);
        }
        return audioSources;
    }

    public AudioListener getAudioListener(int listenerId) {
        return audioListeners[listenerId];
    }
}
