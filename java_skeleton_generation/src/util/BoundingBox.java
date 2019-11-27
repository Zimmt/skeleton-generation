package util;

import de.javagl.obj.Obj;

import javax.media.j3d.Transform3D;
import javax.vecmath.Vector3f;

public class BoundingBox {

    private Vector3f boxOrigin;
    private Vector3f xCorner;
    private Vector3f yCorner;
    private Vector3f zCorner;

    public BoundingBox(Vector3f boxOrigin, Vector3f xCorner, Vector3f yCorner, Vector3f zCorner) {
        this.boxOrigin = boxOrigin;
        this.xCorner = xCorner;
        this.yCorner = yCorner;
        this.zCorner = zCorner;
    }

    public static BoundingBox defaultBox() {
        return new BoundingBox(
                new Vector3f(0f, 0f, 0f),
                new Vector3f(1f, 0f, 0f),
                new Vector3f(0f, 1f, 0f),
                new Vector3f(0f, 0f, 1f)
        );
    }

    public BoundingBox cloneBox() {
        return new BoundingBox(boxOrigin, xCorner, yCorner, zCorner);
    }

    public void transform(Transform3D matrix) {
        matrix.transform(boxOrigin);
        matrix.transform(xCorner);
        matrix.transform(yCorner);
        matrix.transform(zCorner);
    }

    public void addDataToObj(Obj obj) {

        int zero = obj.getNumVertices();

        // 0. origin, 1. x, 2. y, 3. z, 4. xy, 5. xz, 6. yz, 7. xyz
        obj.addVertex(boxOrigin.x, boxOrigin.y, boxOrigin.z);
        obj.addVertex(xCorner.x, xCorner.y, xCorner.z);
        obj.addVertex(yCorner.x, yCorner.y, yCorner.z);
        obj.addVertex(zCorner.x, zCorner.y, zCorner.z);
        Vector3f xyCorner = xyCorner();
        obj.addVertex(xyCorner.x, xyCorner.y, xyCorner.z);
        Vector3f xzCorner = xzCorner();
        obj.addVertex(xzCorner.x, xzCorner.y, xzCorner.z);
        Vector3f yzCorner = yzCorner();
        obj.addVertex(yzCorner.x, yzCorner.y, yzCorner.z);
        Vector3f xyzCorner = xyzCorner();
        obj.addVertex(xyzCorner.x, xyzCorner.y, xyzCorner.z);

        obj.addFace(zero+0, zero+1, zero+5, zero+3); // front
        obj.addFace(zero+2, zero+4, zero+7, zero+6); // back
        obj.addFace(zero+0, zero+1, zero+4, zero+2); // bottom
        obj.addFace(zero+3, zero+5, zero+7, zero+6); // top
        obj.addFace(zero+0, zero+2, zero+6, zero+3); // left
        obj.addFace(zero+1, zero+4, zero+7, zero+5); // right
    }

    private Vector3f xyCorner() {
        Vector3f xyCorner = new Vector3f();
        xyCorner.sub(xCorner, boxOrigin);
        xyCorner.add(yCorner);

        return xyCorner;
    }

    private Vector3f xzCorner() {
        Vector3f xzCorner = new Vector3f();
        xzCorner.sub(xCorner, boxOrigin);
        xzCorner.add(zCorner);

        return xzCorner;
    }

    private Vector3f yzCorner() {
        Vector3f yzCorner = new Vector3f();
        yzCorner.sub(yCorner, boxOrigin);
        yzCorner.add(zCorner);

        return yzCorner;
    }

    private Vector3f xyzCorner() {
        Vector3f xyzCorner = xyCorner();
        xyzCorner.add(zCorner);

        return xyzCorner;
    }
}
