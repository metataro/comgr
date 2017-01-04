package event;

/**
 * EventListeners can be registered to the EventManager to receive occurred events
 * @author Christian Scheller
 */
public interface EventListener {
	
	/**
	 * Receives an Event (gets called by the Even Manager)
	 * @param event The occurred event
	 */
	void receive(Event event);

}
