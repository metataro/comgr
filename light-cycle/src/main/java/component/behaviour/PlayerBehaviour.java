package component.behaviour;

import ch.fhnw.ether.scene.mesh.IMesh;
import ch.fhnw.ether.scene.mesh.MeshUtilities;
import ch.fhnw.util.math.Mat4;
import ch.fhnw.util.math.MathUtilities;
import ch.fhnw.util.math.Vec3;
import component.Mesh;
import component.Transform;
import component.collider.BoxCollider;
import gameobject.GameObject;
import inputdevice.Input;
import inputdevice.Input.Buttons;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

public class PlayerBehaviour extends Behaviour {

    private final HashSet<String> buttonsCurrentlyPressed = new HashSet<>();

    private final LinkedList<GameObject> wallSegments = new LinkedList<>();

    @Override
    public void init() {
        initWallSegments();
    }

    @Override
    public void update(float deltaTime) {
        this.handleControls(deltaTime);
    }

    /**
     * @param deltaTime Time passed.
     * @return Get current velocity of the player depending on deltaTime.
     */
    private float getVelocity(final float deltaTime) {
        float velocity = deltaTime * 20;
        if (Input.getButton(Buttons.SPEED)) {
            velocity *= 5;
        }
        return velocity;
    }

    /**
     * Handle Input that controls the player.
     *
     * @param deltaTime Time passed.
     */
    private void handleControls(final float deltaTime) {
        this.handleSteering(getVelocity(deltaTime));
    }

    // TODO move somehow to Input class if we keep this kind of steering behaviour
    private boolean isButtonNewlyPressed(final boolean down, final String button) {
        if (down) {
            if (this.buttonsCurrentlyPressed.contains(button)) {
                // button was already pressed last update
                return false;
            } else {
                // button is first time pressed this update
                this.buttonsCurrentlyPressed.add(button);
                return true;
            }
        } else {
            // button is not pressed
            this.buttonsCurrentlyPressed.remove(button);
            return false;
        }
    }

    /**
     * Handle Input that controls the player's vehicle.
     *
     * @param velocity Current velocity.
     */
    private void handleSteering(final float velocity) {

        //if (Input.getButton(Buttons.FORWARD)) {
            getGameObject().transform.translateForward(velocity);
        //}

        //if (Input.getButton(Buttons.BACKWARD)) {
        //    getGameObject().transform.translateBackward(velocity);
        //}

        updateWallSegments();

        if (this.isButtonNewlyPressed(Input.getButton(Buttons.LEFT), Buttons.LEFT)) {
            getGameObject().transform.rotateLeft(90);
            addWallSegment();
        }

        if (this.isButtonNewlyPressed(Input.getButton(Buttons.RIGHT), Buttons.RIGHT)) {
            getGameObject().transform.rotateRight(90);
            addWallSegment();
        }

        // if (Input.getButton("Speed")) gameObject.transform.translate(0, velocity, 0);
        // if (Input.getButton("Speed")) gameObject.transform.translate(0, -velocity, 0);

    }

    private void updateWallSegments() {
        if (!this.wallSegments.isEmpty()) {
            Transform wallHeadTransform = this.wallSegments.getFirst().getTransform();
            float targetScaleZ = getTransform().getLocalPosition().distance(wallHeadTransform.getPosition());
            float currentScaleZ = (Math.abs(wallHeadTransform.getScale().x - 1) < MathUtilities.EPSILON ? (Math.abs(wallHeadTransform.getScale().y - 1) < MathUtilities.EPSILON ? wallHeadTransform.getScale().z : wallHeadTransform.getScale().y) : wallHeadTransform.getScale().x);
            float scaleZ = targetScaleZ / currentScaleZ;
            if (scaleZ > 0 ) {
                wallHeadTransform.scale(new Vec3(1, 1, scaleZ));
            }
        }
    }

    private void initWallSegments() {
        addWallSegment(getTransform().getLocalPosition(), getTransform().getLocalEulerAngles());
    }

    private void addWallSegment() {
        //addWallSegment(getTransform().getPosition(), getTransform().getEulerAngles());
        addWallSegment(getTransform().getLocalPosition(), getTransform().getLocalEulerAngles());
    }

    private void addWallSegment(Vec3 position, Vec3 eulerAngles) {
        GameObject wallSegment = getGameObject().getScene().createGameObject();
        wallSegment.getTransform().setLocal(Mat4.multiply(
                Mat4.translate(position),
                Mat4.rotate(eulerAngles.x, 0, 1, 0),
                Mat4.rotate(eulerAngles.y, 1, 0, 0),
                Mat4.scale(1, 1, 0.001f)//,
                //Mat4.rotate(eulerAngles.z, 0, 0, 1)
        ));
        GameObject wallSegmentInner = getGameObject().getScene().createGameObject(wallSegment.transform);
        wallSegmentInner.addComponent(Mesh.class).setMesh(createWallMesh());
        wallSegmentInner.addComponent(BoxCollider.class);
        wallSegmentInner.getTransform().setLocal(Mat4.multiply(Mat4.translate(0,-0.35f,0.5f),Mat4.scale(0.1f, 0.3f, 1)));
        wallSegments.addFirst(wallSegment);
    }

    private IMesh createWallMesh() {
        IMesh mesh = MeshUtilities.createCube();
        getGameObject().getScene().getRenderManager().addMesh(mesh);
        return mesh;
    }
}
