package system;

import component.behaviour.PlayerBehaviour;
import event.Event;

import java.util.ArrayList;
import java.util.List;

public class GamePlaySystem extends System {

    private final ArrayList<PlayerBehaviour> alivePlayers = new ArrayList<>();
    private final ArrayList<PlayerBehaviour> deadPlayers = new ArrayList<>();

    @Override
    protected void processSystem(float deltaTime) {
        ArrayList<PlayerBehaviour> newDeadPlayers = new ArrayList<>();
        alivePlayers.forEach(p -> {
            if (!p.isAlive()) {
                newDeadPlayers.add(p);
            }
        });
        if (!newDeadPlayers.isEmpty()) {
            alivePlayers.removeAll(newDeadPlayers);
            deadPlayers.addAll(newDeadPlayers);
            if (alivePlayers.size() == 0) {
                java.lang.System.out.println("UNENTSCHIEDEN!");
                scene.getEventManager().notify(new Event.DrawEvent(this, newDeadPlayers));
            } else if (alivePlayers.size() == 1) {
                java.lang.System.out.printf("%s won!\n", alivePlayers.get(0).getName());
                PlayerBehaviour winner = alivePlayers.get(0);
                scene.getEventManager().notify(new Event.PlayerWonEvent(this, winner, (ArrayList<PlayerBehaviour>) deadPlayers.clone()));
                alivePlayers.remove(winner);
                deadPlayers.add(winner);
                winner.setAlive(false);
            }
        }

    }

    @Override
    protected void processEvent(Event event) {
        if (event instanceof Event.ComponentCreatedEvent) {
            Event.ComponentCreatedEvent componentCreatedEvent = (Event.ComponentCreatedEvent) event;
            if (componentCreatedEvent.component instanceof PlayerBehaviour) {
                alivePlayers.add((PlayerBehaviour) componentCreatedEvent.component);
            }
        }
    }
}
