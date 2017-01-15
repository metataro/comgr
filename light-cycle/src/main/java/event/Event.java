package event;

import ch.fhnw.ether.view.IView;
import ch.fhnw.ether.view.IWindow;
import component.Component;
import component.behaviour.PlayerBehaviour;
import component.collider.Collider;
import gameobject.GameObject;

import java.util.ArrayList;
import java.util.List;

public class Event {
    public final Object sender;

    public Event(Object sender) {
        this.sender = sender;
    }

    public static class CollisionEvent extends Event{
        public final Collider collider1;
        public final Collider collider2;
        public CollisionEvent(Object sender, Collider collider1, Collider collider2) {
            super(sender);
            this.collider1 = collider1;
            this.collider2 = collider2;
        }
    }

    public static class GameObjectCreatedEvent extends Event{
        public final GameObject gameObject;
        public GameObjectCreatedEvent(Object sender, GameObject gameObject) {
            super(sender);
            this.gameObject = gameObject;
        }
    }

    public static class ComponentCreatedEvent extends Event{
        public final Component component;
        public ComponentCreatedEvent(Object sender, Component component) {
            super(sender);
            this.component = component;
        }
    }

    public static class PlayerWonEvent extends Event{
        public final PlayerBehaviour playerBehaviour;
        public final List<PlayerBehaviour> losers;
        public PlayerWonEvent(Object sender, PlayerBehaviour playerBehaviour, List<PlayerBehaviour> losers) {
            super(sender);
            this.playerBehaviour = playerBehaviour;
            this.losers = losers;
        }
    }

    public static class DrawEvent extends Event{
        public final List<PlayerBehaviour> playerBehaviours;
        public DrawEvent(Object sender, List<PlayerBehaviour> playerBehaviours) {
            super(sender);
            this.playerBehaviours = playerBehaviours;
        }
    }

    public static class ViewDisposedEvent extends Event {
        public final IView view;

        public ViewDisposedEvent(Object sender, IView view) {
            super(sender);
            this.view = view;
        }
    }

    public static class WindowFocusChangedEvent extends Event {
        public final IWindow window;
        public final boolean focused;

        public WindowFocusChangedEvent(Object sender, IView view, IWindow window, boolean focused) {
            super(sender);
            this.window = window;
            this.focused = focused;
        }
    }
}
