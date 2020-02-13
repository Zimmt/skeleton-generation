package util.pca;

import org.apache.commons.math3.linear.RealVector;

import javax.vecmath.Point2d;
import java.util.*;

public class PcaDataPoint {
    private static final int dimension = 29;
    private static final double coordinateScaleFactor = 1000;
    private static final double wingScaleFactor = 1000;
    private static final double flooredLegsScaleFactor = 2000;
    private static final double weightScaleFactor = 120000000;

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
    private boolean logWeight;

    private AnimalClass animalClass;

    public PcaDataPoint(boolean logWeight) {
        this.logWeight = logWeight;
    }

    /**
     * Moves the point by the given vector.
     * Changes: spine, wings, flooredLegs, length of extremities, weight
     * leaves empty: name, back, neck, tail, animalClass
     */
    public PcaDataPoint getMovedPoint(List<RealVector> scaledEigenvectors) {
        PcaDataPoint point = new PcaDataPoint(logWeight);

        List<Point2d> newSpine = new ArrayList<>();
        for (int i = 0; i < spine.size(); i++) {
            Point2d p = new Point2d(spine.get(i));
            for (RealVector scaledEigenvector : scaledEigenvectors) {
                p.add(new Point2d(scaledEigenvector.getEntry(2*i) * coordinateScaleFactor, scaledEigenvector.getEntry(2*i + 1) * coordinateScaleFactor));
            }
            newSpine.add(p);
        }
        point.setSpine(newSpine);

        double newWings = wings;
        double newFlooredLegs = flooredLegs;
        double newLengthUpperArm = lengthUpperArm;
        double newLengthLowerArm = lengthLowerArm;
        double newLengthHand = lengthHand;
        double newLengthUpperLeg = lengthUpperLeg;
        double newLengthLowerLeg = lengthLowerLeg;
        double newLengthFoot = lengthFoot;
        double newWeight = weight;

        for (RealVector scaledEigenvector : scaledEigenvectors) {
            newWings += scaledEigenvector.getEntry(20) * wingScaleFactor;
            newFlooredLegs += scaledEigenvector.getEntry(21) * flooredLegsScaleFactor;
            newLengthUpperArm += scaledEigenvector.getEntry(22) * coordinateScaleFactor;
            newLengthLowerArm += scaledEigenvector.getEntry(23) * coordinateScaleFactor;
            newLengthHand += scaledEigenvector.getEntry(24) * coordinateScaleFactor;
            newLengthUpperLeg += scaledEigenvector.getEntry(25) * coordinateScaleFactor;
            newLengthLowerLeg += scaledEigenvector.getEntry(26) * coordinateScaleFactor;
            newLengthFoot += scaledEigenvector.getEntry(27) * coordinateScaleFactor;
            if (logWeight) {
                // reverse from log(weight+1) / log(scale+1)
                newWeight += Math.pow(10, scaledEigenvector.getEntry(28) * Math.log10(weightScaleFactor + 1)) - 1;
            } else {
                newWeight += scaledEigenvector.getEntry(28) * weightScaleFactor;
            }
        }

        point.setWings(newWings);
        point.setFlooredLegs(newFlooredLegs);
        point.setLengthUpperArm(newLengthUpperArm);
        point.setLengthLowerArm(newLengthLowerArm);
        point.setLengthHand(newLengthHand);
        point.setLengthUpperLeg(newLengthUpperLeg);
        point.setLengthLowerLeg(newLengthLowerLeg);
        point.setLengthFoot(newLengthFoot);
        point.setWeight(newWeight);

        return point;
    }

    public boolean dataSetMaybeComplete() {
        // all other data has primitive types and is set or has default value
        return name != null && spine != null && weight > 0;
    }

    /**
     * Neck, back, tail, name and animal class not tested
     */
    public boolean containsIncorrectData() {
        double eps = 0.001;
        return !(spine != null && spine.size() == 10 &&
                wings >= -eps && wings <= wingScaleFactor+eps &&
                flooredLegs >= -eps && flooredLegs <= flooredLegsScaleFactor+eps &&
                lengthUpperArm >= -eps && lengthUpperArm < coordinateScaleFactor+eps &&
                lengthLowerArm >= -eps && lengthLowerArm < coordinateScaleFactor+eps &&
                lengthHand >= -eps && lengthHand < coordinateScaleFactor+eps &&
                lengthUpperLeg >= -eps && lengthUpperLeg < coordinateScaleFactor+eps &&
                lengthLowerLeg >= -eps && lengthLowerLeg < coordinateScaleFactor+eps &&
                lengthFoot >= -eps && lengthFoot < coordinateScaleFactor+eps &&
                weight > -eps && weight <= weightScaleFactor+eps);
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
        if (logWeight) {
            data[nextIndex] = Math.log10(weight+1) / Math.log10(weightScaleFactor+1);
        } else {
            data[nextIndex] = weight / weightScaleFactor;
        }

        return data;
    }

    /**
     * includes animal class!
     * @return vector of size dimension+1
     */
    public double[] getOriginalData() {
        if (!dataSetMaybeComplete()) {
            System.err.println("Incomplete data!");
        }

        double[] data = new double[dimension+1];
        int nextIndex = 0;

        for (Point2d p : spine) {
            data[nextIndex] = p.x;
            data[nextIndex+1] = p.y;
            nextIndex += 2;
        }
        data[nextIndex] = wings; nextIndex++;
        data[nextIndex] = flooredLegs; nextIndex++;
        data[nextIndex] = lengthUpperArm; nextIndex++;
        data[nextIndex] = lengthLowerArm; nextIndex++;
        data[nextIndex] = lengthHand; nextIndex++;
        data[nextIndex] = lengthUpperLeg; nextIndex++;
        data[nextIndex] = lengthLowerLeg; nextIndex++;
        data[nextIndex] = lengthFoot; nextIndex++;
        data[nextIndex] = weight; nextIndex++;
        data[nextIndex] = animalClass.ordinal();

        return data;
    }

    /**
     * @return comma separated names of dimensions
     */
    public static String getDimensionNames() {
        return "neck (0-7), back (6-13), tail (12-19), wings, floored legs, length upper arm, length lower arm, length hand, length upper leg, length lower leg, length foot, weight, animal class";
    }

    public static String getDimensionName(int dimension) {
        String name;
        if (dimension < 20) { // spine
            if (dimension < 6) {
                name = String.format("neck %d%s", dimension/2 + 1, dimension % 2 == 0 ? "x" : "y");
            } else if (dimension < 14) {
                name = String.format("back %d%s", dimension/2 - 2, dimension % 2 == 0 ? "x" : "y");
            } else {
                name = String.format("tail %d%s", dimension/2 - 5, dimension % 2 == 0 ? "x" : "y");
            }
        } else {
            switch (dimension) {
                case 20:
                    name = "wings"; break;
                case 21:
                    name = "floored_legs"; break;
                case 22:
                    name = "length_upper_arm"; break;
                case 23:
                    name = "length_lower_arm"; break;
                case 24:
                    name = "length_hand"; break;
                case 25:
                    name = "length_upper_leg"; break;
                case 26:
                    name = "length_lower_leg"; break;
                case 27:
                    name = "length_foot"; break;
                case 28:
                    name = "weight"; break;
                default:
                    System.err.println("invalid dimension!");
                    name = "";
            }
        }
        return name;
    }

    public static int getDimension() {
        return dimension;
    }

    public String getName() {
        return name;
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
        this.flooredLegs = flooredLegs;
    }

    public void setLengthUpperArm(double length) {
        this.lengthUpperArm = length;
    }

    public void setLengthLowerArm(double length) {
        this.lengthLowerArm = length;
    }

    public void setLengthHand(double length) {
        this.lengthHand = length;
    }

    public void setLengthUpperLeg(double length) {
        this.lengthUpperLeg = length;
    }

    public void setLengthLowerLeg(double length) {
        this.lengthLowerLeg = length;
    }

    public void setLengthFoot(double length) {
        this.lengthFoot = length;
    }

    public void setWeight(double weight) {
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

    public AnimalClass getAnimalClass() {
        return animalClass;
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
     * (param logWeight is set on mean as the first point in the list has)
     */
    public static PcaDataPoint getMean(List<PcaDataPoint> points) {
        if (points.isEmpty()) {
            System.err.println("Cannot calculate mean from empty list");
        } else if (!points.get(0).dataSetMaybeComplete()) {
            System.err.println("Cannot calculate mean from incomplete data set");
        }

        boolean logWeight = points.get(0).logWeight;
        PcaDataPoint mean = new PcaDataPoint(logWeight);
        mean.setName("mean");

        List<Point2d> meanSpine = Arrays.asList(new Point2d(), new Point2d(), new Point2d(), new Point2d(), new Point2d(), new Point2d(), new Point2d(), new Point2d(), new Point2d(), new Point2d());
        double[] otherMeans = new double[dimension-20]; // 20 dimensions for spine

        for (PcaDataPoint point : points) {
            List<Point2d> spine = point.getSpine();
            for (int i = 0; i < spine.size(); i++) {
                meanSpine.get(i).add(spine.get(i));
            }
            otherMeans[0] += point.getWings();
            otherMeans[1] += point.getFlooredLegs();
            otherMeans[2] += point.getLengthUpperArm();
            otherMeans[3] += point.getLengthLowerArm();
            otherMeans[4] += point.getLengthHand();
            otherMeans[5] += point.getLengthUpperLeg();
            otherMeans[6] += point.getLengthLowerLeg();
            otherMeans[7] += point.getLengthFoot();
            if (logWeight) { // needed as we want mean of log weight, not of linear weight
                otherMeans[8] += Math.log10(point.getWeight() + 1);
            } else {
                otherMeans[8] += point.getWeight();
            }
        }

        for (Point2d meanSpinePoint : meanSpine) {
            meanSpinePoint.scale(1.0 / (double) points.size());
        }
        for (int i = 0; i < otherMeans.length; i++) {
            otherMeans[i] /= points.size();
        }
        if (logWeight) { // needed as we want mean of log weight, not of linear weight
            otherMeans[8] = Math.pow(10, otherMeans[8]) - 1;
        }

        mean.setSpine(meanSpine);
        mean.setWings(otherMeans[0]);
        mean.setFlooredLegs(otherMeans[1]);
        mean.setLengthUpperArm(otherMeans[2]);
        mean.setLengthLowerArm(otherMeans[3]);
        mean.setLengthHand(otherMeans[4]);
        mean.setLengthUpperLeg(otherMeans[5]);
        mean.setLengthLowerLeg(otherMeans[6]);
        mean.setLengthFoot(otherMeans[7]);
        mean.setWeight(otherMeans[8]);

        return mean;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PcaDataPoint that = (PcaDataPoint) o;
        if (that.spine == null || that.spine.size() != 10) {
            System.err.println("Found incorrect spine!");
            return false;
        }
        double eps = 0.0001;

        double spine0Diff = that.getSpine().get(0).distance(spine.get(0));
        double spine1Diff = that.getSpine().get(1).distance(spine.get(1));
        double spine2Diff = that.getSpine().get(2).distance(spine.get(2));
        double spine3Diff = that.getSpine().get(3).distance(spine.get(3));
        double spine4Diff = that.getSpine().get(4).distance(spine.get(4));
        double spine5Diff = that.getSpine().get(5).distance(spine.get(5));
        double spine6Diff = that.getSpine().get(6).distance(spine.get(6));
        double spine7Diff = that.getSpine().get(7).distance(spine.get(7));
        double spine8Diff = that.getSpine().get(8).distance(spine.get(8));
        double spine9Diff = that.getSpine().get(9).distance(spine.get(9));

        double wingDiff = Math.abs(that.wings - wings);
        double flooredLegsDiff = Math.abs(that.flooredLegs - flooredLegs);
        double upperArmDiff = Math.abs(that.lengthUpperArm - lengthUpperArm);
        double lowerArmDiff = Math.abs(that.lengthLowerArm - lengthLowerArm);
        double handDiff = Math.abs(that.lengthHand - lengthHand);
        double upperLegDiff = Math.abs(that.lengthUpperLeg - lengthUpperLeg);
        double lowerLegDiff = Math.abs(that.lengthLowerLeg - lengthLowerLeg);
        double footDiff = Math.abs(that.lengthFoot - lengthFoot);
        double weightDiff =  Math.abs(that.weight - weight);

        return spine0Diff < eps && spine1Diff < eps && spine2Diff < eps && spine3Diff < eps &&
                spine4Diff < eps && spine5Diff < eps && spine6Diff < eps && spine7Diff < eps && spine8Diff < eps &&
                spine9Diff < eps &&
                wingDiff < eps && flooredLegsDiff < eps &&
                upperArmDiff < eps && lowerArmDiff < eps && handDiff < eps &&
                upperLegDiff < eps && lowerLegDiff < eps && footDiff < eps &&
                weightDiff < eps;
    }
}
