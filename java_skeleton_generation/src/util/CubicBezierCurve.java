package util;

import javax.vecmath.Point3f;

public class CubicBezierCurve {
    Point3f controlPoint0;
    Point3f controlPoint1;
    Point3f controlPoint2;
    Point3f controlPoint3;

    public CubicBezierCurve(Point3f controlPoint0, Point3f controlPoint1, Point3f controlPoint2, Point3f controlPoint3) {
        this.controlPoint0 = controlPoint0;
        this.controlPoint1 = controlPoint1;
        this.controlPoint2 = controlPoint2;
        this.controlPoint3 = controlPoint3;
    }

    // function: (1-t)³ p0 + 3t(1-t)² p1 + 3t²(1-t) p2 + t³ p3
    public Point3f apply(float t) {
        Point3f result = new Point3f(controlPoint0);
        result.scale((1 - t) * (1 - t) * (1 - t));

        Point3f p1 = new Point3f(controlPoint1);
        p1.scale(3 * t * (1 - t) * (1 - t));
        result.add(p1);

        Point3f p2 = new Point3f(controlPoint2);
        p2.scale(3 * t * t * (1 - t));
        result.add(p2);

        Point3f p3 = new Point3f(controlPoint3);
        p3.scale(t * t * t);
        result.add(p3);

        return result;
    }

    // derivation: 3 * [(1-t)² (p1 - p0) + 2t(1-t) (p2 - p1) + t² (p3 - p2) ]
    public Point3f applyDerivation(float t) {
        Point3f result = new Point3f(controlPoint1);
        result.sub(controlPoint0);
        result.scale((1 - t) * (1 - t));

        Point3f p2 = new Point3f(controlPoint2);
        p2.sub(controlPoint1);
        p2.scale(2 * t * (1 - t));
        result.add(p2);

        Point3f p3 = new Point3f(controlPoint3);
        p3.sub(controlPoint2);
        p3.scale(t * t);
        result.add(p3);

        result.scale(3);

        return result;
    }
}
