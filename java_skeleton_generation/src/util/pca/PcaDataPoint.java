package util.pca;

import javax.vecmath.Point2d;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PcaDataPoint {
    private static final double coordinateScaleFactor = 1000;
    private static final double animalClassScaleFactor = 4;
    private static final double flooredLegsScaleFactor = 2;
    private static final double weightScaleFactor = 120000;

    private List<Point2d> neck; // if not there empty, else 4 points (1 point is contained in back)
    private List<Point2d> back; // the 4 control points of cubic bezier curve
    private List<Point2d> tail; // if not there empty, else 4 points (1 point is contained in back)
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

    // TODO: get scaled data

    public boolean dataSetMaybeComplete() {
        // all other data has primitive types and is set or has default value
        return spine != null && animalClass > 0 && weight > 0;
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
        calculateSpine();
    }

    public void setBack(List<Point2d> back) {
        if (back.size() != 4) {
            System.err.println("Back has not correct number of control points.");
        }
        this.back = back;
        calculateSpine();
    }

    public void setTail(List<Point2d> tail) {
        if (tail.size() != 4) {
            System.err.println("Tail has not correct number of control points.");
        }
        this.tail = tail;
        calculateSpine();
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
     */
    private void calculateSpine() {
        if (neck == null || back == null || tail == null) {
            return; // can only calculate this when all data is there
        }
        List<Point2d> sortedPoints = new ArrayList<>();

        if (neck.get(0).x > neck.get(3).x) {
            Collections.reverse(neck);
        }
        sortedPoints.addAll(neck);

        if (back.get(0).x > back.get(3).x) {
            Collections.reverse(back);
        }
        if (!sortedPoints.get(3).epsilonEquals(back.get(0), 0.1)) {
            System.err.println("Neck and back don't share point!");
        }
        sortedPoints.remove(3);
        sortedPoints.addAll(back);

        if (tail.get(0).x > tail.get(3).x) {
            Collections.reverse(tail);
        }
        if (!sortedPoints.get(6).epsilonEquals(tail.get(0), 0.1)) {
            System.err.println("Back and tail don't share point!");
        }
        sortedPoints.remove(6);
        sortedPoints.addAll(tail);

        this.spine = sortedPoints;
    }
}
