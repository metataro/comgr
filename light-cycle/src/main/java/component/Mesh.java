package component;

import ch.fhnw.ether.scene.mesh.IMesh;
import ch.fhnw.util.math.Mat4;
import gameobject.GameObject;

public class Mesh extends Component {

    private IMesh mesh;

    public void setMesh(IMesh mesh) {
        this.mesh = mesh;
    }

    public IMesh getMesh() {
        return mesh;
    }
}
