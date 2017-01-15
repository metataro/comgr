package render.mesh.shader;

import ch.fhnw.ether.render.shader.IShader;
import ch.fhnw.ether.render.shader.base.AbstractShader;
import ch.fhnw.ether.render.variable.base.IntUniform;
import ch.fhnw.ether.render.variable.base.Vec4FloatUniform;
import ch.fhnw.ether.render.variable.builtin.*;
import ch.fhnw.ether.scene.mesh.IMesh;
import ch.fhnw.util.color.RGBA;
import ch.fhnw.util.math.Vec4;
import render.mesh.material.DistanceMaterial;

import java.util.function.Supplier;

public class DistanceShader extends AbstractShader {

    private final Supplier<Vec4>[] objectDistances;
    private final int objectCount;

    public DistanceShader(Supplier<Vec4>[] objectDistances) {
        // Most of this shader was taken and modified from ether
        super(IShader.class, "custom.shader.distance", "/shaders/distance_unshaded_vct", IMesh.Primitive.TRIANGLES);

        // TODO: Make thresholds configurable

        this.objectDistances = objectDistances;
        this.objectCount = objectDistances.length;

        addArray(new PositionArray());
        addArray(new ColorMapArray());

        addUniform(new ColorUniform(() -> RGBA.WHITE));

        for(int i = 0; i < this.objectCount; i++) {
            final int index = i;
            addUniform(new Vec4FloatUniform("shader.object_dist_" + index, "objectDistance" + index, this.objectDistances[index]::get));
        }
        for(int i = this.objectCount; i < DistanceMaterial.MAX_POSITIONS; i++) {
            final int index = i;
            addUniform(new Vec4FloatUniform("shader.object_dist_" + index, "objectDistance" + index, () -> Vec4.ZERO));
        }

        addUniform(new IntUniform("shader.object_count", "objectCount", () -> this.objectCount));

        addUniform(new ColorMapUniform());

        addUniform(new ViewUniformBlock());
    }
}
