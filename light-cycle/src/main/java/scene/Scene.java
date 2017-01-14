package scene;

import audio.AudioController;
import ch.fhnw.ether.render.DefaultRenderManager;
import ch.fhnw.ether.render.IRenderManager;
import ch.fhnw.ether.render.forward.ForwardRenderer;
import component.ComponentManager;
import component.Transform;
import event.Event;
import event.EventManager;
import gameobject.GameObject;
import gameobject.GameObjectManager;
import system.System;

import java.util.ArrayList;

public class Scene {

    private final ComponentManager componentManager;

    private final EventManager eventManager;
    private final GameObjectManager gameObjectManager;

    private final ArrayList<System> updateSystems;
    private final ArrayList<System> drawSystems;

    private final SceneGraph<Transform> sceneGraph;

    private final IRenderManager renderManager;

    private final AudioController audioController;

    public Scene() {
        this.componentManager = new ComponentManager();
        this.gameObjectManager = new GameObjectManager();
        this.eventManager = new EventManager();
        this.updateSystems = new ArrayList<>();
        this.drawSystems = new ArrayList<>();
        this.sceneGraph = new SceneGraph<>(Transform.createRoot());
        this.renderManager = new DefaultRenderManager(new ForwardRenderer());
        this.audioController = new AudioController(2); // TODO: where to configure this???
    }


    public void update(float elapsed) {
        for (System system : updateSystems) {
            system.process(elapsed);
        }
    }

    public void draw(float elapsed) {
        for (System system : drawSystems) {
            system.process(elapsed);
        }
    }

    public ArrayList<System> getSystems(ProcessType processType) {
        switch (processType) {
            case Update: {
                return updateSystems;
            }
            case Draw: {
                return drawSystems;
            }
        }
        return null;
    }

    public System getSystem(ProcessType processType, int index) {
        switch (processType) {
            case Update: {
                return updateSystems.get(index);
            }
            case Draw: {
                return drawSystems.get(index);
            }
        }
        return null;
    }

    /**
     * Adds a system to this scene and attach it to a given processSystem type
     * @param processType The processSystem type
     * @param system The system to add
     */
    public void addSystem(ProcessType processType, System system) {
        system.setScene(this);
        eventManager.register(system);
        switch (processType) {
            case Update: {
                updateSystems.add(system);
                break;
            }
            case Draw: {
                drawSystems.add(system);
                break;
            }
        }
    }

    /**
     * Adds a list of systems to this scene and attach them to a given processSystem type
     * @param processType The processSystem type
     * @param systems The systems to add
     */
    public void addSystems(ProcessType processType, ArrayList<System> systems) {
        for (System system : systems) {
            addSystem(processType, system);
        }
    }

    public ComponentManager getComponentManager() {
        return componentManager;
    }

    public EventManager getEventManager() {
        return eventManager;
    }

    public IRenderManager getRenderManager() {
        return renderManager;
    }

    public SceneGraph<Transform> getSceneGraph() {
        return sceneGraph;
    }

    public AudioController getAudioController() {
        return audioController;
    }

    public GameObject createGameObject(Transform parentTransform) {
        Transform transform = componentManager.createComponent(Transform.class);
        transform.setParent(parentTransform);
        GameObject gameObject = gameObjectManager.createGameObject(this, transform);
        eventManager.notify(new Event.GameObjectCreatedEvent(this, gameObject));
        return gameObject;
    }
    public GameObject createGameObject() {
        return createGameObject(sceneGraph.getRoot());
    }

}
