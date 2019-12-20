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
        Matrix3f identity = new Matrix3f(); // all zero matrix
        identity.setIdentity();

        this.transform = new Transform3D(identity, translation, 1f);
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
        Matrix3f identity = new Matrix3f();
        identity.setIdentity();
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

    public TransformationMatrix reflectZ() {
        Transform3D reflection = internReflectZ();
        transform.mul(transform, reflection);
        return this;
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

    public static TransformationMatrix reflectZTransform() {
         Transform3D reflection = internReflectZ();
        return new TransformationMatrix(reflection);
    }

    private static Transform3D internReflectZ() {
        Matrix3f refX = new Matrix3f();
        refX.setIdentity();
        refX.setElement(2, 2, -1f);

        return new Transform3D(refX, new Vector3f(), 1f);
    }
}
