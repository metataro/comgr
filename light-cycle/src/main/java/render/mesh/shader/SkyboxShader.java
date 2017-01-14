package render.mesh.shader;

import ch.fhnw.ether.render.shader.IShader;
import ch.fhnw.ether.render.shader.base.AbstractShader;
import ch.fhnw.ether.render.variable.builtin.ColorMapArray;
import ch.fhnw.ether.render.variable.builtin.ColorMapUniform;
import ch.fhnw.ether.render.variable.builtin.ColorUniform;
import ch.fhnw.ether.render.variable.builtin.PositionArray;
import ch.fhnw.ether.scene.mesh.IMesh;
import ch.fhnw.util.color.RGBA;

/**
 * @autor benikm91
 */
public class SkyboxShader extends AbstractShader {

    public SkyboxShader() {
        super(IShader.class, "custom.shader.skybox", "/shaders/skybox_shader", IMesh.Primitive.TRIANGLES);

        addArray(new PositionArray());

        addArray(new ColorMapArray());

        addUniform(new ColorUniform(() -> RGBA.WHITE));

        addUniform(new ColorMapUniform());
    }
}
