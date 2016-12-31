package gameobject;


import component.Transform;
import scene.Scene;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

public class GameObjectManager {

    private final ArrayList<GameObject> gameObjects;

    public GameObjectManager() {
        gameObjects = new ArrayList<>();
    }

    public GameObject createGameObject(Scene scene, Transform transform) {
        GameObject gameObject = new GameObject(scene, transform);
        this.gameObjects.add(gameObject);
        return gameObject;
    }

    public List<GameObject> getAllGameObjects() {
        return Collections.unmodifiableList(gameObjects);
    }

    public void forEach(Consumer<? super GameObject> consumer) {
        this.gameObjects.forEach(consumer);
    }
}
