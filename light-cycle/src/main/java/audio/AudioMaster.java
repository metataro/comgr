package audio;

import ch.fhnw.util.math.Vec3;
import org.lwjgl.openal.*;
import org.lwjgl.util.WaveData;

import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.openal.AL10.*;
import static org.lwjgl.openal.ALC10.*;
import static org.lwjgl.system.MemoryUtil.*;


public class AudioMaster {

    private static long device;

    private static long context;

    private static List<Integer> buffers = new ArrayList<>();

    public static void init(){
        device = alcOpenDevice((ByteBuffer) null);
        if (device == NULL) {
            throw new RuntimeException("Failed to open the default OpenAL device.");
        }
        ALCCapabilities deviceCaps = ALC.createCapabilities(device);
        context = alcCreateContext(device, (IntBuffer) null);
        if (context == NULL) {
            throw new RuntimeException("Failed to create OpenAL context.");
        }
        alcMakeContextCurrent(context);
        AL.createCapabilities(deviceCaps);
        alDistanceModel(AL10.AL_INVERSE_DISTANCE);
        setListenerData(Vec3.ZERO);
    }

    public static AudioBuffer createAudioBufferFromWAV(URL path) {
        int bufferId = alGenBuffers();
        WaveData waveFile = WaveData.create(path);
        alBufferData(bufferId, waveFile.format, waveFile.data, waveFile.samplerate);
        waveFile.dispose();
        return new AudioBuffer(bufferId);
    }

    public static void setListenerData(Vec3 position) {
        alListener3f(AL_POSITION, position.x, position.y, position.z);
        alListener3f(AL_VELOCITY, 0, 0, 0);
    }

    public static void cleanUp()
    {
        for (int buffer : buffers) {
            alDeleteBuffers(buffer);
        }
        alcDestroyContext(context);
        alcCloseDevice(device);
    }
}
