package component;

import ch.fhnw.ether.render.IRenderManager;
import ch.fhnw.ether.scene.light.ILight;
import gameobject.GameObject;

public class Light extends Component {

    private ILight light;

    public ILight getLight() {
        return light;
    }

    public void setLight(ILight light) {
        this.light = light;
    }
}
