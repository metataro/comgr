package event;

import ch.fhnw.ether.view.IView;
import ch.fhnw.ether.view.IWindow;
import component.Component;
import component.collider.Collider;
import gameobject.GameObject;

public class Event {
	public final Object sender;

	public Event(Object sender) {
		this.sender = sender;
	}

	public static class ComponentCreatedEvent extends Event{

		public ComponentCreatedEvent(Object sender, Component component) {
			super(sender);
		}
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
