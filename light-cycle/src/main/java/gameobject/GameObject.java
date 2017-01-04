package gameobject;

import ch.fhnw.util.math.Mat4;
import component.Component;
import component.ComponentManager;
import component.Transform;
import scene.Scene;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class GameObject {

    private final HashMap<String, Component> components = new HashMap<>();

    private final Scene scene;

    public final Transform transform;

    public GameObject(Scene scene, Transform transform) {
        this.scene = scene;
        this.transform = addComponent(transform);
    }

    @SuppressWarnings("unchecked")
    public <T extends Component> T addComponent(Class<T> type) {
        // TODO: is this the best thing we can do if a component of the same type already is attached?
        if (!components.containsKey(type.getSimpleName())) {
            return addComponent(scene.getComponentManager().createComponent(type));
        }
        return (T) components.get(type.getSimpleName());
    }

    private <T extends Component> T addComponent(T component) {
        component.setGameObject(this);
        this.components.put(component.typeName(), component);
        component.init();
        return component;
    }

    @SuppressWarnings("unchecked")
    public <T extends Component> Optional<T> getComponent(Class<T> type) {
        Component component = components.get(type.getSimpleName());
        return component == null ? Optional.empty() : Optional.of((T)component);
    }

    public Scene getScene() {
        return scene;
    }

    public Transform getTransform() {
        return transform;
    }
}
