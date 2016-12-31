package event;

/**
 * The Event Manager broadcasts Events to registered EventListeners
 * @author Christian Scheller
 */
public class EventManager extends EventQueue {
	private static final int MAX_LISTENERS = 32;
	private EventListener[] listeners = new EventListener[MAX_LISTENERS];
	private int listenerIndex = 0;
	
	@Override
	protected void process(Event event) {
		for (int i = 0; i < listeners.length; i++) {
			if (listeners[i] != null) {
				listeners[i].receive(event);
			}
		}
	}
	
	/**
	 * Registers a EventListener to this event Manager
	 * @param listener The EventListener
	 */
	public void register(EventListener listener) {
		assert (listenerIndex < MAX_LISTENERS);
		
		this.listeners[listenerIndex++] = listener;
	}
}
