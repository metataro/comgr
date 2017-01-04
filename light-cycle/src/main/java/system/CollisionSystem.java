package system;

import audio.AudioBuffer;
import audio.AudioMaster;
import ch.fhnw.ether.scene.mesh.IMesh;
import ch.fhnw.util.math.Vec3;
import component.Mesh;
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
    public void process(float deltaTime) {
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

    private void checkIntersection(BoxCollider c1, BoxCollider c2) {
        Optional<Mesh> mesh1 = c1.getGameObject().getComponent(Mesh.class);
        Optional<Mesh> mesh2 = c2.getGameObject().getComponent(Mesh.class);
        if (mesh1.isPresent() && mesh2.isPresent() && mesh1.get().getMesh().getBounds().intersects(mesh2.get().getMesh().getBounds())) {
            scene.getEventManager().addEvent(new Event.CollisionEvent(this, c1, c2));
        }

        //Transform t1 = c1.getTransform();
        //Transform t2 = c2.getTransform();
        //Vec3 maxA = t1.getWorld().transform(c1.getBoundingBox().getMax());
        //Vec3 minA = t1.getWorld().transform(c1.getBoundingBox().getMin());
        //if (maxA.x < minA.x || maxA.y < minA.y || maxA.z < minA.z) {
        //    Vec3 temp = maxA;
        //    maxA = minA;
        //    minA = temp;
        //}
        //Vec3 maxB = t2.getWorld().transform(c2.getBoundingBox().getMax());
        //Vec3 minB = t2.getWorld().transform(c2.getBoundingBox().getMin());
        //if (maxB.x < minB.x || maxB.y < minB.y || maxB.z < minB.z) {
        //    Vec3 temp = maxB;
        //    maxB = minB;
        //    minB = temp;
        //}
        //if (!(maxA.x < minB.x || minA.x > maxB.x || maxA.y < minB.y || minA.y > maxB.y || maxA.z < minB.z || minA.z > maxB.z)) {
        //    scene.getEventManager().addEvent(new Event.CollisionEvent(this, c1, c2));
        //}
    }

    @Override
    public void receive(Event event) {
        //if (event instanceof Event.CollisionEvent) {
        //    Event.CollisionEvent collisionEvent = (Event.CollisionEvent) event;
        //    //java.lang.System.out.println("Collision! " + ((Event.CollisionEvent) event).collider1.hashCode() +  " <-> " + ((Event.CollisionEvent) event).collider2.hashCode());
        //    Optional<AudioSourceComponent> audio1 = collisionEvent.collider1.getGameObject().getComponent(AudioSourceComponent.class);
        //    Optional<AudioSourceComponent> audio2 = collisionEvent.collider2.getGameObject().getComponent(AudioSourceComponent.class);
        //    AudioBuffer explosion = AudioMaster.createAudioBufferFromWAV(LightCycle.class.getResource("/explosion.wav"));
        //    if (audio1.isPresent()) {
        //        audio1.get().setLooping(false);
        //        audio1.get().play(explosion);
        //    }
        //    if (audio2.isPresent()) {
        //        audio2.get().setLooping(false);
        //        audio2.get().play(explosion);
        //    }
        //}
    }
}
