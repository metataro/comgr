package render;

import ch.fhnw.ether.controller.IController;
import ch.fhnw.ether.controller.event.IEventScheduler;
import ch.fhnw.ether.platform.IMonitor;
import ch.fhnw.ether.platform.Platform;
import ch.fhnw.ether.view.IView;
import ch.fhnw.ether.view.IWindow;
import ch.fhnw.util.Viewport;
import ch.fhnw.util.math.Vec2;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.function.Consumer;

public class View implements IView {

    private final Config viewConfig;

    private final IEventScheduler scheduler;

    private volatile IWindow window;

    private volatile Viewport viewport = new Viewport(0, 0, 1, 1);

    private boolean enabled = true;

    private View(IEventScheduler scheduler, Config viewConfig) {
        this.scheduler = scheduler;
        this.viewConfig = viewConfig;
    }

    @Override
    public void dispose() {
        Platform.get().runOnMainThread(() -> {
            window.dispose();
            window = null;
        });
    }

    @Override
    public final boolean isEnabled() {
        return enabled;
    }

    @Override
    public final void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public Config getConfig() {
        return viewConfig;
    }

    @Override
    public final IController getController() {
        throw new NotImplementedException();
    }

    @Override
    public final Viewport getViewport() {
        return viewport;
    }

    @Override
    public IWindow getWindow() {
        return window;
    }

    private void setWindow(IWindow window) {
        this.window = window;
        this.window.setWindowListener(windowListener);
    }

    @Override
    public String toString() {
        return "[view " + hashCode() + "]";
    }

    private void runOnSceneThread(IEventScheduler.IAction action) {
        if (scheduler.isSchedulerThread())
            action.run(scheduler.getTime());
        else
            scheduler.run(action);
    }

    private IWindow.IWindowListener windowListener = new IWindow.IWindowListener() {
        @Override
        public void windowCloseRequest(IWindow window) {
            dispose();
            runOnSceneThread(time -> {
                // TODO: WindowCLosedEvent: View.this
            });
        }

        @Override
        public void windowRefresh(IWindow window) {
            scheduler.repaint();
        }

        @Override
        public void windowFocusChanged(IWindow w, boolean focused) {
            // TODO: windowFocusChangedEvent: View.this
            //if (focused)
            //    runOnSceneThread(time -> controller.viewGainedFocus(View.this));
            //else
            //    runOnSceneThread(time -> controller.viewLostFocus(View.this));
        }

        @Override
        public void windowResized(IWindow window, Vec2 windowSize, Vec2 framebufferSize) {
            viewport = new Viewport(0, 0, framebufferSize.x, framebufferSize.y);
            // TODO: windowResizedEvent: View.this
            //runOnSceneThread(time -> controller.viewResized(View.this));
        }
    };

    public static View create(IEventScheduler scheduler, IMonitor monitor, Config viewConfig, String title, Consumer<IWindow> windowCallback) {
        return create(scheduler, monitor, monitor.getX() , monitor.getY(),  monitor.getWidth(), monitor.getHeight(), viewConfig, title, windowCallback);
    }

    public static View create(IEventScheduler scheduler, int x, int y, int w, int h, Config viewConfig, String title, Consumer<IWindow> windowCallback) {
        return create(scheduler, null, x, y, w, h, viewConfig, title, windowCallback);
    }

    private static View create(IEventScheduler scheduler, IMonitor monitor, int x, int y, int w, int h, Config viewConfig, String title, Consumer<IWindow> windowCallback) {
        View view = new View(scheduler, viewConfig);
        Platform.get().runOnMainThread(() -> {
            IWindow window = IWindow.create(new Vec2(16, 16), title != null ? title : "", viewConfig.getViewType() == ViewType.INTERACTIVE_VIEW);

            window.setVisible(true);
            window.setFullscreen(monitor);
            view.setWindow(window);

            // note: we open the window initially at a smaller size, and then
            // resize in order to trigger the window listener.
            window.setSize(new Vec2(w, h));
            if (x != -1)
                window.setPosition(new Vec2(x, y));

            windowCallback.accept(window);
        });
        return view;
    }
}
