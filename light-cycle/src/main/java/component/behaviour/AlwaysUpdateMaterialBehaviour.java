package component.behaviour;

import component.MeshGroup;

public class AlwaysUpdateMaterialBehaviour extends Behaviour {
    @Override
    public void update(float deltaTime) {
        this.getGameObject().getComponent(MeshGroup.class).ifPresent(group -> group.getMeshes().forEach(mesh -> mesh.getMaterial().getUpdater().request()));
    }
}
