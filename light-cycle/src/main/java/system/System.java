package system;

import event.EventListener;
import scene.Scene;

public abstract class System implements EventListener {

    protected Scene scene;

    /**
     * Sets the scene this system belongs to.
     * @param scene The scene
     */
    public final void setScene(Scene scene) {
        this.scene = scene;
    }

    public abstract void process(float deltaTime);
}
