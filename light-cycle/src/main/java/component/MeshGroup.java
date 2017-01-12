package component;

import ch.fhnw.ether.scene.mesh.IMesh;
import ch.fhnw.util.math.geometry.BoundingBox;

import java.util.List;

public class MeshGroup extends Component {

    private List<IMesh> meshes;

    public void setMeshes(List<IMesh> meshes) {
        this.meshes = meshes;
    }

    public List<IMesh> getMeshes() {
        return meshes;
    }

    public BoundingBox getBounds() {
        BoundingBox bounds = new BoundingBox();
        for (IMesh mesh: meshes) {
            bounds.add(mesh.getBounds());
        }
        return bounds;
    }
}
