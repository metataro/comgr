package inputdevice;

import ch.fhnw.ether.view.IWindow;
import ch.fhnw.util.math.Vec2;

public class GLFWMouse implements Mouse {

	private boolean present = false;
	private boolean pressed = false;
	private Vec2 scrollDelta = Vec2.ZERO;
	private Vec2 position = Vec2.ZERO;

	@Override
	public boolean isPresent() {
		return present;
	}

	@Override
	public boolean isPressed() {
		return pressed;
	}

	@Override
	public Vec2 getScrollDelta() {
		return scrollDelta;
	}

	@Override
	public Vec2 getPosition() {
		return position;
	}

	@Override
	public void pointerEntered(IWindow window, int mods, Vec2 position) {
		present = true;
	}

	@Override
	public void pointerExited(IWindow window, int mods, Vec2 position) {
		present = false;
	}

	@Override
	public void pointerPressed(IWindow window, int mods, Vec2 position, int button) {
		pressed = true;
	}

	@Override
	public void pointerReleased(IWindow window, int mods, Vec2 position, int button) {
		pressed = false;
	}

	@Override
	public void pointerClicked(IWindow window, int mods, Vec2 position, int button) {
		// what to do? :)
	}

	@Override
	public void pointerMoved(IWindow window, int mods, Vec2 position) {
		this.position = position;
	}

	@Override
	public void pointerDragged(IWindow window, int mods, Vec2 position) {
		this.position = position;
	}

	@Override
	public void pointerWheelMoved(IWindow window, int mods, Vec2 position, Vec2 scroll) {
		this.position = position;
		this.scrollDelta = scroll;
	}
}
