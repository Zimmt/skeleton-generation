package util.pca;

import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealVector;

import javax.vecmath.Point2d;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;

public class PcaDataPointConditioned extends PcaDataPoint {

    private PcaConditions conditions;
    private int reducedDimension;

    public PcaDataPointConditioned(boolean logWeight, PcaConditions conditions) {
        super(logWeight);
        this.conditions = conditions;
        this.reducedDimension = PcaDataPoint.getDimension() - conditions.getConditionCount();
        conditions.setAbsoluteConditions(this);
    }

    public PcaDataPointConditioned(PcaDataPoint point, PcaConditions conditions) {
        super(point.name, point.neck, point.back, point.tail, point.spine,
                point.wings, point.flooredLegs, point.lengthUpperArm, point.lengthLowerArm, point.lengthHand,
                point.lengthUpperLeg, point.lengthLowerLeg, point.lengthFoot,
                point.weight, point.getLogWeight(), point.animalClass);
        this.conditions = conditions;
        this.reducedDimension = PcaDataPoint.getDimension() - conditions.getConditionCount();
        conditions.setAbsoluteConditions(this);
    }

    @Override
    public PcaDataPointConditioned getMovedPoint(List<RealVector> scaledEigenvectors) {
        PcaDataPointConditioned conditionedPoint = new PcaDataPointConditioned(super.getLogWeight(), conditions);

        List<Point2d> newSpine = new ArrayList<>(super.getSpine().size());
        for (Point2d p : super.getSpine()) {
            newSpine.add(new Point2d(p));
        }
        double newWings = super.getWings();
        double newFlooredLegs = super.getFlooredLegs();
        double newLengthUpperArm = super.getLengthUpperArm();
        double newLengthLowerArm = super.getLengthLowerArm();
        double newLengthHand = super.getLengthHand();
        double newLengthUpperLeg = super.getLengthUpperLeg();
        double newLengthLowerLeg = super.getLengthLowerLeg();
        double newLengthFoot = super.getLengthFoot();
        double newWeight = super.getWeight();

        for (RealVector scaledEigenvector : scaledEigenvectors) {
            if (scaledEigenvector.getDimension() != reducedDimension) {
                System.err.println("Found eigenvector with wrong dimension. Can't calculate moved point.");
                return null;
            }
            int eigenvectorPosition = 0;

            for (int i = 0; i < newSpine.size(); i++) {
                Point2d currentSpinePoint = newSpine.get(i);

                // x-coordinate of last control point of tail in PCA space represents difference to x-coordinate of first control point
                // if tail condition is present x-coordinate is replaced with correct value when tail is set (do NOT advance eigenvectorPosition!)
                if (i*2 == PcaDimension.TAIL4X.ordinal()) {
                    if (!conditions.hasTailLength()) {
                        currentSpinePoint.x += (scaledEigenvector.getEntry(PcaDimension.BACK4X.ordinal()) +
                                scaledEigenvector.getEntry(eigenvectorPosition++)) * coordinateScaleFactor;
                    }
                } else {
                    currentSpinePoint.x += scaledEigenvector.getEntry(eigenvectorPosition++) * coordinateScaleFactor;
                }

                // y-coordinate of first control point of back in PCA space represents difference to y-coordinate of first control point of neck
                // if neck condition is present y-coordinate is replaced with correct value when neck is set (do NOT advance eigenvectorPosition!)
                if (i*2 == PcaDimension.BACK1X.ordinal()) {
                    if (!conditions.hasNeckLength()) {
                        currentSpinePoint.y += (scaledEigenvector.getEntry(PcaDimension.NECK1Y.ordinal()) -
                                scaledEigenvector.getEntry(eigenvectorPosition++)) * coordinateScaleFactor;
                    }
                } else {
                    currentSpinePoint.y += scaledEigenvector.getEntry(eigenvectorPosition++) * coordinateScaleFactor;
                }
            }

            if (!conditions.hasWings()) {
                newWings += scaledEigenvector.getEntry(eigenvectorPosition++) * wingScaleFactor * downscaleFactor;
            }
            if (!conditions.hasFlooredLegs()) {
                newFlooredLegs += scaledEigenvector.getEntry(eigenvectorPosition++) * flooredLegsScaleFactor * downscaleFactor;
            }
            newLengthUpperArm += scaledEigenvector.getEntry(eigenvectorPosition++) * coordinateScaleFactor;
            newLengthLowerArm += scaledEigenvector.getEntry(eigenvectorPosition++) * coordinateScaleFactor;
            newLengthHand += scaledEigenvector.getEntry(eigenvectorPosition++) * coordinateScaleFactor;
            newLengthUpperLeg += scaledEigenvector.getEntry(eigenvectorPosition++) * coordinateScaleFactor;
            newLengthLowerLeg += scaledEigenvector.getEntry(eigenvectorPosition++) * coordinateScaleFactor;
            newLengthFoot += scaledEigenvector.getEntry(eigenvectorPosition++) * coordinateScaleFactor;
            if (super.getLogWeight()) {
                // reverse from log(weight+1) / (log(scale+1)*downscale)
                newWeight += Math.pow(10, scaledEigenvector.getEntry(eigenvectorPosition) * Math.log10(weightScaleFactor + 1) * downscaleFactor) - 1;
            } else {
                newWeight += scaledEigenvector.getEntry(eigenvectorPosition) * weightScaleFactor * downscaleFactor;
            }
        }

        conditionedPoint.setNeck(new ArrayList<>(newSpine.subList(0, 4)));
        conditionedPoint.setBack(new ArrayList<>(newSpine.subList(3, 7)));
        conditionedPoint.setTail(new ArrayList<>(newSpine.subList(6, 10)));
        // if absolute conditions are present values are set in advance, so don't overwrite them!
        if (!conditions.hasWings()) {
            conditionedPoint.setWings(newWings);
        }
        if (!conditions.hasFlooredLegs()) {
            conditionedPoint.setFlooredLegs(newFlooredLegs);
        }
        conditionedPoint.setLengthUpperArm(newLengthUpperArm);
        conditionedPoint.setLengthLowerArm(newLengthLowerArm);
        conditionedPoint.setLengthHand(newLengthHand);
        conditionedPoint.setLengthUpperLeg(newLengthUpperLeg);
        conditionedPoint.setLengthLowerLeg(newLengthLowerLeg);
        conditionedPoint.setLengthFoot(newLengthFoot);
        conditionedPoint.setWeight(newWeight);
        conditionedPoint.processData(); // generate spine from neck, back and tail

        return conditionedPoint;
    }

    @Override
    public double[] getScaledDataForPCA() {
        if (!dataSetMaybeComplete()) {
            System.err.println("Incomplete data!");
        }

        List<Double> data = DoubleStream.of(super.getScaledDataForPCA()).boxed().collect(Collectors.toList());
        int removed = 0;

        if (conditions.hasNeckLength()) {
            data.remove(PcaDimension.BACK1Y.ordinal() - removed++);
        }
        if (conditions.hasTailLength()) {
            data.remove(PcaDimension.TAIL4X.ordinal() - removed++);
        }
        if (conditions.hasWings()) {
            data.remove(PcaDimension.WINGS.ordinal() - removed++);
        }
        if (conditions.hasFlooredLegs()) {
            data.remove(PcaDimension.FLOORED_LEGS.ordinal() - removed++);
        }

        return data.stream().mapToDouble(x -> x).toArray();
    }

    @Override
    public void setTail(List<Point2d> tail) {
        super.setTail(tail);
        if (conditions.hasTailLength()) {
            tail.get(3).x = tail.get(0).x + conditions.getTailXLength();
        }
    }

    @Override
    public void setNeck(List<Point2d> neck) {
        super.setNeck(neck);
        if (conditions.hasNeckLength()) {
            double neck4yValue = neck.get(0).y - conditions.getNeckYLength();
            neck.get(3).y = neck4yValue;
            if (back != null && back.size() == 4) { // set new value also on back if it is already initialized
                back.set(0, new Point2d(back.get(0).x, neck4yValue));
            }
        }
    }

    @Override
    public void setBack(List<Point2d> back) {
        super.setBack(back);
        if (conditions.hasNeckLength() && neck != null && neck.size() == 4) { // set new neck value also on back if neck is already initialized
            back.set(0, new Point2d(back.get(0).x, neck.get(3).y));
        }
    }

    public static double[] getScaledConditions(PcaConditions conditions) {
        double[] scaledConditions = new double[conditions.getConditionCount()];
        int next = 0;
        if (conditions.hasNeckLength()) {
            scaledConditions[next++] = conditions.getNeckYLength() / coordinateScaleFactor;
        }
        if (conditions.hasTailLength()) {
            scaledConditions[next++] = conditions.getTailXLength() / coordinateScaleFactor;
        }
        if (conditions.hasWings()) {
            scaledConditions[next++] = conditions.getWings() / (wingScaleFactor * downscaleFactor);
        }
        if (conditions.hasFlooredLegs()) {
            scaledConditions[next++] = conditions.getFlooredLegs() / (flooredLegsScaleFactor * downscaleFactor);
        }
        return scaledConditions;
    }

    /**
     * @param scaledData scaled pca data without conditioned values
     * @return new conditioned pca data point
     */
    public static PcaDataPointConditioned newPointWithValuesFromScaledData(double[] scaledData, PcaConditions conditions, boolean logWeight) {
        // generate vector of scaled data with conditions
        double[] scaledConditions = getScaledConditions(conditions);
        double[] scaledDataWithConditions = new double[PcaDataPoint.getDimension()];
        int nextWithConditions = 0;
        int nextWithoutConditions = 0;
        for (int i = 0; i < PcaDataPoint.getDimension(); i++) {
            if (i == PcaDimension.BACK1Y.ordinal() && conditions.hasNeckLength()) {
                scaledDataWithConditions[i] = scaledConditions[nextWithConditions++];
            } else if (i == PcaDimension.TAIL4X.ordinal() && conditions.hasTailLength()) {
                scaledDataWithConditions[i] = scaledConditions[nextWithConditions++];
            } else if (i == PcaDimension.WINGS.ordinal() && conditions.hasWings()) {
                scaledDataWithConditions[i] = scaledConditions[nextWithConditions++];
            } else if (i == PcaDimension.FLOORED_LEGS.ordinal() && conditions.hasFlooredLegs()) {
                scaledDataWithConditions[i] = scaledConditions[nextWithConditions++];
            } else {
                scaledDataWithConditions[i] = scaledData[nextWithoutConditions++];
            }
        }
        RealVector scale = MatrixUtils.createRealVector(scaledDataWithConditions);

        PcaDataPoint pointWithCorrectValues = new PcaDataPoint(logWeight);
        pointWithCorrectValues.setAllZeros();
        pointWithCorrectValues = pointWithCorrectValues.getMovedPoint(Collections.singletonList(scale));

        return new PcaDataPointConditioned(pointWithCorrectValues, conditions);
    }
}
