package event;

/**
 * Queues events and processes them when update is called. 
 * The events are stored in a ring buffer.
 * @author Christian Scheller
 */
public abstract class EventQueue {
	private int head;
	private int tail;

	private static final int MAX_PENDING = 32;
	private Event[] pending = new Event[MAX_PENDING];
	
	public EventQueue() {
		this.head = 0;
		this.tail = 0;
	}

	/**
	 * Processes all pending events
	 */
	public void update() {
		if (head == tail) {
			return;
		}

		for (int i = head; i != tail; i = (i + 1) % MAX_PENDING) {
			process(pending[i]);
			head = (head + 1) % MAX_PENDING;
		}
	}
	
	/**
	 * Processes an event
	 * @param event The Event
	 */
	protected abstract void process(Event event);

	/**
	 * Adds a new event to the queue
	 * @param event The event
	 */
	public synchronized void addEvent(Event event) {
		assert ((tail + 1) % MAX_PENDING != head);

		pending[tail] = event;
		tail = (tail + 1) % MAX_PENDING;
	}
}
