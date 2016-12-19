package com.michaelaerni.fhnw.comgr;

import ch.fhnw.ether.controller.DefaultController;
import ch.fhnw.ether.controller.IController;
import ch.fhnw.ether.controller.event.IKeyEvent;
import ch.fhnw.ether.formats.IModelReader;
import ch.fhnw.ether.formats.obj.ObjReader;
import ch.fhnw.ether.platform.Platform;
import ch.fhnw.ether.scene.DefaultScene;
import ch.fhnw.ether.scene.IScene;
import ch.fhnw.ether.scene.camera.Camera;
import ch.fhnw.ether.scene.camera.ICamera;
import ch.fhnw.ether.scene.light.DirectionalLight;
import ch.fhnw.ether.scene.mesh.IMesh;
import ch.fhnw.ether.scene.mesh.MeshUtilities;
import ch.fhnw.ether.view.DefaultView;
import ch.fhnw.ether.view.IView;
import ch.fhnw.util.color.RGB;
import ch.fhnw.util.math.Mat4;
import ch.fhnw.util.math.Vec3;
import ch.fhnw.util.math.Vec4;
import org.eclipse.swt.internal.win32.SYSTEMTIME;
import org.eclipse.swt.widgets.IME;
import org.lwjgl.glfw.GLFW;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ApplicationPoC {
    public static void main(String[] args) {
        new ApplicationPoC().run();
    }

    private IMesh plane;
    private Vec3 velocity = Vec3.ZERO;
    private float rotation = 0;

    private final float speed = 0.005f;
    private final float rotationStep = 1f;

    public void run() {
        Platform.get().init();

        IController controller = new DefaultController() {
            @Override
            public void keyPressed(IKeyEvent e) {
                switch(e.getKey()) {
                    case GLFW.GLFW_KEY_UP:
                        velocity = new Vec3(speed, 0, 0);
                        break;
                    case GLFW.GLFW_KEY_DOWN:
                        velocity = new Vec3(-speed, 0, 0);
                        break;
                    case GLFW.GLFW_KEY_LEFT:
                        rotation = rotationStep;
                        break;
                    case GLFW.GLFW_KEY_RIGHT:
                        rotation = -rotationStep;
                        break;
                    default:
                        super.keyPressed(e);
                        break;
                }
            }

            @Override
            public void keyReleased(IKeyEvent e) {
                switch(e.getKey()) {
                    case GLFW.GLFW_KEY_UP:
                    case GLFW.GLFW_KEY_DOWN:
                        velocity = Vec3.ZERO;
                        break;
                    case GLFW.GLFW_KEY_LEFT:
                    case GLFW.GLFW_KEY_RIGHT:
                        rotation = 0;
                        break;
                    default:
                        super.keyReleased(e);
                        break;
                }
            }
        };

        controller.run(time -> {
            IView view = new DefaultView(controller, 100, 100, 512, 512, IView.INTERACTIVE_VIEW, "Obj View");

            IScene scene = new DefaultScene(controller);
            controller.setScene(scene);

            ICamera camera = new Camera(new Vec3(0, -5f, 5f), Vec3.ZERO);
            scene.add3DObject(camera);
            controller.setCamera(view, camera);

            scene.add3DObject(new DirectionalLight(new Vec3(1, 1, 1), new RGB(0.4f, 0.4f, 0.4f), new RGB(0.6f, 0.6f, 0.6f)));

            // Platform
            scene.add3DObject(MeshUtilities.createGroundPlane(3f));

            try {
                final URL obj = getClass().getResource("/vehicle.obj");
                final List<IMesh> meshes = new ArrayList<>();
                new ObjReader(obj, IModelReader.Options.CONVERT_TO_Z_UP).getMeshes().forEach(mesh -> meshes.add(mesh));
                System.out.println("number of meshes before merging: " + meshes.size());
                final List<IMesh> merged = MeshUtilities.mergeMeshes(meshes);
                System.out.println("number of meshes after merging: " + merged.size());
                this.plane = merged.get(0);
                float maxExtent = Math.max(plane.getBounds().getExtentX(), Math.max(plane.getBounds().getExtentY(), plane.getBounds().getExtentZ()));
                plane.setTransform(Mat4.scale(1f / maxExtent));
                scene.add3DObjects(plane);

            } catch (IOException e) {
                e.printStackTrace();
            }

        });

        controller.animate((time, interval) -> {
            plane.setTransform(plane.getTransform().postMultiply(Mat4.rotate(rotation, 0, 0, 1)).postMultiply(Mat4.translate(velocity)));
        });

        Platform.get().run();
    }
}
