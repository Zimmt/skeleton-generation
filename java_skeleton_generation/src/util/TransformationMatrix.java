package util;

import skeleton.elements.SkeletonPart;

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

    // generates a new transform, old transform is not changed
    public static TransformationMatrix reflectTransformAtWorldXYPlane(SkeletonPart element) {
        TransformationMatrix reflection = reflectionTransformZ();
        TransformationMatrix childWorldTransform = element.calculateWorldTransform();
        TransformationMatrix inverseParentWorldTransform = TransformationMatrix.getInverse(element.getParent().calculateWorldTransform());

        // transform to world coordinates, reflect, then transform to parent coordinates
        return TransformationMatrix.multiply(TransformationMatrix.multiply(inverseParentWorldTransform, reflection), childWorldTransform);
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

    private static TransformationMatrix reflectionTransformZ() {
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
