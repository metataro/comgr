package component.audio;

import audio.AudioListener;
import ch.fhnw.util.math.Mat4;
import ch.fhnw.util.math.Vec3;
import component.Component;

public class AudioListenerComoponent extends Component {

    private AudioListener audioListener;

    public AudioListener getAudioListener() {
        return audioListener;
    }

    public void setAudioListener(AudioListener audioListener) {
        this.audioListener = audioListener;
    }

    public void setTransform(Mat4 transform) {
        audioListener.setTransform(transform);
    }

    public void setVelocity(Vec3 velocity) {
        audioListener.setVelocity(velocity);
    }

}
