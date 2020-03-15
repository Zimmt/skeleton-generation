package util;

import javax.vecmath.Point2f;
import javax.vecmath.Point3f;
import java.util.ArrayList;
import java.util.List;

public class CubicBezierCurve {
    Point2f controlPoint0;
    Point2f controlPoint1;
    Point2f controlPoint2;
    Point2f controlPoint3;

    public CubicBezierCurve(Point2f controlPoint0, Point2f controlPoint1, Point2f controlPoint2, Point2f controlPoint3) {
        this.controlPoint0 = controlPoint0;
        this.controlPoint1 = controlPoint1;
        this.controlPoint2 = controlPoint2;
        this.controlPoint3 = controlPoint3;
    }

    public Point2f getControlPoint0() {
        return controlPoint0;
    }

    public Point2f getControlPoint3() {
        return controlPoint3;
    }

    // function: (1-t)³ p0 + 3t(1-t)² p1 + 3t²(1-t) p2 + t³ p3
    public Point2f apply(float t) {
        Point2f result = new Point2f(controlPoint0);
        result.scale((1 - t) * (1 - t) * (1 - t));

        Point2f p1 = new Point2f(controlPoint1);
        p1.scale(3 * t * (1 - t) * (1 - t));
        result.add(p1);

        Point2f p2 = new Point2f(controlPoint2);
        p2.scale(3 * t * t * (1 - t));
        result.add(p2);

        Point2f p3 = new Point2f(controlPoint3);
        p3.scale(t * t * t);
        result.add(p3);

        return result;
    }

    public Point3f apply3d(float t) {
        Point2f point2d = apply(t);
        return new Point3f(point2d.x, point2d.y, 0f);
    }

    public float getGradient(float t) {
        Point2f derivation = applyDerivation(t);
        return derivation.y / derivation.x;
    }

    /**
     * derivation: 3 * [(1-t)² (p1 - p0) + 2t(1-t) (p2 - p1) + t² (p3 - p2) ]
     */
    private Point2f applyDerivation(float t) {
        Point2f result = new Point2f(controlPoint1);
        result.sub(controlPoint0);
        result.scale((1 - t) * (1 - t));

        Point2f p2 = new Point2f(controlPoint2);
        p2.sub(controlPoint1);
        p2.scale(2 * t * (1 - t));
        result.add(p2);

        Point2f p3 = new Point2f(controlPoint3);
        p3.sub(controlPoint2);
        p3.scale(t * t);
        result.add(p3);

        result.scale(3);

        return result;
    }

    /**
     * Calculates all intervals of the curve where the gradient is in the interval [x-eps, x+eps]
     * by testing the derivation with the step size of 0.01
     * The intervals are sorted in ascending order.
     */
    public List<Float> getIntervalsByGradientEpsilon(float x, float eps) {

        List<Float> intervals = new ArrayList<>();
        boolean inInterval = false;

        for (float t = 0f; t <= 1f; t += 0.01) {
            float gradient = getGradient(t);

            if (!inInterval && x-eps <= gradient && gradient <= x+eps) {
                intervals.add(t);
                inInterval = true;

            } else if (inInterval && (gradient < x-eps || gradient > x+eps)) {
                intervals.add(t);
                inInterval = false;
            }
        }
        if (inInterval) { // interval not closed
            intervals.add(1f);
        }
        if (intervals.size() % 2 != 0) {
            System.err.println("Spine interval calculation wrong!");
        }

        return intervals;
    }
}
