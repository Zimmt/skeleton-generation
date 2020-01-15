package util.pca;

import javax.vecmath.Point2d;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PcaDataPoint {
    private static final int dimension = 28;
    private static final double coordinateScaleFactor = 1000;
    private static final double animalClassScaleFactor = 4;
    private static final double flooredLegsScaleFactor = 2;
    private static final double weightScaleFactor = 120000;

    private List<Point2d> neck = new ArrayList<>(); // if not there empty, else 4 points (1 point is contained in back)
    private List<Point2d> back; // the 4 control points of cubic bezier curve
    private List<Point2d> tail = new ArrayList<>(); // if not there empty, else 4 points (1 point is contained in back)
    private List<Point2d> spine; // is calculated from neck, back and tail, consists of 10 points

    private int animalClass = -1; // 0: fish, 1: amphibian, 2: reptilian, 3: bird, 4: mammal

    private boolean wings;
    private int flooredLegs; // #legs/2, [0,2]
    private boolean arms;

    private double lengthFrontLegs; // [0, 1000]
    private double lengthBackLegs; // [0, 1000]
    private double lengthWings; // [0, 1000]

    private double weight; // [0, 120.000]

    public PcaDataPoint() {}

    public boolean dataSetMaybeComplete() {
        // all other data has primitive types and is set or has default value
        return spine != null && animalClass > 0 && weight > 0;
    }

    /**
     * Need to call this to calculate spine before pca data can be generated!
     * Generates spine if data set maybe complete
     * @return true if successful, false otherwise
     */
    public boolean processData() {
        if (back != null && animalClass > 0 && weight > 0) {
            return calculateSpine();
        } else {
            return false;
        }
    }

    /**
     * Over all there are 28 dimensions:
     * spine: 20 doubles = 10 points
     * animal class: 1
     * extremities: 3
     * extremity lengths: 3
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
        data[nextIndex] = animalClass / animalClassScaleFactor; nextIndex++;
        data[nextIndex] = wings ? 1.0 : 0.0; nextIndex++;
        data[nextIndex] = flooredLegs / flooredLegsScaleFactor; nextIndex++;
        data[nextIndex] = arms ? 1.0 : 0.0; nextIndex++;
        data[nextIndex] = lengthFrontLegs / coordinateScaleFactor; nextIndex++;
        data[nextIndex] = lengthBackLegs / coordinateScaleFactor; nextIndex++;
        data[nextIndex] = lengthWings / coordinateScaleFactor; nextIndex++;
        data[nextIndex] = weight / weightScaleFactor;

        return data;
    }

    public static int getDimension() {
        return dimension;
    }

    public List<Point2d> getNeck() {
        return neck;
    }

    public List<Point2d> getBack() {
        return back;
    }

    public List<Point2d> getTail() {
        return tail;
    }

    public int getAnimalClass() {
        return animalClass;
    }

    public boolean hasWings() {
        return wings;
    }

    public int getFlooredLegs() {
        return flooredLegs;
    }

    public boolean hasArms() {
        return arms;
    }

    public double getLengthFrontLegs() {
        return lengthFrontLegs;
    }

    public double getLengthBackLegs() {
        return lengthBackLegs;
    }

    public double getLengthWings() {
        return lengthWings;
    }

    public double getWeight() {
        return weight;
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
        this.animalClass = animalClass;
    }

    public void setWings(boolean wings) {
        this.wings = wings;
    }

    public void setFlooredLegs(int flooredLegs) {
        if (flooredLegs < 0 || flooredLegs > 2) {
            System.err.println("Incorrect number of floored legs found.");
        }
        this.flooredLegs = flooredLegs;
    }

    public void setArms(boolean arms) {
        this.arms = arms;
    }

    public void setLengthFrontLegs(double lengthFrontLegs) {
        if (lengthFrontLegs < 0 || lengthFrontLegs > 1000) {
            System.err.println("Incorrect length of front legs found.");
        }
        this.lengthFrontLegs = lengthFrontLegs;
    }

    public void setLengthBackLegs(double lengthBackLegs) {
        if (lengthBackLegs < 0 || lengthBackLegs > 1000) {
            System.err.println("Incorrect length of back legs found.");
        }
        this.lengthBackLegs = lengthBackLegs;
    }

    public void setLengthWings(double lengthWings) {
        if (lengthWings < 0 || lengthWings > 1000) {
            System.err.println("Incorrect length of wings found.");
        }
        this.lengthWings = lengthWings;
    }

    public void setWeight(double weight) {
        if (weight < 0 || weight > 120000) {
            System.err.println("Incorrect weight found.");
        }
        this.weight = weight;
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
        } else if (neck.get(0).x > neck.get(3).x) {
            Collections.reverse(neck);
        }
        sortedPoints.addAll(neck);

        // back
        if (!sortedPoints.get(3).epsilonEquals(back.get(0), 0.1)) {
            System.err.println("Neck and back don't share point!");
        }
        sortedPoints.remove(3);
        sortedPoints.addAll(back);

        //tail
        if (tail.isEmpty()) {
            for (int i = 0; i < 4; i++) {
                tail.add(back.get(3));
            }
        } else if (tail.get(0).x > tail.get(3).x) {
            Collections.reverse(tail);
        }
        if (!sortedPoints.get(6).epsilonEquals(tail.get(0), 0.1)) {
            System.err.println("Back and tail don't share point!");
        }
        sortedPoints.remove(6);
        sortedPoints.addAll(tail);

        this.spine = sortedPoints;

        return true;
    }
}
