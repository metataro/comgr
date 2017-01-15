package component;

import ch.fhnw.ether.render.DefaultRenderManager;
import ch.fhnw.ether.render.IRenderManager;
import ch.fhnw.ether.render.IRenderer;
import ch.fhnw.ether.scene.camera.ICamera;
import ch.fhnw.ether.view.IView;
import gameobject.GameObject;

public class Camera extends Component {

    private IRenderManager renderManager;

    private IView targetView;
    private ICamera camera;

    @Override
    public void init() {
        renderManager = getGameObject().getScene().getRenderManager();
    }

    public void setState(IView targetView, ICamera camera) {
        if (this.targetView != null || this.camera != null)
            throw new IllegalStateException("Cannot reassign targetView/camera!");
        this.targetView = targetView;
        this.camera = camera;
        renderManager.addView(targetView);
        renderManager.setCamera(targetView, camera);
    }

    public IView getTargetView() {
        return targetView;
    }

    public ICamera getCamera() {
        return camera;
    }

    @Override
    public void destroy() {
        //renderManager.removeView(targetView);
    }
}
