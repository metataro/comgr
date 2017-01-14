package event;

import java.util.LinkedList;
import java.util.function.Consumer;

public abstract class EventListener {

	private final LinkedList<Event> pending;

	public EventListener() {
		pending = new LinkedList<Event>();
	}

	public final void receive(Event event) {
		this.pending.addLast(event);
	}

	protected void processNext(Consumer<Event> consumer) {
		if (!pending.isEmpty()) {
			consumer.accept(pending.removeFirst());
		}
	}

	protected void processAllPending(Consumer<Event> consumer) {
		while (!pending.isEmpty()) {
			processNext(consumer);
		}
	}

}
