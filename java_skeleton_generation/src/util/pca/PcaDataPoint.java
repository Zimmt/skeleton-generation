package util.pca;

import org.apache.commons.math3.linear.RealVector;

import javax.vecmath.Point2d;
import java.util.*;

/**
 * Attention!
 * For PCA the data is scaled by the factors below.
 * Also the x-coordinate of the last control point of tail represents the difference to the x-coordinate of the first control point.
 */
public class PcaDataPoint {
    private static final int dimension = 29;
    static final double coordinateScaleFactor = 1000;
    static final double wingScaleFactor = 1;
    static final double flooredLegsScaleFactor = 2;
    static final double weightScaleFactor = 120000;
    static final double downscaleFactor = 100;

    String name;

    List<Point2d> neck = new ArrayList<>(); // if not there empty, else 4 points (1 point is contained in back)
    List<Point2d> back; // the 4 control points of cubic bezier curve
    List<Point2d> tail = new ArrayList<>(); // if not there empty, else 4 points (1 point is contained in back)
    List<Point2d> spine; // is calculated from neck, back and tail (should not be set "by hand"), consists of 10 points

    double wings;
    double flooredLegs; // #legs/2, [0,2]

    double lengthUpperArm; // [0, 1000]
    double lengthLowerArm; // [0, 1000]
    double lengthHand; // [0, 1000]

    double lengthUpperLeg; // [0, 1000]
    double lengthLowerLeg; // [0, 1000]
    double lengthFoot; // [0, 1000]

    double weight; // [0, 120.000]
    private boolean logWeight;

    AnimalClass animalClass;

    public PcaDataPoint(boolean logWeight) {
        this.logWeight = logWeight;
    }

    PcaDataPoint(String name, List<Point2d> neck, List<Point2d> back, List<Point2d> tail, List<Point2d> spine,
                 double wings, double flooredLegs,
                 double lengthUpperArm, double lengthLowerArm, double lengthHand,
                 double lengthUpperLeg, double lengthLowerLeg, double lengthFoot,
                 double weight, boolean logWeight, AnimalClass animalClass) {
        this.name = name;
        this.neck = neck;
        this.back = back;
        this.tail = tail;
        this.spine = spine;
        this.wings = wings;
        this.flooredLegs = flooredLegs;
        this.lengthUpperArm = lengthUpperArm;
        this.lengthLowerArm = lengthLowerArm;
        this.lengthHand = lengthHand;
        this.lengthUpperLeg = lengthUpperLeg;
        this.lengthLowerLeg = lengthLowerLeg;
        this.lengthFoot = lengthFoot;
        this.weight = weight;
        this.logWeight = logWeight;
        this.animalClass = animalClass;
    }

    public void setAllZeros() {
        neck = Arrays.asList(new Point2d(), new Point2d(), new Point2d(), new Point2d());
        back = Arrays.asList(new Point2d(), new Point2d(), new Point2d(), new Point2d());
        tail = Arrays.asList(new Point2d(), new Point2d(), new Point2d(), new Point2d());
        spine = Arrays.asList(new Point2d(), new Point2d(), new Point2d(), new Point2d(), new Point2d(), new Point2d(), new Point2d(), new Point2d(), new Point2d(), new Point2d());
        wings = 0f;
        flooredLegs = 0f;
        lengthUpperArm = 0f;
        lengthLowerArm = 0f;
        lengthHand = 0f;
        lengthUpperLeg = 0f;
        lengthLowerLeg = 0f;
        lengthFoot = 0f;
        weight = 0f;
    }

    /**
     * Creates a new point moved by the given vectors.
     * Changes: neck, back, tail, spine, wings, flooredLegs, length of extremities, weight
     * leaves empty: name, animalClass
     */
    public PcaDataPoint getMovedPoint(List<RealVector> scaledEigenvectors) {
        PcaDataPoint point = new PcaDataPoint(logWeight);

        List<Point2d> newSpine = new ArrayList<>(spine.size());
        for (Point2d p : spine) {
            newSpine.add(new Point2d(p));
        }
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
            if (scaledEigenvector.getDimension() != dimension) {
                System.err.println("Found eigenvector with wrong dimension. Can't calculate moved point.");
                return null;
            }

            int eigenvectorPosition = 0;
            for (int i = 0; i < newSpine.size(); i++) {
                Point2d currentSpinePoint = newSpine.get(i);

                // x-coordinate of last control point of tail in PCA space represents difference to x-coordinate of first control point
                if (i*2 == PcaDimension.TAIL4X.ordinal()) {
                    currentSpinePoint.x += (scaledEigenvector.getEntry(PcaDimension.BACK4X.ordinal()) +
                        scaledEigenvector.getEntry(eigenvectorPosition++)) * coordinateScaleFactor;
                } else {
                    currentSpinePoint.x += scaledEigenvector.getEntry(eigenvectorPosition++) * coordinateScaleFactor;
                }

                // y-coordinate of first control point of back in PCA space represents difference to y-coordinate of first control point of neck
                if (i*2 == PcaDimension.BACK1X.ordinal()) {
                    currentSpinePoint.y += (scaledEigenvector.getEntry(PcaDimension.NECK1Y.ordinal()) -
                            scaledEigenvector.getEntry(eigenvectorPosition++)) * coordinateScaleFactor;
                } else {
                    currentSpinePoint.y += scaledEigenvector.getEntry(eigenvectorPosition++) * coordinateScaleFactor;
                }
            }

            newWings += scaledEigenvector.getEntry(PcaDimension.WINGS.ordinal()) * wingScaleFactor * downscaleFactor;
            newFlooredLegs += scaledEigenvector.getEntry(PcaDimension.FLOORED_LEGS.ordinal()) * flooredLegsScaleFactor * downscaleFactor;
            newLengthUpperArm += scaledEigenvector.getEntry(PcaDimension.LENGTH_UPPER_ARM.ordinal()) * coordinateScaleFactor;
            newLengthLowerArm += scaledEigenvector.getEntry(PcaDimension.LENGTH_LOWER_ARM.ordinal()) * coordinateScaleFactor;
            newLengthHand += scaledEigenvector.getEntry(PcaDimension.LENGTH_HAND.ordinal()) * coordinateScaleFactor;
            newLengthUpperLeg += scaledEigenvector.getEntry(PcaDimension.LENGTH_UPPER_LEG.ordinal()) * coordinateScaleFactor;
            newLengthLowerLeg += scaledEigenvector.getEntry(PcaDimension.LENGTH_LOWER_LEG.ordinal()) * coordinateScaleFactor;
            newLengthFoot += scaledEigenvector.getEntry(PcaDimension.LENGTH_FOOT.ordinal()) * coordinateScaleFactor;
            if (logWeight) {
                // reverse from log(weight+1) / (log(scale+1)*downscale)
                newWeight += Math.pow(10, scaledEigenvector.getEntry(PcaDimension.WEIGHT.ordinal()) * Math.log10(weightScaleFactor + 1) * downscaleFactor) - 1;
            } else {
                newWeight += scaledEigenvector.getEntry(PcaDimension.WEIGHT.ordinal()) * weightScaleFactor * downscaleFactor;
            }
        }

        point.setNeck(new ArrayList<>(newSpine.subList(0, 4)));
        point.setBack(new ArrayList<>(newSpine.subList(3, 7)));
        point.setTail(new ArrayList<>(newSpine.subList(6, 10)));
        point.setWings(newWings);
        point.setFlooredLegs(newFlooredLegs);
        point.setLengthUpperArm(newLengthUpperArm);
        point.setLengthLowerArm(newLengthLowerArm);
        point.setLengthHand(newLengthHand);
        point.setLengthUpperLeg(newLengthUpperLeg);
        point.setLengthLowerLeg(newLengthLowerLeg);
        point.setLengthFoot(newLengthFoot);
        point.setWeight(newWeight);
        point.processData();

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

        for (int i = 0; i < spine.size(); i++) {
            Point2d p = spine.get(i);
            if (i*2 == PcaDimension.TAIL4X.ordinal()) {
                data[nextIndex++] = (p.x - spine.get(PcaDimension.BACK4X.ordinal()/2).x) / coordinateScaleFactor;
            } else {
                data[nextIndex++] = p.x / coordinateScaleFactor;
            }

            if (i*2 == PcaDimension.BACK1X.ordinal()) {
                data[nextIndex++] = (spine.get(PcaDimension.NECK1X.ordinal()/2).y - p.y) / coordinateScaleFactor;
            } else {
                data[nextIndex++] = p.y / coordinateScaleFactor;
            }
        }
        data[nextIndex++] = wings / (wingScaleFactor * downscaleFactor);
        data[nextIndex++] = flooredLegs / (flooredLegsScaleFactor * downscaleFactor);
        data[nextIndex++] = lengthUpperArm / coordinateScaleFactor;
        data[nextIndex++] = lengthLowerArm / coordinateScaleFactor;
        data[nextIndex++] = lengthHand / coordinateScaleFactor;
        data[nextIndex++] = lengthUpperLeg / coordinateScaleFactor;
        data[nextIndex++] = lengthLowerLeg / coordinateScaleFactor;
        data[nextIndex++] = lengthFoot / coordinateScaleFactor;
        if (logWeight) {
            if (weight < 0) {
                data[nextIndex] = 0.0;
            } else {
                data[nextIndex] = Math.log10(weight+1) / (Math.log10(weightScaleFactor+1) * downscaleFactor);
            }
        } else {
            data[nextIndex] = weight / (weightScaleFactor * downscaleFactor);
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
            data[nextIndex++] = p.x;
            data[nextIndex++] = p.y;
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
        return PcaDimension.values()[dimension].name().toLowerCase();
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

    public boolean getLogWeight() {
        return logWeight;
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
     * animal class and name are left empty
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
            // no special case for tail length needed as mean of differences is the same as the difference of means
            meanSpinePoint.scale(1.0 / (double) points.size());
        }
        for (int i = 0; i < otherMeans.length; i++) {
            otherMeans[i] /= points.size();
        }
        if (logWeight) { // needed as we want mean of log weight, not of linear weight
            otherMeans[8] = Math.pow(10, otherMeans[8]) - 1;
        }

        mean.setNeck(new ArrayList<>(meanSpine.subList(0, 4)));
        mean.setBack(new ArrayList<>(meanSpine.subList(3, 7)));
        mean.setTail(new ArrayList<>(meanSpine.subList(6, 10)));
        mean.setWings(otherMeans[0]);
        mean.setFlooredLegs(otherMeans[1]);
        mean.setLengthUpperArm(otherMeans[2]);
        mean.setLengthLowerArm(otherMeans[3]);
        mean.setLengthHand(otherMeans[4]);
        mean.setLengthUpperLeg(otherMeans[5]);
        mean.setLengthLowerLeg(otherMeans[6]);
        mean.setLengthFoot(otherMeans[7]);
        mean.setWeight(otherMeans[8]);
        mean.processData();

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
