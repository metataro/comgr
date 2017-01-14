package main;

import audio.AudioMaster;
import ch.fhnw.ether.controller.event.DefaultEventScheduler;
import ch.fhnw.ether.controller.event.IEventScheduler;
import ch.fhnw.ether.formats.obj.ObjReader;
import ch.fhnw.ether.image.IGPUImage;
import ch.fhnw.ether.media.RenderCommandException;
import ch.fhnw.ether.platform.Platform;
import ch.fhnw.ether.render.IRenderManager;
import ch.fhnw.ether.scene.camera.Camera;
import ch.fhnw.ether.scene.camera.ICamera;
import ch.fhnw.ether.scene.light.DirectionalLight;
import ch.fhnw.ether.scene.light.ILight;
import ch.fhnw.ether.scene.light.SpotLight;
import ch.fhnw.ether.scene.mesh.DefaultMesh;
import ch.fhnw.ether.scene.mesh.IMesh;
import ch.fhnw.ether.scene.mesh.MeshUtilities;
import ch.fhnw.ether.scene.mesh.IMesh.Flag;
import ch.fhnw.ether.scene.mesh.IMesh.Primitive;
import ch.fhnw.ether.scene.mesh.IMesh.Queue;
import ch.fhnw.ether.scene.mesh.geometry.DefaultGeometry;
import ch.fhnw.ether.scene.mesh.geometry.IGeometry;
import ch.fhnw.ether.scene.mesh.material.ColorMapMaterial;
import ch.fhnw.ether.scene.mesh.material.IMaterial;
import ch.fhnw.ether.scene.mesh.material.ShadedMaterial;
import ch.fhnw.ether.view.IView;
import ch.fhnw.util.ArrayUtilities;
import ch.fhnw.util.color.RGB;
import ch.fhnw.util.color.RGBA;
import ch.fhnw.util.math.Mat4;
import ch.fhnw.util.math.Vec3;
import component.Light;
import component.Mesh;
import component.MeshGroup;
import component.audio.AudioListenerComoponent;
import component.audio.AudioSourceComponent;
import component.behaviour.*;
import component.collider.BoxCollider;
import gameobject.GameObject;
import inputdevice.*;
import inputdevice.Input.Buttons;
import org.lwjgl.glfw.GLFW;
import render.View;
import scene.ProcessType;
import scene.Scene;
import system.*;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ThreadFactory;
import java.util.stream.Collectors;

public class LightCycle {

    private final float fps = 60;

    private IEventScheduler scheduler;
    private Scene currentScene;

    public LightCycle() {
        scheduler = new DefaultEventScheduler(() -> {
            if (currentScene != null) {
                currentScene.update(1 / fps);
                currentScene.draw(1 / fps);
            }
            scheduler.repaint();
        }, fps);
    }

    public void run() throws IOException, RenderCommandException, InterruptedException {
        Platform.get().init();
        AudioMaster.init();

        Keyboard keyboard = new GLFWKeyboard();
        Mouse mouse = new GLFWMouse();
        InputDeviceLocator.provideKeyboard(keyboard);
        InputDeviceLocator.provideMouse(mouse);
        InputDeviceLocator.provideVirtualButton(new GLFWVirtualButtons(new HashMap<String, Integer>() {{
            put(Buttons.P1_LEFT,        GLFW.GLFW_KEY_A);
            put(Buttons.P1_RIGHT,       GLFW.GLFW_KEY_D);
            put(Buttons.P1_SPEED,       GLFW.GLFW_KEY_LEFT_ALT);
            put(Buttons.P1_LOOK_LEFT,   GLFW.GLFW_KEY_Q);
            put(Buttons.P1_LOOK_RIGHT,  GLFW.GLFW_KEY_E);
            put(Buttons.P2_LEFT,        GLFW.GLFW_KEY_LEFT);
            put(Buttons.P2_RIGHT,       GLFW.GLFW_KEY_RIGHT);
            put(Buttons.P2_SPEED,       GLFW.GLFW_KEY_SPACE);
            put(Buttons.P2_LOOK_LEFT,   GLFW.GLFW_KEY_N);
            put(Buttons.P2_LOOK_RIGHT,  GLFW.GLFW_KEY_M);
        }}));

        int w = Platform.get().getMonitors()[0].getWidth();
        int h = Platform.get().getMonitors()[0].getHeight();

        // TODO: use frame buffers instead of two windows
        IView player1View = View.create(scheduler, 0, 0, w / 2, h, new IView.Config(IView.ViewType.RENDER_VIEW, 4, RGBA.BLACK), "Player1", window -> {
            window.setKeyListener(keyboard);
            window.setPointerListener(mouse);
        });

        IView player2View = View.create(scheduler, w / 2, 0, w / 2, h, new IView.Config(IView.ViewType.RENDER_VIEW, 4, RGBA.BLACK), "Player2", window -> {
            window.setKeyListener(keyboard);
            window.setPointerListener(mouse);
        });

        scheduler.run(time -> {
            currentScene = new Scene();

            IRenderManager renderManager = currentScene.getRenderManager();

            // cameras
            ICamera player1Camera = new Camera();
            ICamera player2Camera = new Camera(Vec3.ZERO, Vec3.Z);
            renderManager.addView(player1View);
            renderManager.setCamera(player1View, player1Camera);
            renderManager.addView(player2View);
            renderManager.setCamera(player2View, player2Camera);

            // lights
            ILight mainLight = new DirectionalLight(new Vec3(-0.5, -0.5, -0.5f), RGB.GRAY50, RGB.GRAY50);
            ILight light1 = new SpotLight(Vec3.ZERO, RGB.YELLOW, RGB.YELLOW, Vec3.Z, 30, 1f);
            ILight light2 = new SpotLight(Vec3.ZERO, RGB.YELLOW, RGB.YELLOW, Vec3.Z, 30, 1f);
            renderManager.addLight(mainLight);
            renderManager.addLight(light1);
            renderManager.addLight(light2);

            // meshes
            IGPUImage t = null;
            try {
                t = IGPUImage.read(LightCycle.class.getResource("/textures/floor.jpg"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            IMaterial textureMaterial = new ShadedMaterial(RGB.BLACK, RGB.WHITE, RGB.WHITE, RGB.WHITE, 10, 1, 0.8f, t);
            IMesh groundMesh = createGroundPlane(textureMaterial, 1000);

            final URL objSphere = getClass().getResource("/sphere.obj");
            final List<IMesh> meshesSphere = new ArrayList<>();
            try {
                new ObjReader(objSphere).getMeshes().forEach(meshesSphere::add);
            } catch (IOException e) {
                e.printStackTrace();
            }
            final List<IMesh> mergedSphere = MeshUtilities.mergeMeshes(meshesSphere);
            IMesh sphere = mergedSphere.get(0);

            final URL obj = getClass().getResource("/lightcycle/HQ_Moviecycle.obj");
            final List<IMesh> meshes = new ArrayList<>();
            try {
                new ObjReader(obj).getMeshes().forEach(meshes::add);
            } catch (IOException e) {
                e.printStackTrace();
            }
            final List<IMesh> lightCycle1 = MeshUtilities.mergeMeshes(meshes);
            final List<IMesh> lightCycle2 = lightCycle1.stream().map(IMesh::createInstance).collect(Collectors.toList());

            renderManager.addMesh(groundMesh);
            lightCycle1.forEach(renderManager::addMesh);
            lightCycle2.forEach(renderManager::addMesh);
            renderManager.addMesh(sphere);

            //Ground
            GameObject ground = currentScene.createGameObject();
            ground.getTransform().setLocal(Mat4.multiply(Mat4.translate(0,-1,0), Mat4.rotate(-90,1,0,0)));
            Mesh groundMeshComp = ground.addComponent(Mesh.class);
            groundMeshComp.setMesh(groundMesh);

            //powerUp
            GameObject powerup = currentScene.createGameObject();
            powerup.getTransform().setLocal(Mat4.translate(0, 0, 0));
            powerup.addComponent(Mesh.class).setMesh(sphere);
            powerup.addComponent(PowerUpBehaviour.class);
            powerup.addComponent(BoxCollider.class).setTrigger(true);//.setBoundingBox(sphere.getBounds());

            // player 1
            GameObject player1 = currentScene.createGameObject();
            player1.getTransform().setLocal(Mat4.translate(0, 0, -50));
            PlayerBehaviour player1Behaviour = player1.addComponent(PlayerBehaviour.class);
            player1Behaviour.setButtons(Buttons.P1_LEFT, Buttons.P1_RIGHT, Buttons.P1_SPEED, "wall_green");

            // player 1 lightCycle1
            GameObject player1Vehicle = currentScene.createGameObject(player1.transform);
            
            MeshGroup player1VehicleMeshGroup = player1Vehicle.addComponent(MeshGroup.class);
            player1VehicleMeshGroup.setMeshes(lightCycle1);
            LightCycleBehaviour player1VehicleBehaviour = player1Vehicle.addComponent(LightCycleBehaviour.class);
            player1VehicleBehaviour.setPlayerBehaviour(player1Behaviour);
            player1Vehicle.addComponent(BoxCollider.class);//.setBoundingBox(player1VehicleMeshGroup.getBounds());
            float maxExtent = Math.max(player1VehicleMeshGroup.getBounds().getExtentX(), Math.max(player1VehicleMeshGroup.getBounds().getExtentY(), player1VehicleMeshGroup.getBounds().getExtentZ()));
            player1Vehicle.getTransform().setLocal(Mat4.multiply(Mat4.translate(0, -0.5f, 1.1f), Mat4.scale(1f / maxExtent), Mat4.rotate(90,0,0,1), Mat4.rotate(90,0,1,0), Mat4.rotate(180,0,0,1)));

            // player 1 camera follow
            GameObject player1CameraFollow = currentScene.createGameObject();
            player1CameraFollow.addComponent(FollowBehaviour.class).setTarget(player1.getTransform());

            // player 1 camera wrapper
            GameObject player1CameraWrapper = currentScene.createGameObject(player1CameraFollow.getTransform());
            player1CameraWrapper.addComponent(LookAroundBehaviour.class).setButtons(Buttons.P1_LOOK_LEFT, Buttons.P1_LOOK_RIGHT);

            // player 1 camera
            GameObject player1CameraObject = currentScene.createGameObject(player1CameraWrapper.getTransform());
            player1CameraObject.getTransform().setLocal(Mat4.multiply(Mat4.translate(0,0.85f,-5f), Mat4.rotate(-5,1,0,0)));
            component.Camera player1CameraComponent = player1CameraObject.addComponent(component.Camera.class);
            player1CameraComponent.setCamera(player1Camera);
            player1CameraComponent.setTargetView(player1View);
            player1CameraObject.addComponent(Light.class).setLight(light1);

            // player 2
            GameObject player2 = currentScene.createGameObject();
            player2.getTransform().setLocal(Mat4.multiply(Mat4.translate(0, 0, 50), Mat4.rotate(180, 0, 1, 0)));
            PlayerBehaviour player2Behaviour = player2.addComponent(PlayerBehaviour.class);
            player2Behaviour.setButtons(Buttons.P2_LEFT, Buttons.P2_RIGHT, Buttons.P2_SPEED, "wall_yellow");

            // player 2 lightCycle1
            GameObject player2Vehicle = currentScene.createGameObject(player2.transform);
            MeshGroup player2VehicleMeshGroup = player2Vehicle.addComponent(MeshGroup.class);
            player2VehicleMeshGroup.setMeshes(lightCycle2);
            LightCycleBehaviour player2VehicleBehaviour = player2Vehicle.addComponent(LightCycleBehaviour.class);
            player2VehicleBehaviour.setPlayerBehaviour(player2Behaviour);
            player2Vehicle.addComponent(BoxCollider.class);//.setBoundingBox(player2VehicleMeshGroup.getBounds());
            maxExtent = Math.max(player2VehicleMeshGroup.getBounds().getExtentX(), Math.max(player2VehicleMeshGroup.getBounds().getExtentY(), player2VehicleMeshGroup.getBounds().getExtentZ()));
            player2Vehicle.getTransform().setLocal(Mat4.multiply(Mat4.translate(0, -0.5f, 1.1f), Mat4.multiply(Mat4.scale(1f / maxExtent), Mat4.rotate(90,0,0,1), Mat4.rotate(90,0,1,0),Mat4.rotate(180,0,0,1))));

            // player 1 camera follow
            GameObject player2CameraFollow = currentScene.createGameObject();
            player2CameraFollow.addComponent(FollowBehaviour.class).setTarget(player2.getTransform());

            // player 1 camera wrapper
            GameObject player2CameraWrapper = currentScene.createGameObject(player2CameraFollow.getTransform());
            player2CameraWrapper.addComponent(LookAroundBehaviour.class).setButtons(Buttons.P2_LOOK_LEFT, Buttons.P2_LOOK_RIGHT);

            // player 2 camera
            GameObject player2CameraObject = currentScene.createGameObject(player2CameraWrapper.getTransform());
            player2CameraObject.getTransform().setLocal(Mat4.multiply(Mat4.translate(0,0.85f,-5f), Mat4.rotate(-5,1,0,0)));
            component.Camera player2CameraComponent = player2CameraObject.addComponent(component.Camera.class);
            player2CameraComponent.setCamera(player2Camera);
            player2CameraComponent.setTargetView(player2View);
            player2CameraObject.addComponent(Light.class).setLight(light2);

            // attach listeners to game objects
            player1CameraObject.addComponent(AudioListenerComoponent.class).setAudioListener(currentScene.getAudioController().getAudioListener(0));
            player2CameraObject.addComponent(AudioListenerComoponent.class).setAudioListener(currentScene.getAudioController().getAudioListener(1));

            // scene background audio
            GameObject hambbe = currentScene.createGameObject();
            AudioSourceComponent hambbeAudioSourceComponent = hambbe.addComponent(AudioSourceComponent.class);
            hambbeAudioSourceComponent.setAudioSource(currentScene.getAudioController().createAudioSources());
            hambbeAudioSourceComponent.setLooping(true);
            hambbeAudioSourceComponent.play(AudioMaster.createAudioBufferFromWAV(LightCycle.class.getResource("/daft_punk-the_game_has_changed.wav")));

            // Skybox
            GameObject skybox = currentScene.createGameObject();
            IMesh[] skyboxMeshes = createSkyboxMeshes(10000f);
            for(IMesh currentMesh : skyboxMeshes) {
                renderManager.addMesh(currentMesh);
                skybox.addComponent(Mesh.class).setMesh(currentMesh);
            }

            // init scenes
            currentScene.addSystem(ProcessType.Update, new BehaviourSystem());
            currentScene.addSystem(ProcessType.Update, new TransformSystem());
            currentScene.addSystem(ProcessType.Update, new CollisionSystem());
            currentScene.addSystem(ProcessType.Update, new AudioSystem());

            currentScene.addSystem(ProcessType.Draw, new RenderSystem(renderManager));
        });

        Thread.sleep(100);

        Platform.get().run();
    }

    public static void main(String[] args) throws InterruptedException, IOException, RenderCommandException {
        LightCycle ls = new LightCycle();
        ls.run();
    }
    
	public static IMesh createGroundPlane(IMaterial material, float extent) {
		float e = extent;
		float z = 0;
		float[] v = { -e, -e, z, e, -e, z, e, e, z, -e, -e, z, e, e, z, -e, e, z };
		float[] n = MeshUtilities.UNIT_QUAD_NORMALS;
		float[] m = MeshUtilities.UNIT_QUAD_TEX_COORDS;
		IGeometry g = requireTexCoords(material) ? DefaultGeometry.createVNM(v, n, m) :  DefaultGeometry.createVN(v, n);
		return new DefaultMesh(Primitive.TRIANGLES, material, g, Queue.TRANSPARENCY,  Flag.DONT_CAST_SHADOW);
	}
	
	private static boolean requireTexCoords(IMaterial material) {
		return ArrayUtilities.contains(material.getRequiredAttributes(), IGeometry.COLOR_MAP_ARRAY);
	}

    private static IMesh[] createSkyboxMeshes(float scale) {
        // Bottom and top are skipped for the moment, can't see them anyways
        final float[][] vertices = {
                { -scale, scale, scale, scale, scale, scale, scale, -scale, scale, -scale, scale, scale, scale, -scale, scale, -scale, -scale, scale }, // Front
                { scale, -scale, -scale, scale, -scale, scale, scale, scale, scale, scale, -scale, -scale, scale, scale, scale, scale, scale, -scale }, // Left
                { -scale, -scale, -scale, scale, -scale, -scale, scale, scale, -scale, -scale, -scale, -scale, scale, scale, -scale, -scale, scale, -scale}, // Back
                { -scale, -scale, scale, -scale, -scale, -scale, -scale, scale, -scale, -scale, -scale, scale, -scale, scale, -scale, -scale, scale, scale }, // Right
        };
        final String[] textureNames = {
                "/textures/front.png",
                "/textures/left.png",
                "/textures/back.png",
                "/textures/right.png",
        };
        final float[][] texCoords = {
                { 1, 1, 0, 1, 0, 0, 1, 1, 0, 0, 1, 0 },
                { 0, 0, 1, 0, 1, 1, 0, 0, 1, 1, 0, 1 },
                { 0, 0, 1, 0, 1, 1, 0, 0, 1, 1, 0, 1 },
                { 0, 0, 1, 0, 1, 1, 0, 0, 1, 1, 0, 1 },
        };

        IMesh[] result = new IMesh[vertices.length];
        for(int faceIndex = 0; faceIndex < vertices.length; faceIndex++) {
            IGPUImage currentTexture = null;
            try {
                currentTexture = IGPUImage.read(LightCycle.class.getResource(textureNames[faceIndex]));
            } catch (IOException e) {
                e.printStackTrace();
            }

            IGeometry currentGeometry = DefaultGeometry.createVNM(
                    vertices[faceIndex],
                    IGeometry.createNormals(vertices[faceIndex]),
                    texCoords[faceIndex]);

            result[faceIndex] = new DefaultMesh(IMesh.Primitive.TRIANGLES,
                    new ColorMapMaterial(currentTexture),
                    currentGeometry);
        }

        return result;
    }

}