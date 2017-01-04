package scene;

import java.util.Collection;

public class SceneGraph<T extends SceneGraphNode<T>> {

    private final T root;

    public SceneGraph(T root) {
        this.root = root;
    }

    public void update() {
        root.apply(false);
    }

    public T getRoot() {
        return root;
    }

    public void add(T transform) {
        root.addChild(transform);
    }

    public void addAll(Collection<T> transforms) {
        root.addChildren(transforms);
    }
}
