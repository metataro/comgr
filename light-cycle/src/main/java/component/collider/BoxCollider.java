package component.collider;


import ch.fhnw.util.math.geometry.BoundingBox;
import component.Mesh;
import component.MeshGroup;

import java.util.Optional;

//TODO: this is not how it is supposed to be! however it works for the moment ;-)
public class BoxCollider extends Collider {
    //private BoundingBox boundingBox;

    public BoundingBox getBoundingBox() {
        Optional<Mesh> mesh1 = getGameObject().getComponent(Mesh.class);
        if (mesh1.isPresent()) {
            return mesh1.get().getMesh().getBounds();
        }  else {
            Optional<MeshGroup> meshGroup1 = getGameObject().getComponent(MeshGroup.class);
            if (meshGroup1.isPresent()) {
                return meshGroup1.get().getBounds();
            }
        }
        return new BoundingBox();
    }
}
