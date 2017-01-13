package event;

import java.util.ArrayList;

public class EventManager {
	private ArrayList<EventListener> listeners = new ArrayList<>();

	public void notify(Event event) {
		for (EventListener listener: listeners) {
			listener.receive(event);
		}
	}

	public void register(EventListener listener) {
		this.listeners.add(listener);
	}
}
