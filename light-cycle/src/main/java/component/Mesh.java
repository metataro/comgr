package component;

import ch.fhnw.ether.scene.mesh.IMesh;
import ch.fhnw.util.math.Mat4;
import gameobject.GameObject;

public class Mesh extends Component {

    private IMesh mesh;

    public void setMesh(IMesh mesh) {
        if (this.mesh != null)
            removeMesh();
        this.mesh = mesh;
        getGameObject().getScene().getRenderManager().addMesh(mesh);
    }

    public IMesh getMesh() {
        return mesh;
    }

    private void removeMesh() {
        getGameObject().getScene().getRenderManager().removeMesh(mesh);
    }

    @Override
    public void destroy() {
        removeMesh();
    }
}
