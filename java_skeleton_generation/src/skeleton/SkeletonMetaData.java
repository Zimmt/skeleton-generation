package skeleton;

import util.pca.PcaDataPoint;

import javax.vecmath.Matrix3d;
import javax.vecmath.Point2d;
import javax.vecmath.Point3d;
import javax.vecmath.Vector2d;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SkeletonMetaData {
    private SpineData spine;
    private ExtremityData extremities;
    private double weight;


    public SkeletonMetaData(PcaDataPoint p) {
        this.spine = preprocessSpine(p.getSpine());
        if ((Math.abs(spine.getNeck().getGradient(1f) - spine.getBack().getGradient(0f)) > 0.01) ||
                (Math.abs(spine.getBack().getGradient(1f) - spine.getTail().getGradient(0f)) > 0.01)) {
            System.err.println("Alignment of spine went wrong");
        }
        this.extremities = new ExtremityData(p.getWings(), p.getFlooredLegs(),
                p.getLengthUpperArm(), p.getLengthLowerArm(), p.getLengthHand(),
                p.getLengthUpperLeg(), p.getLengthLowerLeg(), p.getLengthFoot(), spine);
        this.weight = p.getWeight();
    }

    public SpineData getSpine() {
        return spine;
    }

    public ExtremityData getExtremities() {
        return extremities;
    }

    public double getWeight() {
        return weight;
    }

    private SpineData preprocessSpine(List<Point2d> spinePoints) {
        List<Point2d> preprocessedPoints = new ArrayList<>(spinePoints.size());
        for (Point2d p : spinePoints) {
            preprocessedPoints.add(new Point2d(p));
        }

        List<Point2d> alignedNeckPoints = alignControlPoints(preprocessedPoints.get(2), preprocessedPoints.get(3), preprocessedPoints.get(4));
        List<Point2d> alignedTailPoints = alignControlPoints(preprocessedPoints.get(5), preprocessedPoints.get(6), preprocessedPoints.get(7));
        preprocessedPoints.set(2, alignedNeckPoints.get(0));
        preprocessedPoints.set(4, alignedNeckPoints.get(1));
        preprocessedPoints.set(5, alignedTailPoints.get(0));
        preprocessedPoints.set(7, alignedTailPoints.get(1));

        return new SpineData(preprocessedPoints);
    }

    private List<Point2d> alignControlPoints(Point2d p1, Point2d center, Point2d p2) {
        double eps = 0.01;

        double angle = getAngle(p1, center, p2);
        if (Math.PI - angle < eps) {
            return Arrays.asList(p1, p2);
        }
        double turnAngle = (Math.PI - angle) / 2.0;
        Point2d alignedP1 = turnPointCounterclockwiseAroundCenter(p1, center, turnAngle);
        Point2d alignedP2 = turnPointCounterclockwiseAroundCenter(p2, center, -turnAngle);

        double newAngle = getAngle(alignedP1, center, alignedP2);
        if (Math.PI - newAngle < eps) {
            return Arrays.asList(alignedP1, alignedP2);
        }

        alignedP1 = turnPointCounterclockwiseAroundCenter(alignedP1, center, -2 * turnAngle);
        alignedP2 = turnPointCounterclockwiseAroundCenter(alignedP2, center, 2 * turnAngle);

        double newerAngle = getAngle(alignedP1, center, alignedP2);
        if (! (Math.PI - newerAngle < eps)) {
            System.err.println("Could not align control points. Angle should be 180 but is " + Math.toDegrees(newerAngle));
        }
        return Arrays.asList(alignedP1, alignedP2);
    }

    private Point2d turnPointCounterclockwiseAroundCenter(Point2d p, Point2d center, double angle) {
        Matrix3d rotation = new Matrix3d();
        rotation.rotZ(angle);

        Point3d turnedPoint = new Point3d(p.x, p.y, 0);
        Point3d c = new Point3d(center.x, center.y, 0);
        turnedPoint.sub(c);
        rotation.transform(turnedPoint);
        turnedPoint.add(c);

        if (turnedPoint.z != 0) {
            System.err.println("Something went wrong with the rotation!");
        }
        return new Point2d(turnedPoint.x, turnedPoint.y);
    }

    private double getAngle(Point2d p1, Point2d center, Point2d p2) {
        Vector2d firstGradient = new Vector2d(p1);
        firstGradient.sub(center);
        Vector2d secondGradient = new Vector2d(p2);
        secondGradient.sub(center);
        return firstGradient.angle(secondGradient);
    }
}
