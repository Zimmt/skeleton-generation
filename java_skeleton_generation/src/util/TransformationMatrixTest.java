package util;

import javax.vecmath.Matrix3f;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

import static org.junit.jupiter.api.Assertions.*;

class TransformationMatrixTest {

    @org.junit.jupiter.api.Test
    void translate() {
        TransformationMatrix transform = new TransformationMatrix();
        Vector3f translation = new Vector3f(1f, 2f, 3f);
        transform.translate(translation);

        Point3f testPoint = new Point3f(1f, 1f, 1f);
        transform.applyOnPoint(testPoint);

        assertEquals(new Point3f(2f, 3f, 4f), testPoint);
    }

    @org.junit.jupiter.api.Test
    void rotate() {
        TransformationMatrix transform = new TransformationMatrix();
        Matrix3f rotation = new Matrix3f();
        rotation.rotZ((float)Math.toRadians(90));
        transform.rotate(rotation);

        Point3f testPoint = new Point3f(1f, 0f, 0f);
        transform.applyOnPoint(testPoint);

        assertEquals(0f, testPoint.x, 0.001);
        assertEquals(1f, testPoint.y, 0.001);
        assertEquals(0f, testPoint.z, 0.001);
    }

    @org.junit.jupiter.api.Test
    void rotateAndTranslate() {
        TransformationMatrix transform = new TransformationMatrix();
        Matrix3f rotation = new Matrix3f();
        rotation.rotZ((float)Math.toRadians(90));
        transform.rotate(rotation);

        Vector3f translation = new Vector3f(1f, 0f, 0f);
        transform.translate(translation);

        Point3f testPoint = new Point3f(1f, 0f, 0f);
        transform.applyOnPoint(testPoint);

        assertEquals(1f, testPoint.x, 0.001);
        assertEquals(1f, testPoint.y, 0.001);
        assertEquals(0f, testPoint.z, 0.001);
    }
}