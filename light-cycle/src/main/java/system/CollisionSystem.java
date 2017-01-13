package system;

import audio.AudioBuffer;
import audio.AudioMaster;
import ch.fhnw.ether.scene.mesh.IMesh;
import ch.fhnw.util.math.Vec3;
import ch.fhnw.util.math.geometry.BoundingBox;
import component.Mesh;
import component.MeshGroup;
import component.Transform;
import component.audio.AudioSourceComponent;
import component.collider.BoxCollider;
import component.collider.Collider;
import main.LightCycle;
import event.Event;

import java.util.ArrayList;
import java.util.Optional;

public class CollisionSystem extends System {

    @Override
    public void processSystem(float deltaTime) {
        ArrayList<Collider> colliders = scene.getComponentManager().getComponents(Collider.class);
        for (int i = 0; i < colliders.size(); i++) {
            Collider c1 = colliders.get(i);
            for (int j = i + 1; j < colliders.size(); j++) {
                Collider c2 = colliders.get(j);
                if (c1 instanceof BoxCollider && c2 instanceof BoxCollider) {
                    checkIntersection((BoxCollider) c1, (BoxCollider) c2);
                }
            }
        }
    }

    @Override
    protected void processEvent(Event event) {

    }

    private void checkIntersection(BoxCollider c1, BoxCollider c2) {
        if (c1.getBoundingBox().intersects(c2.getBoundingBox())) {
            scene.getEventManager().notify(new Event.CollisionEvent(this, c1, c2));
        }
    }
}
