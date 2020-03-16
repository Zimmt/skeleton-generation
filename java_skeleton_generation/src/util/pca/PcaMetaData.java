package util.pca;

import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.stat.correlation.Covariance;

import java.util.Arrays;
import java.util.List;

public class PcaMetaData {

    private boolean logWeight;

    private RealMatrix originalCovariance;
    private PcaDataPoint originalMean;

    private PcaConditions conditions;
    private RealMatrix conditionedCovariance;
    private PcaDataPointConditioned conditionedMean;

    public PcaMetaData(List<PcaDataPoint> dataPoints, PcaConditions conditions) {
        this.logWeight = dataPoints.get(0).getLogWeight();
        this.originalMean = PcaDataPoint.getMean(dataPoints);
        this.originalCovariance = calculateCovariance(dataPoints);
        this.conditions = conditions;
        calculateConditionedCovarianceAndMean();
    }

    public PcaDataPoint getOriginalMean() {
        return originalMean;
    }

    public PcaConditions getConditions() {
        return conditions;
    }

    public RealMatrix getConditionedCovariance() {
        return conditionedCovariance;
    }

    public PcaDataPointConditioned getConditionedMean() {
        return conditionedMean;
    }

    private RealMatrix calculateCovariance(List<PcaDataPoint> dataPoints) {

        double[][] pcaData = new double[dataPoints.size()][PcaDataPoint.getDimension()]; // one row represents one data point
        for (int i = 0; i < dataPoints.size(); i++) {
            pcaData[i] = dataPoints.get(i).getScaledDataForPCA();
        }
        RealMatrix data = MatrixUtils.createRealMatrix(pcaData); // preprocessing not needed as Covariance calculates mean
        Covariance covariance = new Covariance(data, false);
        return covariance.getCovarianceMatrix();
    }

    private void calculateConditionedCovarianceAndMean() {
        if (conditions == null || !conditions.anyConditionPresent()) {
            this.conditionedCovariance = originalCovariance;
            this.conditionedMean = new PcaDataPointConditioned(originalMean, conditions);
            return;
        }

        List<int[]> lineIndicesWithAndWithoutConditions = getLineIndicesWithAndWithoutConditions();
        int[] linesWithConditions = lineIndicesWithAndWithoutConditions.get(0);
        int[] linesWithoutConditions = lineIndicesWithAndWithoutConditions.get(1);

        List<RealMatrix> covariancePartitions = getCovariancePartitions(linesWithoutConditions, linesWithConditions);
        RealMatrix covariance11 = covariancePartitions.get(0);
        RealMatrix covariance12 = covariancePartitions.get(1);
        RealMatrix covariance21 = covariancePartitions.get(2);
        RealMatrix covariance22 = covariancePartitions.get(3);
        RealMatrix inverseCovariance22 = MatrixUtils.inverse(covariance22);

        this.conditionedCovariance = covariance11.subtract(covariance12.multiply(inverseCovariance22.multiply(covariance21)));

        double[] meanData = originalMean.getScaledDataForPCA();
        RealVector meanWithoutConditionedEntries = pickSpecificEntriesAndConvertToVector(meanData, linesWithoutConditions);
        RealVector meanOnlyConditionedEntries = pickSpecificEntriesAndConvertToVector(meanData, linesWithConditions);

        RealVector condition = MatrixUtils.createRealVector(PcaDataPointConditioned.getScaledConditions(conditions));
        RealMatrix conditionSub2 = MatrixUtils.createColumnRealMatrix(
                condition.subtract(meanOnlyConditionedEntries).toArray());

        RealVector toAdd = covariance12.multiply(inverseCovariance22.multiply(conditionSub2)).getColumnVector(0);
        RealVector newScaledMeanData = meanWithoutConditionedEntries.add(toAdd);

        this.conditionedMean = PcaDataPointConditioned.newPointWithValuesFromScaledData(
                newScaledMeanData.toArray(), conditions, logWeight);
    }

    private List<RealMatrix> getCovariancePartitions(int[] linesWithoutConditions, int[] linesWithConditions) {
        double[][] sigma11Data = new double[linesWithoutConditions.length][linesWithoutConditions.length];
        originalCovariance.copySubMatrix(linesWithConditions, linesWithConditions, sigma11Data);
        RealMatrix covariance11 = MatrixUtils.createRealMatrix(sigma11Data);

        double[][] sigma12Data = new double[linesWithoutConditions.length][linesWithConditions.length];
        originalCovariance.copySubMatrix(linesWithoutConditions, linesWithConditions, sigma12Data);
        RealMatrix covariance12 = MatrixUtils.createRealMatrix(sigma12Data);

        double[][] sigma21Data = new double[linesWithConditions.length][linesWithoutConditions.length];
        originalCovariance.copySubMatrix(linesWithConditions, linesWithoutConditions, sigma21Data);
        RealMatrix covariance21 = MatrixUtils.createRealMatrix(sigma21Data);

        double[][] sigma22Data = new double[linesWithConditions.length][linesWithConditions.length];
        originalCovariance.copySubMatrix(linesWithConditions, linesWithConditions, sigma22Data);
        RealMatrix covariance22 = MatrixUtils.createRealMatrix(sigma22Data);

        return Arrays.asList(covariance11, covariance12, covariance21, covariance22);
    }

    private List<int[]> getLineIndicesWithAndWithoutConditions() {
        int[] linesWithConditions = conditions.getPcaDimensionsWithConditions();
        int[] linesWithoutConditions = new int[PcaDataPoint.getDimension() - linesWithConditions.length];
        int nextWithC = 0;
        int nextWithoutC = 0;
        for (int i = 0; i < PcaDataPoint.getDimension(); i++) {
            if (linesWithConditions.length > nextWithC && linesWithConditions[nextWithC] == i) {
                nextWithC++;
            } else {
                linesWithoutConditions[nextWithoutC++] = i;
            }
        }
        return Arrays.asList(linesWithConditions, linesWithoutConditions);
    }

    private RealVector pickSpecificEntriesAndConvertToVector(double[] array, int[] indicesOfEntriesToPick) {
        double[] result = new double[indicesOfEntriesToPick.length];
        for (int i = 0; i < indicesOfEntriesToPick.length; i++) {
            result[i] = array[indicesOfEntriesToPick[i]];
        }
        return MatrixUtils.createRealVector(result);
    }
}
