package audio;

public class AudioBuffer {

    private final int bufferId;

    AudioBuffer(int bufferId) {
        this.bufferId = bufferId;
    }

    public int getBufferId() {
        return bufferId;
    }
}
