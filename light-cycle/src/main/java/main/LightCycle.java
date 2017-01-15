package main;

import audio.AudioMaster;
import ch.fhnw.ether.controller.event.DefaultEventScheduler;
import ch.fhnw.ether.controller.event.IEventScheduler;
import ch.fhnw.ether.formats.obj.ObjReader;
import ch.fhnw.ether.image.IGPUImage;
import ch.fhnw.ether.media.RenderCommandException;
import ch.fhnw.ether.platform.Platform;
import ch.fhnw.ether.scene.camera.Camera;
import ch.fhnw.ether.scene.camera.ICamera;
import ch.fhnw.ether.scene.light.DirectionalLight;
import ch.fhnw.ether.scene.light.ILight;
import ch.fhnw.ether.scene.light.SpotLight;
import ch.fhnw.ether.scene.mesh.DefaultMesh;
import ch.fhnw.ether.scene.mesh.IMesh;
import ch.fhnw.ether.scene.mesh.IMesh.Flag;
import ch.fhnw.ether.scene.mesh.IMesh.Primitive;
import ch.fhnw.ether.scene.mesh.IMesh.Queue;
import ch.fhnw.ether.scene.mesh.MeshUtilities;
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
import ch.fhnw.util.math.Vec2;
import ch.fhnw.util.math.Vec3;
import component.Light;
import component.Mesh;
import component.MeshGroup;
import component.audio.AudioListenerComoponent;
import component.audio.AudioSourceComponent;
import component.behaviour.*;
import component.behaviour.ai.FilmingAI;
import component.collider.BoxCollider;
import component.powerup.DestroyOtherTrailPowerUp;
import component.powerup.DestroyTrailPowerUp;
import component.powerup.SpeedPowerUp;
import gameobject.GameObject;
import inputdevice.*;
import inputdevice.Input.Buttons;
import org.lwjgl.glfw.GLFW;
import render.View;
import render.mesh.material.PanelMaterial;
import render.mesh.material.SkyboxMaterial;
import scene.ProcessType;
import scene.Scene;
import system.*;

import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

public class LightCycle {

    private final float fps = 60;
    private final int groundSize = 500;
    private final int playAreaExtends = 200;
    
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

    private List<IMesh> loadMeshList(String resource) {
        final URL obj = getClass().getResource(resource);
        final List<IMesh> meshes = new ArrayList<>();
        try {
            new ObjReader(obj).getMeshes().forEach(meshes::add);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return MeshUtilities.mergeMeshes(meshes);

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

            // init scene
            currentScene.addSystem(ProcessType.Update, new BehaviourSystem());
            currentScene.addSystem(ProcessType.Update, new TransformSystem());
            currentScene.addSystem(ProcessType.Update, new CollisionSystem());
            currentScene.addSystem(ProcessType.Update, new AudioSystem());
            currentScene.addSystem(ProcessType.Update, new GamePlaySystem());
            currentScene.addSystem(ProcessType.Draw, new RenderSystem());

            // cameras
            ICamera player1Camera = new Camera();
            ICamera player2Camera = new Camera(Vec3.ZERO, Vec3.Z);

            // lights
            ILight mainLight1 = new DirectionalLight(new Vec3(0.2, -1, -0.2), RGB.WHITE, RGB.WHITE);
            ILight mainLight2 = new DirectionalLight(new Vec3(-0.2, -1, 0.2), RGB.WHITE, RGB.WHITE);
            //ILight light1 = new SpotLight(Vec3.ZERO, RGB.YELLOW, RGB.YELLOW, Vec3.Z, 30, 1f);
            //ILight light2 = new SpotLight(Vec3.ZERO, RGB.YELLOW, RGB.YELLOW, Vec3.Z, 30, 1f);
            ILight light3 = new SpotLight(Vec3.ZERO, RGB.YELLOW, RGB.YELLOW, Vec3.Z, 30, 1f);
            ILight light4 = new SpotLight(Vec3.ZERO, RGB.YELLOW, RGB.YELLOW, Vec3.Z, 30, 1f);
            currentScene.getRenderManager().addLight(mainLight1);
            currentScene.getRenderManager().addLight(mainLight2);

            // meshes
            final List<IMesh> lightCycle1 = loadMeshList("/lightcycle/HQ_Moviecycle.obj");

            final List<IMesh> lightCycle2 = lightCycle1.stream().map(IMesh::createInstance).collect(Collectors.toList());
            final IMesh sphere = loadMeshList("/sphere.obj").get(0);
            IGPUImage t2 = null;
            try {
                t2 = IGPUImage.read(LightCycle.class.getResource("/textures/glow.png"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            final IMesh boostPower1 = MeshUtilities.createQuad(new ShadedMaterial(RGB.WHITE, RGB.WHITE, RGB.WHITE, RGB.WHITE, 10, 10, 1, t2), Queue.TRANSPARENCY, EnumSet.of(Flag.DONT_CAST_SHADOW));//loadMeshList("/boostPower.obj").get(0);
            final IMesh boostPower2 = boostPower1.createInstance();
            final List<IMesh> player1FakeCameraMesh = loadMeshList("/camera.obj");
            for (IMesh a : player1FakeCameraMesh) {
                java.lang.System.out.println(Arrays.toString(a.getMaterial().getData()));
            }
            final List<IMesh> player2FakeCameraMesh = player1FakeCameraMesh.stream().map(IMesh::createInstance).collect(Collectors.toList());

            //Ground
            GameObject ground = createGround(currentScene);

            // Playing area bounding walls
            GameObject boundingWalls = createBoundingWalls(currentScene);

            // player 1
            GameObject player1 = currentScene.createGameObject();
            //player1.getTransform().setLocal(Mat4.translate(0.4f, 0, -50));
            player1.getTransform().setLocal(Mat4.translate(0, 0, -50));
            PlayerBehaviour player1Behaviour = player1.addComponent(PlayerBehaviour.class);
            player1Behaviour.setName("Player 1");
            player1Behaviour.setButtons(Buttons.P1_LEFT, Buttons.P1_RIGHT, Buttons.P1_SPEED);
            player1Behaviour.setTrailMaterial("wall_green");

            // player 1 fake camera wrapper
            GameObject player1FakeCameraWrapper = currentScene.createGameObject();
            player1FakeCameraWrapper.getTransform().setLocal(Mat4.translate(0,10f,10f));
            player1FakeCameraWrapper.addComponent(FilmingAI.class).setTarget(player1.getTransform());

            // player 1 fake camera
            GameObject player1FakeCamera = currentScene.createGameObject(player1FakeCameraWrapper.getTransform());
            player1FakeCamera.getTransform().setLocal(Mat4.rotate(50, 1, 0, 0));
            player1FakeCamera.addComponent(MeshGroup.class).setMeshes(player1FakeCameraMesh);
            player1FakeCamera.addComponent(Light.class).setLight(light3);

            // player 1 Boostpower
            GameObject player1Boostpower = currentScene.createGameObject(player1.getTransform());
            player1Boostpower.getTransform().setLocal(Mat4.multiply(Mat4.translate(0, -0.49999f, 1), Mat4.scale(0.6f, 1, 1), Mat4.rotate(-90, 1, 0, 0)));
            player1Boostpower.addComponent(Mesh.class).setMesh(boostPower1);

            player1Behaviour.setBoostPowerObject(player1Boostpower);

            // player 1 lightCycle1
            GameObject player1Vehicle = currentScene.createGameObject(player1.transform);
            
            MeshGroup player1VehicleMeshGroup = player1Vehicle.addComponent(MeshGroup.class);
            player1VehicleMeshGroup.setMeshes(lightCycle1);
            LightCycleBehaviour player1VehicleBehaviour = player1Vehicle.addComponent(LightCycleBehaviour.class);
            player1VehicleBehaviour.setPlayerBehaviour(player1Behaviour);
            player1Vehicle.addComponent(BoxCollider.class);
            float maxExtent = Math.max(player1VehicleMeshGroup.getBounds().getExtentX(), Math.max(player1VehicleMeshGroup.getBounds().getExtentY(), player1VehicleMeshGroup.getBounds().getExtentZ()));
            float yOffset = -0.53f;
            player1Vehicle.getTransform().setLocal(Mat4.multiply(Mat4.translate(0, yOffset, 1.1f), Mat4.scale(1f / maxExtent), Mat4.rotate(90,0,0,1), Mat4.rotate(90,0,1,0), Mat4.rotate(180,0,0,1)));

            int nPower = 21;
            GameObject[] powerup = new GameObject[nPower];
            IMesh[] spheres = new IMesh[nPower];
            //add nPower powerups
            for(int i = 0; i < nPower/3; i++) {
            
                spheres[i] = sphere.createInstance();
                powerup[i] = currentScene.createGameObject();
                Random r = new Random();
                int rx = r.nextInt(2 * playAreaExtends) - playAreaExtends;
                int ry = r.nextInt(2 * playAreaExtends) - playAreaExtends;

                powerup[i].getTransform().setLocal(Mat4.translate(rx, 0, ry));
                powerup[i].addComponent(Mesh.class).setMesh(spheres[i]);
                powerup[i].addComponent(PowerUpBehaviour.class);
                powerup[i].addComponent(SpeedPowerUp.class).setBoostTime(4);
                powerup[i].addComponent(BoxCollider.class).setTrigger(true);
            }
            for(int i = nPower / 3; i < 2*(nPower/3); i++) {
                spheres[i] = sphere.createInstance();
                powerup[i] = currentScene.createGameObject();
                Random r = new Random();
                int rx = r.nextInt(2 * playAreaExtends) - playAreaExtends;
                int ry = r.nextInt(2 * playAreaExtends) - playAreaExtends;

                powerup[i].getTransform().setLocal(Mat4.translate(rx, 0, ry));
                powerup[i].addComponent(Mesh.class).setMesh(spheres[i]);
                powerup[i].addComponent(PowerUpBehaviour.class);
                powerup[i].addComponent(DestroyTrailPowerUp.class);
                powerup[i].addComponent(BoxCollider.class).setTrigger(true);
            }
            for(int i = 2*(nPower/3); i < nPower; i++) {
                spheres[i] = sphere.createInstance();
                powerup[i] = currentScene.createGameObject();
                Random r = new Random();
                int rx = r.nextInt(2 * playAreaExtends) - playAreaExtends;
                int ry = r.nextInt(2 * playAreaExtends) - playAreaExtends;

                powerup[i].getTransform().setLocal(Mat4.translate(rx, 0, ry));
                powerup[i].addComponent(Mesh.class).setMesh(spheres[i]);
                powerup[i].addComponent(PowerUpBehaviour.class);
                powerup[i].addComponent(DestroyOtherTrailPowerUp.class);
                powerup[i].addComponent(BoxCollider.class).setTrigger(true);
            }
            
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
            player1CameraComponent.setState(player1View, player1Camera);
            //player1CameraObject.addComponent(Light.class).setLight(light1);

            // player 2
            GameObject player2 = currentScene.createGameObject();
            player2.getTransform().setLocal(Mat4.multiply(Mat4.translate(0, 0, 50), Mat4.rotate(180, 0, 1, 0)));
            PlayerBehaviour player2Behaviour = player2.addComponent(PlayerBehaviour.class);
            player2Behaviour.setName("Player 2");
            player2Behaviour.setButtons(Buttons.P2_LEFT, Buttons.P2_RIGHT, Buttons.P2_SPEED);
            player2Behaviour.setTrailMaterial("wall_yellow");

            // player 2 Boostpower
            GameObject player2Boostpower = currentScene.createGameObject(player2.getTransform());
            player2Boostpower.getTransform().setLocal(Mat4.multiply(Mat4.translate(0, -0.49999f, 1), Mat4.scale(0.6f, 1, 1), Mat4.rotate(-90, 1, 0, 0)));
            player2Boostpower.addComponent(Mesh.class).setMesh(boostPower2);

            player2Behaviour.setBoostPowerObject(player2Boostpower);

            // player 2 lightCycle1
            GameObject player2Vehicle = currentScene.createGameObject(player2.transform);
            MeshGroup player2VehicleMeshGroup = player2Vehicle.addComponent(MeshGroup.class);
            player2VehicleMeshGroup.setMeshes(lightCycle2);
            LightCycleBehaviour player2VehicleBehaviour = player2Vehicle.addComponent(LightCycleBehaviour.class);
            player2VehicleBehaviour.setPlayerBehaviour(player2Behaviour);
            player2Vehicle.addComponent(BoxCollider.class);
            maxExtent = Math.max(player2VehicleMeshGroup.getBounds().getExtentX(), Math.max(player2VehicleMeshGroup.getBounds().getExtentY(), player2VehicleMeshGroup.getBounds().getExtentZ()));
            yOffset = -0.53f;
            player2Vehicle.getTransform().setLocal(Mat4.multiply(Mat4.translate(0, yOffset, 1.1f), Mat4.multiply(Mat4.scale(1f / maxExtent), Mat4.rotate(90,0,0,1), Mat4.rotate(90,0,1,0),Mat4.rotate(180,0,0,1))));

            // player 2 camera follow
            GameObject player2CameraFollow = currentScene.createGameObject();
            player2CameraFollow.addComponent(FollowBehaviour.class).setTarget(player2.getTransform());

            // player 2 camera wrapper
            GameObject player2CameraWrapper = currentScene.createGameObject(player2CameraFollow.getTransform());
            player2CameraWrapper.addComponent(LookAroundBehaviour.class).setButtons(Buttons.P2_LOOK_LEFT, Buttons.P2_LOOK_RIGHT);

            // player 2 camera
            GameObject player2CameraObject = currentScene.createGameObject(player2CameraWrapper.getTransform());
            player2CameraObject.getTransform().setLocal(Mat4.multiply(Mat4.translate(0,0.85f,-5f), Mat4.rotate(-5,1,0,0)));
            component.Camera player2CameraComponent = player2CameraObject.addComponent(component.Camera.class);
            player2CameraComponent.setState(player2View, player2Camera);
            //player2CameraObject.addComponent(Light.class).setLight(light2);

            // player 2 fake camera wrapper
            GameObject player2FakeCameraWrapper = currentScene.createGameObject();
            player2FakeCameraWrapper.getTransform().setLocal(Mat4.translate(0,10f,-10f));
            player2FakeCameraWrapper.addComponent(FilmingAI.class).setTarget(player2.getTransform());

            // player 2fake camera
            GameObject player2FakeCamera = currentScene.createGameObject(player2FakeCameraWrapper.getTransform());
            player2FakeCamera.getTransform().setLocal(Mat4.rotate(50, 1, 0, 0));
            player2FakeCamera.addComponent(MeshGroup.class).setMeshes(player2FakeCameraMesh);
            player2FakeCamera.addComponent(Light.class).setLight(light4);

            // attach listeners to game objects
            player1CameraObject.addComponent(AudioListenerComoponent.class).setAudioListener(currentScene.getAudioController().getAudioListener(0));
            player2CameraObject.addComponent(AudioListenerComoponent.class).setAudioListener(currentScene.getAudioController().getAudioListener(1));
            
            // scene background audio
            GameObject hambbe = currentScene.createGameObject();
            AudioSourceComponent hambbeAudioSourceComponent = hambbe.addComponent(AudioSourceComponent.class);
            hambbeAudioSourceComponent.setAudioSource(currentScene.getAudioController().createAudioSources());
            hambbeAudioSourceComponent.setLooping(true);

            // Skybox
            GameObject skybox = currentScene.createGameObject();
            skybox.addComponent(MeshGroup.class).setMeshes(createSkyboxMeshes(500f));
        });

        Thread.sleep(100);

        Platform.get().run();
    }

    private GameObject createBoundingWalls(Scene currentScene) {
        GameObject result = currentScene.createGameObject();

        IGPUImage wallTexture = null;
        try {
            wallTexture = IGPUImage.read(LightCycle.class.getResource("/textures/boundary.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        IMaterial material = new ColorMapMaterial(wallTexture);

        final float middleY = -0.5f;
        final int sideCount = 50;
        final int layers = 1 * 2;

        List<IMesh> rawMeshes = new ArrayList<>();

        for(int layer = 0; layer < layers; layer++) {
            IMesh newMesh = createCylinder(sideCount, material);
            newMesh.setPosition(new Vec3(0, 16f * layer, 0));
            rawMeshes.add(newMesh);
        }

        MeshGroup meshGroup = result.addComponent(MeshGroup.class);
        meshGroup.setMeshes(rawMeshes);
        meshGroup.getTransform().scale(new Vec3(playAreaExtends, 16, playAreaExtends));
        meshGroup.getTransform().translate(0, -(layers * 16 / 2f) + middleY, 0);

        return result;
    }

    private IMesh createCylinder(int n, IMaterial material) {
        float[] v = new float[6 * 3 * n];
        float[] m = new float[6 * 2 * n];
        final double fullCircle = Math.PI * 2.0;
        final double step = fullCircle / n;

        for(int i = 0; i < n; i++) {
            double angle = step * i;
            double prevAngle = step * (i - 1);

            float x = (float)Math.sin(angle);
            float z = (float)Math.cos(angle);
            float prevX = (float)Math.sin(prevAngle);
            float prevZ = (float)Math.cos(prevAngle);

            int offset = 0;

            v[18 * i + (offset++)] = x;
            v[18 * i + (offset++)] = 0;
            v[18 * i + (offset++)] = z;

            v[18 * i + (offset++)] = prevX;
            v[18 * i + (offset++)] = 1;
            v[18 * i + (offset++)] = prevZ;

            v[18 * i + (offset++)] = x;
            v[18 * i + (offset++)] = 1;
            v[18 * i + (offset++)] = z;

            v[18 * i + (offset++)] = x;
            v[18 * i + (offset++)] = 0;
            v[18 * i + (offset++)] = z;

            v[18 * i + (offset++)] = prevX;
            v[18 * i + (offset++)] = 0;
            v[18 * i + (offset++)] = prevZ;

            v[18 * i + (offset++)] = prevX;
            v[18 * i + (offset++)] = 1;
            v[18 * i + (offset++)] = prevZ;

            offset = 0;
            m[12 * i + (offset++)] = 0;
            m[12 * i + (offset++)] = 1;
            m[12 * i + (offset++)] = 1;
            m[12 * i + (offset++)] = 0;
            m[12 * i + (offset++)] = 0;
            m[12 * i + (offset++)] = 0;

            m[12 * i + (offset++)] = 0;
            m[12 * i + (offset++)] = 1;
            m[12 * i + (offset++)] = 1;
            m[12 * i + (offset++)] = 1;
            m[12 * i + (offset++)] = 1;
            m[12 * i + (offset++)] = 0;
        }

        IGeometry geometry = DefaultGeometry.createVM(v, m);
        return new DefaultMesh(Primitive.TRIANGLES, material, geometry, Queue.TRANSPARENCY, Flag.DONT_CAST_SHADOW);
    }

    public static void main(String[] args) throws InterruptedException, IOException, RenderCommandException {
        LightCycle ls = new LightCycle();
        ls.run();
    }

    private GameObject createGround(Scene currentScene) {
        // Create material
        IGPUImage groundTexture = null;
        try {
            groundTexture = IGPUImage.read(LightCycle.class.getResource("/textures/floor.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        // TODO: Don't use shaded material?
        IMaterial textureMaterial = new ShadedMaterial(RGB.BLACK, RGB.WHITE, RGB.WHITE, RGB.WHITE, 10, 1, 0.8f, groundTexture);

        // Create meshes
        List<IMesh> groundMeshes = createGroundPlane(textureMaterial, groundSize, 20);

        // Create game object
        GameObject ground = currentScene.createGameObject();
        MeshGroup meshComponent = ground.addComponent(MeshGroup.class);
        meshComponent.setMeshes(groundMeshes);

        return ground;
    }

	public static List<IMesh> createGroundPlane(IMaterial material, float extent, int tileCount) {
		float y = -0.5f;

		// Normals
        final float[] n = {
            0, 1, 0, 0, 1, 0, 0, 1, 0,
            0, 1, 0, 0, 1, 0, 0, 1, 0,
        };

        // Tex coords
        final float[] m = {
            1, 0, 0, 0, 1, 1,
            1, 1, 0, 0, 0, 1,
        };

		// Calculate tile size per dimension
        float tileSize = 2 * extent / tileCount;

        // Create tiles
		List<IMesh> result = new ArrayList<>(tileCount * tileCount);
        for(int tileZ = 0; tileZ < tileCount; tileZ++) {
            for(int tileX = 0; tileX < tileCount; tileX++) {
                // Calculate tile position
                float posX = tileX * tileSize - extent;
                float posZ = tileZ * tileSize - extent;

                // Calculate tile vertices
                float[] v = {
                        posX + tileSize, y, posZ,
                        posX, y, posZ,
                        posX + tileSize, y, posZ + tileSize,

                        posX + tileSize, y, posZ + tileSize,
                        posX, y, posZ,
                        posX, y, posZ + tileSize,
                };

                // Create tile geometry and mesh
                IGeometry tileGeometry = requireTexCoords(material) ? DefaultGeometry.createVNM(v, n, m) :  DefaultGeometry.createVN(v, n);
                result.add(new DefaultMesh(Primitive.TRIANGLES, material, tileGeometry, Queue.TRANSPARENCY,  Flag.DONT_CAST_SHADOW));
            }
        }

		return result;
	}
	
	private static boolean requireTexCoords(IMaterial material) {
		return ArrayUtilities.contains(material.getRequiredAttributes(), IGeometry.COLOR_MAP_ARRAY);
	}

    private static ArrayList<IMesh> createSkyboxMeshes(float scale) {
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

        ArrayList<IMesh> result = new ArrayList<>(vertices.length);
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

            result.add(new DefaultMesh(IMesh.Primitive.TRIANGLES,
                    new SkyboxMaterial(currentTexture),
                    currentGeometry
            ));
        }

        return result;
    }

    public static IMesh createPanel(Vec2 position, Vec2 size, IGPUImage texture, RGBA color) {
        float x0 = position.x;
        float y0 = position.y;
        float x1 = x0 + size.x;
        float y1 = y0 + size.y;
        float[] v = {
                x0, y0, 0,
                x1, y0, 0,
                x1, y1, 0,
                x0, y0, 0,
                x1, y1, 0,
                x0, y1, 0,
        };
        float[] c = RGBA.toArray(Arrays.asList(color, color, color, color, color, color));
        float[] m = MeshUtilities.UNIT_QUAD_TEX_COORDS;
        IGeometry testGeometry = DefaultGeometry.createVCM(v, c, m);
        return new DefaultMesh(Primitive.TRIANGLES, new PanelMaterial(texture), testGeometry, Queue.SCREEN_SPACE_OVERLAY);
    }

}