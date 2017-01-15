package component.behaviour;

import ch.fhnw.ether.image.IGPUImage;
import ch.fhnw.ether.scene.mesh.DefaultMesh;
import ch.fhnw.ether.scene.mesh.IMesh;
import ch.fhnw.ether.scene.mesh.MeshUtilities;
import ch.fhnw.ether.scene.mesh.IMesh.Primitive;
import ch.fhnw.ether.scene.mesh.IMesh.Queue;
import ch.fhnw.ether.scene.mesh.geometry.DefaultGeometry;
import ch.fhnw.ether.scene.mesh.geometry.IGeometry;
import ch.fhnw.ether.scene.mesh.material.IMaterial;
import ch.fhnw.ether.scene.mesh.material.ShadedMaterial;
import ch.fhnw.util.color.RGB;
import ch.fhnw.util.color.RGBA;
import ch.fhnw.util.math.Mat4;
import ch.fhnw.util.math.MathUtilities;
import ch.fhnw.util.math.Vec2;
import ch.fhnw.util.math.Vec3;
import component.Mesh;
import component.Transform;
import component.collider.BoxCollider;
import gameobject.GameObject;
import inputdevice.Input;
import main.LightCycle;
import render.mesh.material.PanelMaterial;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;

public class PlayerBehaviour extends Behaviour {

    private final HashSet<String> buttonsCurrentlyPressed = new HashSet<>();

    private final LinkedList<GameObject> trailSegments = new LinkedList<>();

    private boolean alive;
    private float boostTime = 1;

    private String name;

    private String leftButton;
    private String rightButton;
    private String speedButton;

    private IMaterial trailMaterial;
    private String material;

    private GameObject boostPowerObject;
    private IMesh deletedBoostMesh = null;

    private GameObject camera;
    private GameObject endPosition;

    public boolean isAlive() {
        return alive;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCameraEndPosition(GameObject camera, GameObject endPosition) {
        this.camera = camera;
        this.endPosition = endPosition;
    }

    public void updateCameraPositionToEnd() {
        this.camera.getTransform().setParent(endPosition.getTransform());
    }

    public void setButtons(String left, String right, String speed) {
        this.alive = true;
        this.leftButton = left;
        this.rightButton = right;
        this.speedButton = speed;
    }

    public void setTrailMaterial(String m) {
        this.material = m;
        initTrailSegments();
    }

    public void addBoostTime(float boostTime) {
        this.boostTime += boostTime;
    }

    @Override
    public void init() {

    }

    @Override
    public void update(float deltaTime) {
        if (isAlive()) {
            this.handleControls(deltaTime);
            checkBoostPowerObject();
        }
    }

    private void checkBoostPowerObject() {
        // HACKY HACK!
        if (boostTime == 0 && deletedBoostMesh == null) {
            this.deletedBoostMesh = this.boostPowerObject.getComponent(Mesh.class).get().getMesh();
            this.boostPowerObject.getScene().getRenderManager().removeMesh(deletedBoostMesh);
        } else if (boostTime > 0 && deletedBoostMesh != null) {
            this.boostPowerObject.getScene().getRenderManager().addMesh(deletedBoostMesh);
            this.deletedBoostMesh = null;
        }
    }

    /**
     * @param deltaTime Time passed.
     * @return Get current velocity of the player depending on deltaTime.
     */
    private float getVelocity(final float deltaTime) {
        float velocity = deltaTime * 20;
        if (Input.getButton(speedButton) && boostTime > 0) {
            boostTime = Math.max(boostTime - deltaTime, 0);
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

        //if (Input.getButton(Buttons.BACKWARD)) {
        //    getGameObject().transform.translateBackward(velocity);
        //}

        if (this.isButtonNewlyPressed(Input.getButton(leftButton), leftButton)) {
            getGameObject().transform.rotateLeft(90);
            addTrailSegment();
        }

        if (this.isButtonNewlyPressed(Input.getButton(rightButton), rightButton)) {
            getGameObject().transform.rotateRight(90);
            addTrailSegment();
        }

        getGameObject().transform.translateForward(velocity);

        updateTrailSegments();
    }

    private void updateTrailSegments() {
        if (!this.trailSegments.isEmpty()) {
            Transform trailHeadTransform = this.trailSegments.getFirst().getTransform();
            float targetScaleZ = getTransform().getLocalPosition().distance(trailHeadTransform.getPosition());
            float currentScaleZ = (Math.abs(trailHeadTransform.getScale().x - 1) < MathUtilities.EPSILON ? (Math.abs(trailHeadTransform.getScale().y - 1) < MathUtilities.EPSILON ? trailHeadTransform.getScale().z : trailHeadTransform.getScale().y) : trailHeadTransform.getScale().x);
            float scaleZ = targetScaleZ / currentScaleZ;
            if (scaleZ > 0 ) {
                trailHeadTransform.scale(new Vec3(1, 1, scaleZ));
            }
        }
    }

    private void initTrailSegments() {
    	IGPUImage t = null;
        try {
            t = IGPUImage.read(LightCycle.class.getResource("/textures/"+material+".png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        trailMaterial = new ShadedMaterial(RGB.BLACK, RGB.WHITE, RGB.WHITE, RGB.WHITE, 10, 1, 1f, t);
        addTrailSegment();
    }

    private void addTrailSegment() {
        //addTrailSegment(getTransform().getPosition(), getTransform().getEulerAngles());
        addTrailSegment(getTransform().getLocalPosition(), getTransform().getLocalEulerAngles());
    }

    private void addTrailSegment(Vec3 position, Vec3 eulerAngles) {
        GameObject trailSegment = getGameObject().getScene().createGameObject();
        trailSegment.getTransform().setLocal(Mat4.multiply(
                Mat4.translate(position),
                Mat4.rotate(eulerAngles.x, 0, 1, 0),
                Mat4.rotate(eulerAngles.y, 1, 0, 0),
                Mat4.scale(1, 1, 0.001f)//,
                //Mat4.rotate(eulerAngles.z, 0, 0, 1)
        ));
        GameObject trailSegmentInner = getGameObject().getScene().createGameObject(trailSegment.transform);
        trailSegmentInner.addComponent(Mesh.class).setMesh(createTrailMesh());
        trailSegmentInner.addComponent(BoxCollider.class);
        trailSegmentInner.getTransform().setLocal(Mat4.multiply(Mat4.translate(0,-0.35f,0.5f),Mat4.scale(0.1f, 0.3f, 1)));
        trailSegments.addFirst(trailSegment);
    }

    private IMesh createTrailMesh() {
        return MeshUtilities.createCube(trailMaterial);
    }

    public void destroyTrail() {
        trailSegments.forEach(GameObject::destroy);
        trailSegments.clear();
        addTrailSegment();
    }

    public float getBoostTime() {
        return boostTime;
    }

    public void setBoostPowerObject(GameObject boostPowerObject) {
        this.boostPowerObject = boostPowerObject;
    }
    
    public void onDraw(){
    	IGPUImage t = null;
        try {
            t = IGPUImage.read(LightCycle.class.getResource("/textures/draw.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    	IMesh raw = LightCycle.createPanel(new Vec2(100,200), new Vec2(600,400),t, RGBA.BLACK  );
    	GameObject g = getGameObject().getScene().createGameObject(); 
    	g.getTransform().setLocal(Mat4.translate(0,-0.5f,0));
    	g.addComponent(Mesh.class).setMesh(raw);
        updateCameraPositionToEnd();
    }
    public void onWin(){
    	IGPUImage t = null;
        try {
        	if(name.contains("1")){
        		t = IGPUImage.read(LightCycle.class.getResource("/textures/player1won.png"));
        	}else{
        		t = IGPUImage.read(LightCycle.class.getResource("/textures/player2won.png"));
        		
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    	IMesh raw = LightCycle.createPanel(new Vec2(100,200), new Vec2(600,400),t, RGBA.BLACK  );
    	GameObject g = getGameObject().getScene().createGameObject(); 
    	g.getTransform().setLocal(Mat4.translate(0,-0.5f,0));
    	g.addComponent(Mesh.class).setMesh(raw);
        updateCameraPositionToEnd();
    }
    
}
