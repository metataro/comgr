package inputdevice;

import ch.fhnw.ether.view.IWindow;
import ch.fhnw.util.math.Vec2;

/**
 * Created by Christian on 18.12.2016.
 */
public interface Mouse extends IWindow.IPointerListener {
    boolean isPresent();

    boolean isPressed();

    Vec2 getScrollDelta();

    Vec2 getPosition();
}
