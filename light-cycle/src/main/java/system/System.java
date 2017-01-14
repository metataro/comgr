package system;

import event.Event;
import event.EventListener;
import scene.Scene;

public abstract class System extends EventListener {

    protected Scene scene;

    public final void setScene(Scene scene) {
        this.scene = scene;
    }

    public void process(float deltaTime) {
        processAllPending(this::processEvent);
        processSystem(deltaTime);
    }

    protected void processSystem(float deltaTime) {}

    protected void processEvent(Event event) {}
}
