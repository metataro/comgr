package system;

import event.Event;

public class TransformSystem extends System {

    @Override
    public void process(float deltaTime) {
        scene.getSceneGraph().update();
    }

    @Override
    public void receive(Event event) {

    }
}
