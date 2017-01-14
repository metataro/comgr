package system;

import ch.fhnw.ether.render.IRenderManager;
import event.Event;

public class RenderSystem extends System {

    @Override
    public void processSystem(float deltaTime) {
        this.scene.getRenderManager().update();
    }
}
