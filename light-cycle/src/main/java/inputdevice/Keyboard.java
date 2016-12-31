package inputdevice;

import ch.fhnw.ether.view.IWindow;

/**
 * Created by Christian on 18.12.2016.
 */
public interface Keyboard extends IWindow.IKeyListener {

    // TODO Remove this method? Is implemented inefficient anyways in child class.
    boolean isAnyKeyDown();

    boolean isKeyDown(int keyCode);

}
