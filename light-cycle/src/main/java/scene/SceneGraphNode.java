package scene;

import component.Transform;

import java.util.Collection;


public interface SceneGraphNode<T> {

    void addChild(T transform);

    void addChildren(Collection<T> transforms);

    boolean add(T parent, T transform);

    boolean remove(T root, T transform);

    void apply(boolean dirty);
}
