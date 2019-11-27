package util;

import javax.media.j3d.Transform3D;
import javax.vecmath.Matrix3f;
import javax.vecmath.Vector3f;

public class TransformationMatrix {

    // first rotation then translation
    private Transform3D transform;

    public TransformationMatrix(Transform3D transform) {
        this.transform = transform;
    }

    public TransformationMatrix translate(Vector3f t) {
        Vector3f translation = new Vector3f();
        transform.get(translation);
        translation.add(t);
        transform.setTranslation(translation);
        return this;
    }

    public TransformationMatrix rotate(Matrix3f rot) {
        Transform3D rotation = new Transform3D(rot, new Vector3f(0f, 0f, 0f), 1);
        transform.mul(rotation, transform); // new transform = rotation * old transform
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
}
