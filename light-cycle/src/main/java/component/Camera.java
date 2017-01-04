package component;

import ch.fhnw.ether.render.DefaultRenderManager;
import ch.fhnw.ether.render.IRenderManager;
import ch.fhnw.ether.render.IRenderer;
import ch.fhnw.ether.scene.camera.ICamera;
import ch.fhnw.ether.view.IView;
import gameobject.GameObject;

public class Camera extends Component {

    private IView targetView;
    private ICamera camera;

    public IView getTargetView() {
        return targetView;
    }

    public void setTargetView(IView targetView) {
        this.targetView = targetView;
    }

    public ICamera getCamera() {
        return camera;
    }

    public void setCamera(ICamera camera) {
        this.camera = camera;
    }
}
