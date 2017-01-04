package component;


import gameobject.GameObject;

public abstract class Component {

    private GameObject gameObject;

    public GameObject getGameObject() {
        return gameObject;
    }

    public Transform getTransform() {
        return gameObject.getTransform();
    }

    public void setGameObject(GameObject gameObject) {
        this.gameObject = gameObject;
    }

    public String typeName() {
        return this.getClass().getSimpleName();
    }

    public void init() {}

    public void destroy() {}

}
