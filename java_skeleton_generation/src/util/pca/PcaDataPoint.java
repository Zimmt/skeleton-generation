package util.pca;

import javax.vecmath.Point2d;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class PcaDataPoint {
    private static final int dimension = 25; //TODO
    private static final double coordinateScaleFactor = 1000;
    private static final double flooredLegsScaleFactor = 2;
    private static final double weightScaleFactor = 120000;

    private String name;

    private List<Point2d> neck = new ArrayList<>(); // if not there empty, else 4 points (1 point is contained in back)
    private List<Point2d> back; // the 4 control points of cubic bezier curve
    private List<Point2d> tail = new ArrayList<>(); // if not there empty, else 4 points (1 point is contained in back)
    private List<Point2d> spine; // is calculated from neck, back and tail, consists of 10 points

    private AnimalClass animalClass;

    private boolean wings;
    private double flooredLegs; // #legs/2, [0,2]
    private boolean arms;

    private double lengthFrontLegs; // [0, 1000]
    private double lengthBackLegs; // [0, 1000]
    private double lengthWings; // [0, 1000]

    private double weight; // [0, 120.000]

    public PcaDataPoint() {}

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
     * Over all there are 27 dimensions (animal class is not a dimension for PCA as it has no continuous scala):
     * spine: 20 doubles = 10 points
     * extremities: 3
     * extremity lengths: 3 TODO: 2?
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
        data[nextIndex] = wings ? 1.0 : 0.0; nextIndex++;
        data[nextIndex] = flooredLegs / flooredLegsScaleFactor; nextIndex++;
        data[nextIndex] = lengthFrontLegs / coordinateScaleFactor; nextIndex++;
        data[nextIndex] = lengthBackLegs / coordinateScaleFactor; nextIndex++;
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

    public void setWings(boolean wings) {
        this.wings = wings;
    }

    public void setFlooredLegs(double flooredLegs) {
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

    public boolean hasWings() {
        return wings;
    }

    public boolean hasArms() {
        return arms;
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

    public List<Point2d> getSpine() {
        return spine;
    }

    public double getFlooredLegs() {
        return flooredLegs;
    }

    public double getLengthFrontLegs() {
        return lengthFrontLegs;
    }

    public double getLengthBackLegs() {
        return lengthBackLegs;
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

    public static PcaDataPoint getMean(List<PcaDataPoint> points) {
        if (points.isEmpty()) {
            System.err.println("Cannot calculate mean from empty list");
        } else if (!points.get(0).dataSetMaybeComplete()) {
            System.err.println("Cannot calculate mean from incomplete data set");
        }

        PcaDataPoint mean = new PcaDataPoint();
        mean.setName("mean");

        List<Point2d> meanNeck = Arrays.asList(new Point2d(0,0), new Point2d(0,0), new Point2d(0,0), new Point2d(0,0));
        List<Point2d> meanBack = Arrays.asList(new Point2d(0,0), new Point2d(0,0), new Point2d(0,0), new Point2d(0,0));
        List<Point2d> meanTail = Arrays.asList(new Point2d(0,0), new Point2d(0,0), new Point2d(0,0), new Point2d(0,0));
        List<Point2d> meanSpine = Arrays.asList(new Point2d(0,0), new Point2d(0,0), new Point2d(0,0), new Point2d(0,0), new Point2d(0,0), new Point2d(0,0), new Point2d(0,0), new Point2d(0,0), new Point2d(0,0), new Point2d(0,0));
        double meanFlooredLegs = 0.0;
        double meanLengthFrontLegs = 0.0;
        double meanLengthBackLegs = 0.0;
        double meanWeight = 0.0;

        for (PcaDataPoint point : points) {
            List<Point2d> neck = point.getNeck();
            for (int i = 0; i < neck.size(); i++) {
                meanNeck.get(i).add(neck.get(i));
            }
            List<Point2d> back = point.getBack();
            for (int i = 0; i < back.size(); i++) {
                meanBack.get(i).add(back.get(i));
            }
            List<Point2d> tail = point.getTail();
            for (int i = 0; i < tail.size(); i++) {
                meanTail.get(i).add(tail.get(i));
            }
            List<Point2d> spine = point.getSpine();
            for (int i = 0; i < spine.size(); i++) {
                meanSpine.get(i).add(spine.get(i));
            }
            meanFlooredLegs += point.getFlooredLegs();
            meanLengthFrontLegs += point.getLengthFrontLegs();
            meanLengthBackLegs += point.getLengthBackLegs();
            meanWeight += point.getWeight();
        }

        for (Point2d meanNeckPoint : meanNeck) {
            meanNeckPoint.scale(1.0 / (double) points.size());
        }
        for (Point2d meanBackPoint : meanBack) {
            meanBackPoint.scale(1.0 / (double) points.size());
        }
        for (Point2d meanTailPoint : meanTail) {
            meanTailPoint.scale(1.0 / (double) points.size());
        }
        for (Point2d meanSpinePoint : meanSpine) {
            meanSpinePoint.scale(1.0 / (double) points.size());
        }
        meanFlooredLegs /= points.size();
        meanLengthFrontLegs /= points.size();
        meanLengthBackLegs /= points.size();
        meanWeight /= points.size();

        mean.setNeck(meanNeck);
        mean.setBack(meanBack);
        mean.setTail(meanTail);
        mean.setSpine(meanSpine);
        mean.setFlooredLegs(meanFlooredLegs);
        mean.setLengthFrontLegs(meanLengthFrontLegs);
        mean.setLengthBackLegs(meanLengthBackLegs);
        mean.setWeight(meanWeight);

        return mean;
    }
}
