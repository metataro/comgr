package render;

import java.io.File;
import java.nio.ByteBuffer;

import ch.fhnw.ether.render.gl.*;
import ch.fhnw.ether.view.IView;
import ch.fhnw.ether.view.IWindow;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import ch.fhnw.ether.controller.IController;
import ch.fhnw.ether.controller.event.IEventScheduler.IAction;
import ch.fhnw.ether.image.IGPUImage;
import ch.fhnw.ether.image.IHostImage;
import ch.fhnw.ether.image.IImage.ComponentFormat;
import ch.fhnw.ether.image.IImage.ComponentType;
import ch.fhnw.ether.platform.IMonitor;
import ch.fhnw.ether.render.gl.GLObject.Type;
import ch.fhnw.util.Log;
import ch.fhnw.util.Viewport;
import ch.fhnw.util.math.Vec2;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class OffscreenView implements IView, IWindow {
    private static final Log log = Log.create();

    private boolean           enabled = true;
    private final Config      config;
    private final Viewport    viewport;
    private FrameBuffer       frameBuffer;
    private String            title;
    private IGPUImage         colorTexture;
    private Texture           depthTexture;
    private IWindow.IContext context;

    public OffscreenView(int w, int h, Config config) {
        this.config     = config;
        this.viewport   = new Viewport(0, 0, w, h);
        setSize(new Vec2(w, h));
    }

    @Override
    public void dispose() {
        frameBuffer = null;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public Config getConfig() {
        return config;
    }

    @Override
    public IController getController() {
        throw new NotImplementedException();
    }

    @Override
    public Viewport getViewport() {
        return viewport;
    }

    @Override
    public IWindow getWindow() {
        return this;
    }

    @Override
    public boolean isDisposed() {
        return frameBuffer == null;
    }

    class Context implements IContext {
        private final IContext  ctx;

        public Context() {
            ctx = GLContextManager.acquireContext();
        }

        @Override
        public void close() throws Exception {
            GL11.glFinish();
            FrameBuffer.unbind();
            try {
                ctx.close();
            } catch (Throwable t) {log.severe(t);}
            context = null;
        }
    }

    @Override
    public IContext acquireContext() {
        context = new Context();

        if (frameBuffer == null || viewport.w != colorTexture.getWidth() || viewport.h != colorTexture.getHeight()) {
            if (frameBuffer == null)
                frameBuffer = new FrameBuffer();

            frameBuffer.bind();

            int width = (int)viewport.w;
            int height = (int)viewport.h;

            colorTexture = IGPUImage.create(width, height, ComponentType.BYTE, ComponentFormat.RGBA);
            frameBuffer.attach(GL30.GL_COLOR_ATTACHMENT0, colorTexture);
            depthTexture = new Texture(new GLObject(Type.TEXTURE), width, height);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, (int)depthTexture.getGPUHandle());
            GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_DEPTH_COMPONENT, width, height, 0, GL11.GL_DEPTH_COMPONENT, GL11.GL_UNSIGNED_INT, (ByteBuffer)null);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
            frameBuffer.attach(GL30.GL_DEPTH_ATTACHMENT, depthTexture);

            GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);

            GL20.glDrawBuffers(GL30.GL_COLOR_ATTACHMENT0);

            if(!frameBuffer.isComplete())
                log.severe("Status: " + FrameBuffer.toString(frameBuffer.getStatus()));
        } else {
            frameBuffer.bind();
        }
        System.out.println("aquired!");
        return context;
    }

    @Override
    public void releaseContext() {
        System.out.println("released!");
        try {
            context.close();
        } catch (Throwable t) {log.severe(t);}
        context = null;
    }

    @Override
    public void swapBuffers() {
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public boolean isVisible() {
        return !(isDisposed());
    }

    @Override
    public void setVisible(boolean visible) {
    }

    @Override
    public Vec2 getPosition() {
        return Vec2.ZERO;
    }

    @Override
    public void setPosition(Vec2 position) {}

    @Override
    public Vec2 getSize() {
        return new Vec2(colorTexture.getWidth(), colorTexture.getHeight());
    }

    @Override
    public void setSize(Vec2 size) {
        colorTexture = IGPUImage.create((int)size.x, (int)size.y, ComponentType.BYTE, ComponentFormat.RGBA);
    }

    @Override
    public Vec2 getFramebufferSize() {
        return getSize();
    }

    public IHostImage getImage() {
        return colorTexture.createHostImage();
    }

    @Override public void setFullscreen(IMonitor monitor) {}
    @Override public void setPointerMode(PointerMode mode) {}
    @Override public void setPointerPosition(float x, float y) {}
    @Override public void setWindowListener(IWindowListener windowListener) {}
    @Override public void setKeyListener(IKeyListener keyListener) {}
    @Override public void setPointerListener(IPointerListener pointerListener) {}

    @Override
    public void setPointerIcon(File file, int hotX, int hotY) {}
}
