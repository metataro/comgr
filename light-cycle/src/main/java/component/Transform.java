package component;

import ch.fhnw.util.math.Mat4;
import ch.fhnw.util.math.MathUtilities;
import ch.fhnw.util.math.Vec3;
import gameobject.GameObject;
import scene.SceneGraphNode;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

public class Transform extends Component implements SceneGraphNode<Transform> {

    private boolean dirty;
    private Mat4 local;
    private Mat4 world;
    private Transform parent;
    private ArrayList<Transform> children = new ArrayList<>();

    public Transform() {
        this.parent = null;
        this.local = Mat4.ID;
        this.world = Mat4.ID;
        this.dirty = true;
    }

    public void setParent(Transform parent) {
        parent.addChild(this);
        this.parent = parent;
    }

    public void setLocal(Mat4 transform) {
        this.local = transform;
        this.dirty = true;
    }

    public Mat4 getWorld() {
        return world;
    }

    public Mat4 getLocal() {
        return local;
    }

    public Vec3 getPosition() {
        return new Vec3(world.m03, world.m13, world.m23);
    }

    public Vec3 getForward() {
        return new Vec3(world.m02, world.m12, world.m22);
    }

    public Vec3 getUp() {
        return new Vec3(world.m01, world.m11, world.m21);
    }

    private Vec3 getEulerAngles(Mat4 mat) {
        float yaw, pitch, roll;

        if (Math.abs(mat.m12) > 1 - MathUtilities.EPSILON) {
            yaw = (float)(Math.atan2(-mat.m21, mat.m00) * MathUtilities.RADIANS_TO_DEGREES);
            roll = 0;
            pitch = 0;
        } else {
            yaw = (float)(Math.atan2(mat.m02, mat.m22) * MathUtilities.RADIANS_TO_DEGREES);
            roll = (float)(Math.atan2(mat.m20, mat.m11) * MathUtilities.RADIANS_TO_DEGREES);
            pitch = (float)(Math.asin(-mat.m12) * MathUtilities.RADIANS_TO_DEGREES);
        }

        return new Vec3(yaw, pitch, roll);
    }

    public Vec3 getLocalEulerAngles() {
        return getEulerAngles(local);
    }

    public Vec3 getEulerAngles() {
        return getEulerAngles(world);
    }

    public void translate(Vec3 translation) {
        local = Mat4.multiply(Mat4.translate(translation), local);
        this.dirty = true;
    }

    public void translate(float x, float y, float z) {
        local = Mat4.multiply(Mat4.translate(x, y, z), local);
        this.dirty = true;
    }

    public void translateForward(float scaleFactor) {
        this.translate(this.getForward().scale(scaleFactor));
    }

    public void translateBackward(float scaleFactor) {
        this.translate(this.getForward().negate().scale(scaleFactor));
    }

    public void rotate(float angle, float x, float y, float z) {
        local = Mat4.multiply(local, Mat4.rotate(angle, x, y, z));
        this.dirty = true;
    }

    public void rotateLeft(final float angle) {
        getGameObject().transform.rotate(angle, 0, 1, 0);
    }

    public void rotateRight(final float angle) {
        getGameObject().transform.rotate(angle, 0, -1, 0);
    }

    @Override
    public void addChild(Transform childTransform) {
        children.add(childTransform);
        childTransform.parent = this;
        childTransform.dirty = true;
    }

    @Override
    public void addChildren(Collection<Transform> transforms) {
        transforms.forEach(this::addChild);
    }

    @Override
    public boolean add(Transform parent, Transform transform) {
        if (this == parent) {
            addChild(transform);
            return true;
        }

        for (Transform child : children) {
            boolean result = child.add(parent, transform);
            if (result) return true;
        }

        return false;
    }

    @Override
    public boolean remove(Transform root, Transform transform) {
        for(int i = 0; i < children.size(); i++) {
            Transform currentChild = children.get(i);
            if (currentChild == transform) {
                int lastChildIndex = children.size() - 1;
                if (i != lastChildIndex) {
                    this.children.set(i, this.children.get(lastChildIndex));
                    this.children.set(lastChildIndex, currentChild);
                }
                children.remove(lastChildIndex);
                // TODO: オッケーかな?
                root.addChildren(currentChild.children);
                return true;
            } else if (currentChild.remove(root, transform)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void apply(boolean dirty) {
        dirty = this.dirty || dirty;
        if (dirty && parent != null) {
            world = Mat4.multiply(parent.world, local);
            this.dirty = false;
            // hacky hack start
            Optional<Mesh> mesh = getGameObject().getComponent(Mesh.class);
            Optional<Camera> camera = getGameObject().getComponent(Camera.class);
            Optional<Light> light = getGameObject().getComponent(Light.class);
            if (mesh.isPresent()) {
                mesh.get().getMesh().setTransform(world);
            }
            if (camera.isPresent()) {
                Vec3 position = getPosition();
                camera.get().getCamera().setPosition(position);
                camera.get().getCamera().setTarget(position.add(getForward()));
                camera.get().getCamera().setUp(getUp());
            }
            if (light.isPresent()) {
                light.get().getLight().setPosition(getPosition());
                light.get().getLight().setSpotDirection(getForward());
            }
            // hacky hack end
        }
        for (Transform child : children) {
            child.apply(dirty);
        }
    }

    public static Transform createRoot() {
        return new Transform();
    }
}
