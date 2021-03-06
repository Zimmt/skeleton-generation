package util;

import javax.vecmath.Vector3f;

/**
 * In the local coordinates of a skeleton part this should not be rotated
 */
public class BoundingBox {

    private Vector3f xCorner;
    private Vector3f yCorner;
    private Vector3f zCorner;

    public BoundingBox(Vector3f xCorner, Vector3f yCorner, Vector3f zCorner) {
        this.xCorner = xCorner;
        this.yCorner = yCorner;
        this.zCorner = zCorner;
    }

    public BoundingBox(Vector3f scale) {
        this.xCorner = new Vector3f(scale.x, 0f, 0f);
        this.yCorner = new Vector3f(0f, scale.y, 0f);
        this.zCorner = new Vector3f(0f, 0f, scale.z);
    }

    public BoundingBox(BoundingBox boundingBox) {
        this.xCorner = new Vector3f(boundingBox.xCorner);
        this.yCorner = new Vector3f(boundingBox.yCorner);
        this.zCorner = new Vector3f(boundingBox.zCorner);
    }

    public static BoundingBox defaultBox() {
        return new BoundingBox(
                new Vector3f(1f, 0f, 0f),
                new Vector3f(0f, 1f, 0f),
                new Vector3f(0f, 0f, 1f)
        );
    }

    public BoundingBox cloneBox() {
        return new BoundingBox(
                new Vector3f(xCorner), new Vector3f(yCorner), new Vector3f(zCorner));
    }

    public BoundingBox transform(TransformationMatrix matrix) {
        TransformationMatrix transform = new TransformationMatrix(matrix);
        transform.clearTranslation(); // there is no point in translating when the bounding box itself does not store an origin
        transform.applyOnVector(xCorner);
        transform.applyOnVector(yCorner);
        transform.applyOnVector(zCorner);
        return this;
    }

    public BoundingBox scale(Vector3f scale) {
        xCorner.scale(scale.getX());
        yCorner.scale(scale.getY());
        zCorner.scale(scale.getZ());
        return this;
    }

    public BoundingBox setXLength(float xLength) {
        xCorner.normalize();
        xCorner.scale(xLength);
        return this;
    }

    public BoundingBox setYLength(float yLength) {
        yCorner.normalize();
        yCorner.scale(yLength);
        return this;
    }

    public BoundingBox setZLength(float zLength) {
        zCorner.normalize();
        zCorner.scale(zLength);
        return this;
    }

    public Vector3f getXVector() {
        return xCorner;
    }

    public Vector3f getYVector() {
        return yCorner;
    }

    public Vector3f getZVector() {
        return zCorner;
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
