package component.collider;


import ch.fhnw.util.math.geometry.BoundingBox;
import component.Mesh;

import java.util.Optional;

public class BoxCollider extends Collider {
    private BoundingBox boundingBox;

    public BoundingBox getBoundingBox() {
        return boundingBox;
    }

    public void setBoundingBox(BoundingBox boundingBox) {
        this.boundingBox = boundingBox;
    }
}
