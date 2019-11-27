package util;

import javax.media.j3d.Transform3D;
import javax.vecmath.Vector3f;

public class BoundingBox {

    private Vector3f xCorner;
    private Vector3f yCorner;
    private Vector3f zCorner;

    public BoundingBox(Vector3f xCorner, Vector3f yCorner, Vector3f zCorner) {
        this.xCorner = xCorner;
        this.yCorner = yCorner;
        this.zCorner = zCorner;
    }

    public static BoundingBox defaultBox() {
        return new BoundingBox(
                new Vector3f(1f, 0f, 0f),
                new Vector3f(0f, 1f, 0f),
                new Vector3f(0f, 0f, 1f)
        );
    }

    public BoundingBox cloneBox() {
        return new BoundingBox(xCorner, yCorner, zCorner);
    }

    public BoundingBox transform(Transform3D matrix) {
        matrix.setTranslation(new Vector3f()); // there is no point in translating when the bounding box itself does not store an origin
        matrix.transform(xCorner);
        matrix.transform(yCorner);
        matrix.transform(zCorner);
        return this;
    }

    public BoundingBox scale(Vector3f scale) {
        xCorner.scale(scale.getX());
        yCorner.scale(scale.getY());
        zCorner.scale(scale.getZ());
        return this;
    }

    public float getXLength() {
        return xCorner.length();
    }

    public float getYLength() {
        return yCorner.length();
    }

    public float getZLength() {
        return zCorner.length();
    }
}
