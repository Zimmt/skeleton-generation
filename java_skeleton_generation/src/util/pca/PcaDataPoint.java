package util.pca;

import org.apache.commons.math3.linear.RealVector;

import javax.vecmath.Point2d;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class PcaDataPoint {
    private static final int dimension = 29;
    private static final double coordinateScaleFactor = 1000;
    private static final double wingScaleFactor = 1;
    private static final double flooredLegsScaleFactor = 2;
    private static final double weightScaleFactor = 120000;

    private String name;

    private List<Point2d> neck = new ArrayList<>(); // if not there empty, else 4 points (1 point is contained in back)
    private List<Point2d> back; // the 4 control points of cubic bezier curve
    private List<Point2d> tail = new ArrayList<>(); // if not there empty, else 4 points (1 point is contained in back)
    private List<Point2d> spine; // is calculated from neck, back and tail, consists of 10 points

    private double wings;
    private double flooredLegs; // #legs/2, [0,2]

    private double lengthUpperArm; // [0, 1000]
    private double lengthLowerArm; // [0, 1000]
    private double lengthHand; // [0, 1000]

    private double lengthUpperLeg; // [0, 1000]
    private double lengthLowerLeg; // [0, 1000]
    private double lengthFoot; // [0, 1000]

    private double weight; // [0, 120.000]

    private AnimalClass animalClass;

    public PcaDataPoint() {}

    /**
     * Moves the point by the given vector.
     * Changes: spine, wings, flooredLegs, lengthFrontLegs, lengthBackLegs, weight
     * Doesn't change: back, neck, tail, animalClass, arms, lengthWings
     */
    public PcaDataPoint add(RealVector scaledEigenvector) {
        PcaDataPoint point = new PcaDataPoint();

        List<Point2d> newSpine = new ArrayList<>();
        for (int i = 0; i < spine.size(); i++) {
            Point2d p = new Point2d(spine.get(i));
            p.add(new Point2d(scaledEigenvector.getEntry(2*i) * coordinateScaleFactor, scaledEigenvector.getEntry(2*i + 1) * coordinateScaleFactor));
            newSpine.add(p);
        }
        point.setSpine(newSpine);

        point.setWings(wings + scaledEigenvector.getEntry(20) * wingScaleFactor);
        point.setFlooredLegs(flooredLegs + scaledEigenvector.getEntry(21) * flooredLegsScaleFactor);
        point.setLengthUpperArm(lengthUpperArm + scaledEigenvector.getEntry(22) * coordinateScaleFactor);
        point.setLengthLowerArm(lengthLowerArm + scaledEigenvector.getEntry(23) * coordinateScaleFactor);
        point.setLengthHand(lengthHand + scaledEigenvector.getEntry(24) * coordinateScaleFactor);
        point.setLengthUpperLeg(lengthUpperLeg + scaledEigenvector.getEntry(25) * coordinateScaleFactor);
        point.setLengthLowerLeg(lengthLowerLeg + scaledEigenvector.getEntry(26) * coordinateScaleFactor);
        point.setLengthFoot(lengthFoot + scaledEigenvector.getEntry(27) * coordinateScaleFactor);
        point.setWeight(weight + scaledEigenvector.getEntry(28) * weightScaleFactor);

        return point;
    }

    public boolean dataSetMaybeComplete() {
        // all other data has primitive types and is set or has default value
        return name != null && spine != null && animalClass != null && weight > 0;
    }

    /**
     * Need to call this to calculate spine before pca data can be generated!
     * Generates spine if data set maybe complete
     * @return true if successful, false otherwise
     */
    public boolean processData() {
        if (back != null) {
            return calculateSpine();
        } else {
            return false;
        }
    }

    /**
     * Over all there are 29 dimensions (animal class is not a dimension for PCA as it has no continuous scala):
     * spine: 20 doubles = 10 points
     * wings: 1
     * floored legs: 1
     * extremity lengths: 6
     * weight: 1
     * @return array with this dimensions in the order as above
     */
    public double[] getScaledDataForPCA() {
        if (!dataSetMaybeComplete()) {
            System.err.println("Incomplete data!");
        }

        double[] data = new double[dimension];
        int nextIndex = 0;

        for (Point2d p : spine) {
            data[nextIndex] = p.x / coordinateScaleFactor;
            data[nextIndex+1] = p.y / coordinateScaleFactor;
            nextIndex += 2;
        }
        data[nextIndex] = wings / wingScaleFactor; nextIndex++;
        data[nextIndex] = flooredLegs / flooredLegsScaleFactor; nextIndex++;
        data[nextIndex] = lengthUpperArm / coordinateScaleFactor; nextIndex++;
        data[nextIndex] = lengthLowerArm / coordinateScaleFactor; nextIndex++;
        data[nextIndex] = lengthHand / coordinateScaleFactor; nextIndex++;
        data[nextIndex] = lengthUpperLeg / coordinateScaleFactor; nextIndex++;
        data[nextIndex] = lengthLowerLeg / coordinateScaleFactor; nextIndex++;
        data[nextIndex] = lengthFoot / coordinateScaleFactor; nextIndex++;
        data[nextIndex] = weight / weightScaleFactor;

        return data;
    }

    public static int getDimension() {
        return dimension;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setNeck(List<Point2d> neck) {
        if (neck.size() != 4) {
            System.err.println("Neck has not correct number of control points.");
            return;
        }
        this.neck = neck;
    }

    public void setBack(List<Point2d> back) {
        if (back.size() != 4) {
            System.err.println("Back has not correct number of control points.");
        }
        this.back = back;
    }

    public void setTail(List<Point2d> tail) {
        if (tail.size() != 4) {
            System.err.println("Tail has not correct number of control points.");
        }
        this.tail = tail;
    }

    private void setSpine(List<Point2d> spine) {
        if (spine.size() != 10) {
            System.err.println("Spine has not correct number of control points.");
        }
        this.spine = spine;
    }

    public void setAnimalClass(int animalClass) {
        if (animalClass < 0 || animalClass > 4) {
            System.err.println("Incorrect animal class found.");
        }
        switch(animalClass) {
            case 0:
                this.animalClass = AnimalClass.FISH;
                break;
            case 1:
                this.animalClass = AnimalClass.AMPHIBIAN;
                break;
            case 2:
                this.animalClass = AnimalClass.REPTILIAN;
                break;
            case 3:
                this.animalClass = AnimalClass.BIRD;
                break;
            case 4:
                this.animalClass = AnimalClass.MAMMAL;
        }
    }

    public void setWings(double wings) {
        this.wings = wings;
    }

    public void setFlooredLegs(double flooredLegs) {
        if (flooredLegs < 0 || flooredLegs > 2) {
            System.err.println("Incorrect number of floored legs found.");
        }
        this.flooredLegs = flooredLegs;
    }

    public void setLengthUpperArm(double length) {
        if (length < 0 || length > 1000) {
            System.err.println("Incorrect length found.");
        }
        this.lengthUpperArm = length;
    }

    public void setLengthLowerArm(double length) {
        if (length < 0 || length > 1000) {
            System.err.println("Incorrect length found.");
        }
        this.lengthLowerArm = length;
    }

    public void setLengthHand(double length) {
        if (length < 0 || length > 1000) {
            System.err.println("Incorrect length found.");
        }
        this.lengthHand = length;
    }

    public void setLengthUpperLeg(double length) {
        if (length < 0 || length > 1000) {
            System.err.println("Incorrect length found.");
        }
        this.lengthUpperLeg = length;
    }

    public void setLengthLowerLeg(double length) {
        if (length < 0 || length > 1000) {
            System.err.println("Incorrect length found.");
        }
        this.lengthLowerLeg = length;
    }

    public void setLengthFoot(double length) {
        if (length < 0 || length > 1000) {
            System.err.println("Incorrect length found.");
        }
        this.lengthFoot = length;
    }

    public void setWeight(double weight) {
        if (weight < 0 || weight > 120000) {
            System.err.println("Incorrect weight found.");
        }
        this.weight = weight;
    }

    public double getWings() {
        return wings;
    }

    public List<Point2d> getSpine() {
        return spine;
    }

    public double getFlooredLegs() {
        return flooredLegs;
    }

    public double getLengthUpperArm() {
        return lengthUpperArm;
    }

    public double getLengthLowerArm() {
        return lengthLowerArm;
    }

    public double getLengthHand() {
        return lengthHand;
    }

    public double getLengthUpperLeg() {
        return lengthUpperLeg;
    }

    public double getLengthLowerLeg() {
        return lengthLowerLeg;
    }

    public double getLengthFoot() {
        return lengthFoot;
    }

    public double getWeight() {
        return weight;
    }

    /**
     * Potentially reverts the order of the points in neck, back and tail.
     * Sorts the points so that they are in the order they appear on the spine.
     * If neck or tail are empty they are filled with the first or last point of back.
     * @return true if back is set, false otherwise (then nothing can be calculated)
     */
    private boolean calculateSpine() {
        if (back == null) {
            return false; // can only calculate this when all data is there
        }
        List<Point2d> sortedPoints = new ArrayList<>();
        if (back.get(0).x > back.get(3).x) {
            Collections.reverse(back);
        }

        // neck
        if (neck.isEmpty()) {
            for (int i = 0; i < 4; i++) {
                neck.add(back.get(0));
            }
        } else if (neck.get(0).epsilonEquals(back.get(0), 0.2)) {
            Collections.reverse(neck);
        }
        if (!neck.get(3).epsilonEquals(back.get(0), 0.2)) {
            System.err.println("Neck and back don't share point!");
        }
        sortedPoints.addAll(neck);

        // back
        sortedPoints.remove(3);
        sortedPoints.addAll(back);

        //tail
        if (tail.isEmpty()) {
            for (int i = 0; i < 4; i++) {
                tail.add(back.get(3));
            }
        } else if (tail.get(3).epsilonEquals(sortedPoints.get(6), 0.2)) {
            Collections.reverse(tail);
        }
        if (!sortedPoints.get(6).epsilonEquals(tail.get(0), 0.2)) {
            System.err.println("Back and tail don't share point!");
        }
        sortedPoints.remove(6);
        sortedPoints.addAll(tail);

        this.spine = sortedPoints;

        return true;
    }

    /**
     * Calculates mean of spine but not of neck, back and tail!
     * And not of the animal class
     */
    public static PcaDataPoint getMean(List<PcaDataPoint> points) {
        if (points.isEmpty()) {
            System.err.println("Cannot calculate mean from empty list");
        } else if (!points.get(0).dataSetMaybeComplete()) {
            System.err.println("Cannot calculate mean from incomplete data set");
        }

        PcaDataPoint mean = new PcaDataPoint();
        mean.setName("mean");

        List<Point2d> meanSpine = Arrays.asList(new Point2d(0,0), new Point2d(0,0), new Point2d(0,0), new Point2d(0,0), new Point2d(0,0), new Point2d(0,0), new Point2d(0,0), new Point2d(0,0), new Point2d(0,0), new Point2d(0,0));
        double meanWings = 0.0;
        double meanFlooredLegs = 0.0;
        double meanLengthUpperArm = 0.0;
        double meanLengthLowerArm = 0.0;
        double meanLengthHand = 0.0;
        double meanLengthUpperLeg = 0.0;
        double meanLengthLowerLeg = 0.0;
        double meanLengthFoot = 0.0;
        double meanWeight = 0.0;

        for (PcaDataPoint point : points) {
            List<Point2d> spine = point.getSpine();
            for (int i = 0; i < spine.size(); i++) {
                meanSpine.get(i).add(spine.get(i));
            }
            meanWings += point.getWings();
            meanFlooredLegs += point.getFlooredLegs();
            meanLengthUpperArm += point.getLengthUpperArm();
            meanLengthLowerArm += point.getLengthLowerArm();
            meanLengthHand += point.getLengthHand();
            meanLengthUpperLeg += point.getLengthUpperLeg();
            meanLengthLowerLeg += point.getLengthLowerLeg();
            meanLengthFoot += point.getLengthFoot();
            meanWeight += point.getWeight();
        }

        for (Point2d meanSpinePoint : meanSpine) {
            meanSpinePoint.scale(1.0 / (double) points.size());
        }
        meanWings /= points.size();
        meanFlooredLegs /= points.size();
        meanLengthUpperArm /= points.size();
        meanLengthLowerArm /= points.size();
        meanLengthHand /= points.size();
        meanLengthUpperLeg /= points.size();
        meanLengthLowerLeg /= points.size();
        meanLengthFoot /= points.size();
        meanWeight /= points.size();


        mean.setSpine(meanSpine);
        mean.setWings(meanWings);
        mean.setFlooredLegs(meanFlooredLegs);
        mean.setLengthUpperArm(meanLengthUpperArm);
        mean.setLengthLowerArm(meanLengthLowerArm);
        mean.setLengthHand(meanLengthHand);
        mean.setLengthUpperLeg(meanLengthUpperLeg);
        mean.setLengthLowerLeg(meanLengthLowerLeg);
        mean.setLengthFoot(meanLengthFoot);
        mean.setWeight(meanWeight);

        return mean;
    }
}
