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
        conditions.setConditions(this);
    }

    public PcaDataPointConditioned(PcaDataPoint point, PcaConditions conditions) {
        super(point.name, point.neck, point.back, point.tail, point.spine,
                point.wings, point.flooredLegs, point.lengthUpperArm, point.lengthLowerArm, point.lengthHand,
                point.lengthUpperLeg, point.lengthLowerLeg, point.lengthFoot,
                point.weight, point.getLogWeight(), point.animalClass);
        this.conditions = conditions;
        this.reducedDimension = PcaDataPoint.getDimension() - conditions.getConditionCount();
        conditions.setConditions(this);
    }

    @Override
    public PcaDataPointConditioned getMovedPoint(List<RealVector> scaledEigenvectors) {
        PcaDataPointConditioned conditionedPoint = new PcaDataPointConditioned(super.getLogWeight(), conditions);

        List<Point2d> newSpine = new ArrayList<>();
        for (int i = 0; i < super.getSpine().size(); i++) {
            Point2d p = new Point2d(super.getSpine().get(i));
            for (RealVector scaledEigenvector : scaledEigenvectors) {
                if (scaledEigenvector.getDimension() != reducedDimension) {
                    System.err.println("Wrong eigenvector dimension found!");
                    return null;
                }
                p.add(new Point2d(scaledEigenvector.getEntry(2*i) * coordinateScaleFactor, scaledEigenvector.getEntry(2*i + 1) * coordinateScaleFactor));
            }
            newSpine.add(p);
        }
        conditionedPoint.setSpine(newSpine);

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
            int eigenvectorPosition = 20;

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

        return conditionedPoint;
    }

    @Override
    public double[] getScaledDataForPCA() {
        if (!dataSetMaybeComplete()) {
            System.err.println("Incomplete data!");
        }

        double[] data = new double[reducedDimension];
        int nextIndex = 0;

        for (Point2d p : super.getSpine()) {
            data[nextIndex] = p.x / coordinateScaleFactor;
            data[nextIndex+1] = p.y / coordinateScaleFactor;
            nextIndex += 2;
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

    public static double[] getScaledConditions(PcaConditions conditions) {
        double[] scaledConditions = new double[conditions.getConditionCount()];
        int next = 0;
        if (conditions.hasWings()) {
            scaledConditions[next++] = conditions.getWings() / (wingScaleFactor * downscaleFactor);
        }
        if (conditions.hasFlooredLegs()) {
            scaledConditions[next] = conditions.getFlooredLegs() / (flooredLegsScaleFactor * downscaleFactor);
        }
        return scaledConditions;
    }

    public static PcaDataPointConditioned newPointWithValuesFromScaledData(double[] scaledData, PcaConditions conditions, boolean logWeight) {
        double[] scaledDataWithConditions = new double[PcaDataPoint.getDimension()];
        int nextWithoutConditions = 0;
        for (int i = 0; i < PcaDataPoint.getDimension(); i++) {
            if (i == 20 && conditions.hasWings()) {
                scaledDataWithConditions[i] = conditions.getWings();
            } else if (i == 21 && conditions.hasFlooredLegs()) {
                scaledDataWithConditions[i] = conditions.getFlooredLegs();
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
