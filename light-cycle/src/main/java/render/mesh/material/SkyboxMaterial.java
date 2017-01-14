package render.mesh.material;

import ch.fhnw.ether.image.IGPUImage;
import ch.fhnw.ether.render.shader.IShader;
import ch.fhnw.ether.scene.mesh.geometry.IGeometry;
import ch.fhnw.ether.scene.mesh.material.AbstractMaterial;
import ch.fhnw.ether.scene.mesh.material.ICustomMaterial;
import ch.fhnw.ether.scene.mesh.material.IMaterial;
import ch.fhnw.util.color.RGBA;
import render.mesh.shader.SkyboxShader;

/**
 * @autor benikm91
 */
public final class SkyboxMaterial extends AbstractMaterial implements ICustomMaterial {

    private RGBA color;
    private IGPUImage colorMap;

    private final IShader shader;

    public SkyboxMaterial(IGPUImage colorMap) {
        this(RGBA.WHITE, colorMap);
    }

    public SkyboxMaterial(RGBA color, IGPUImage colorMap) {
        super(provide(IMaterial.COLOR, IMaterial.COLOR_MAP), require(IGeometry.POSITION_ARRAY, IGeometry.COLOR_MAP_ARRAY));

        this.color = color;
        this.colorMap = colorMap;

        this.shader = new SkyboxShader();

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
