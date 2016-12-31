package system;


import component.audio.AudioListenerComoponent;
import component.audio.AudioSourceComponent;
import event.Event;

import java.util.ArrayList;

public class AudioSystem extends System {

    @Override
    public void process(float deltaTime) {
        ArrayList<AudioListenerComoponent> listeners = scene.getComponentManager().getComponents(AudioListenerComoponent.class);
        ArrayList<AudioSourceComponent> sources = scene.getComponentManager().getComponents(AudioSourceComponent.class);
        for(AudioListenerComoponent listener : listeners) {
            listener.setTransform(listener.getTransform().getWorld().inverse());
        }
        for(AudioSourceComponent source : sources) {
            source.setPosition(source.getTransform().getPosition());
        }
    }

    @Override
    public void receive(Event event) {

    }
}
