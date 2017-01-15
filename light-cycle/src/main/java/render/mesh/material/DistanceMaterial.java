package render.mesh.material;

import ch.fhnw.ether.image.IGPUImage;
import ch.fhnw.ether.render.shader.IShader;
import ch.fhnw.ether.scene.mesh.geometry.IGeometry;
import ch.fhnw.ether.scene.mesh.material.AbstractMaterial;
import ch.fhnw.ether.scene.mesh.material.ICustomMaterial;
import ch.fhnw.ether.scene.mesh.material.IMaterial;
import ch.fhnw.util.color.RGBA;
import ch.fhnw.util.math.Vec4;
import render.mesh.shader.DistanceShader;

import java.util.function.Supplier;

public class DistanceMaterial extends AbstractMaterial implements ICustomMaterial {

    public static final int MAX_POSITIONS = 4;

    private RGBA color;
    private IGPUImage colorMap;
    private Supplier<Vec4>[] objectPositions;
    private int objectCount;
    private DistanceShader shader;

    public DistanceMaterial(IGPUImage colorMap, Supplier<Vec4>... objectPositions) {
        super(provide(IMaterial.COLOR, IMaterial.COLOR_MAP), require(IGeometry.POSITION_ARRAY, IGeometry.COLOR_MAP_ARRAY));

        if(objectPositions.length > MAX_POSITIONS) {
            throw new IllegalArgumentException("Maximum allowed positions: " + MAX_POSITIONS);
        }

        this.color = RGBA.WHITE;
        this.colorMap = colorMap;
        this.objectPositions = objectPositions;
        this.objectCount = objectPositions.length;
        this.shader = new DistanceShader(objectPositions);
    }

    public RGBA getColor() {
        return color;
    }

    public void setColor(RGBA color) {
        this.color = color;
    }

    public IGPUImage getColorMap() {
        return colorMap;
    }

    public void setColorMap(IGPUImage colorMap) {
        this.colorMap = colorMap;
        updateRequest();
    }

    @Override
    public Object[] getData() {
        return data(color, colorMap);
    }

    @Override
    public String toString() {
        return super.toString() + "[" + color + ", " + colorMap + "]";
    }

    @Override
    public IShader getShader() {
        return this.shader;
    }
}
