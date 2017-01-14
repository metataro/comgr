package system;

import event.Event;

public class TransformSystem extends System {

    @Override
    public void processSystem(float deltaTime) {
        scene.getSceneGraph().update();
    }

}
