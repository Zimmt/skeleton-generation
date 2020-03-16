package util.pca;

import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealVector;

import javax.vecmath.Point2d;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
                // if tail condition is present x-coordinate is replaced with correct value when tail is set
                if (i*2 == PcaDimension.TAIL4X.ordinal()) {
                    if (!conditions.hasTailLength()) {
                        currentSpinePoint.x += (scaledEigenvector.getEntry(PcaDimension.BACK4X.ordinal()) +
                                scaledEigenvector.getEntry(eigenvectorPosition++)) * coordinateScaleFactor;
                    }
                } else {
                    currentSpinePoint.x += scaledEigenvector.getEntry(eigenvectorPosition++) * coordinateScaleFactor;
                }

                currentSpinePoint.y += scaledEigenvector.getEntry(eigenvectorPosition++) * coordinateScaleFactor;
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
    public double[] getScaledDataForPCA() { // todo simplify
        if (!dataSetMaybeComplete()) {
            System.err.println("Incomplete data!");
        }

        double[] data = new double[reducedDimension];
        int nextIndex = 0;

        for (int i = 0; i < super.getSpine().size(); i++) {
            Point2d p = super.getSpine().get(i);
            if (!(i*2 == PcaDimension.TAIL4X.ordinal() && conditions.hasTailLength())) {
                data[nextIndex++] = p.x / coordinateScaleFactor;
            }
                data[nextIndex++] = p.y / coordinateScaleFactor;
        }
        if (!conditions.hasWings()) {
            data[nextIndex++] = super.getWings() / (wingScaleFactor * downscaleFactor);
        }
        if (!conditions.hasFlooredLegs()) {
            data[nextIndex++] = super.getFlooredLegs() / (flooredLegsScaleFactor * downscaleFactor);
        }
        data[nextIndex++] = super.getLengthUpperArm() / coordinateScaleFactor;
        data[nextIndex++] = super.getLengthLowerArm() / coordinateScaleFactor;
        data[nextIndex++] = super.getLengthHand() / coordinateScaleFactor;
        data[nextIndex++] = super.getLengthUpperLeg() / coordinateScaleFactor;
        data[nextIndex++] = super.getLengthLowerLeg() / coordinateScaleFactor;
        data[nextIndex++] = super.getLengthFoot() / coordinateScaleFactor;
        if (super.getLogWeight()) {
            data[nextIndex] = Math.log10(super.getWeight()+1) / (Math.log10(weightScaleFactor+1) * downscaleFactor);
        } else {
            data[nextIndex] = super.getWeight() / (weightScaleFactor * downscaleFactor);
        }

        return data;
    }

    @Override
    public void setTail(List<Point2d> tail) {
        if (tail.size() != 4) {
            System.err.println("Tail has not correct number of control points.");
        }
        if (conditions.hasTailLength()) {
            tail.get(3).x = tail.get(0).x + conditions.getTailLength();
        }
        this.tail = tail;
    }

    public static double[] getScaledConditions(PcaConditions conditions) {
        double[] scaledConditions = new double[conditions.getConditionCount()];
        int next = 0;
        if (conditions.hasTailLength()) {
            scaledConditions[next++] = conditions.getTailLength() / coordinateScaleFactor;
        }
        if (conditions.hasWings()) {
            scaledConditions[next++] = conditions.getWings() / (wingScaleFactor * downscaleFactor);
        }
        if (conditions.hasFlooredLegs()) {
            scaledConditions[next++] = conditions.getFlooredLegs() / (flooredLegsScaleFactor * downscaleFactor);
        }
        return scaledConditions;
    }

    public static PcaDataPointConditioned newPointWithValuesFromScaledData(double[] scaledData, PcaConditions conditions, boolean logWeight) {
        double[] scaledConditions = getScaledConditions(conditions);
        double[] scaledDataWithConditions = new double[PcaDataPoint.getDimension()];
        int nextWithoutConditions = 0;
        for (int i = 0; i < PcaDataPoint.getDimension(); i++) {
            if (i == PcaDimension.TAIL4Y.ordinal() && conditions.hasTailLength()) {
                scaledDataWithConditions[i] = scaledConditions[0];
            } else if (i == PcaDimension.WINGS.ordinal() && conditions.hasWings()) {
                scaledDataWithConditions[i] = scaledConditions[1];
            } else if (i == PcaDimension.FLOORED_LEGS.ordinal() && conditions.hasFlooredLegs()) {
                scaledDataWithConditions[i] = scaledConditions[2];
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
