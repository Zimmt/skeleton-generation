package util;

import javax.media.j3d.Transform3D;
import javax.vecmath.Matrix3f;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

public class TransformationMatrix {

    // first rotation then translation
    private Transform3D transform;

    public TransformationMatrix() {
        this.transform = new Transform3D();
    }

    public TransformationMatrix(Transform3D transform) {
        this.transform = new Transform3D(transform);
    }

    public TransformationMatrix(TransformationMatrix transformationMatrix) {
        this.transform = new Transform3D(transformationMatrix.transform);
    }

    public TransformationMatrix(Vector3f translation) {
        this(matrix3fIdentity(), translation);
    }

    public TransformationMatrix(Matrix3f rotation, Vector3f translation) {
        this.transform = new Transform3D(rotation, translation, 1f);
    }

    // transforms p and places result back into p, the fourth coordinate is assumed to be 1
    // (when it is applied on a vector the fourth coordinate would be assumed to be 0!)
    public void applyOnPoint(Point3f p) {
        transform.transform(p);
    }

    public void applyOnVector(Vector3f v) {
        transform.transform(v);
    }

    public TransformationMatrix translate(Vector3f t) {
        Matrix3f identity = matrix3fIdentity();
        Transform3D translation = new Transform3D(identity, t, 1f);
        transform.mul(transform, translation); // new transform = old transform * translation
        return this;
    }

    public TransformationMatrix clearTranslation() {
        transform.setTranslation(new Vector3f());
        return this;
    }

    public TransformationMatrix rotate(Matrix3f rot) {
        Transform3D rotation = new Transform3D(rot, new Vector3f(), 1f);
        transform.mul(transform, rotation); // new transform = old transform * rotation
        return this;
    }

    // counter clockwise rotation of the existing matrix around the local x axis
    public TransformationMatrix rotateAroundX(float angle) {
        Matrix3f rotation = new Matrix3f();
        rotation.rotX(angle);
        return this.rotate(rotation);
    }

    // counter clockwise rotation of the existing matrix around the local y axis
    public TransformationMatrix rotateAroundY(float angle) {
        Matrix3f rotation = new Matrix3f();
        rotation.rotY(angle);
        return this.rotate(rotation);
    }

    // counter clockwise rotation of the existing matrix around the local z axis
    public TransformationMatrix rotateAroundZ(float angle) {
        Matrix3f rotation = new Matrix3f();
        rotation.rotZ(angle);
        return this.rotate(rotation);
    }

    /**
     *
     * @return true if this is a right handed coordinate system, false otherwise
     * it is left handed if the cross product of x with y = z and
     * right handed if it is = -z
     */
    public boolean getHandedness() {
        Matrix3f rotation = new Matrix3f();
        transform.get(rotation);
        Vector3f x = new Vector3f(); rotation.getColumn(0, x);
        Vector3f y = new Vector3f(); rotation.getColumn(1, y);
        Vector3f z = new Vector3f(); rotation.getColumn(2, z);

        Vector3f crossProduct = new Vector3f();
        crossProduct.cross(x, y);

        if (crossProduct.epsilonEquals(z, 0.001f)) {
            return true;
        }
        crossProduct.scale(-1);
        if (crossProduct.epsilonEquals(z, 0.001f)) {
            return false;
        } else {
            System.err.println("Something went wrong with the coordinate systems here...");
        }

        return false;
    }

    public static TransformationMatrix multiply(TransformationMatrix t1, TransformationMatrix t2) {
        Transform3D newTransform = new Transform3D();
        newTransform.mul(t1.transform, t2.transform);
        return new TransformationMatrix(newTransform);
    }

    public static TransformationMatrix getInverse(TransformationMatrix t) {
        Transform3D transform = new Transform3D(t.transform);
        transform.invert();
        return new TransformationMatrix(transform);
    }

    public static TransformationMatrix getReflectionTransformZ() {
        Matrix3f refZ = matrix3fIdentity();
        refZ.setElement(2, 2, -1f);
        return new TransformationMatrix(refZ, new Vector3f());
    }

    private static Matrix3f matrix3fIdentity() {
        Matrix3f matrix = new Matrix3f();
        matrix.setIdentity();
        return matrix;
    }
}
