package component;

import java.util.*;

public class ComponentManager {

    private HashMap<String, ArrayList<Component>> components;

    public ComponentManager() {
        components = new HashMap<>();
    }

    public <T extends Component> T createComponent(Class<T> type) {
        try {
            T component = type.newInstance();
            addComponent(component);
            return component;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to init component " + type.getSimpleName());
        }
    }

    private <T extends Component> void addComponent(T component) {
        String componentType = component.typeName();

        ensureComponentType(componentType);

        this.components.get(componentType).add(component);
    }

    @SuppressWarnings("unchecked")
    public <T extends Component> ArrayList<T> getComponents(Class<T> type) {
        ArrayList<Component> components = this.components.get(type.getSimpleName());
        if (components == null) {
            return new ArrayList<T>();
        }
        return (ArrayList<T>) components;
    }

    public <T extends Component> boolean removeComponent(T component) {
        String componentType = component.typeName();

        ArrayList<Component> components = this.components.get(componentType);

        if (components != null) {
            components.remove(component);
            return true;
        }

        return false;
    }

    public <T extends Component> void removeComponents(Class<T> type) {
        components.remove(type.getSimpleName());
    }

    public <T extends Component> boolean existsComponentOfType(Class<T> type) {
        return components.containsKey(type.getSimpleName());
    }

    private void ensureComponentType(String componentType) {
        if (!components.containsKey(componentType)) {
            components.put(componentType, new ArrayList<Component>());
        }
    }
}
