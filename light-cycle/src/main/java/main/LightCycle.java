package main;

import audio.AudioController;
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
import ch.fhnw.ether.scene.mesh.IMesh;
import ch.fhnw.ether.scene.mesh.MeshUtilities;
import ch.fhnw.ether.scene.mesh.material.IMaterial;
import ch.fhnw.ether.scene.mesh.material.ShadedMaterial;
import ch.fhnw.ether.view.IView;
import ch.fhnw.util.color.RGB;
import ch.fhnw.util.color.RGBA;
import ch.fhnw.util.math.Mat4;
import ch.fhnw.util.math.Vec3;
import component.Light;
import component.Mesh;
import component.audio.AudioListenerComoponent;
import component.audio.AudioSourceComponent;
import component.behaviour.FollowBehaviour;
import component.behaviour.LightCycleBehaviour;
import component.behaviour.PlayerBehaviour;
import component.behaviour.PlayerBehaviour2;
import component.collider.BoxCollider;
import gameobject.GameObject;
import inputdevice.*;
import inputdevice.Input.Buttons;
import org.lwjgl.glfw.GLFW;
import render.OffscreenView;
import render.View;
import scene.ProcessType;
import scene.Scene;
import system.*;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
            put(Buttons.FORWARD, GLFW.GLFW_KEY_UP);
            put(Buttons.BACKWARD, GLFW.GLFW_KEY_DOWN);
            put(Buttons.LEFT, GLFW.GLFW_KEY_LEFT);
            put(Buttons.RIGHT, GLFW.GLFW_KEY_RIGHT);
            put(Buttons.SPEED, GLFW.GLFW_KEY_SPACE);
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
                t = IGPUImage.read(LightCycle.class.getResource("/textures/space1.jpg"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            IMaterial textureMaterial = new ShadedMaterial(RGB.BLACK, RGB.WHITE, RGB.WHITE, RGB.WHITE, 10, 1, 1f, t);
            IMesh groundMesh = MeshUtilities.createGroundPlane(textureMaterial, 1000);

            final URL obj = getClass().getResource("/lightcycle/HQ_Moviecycle.obj");
            final List<IMesh> meshes = new ArrayList<>();
            try {
                new ObjReader(obj).getMeshes().forEach(meshes::add);
            } catch (IOException e) {
                e.printStackTrace();
            }
            final List<IMesh> merged = MeshUtilities.mergeMeshes(meshes);
            IMesh lightCycle1 = merged.get(0);
            IMesh lightCycle2 = lightCycle1.createInstance();

            renderManager.addMesh(groundMesh);
            renderManager.addMesh(lightCycle1);
            renderManager.addMesh(lightCycle2);

            //Ground
            GameObject ground = currentScene.createGameObject();
            ground.getTransform().setLocal(Mat4.multiply(Mat4.translate(0,-1,0), Mat4.rotate(-90,1,0,0)));
            Mesh groundMeshComp = ground.addComponent(Mesh.class);
            groundMeshComp.setMesh(groundMesh);

            // player 1
            GameObject player1 = currentScene.createGameObject();
            player1.getTransform().setLocal(Mat4.translate(0, 0, -20));
            player1.addComponent(PlayerBehaviour.class);

            // player 1 lightCycle1
            float maxExtent = Math.max(lightCycle1.getBounds().getExtentX(), Math.max(lightCycle1.getBounds().getExtentY(), lightCycle1.getBounds().getExtentZ()));
            GameObject player1Vehicle = currentScene.createGameObject(player1.transform);
            player1Vehicle.getTransform().setLocal(Mat4.multiply(Mat4.scale(1f / maxExtent), Mat4.rotate(90,0,0,1), Mat4.rotate(90,0,1,0)));
            player1Vehicle.addComponent(Mesh.class).setMesh(lightCycle1);
            player1Vehicle.addComponent(LightCycleBehaviour.class);
            player1Vehicle.addComponent(BoxCollider.class).setBoundingBox(lightCycle1.getBounds());

            // player 1 camera wrapper
            GameObject playerCameraWrapper = currentScene.createGameObject();
            playerCameraWrapper.addComponent(FollowBehaviour.class).setTarget(player1.getTransform());

            // player 1 camera
            GameObject player1CameraObject = currentScene.createGameObject(playerCameraWrapper.getTransform());
            player1CameraObject.getTransform().setLocal(Mat4.multiply(Mat4.translate(0,1,-7.5f), Mat4.rotate(-5,1,0,0)));
            component.Camera player1CameraComponent = player1CameraObject.addComponent(component.Camera.class);
            player1CameraComponent.setCamera(player1Camera);
            player1CameraComponent.setTargetView(player1View);
            player1CameraObject.addComponent(Light.class).setLight(light1);

            // player 2
            GameObject player2 = currentScene.createGameObject();
            player2.getTransform().setLocal(Mat4.multiply(Mat4.translate(0, 0, 20), Mat4.rotate(180, 0, 1, 0)));
            player2.addComponent(PlayerBehaviour2.class);

            // player 2 lightCycle1
            maxExtent = Math.max(lightCycle2.getBounds().getExtentX(), Math.max(lightCycle2.getBounds().getExtentY(), lightCycle2.getBounds().getExtentZ()));
            GameObject player2Vehicle = currentScene.createGameObject(player2.transform);
            player2Vehicle.getTransform().setLocal(Mat4.multiply(Mat4.scale(1f / maxExtent), Mat4.rotate(90,0,0,1), Mat4.rotate(90,0,1,0)));
            player2Vehicle.addComponent(Mesh.class).setMesh(lightCycle2);
            player2Vehicle.addComponent(LightCycleBehaviour.class);
            player2Vehicle.addComponent(BoxCollider.class).setBoundingBox(lightCycle2.getBounds());

            // player 2 camera wrapper
            GameObject player2CameraWrapper = currentScene.createGameObject();
            player2CameraWrapper.addComponent(FollowBehaviour.class).setTarget(player2.getTransform());

            // player 2 camera
            GameObject player2CameraObject = currentScene.createGameObject(player2CameraWrapper.getTransform());
            player2CameraObject.getTransform().setLocal(Mat4.multiply(Mat4.translate(0,1,-7.5f), Mat4.rotate(-5,1,0,0)));
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

            // init scenes
            currentScene.addSystem(ProcessType.Update, new BehaviourSystem());
            currentScene.addSystem(ProcessType.Update, new TransformSystem());
            currentScene.addSystem(ProcessType.Update, new CollisionSystem());
            currentScene.addSystem(ProcessType.Update, new AudioSystem());

            currentScene.addSystem(ProcessType.Draw, new RenderSystem(renderManager));
        });

        Platform.get().run();
    }

    public static void main(String[] args) throws InterruptedException, IOException, RenderCommandException {
        LightCycle ls = new LightCycle();
        ls.run();
    }

}
