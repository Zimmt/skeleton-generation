package util.pca;

import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;

import java.util.Arrays;
import java.util.List;

public class StatisticalEvaluation {

    public static List<RealVector> getValuesForQQDiagram(Double[] data, double variance) {

        Double[] sortedData = data.clone();
        Arrays.sort(sortedData);
        double mean = getMean(sortedData);
        double standardDeviation = Math.sqrt(variance);
        NormalDistribution normalDistribution = new NormalDistribution(mean, standardDeviation);

        double[] quantiles = new double[data.length];

        for (int i = 0; i < sortedData.length; i++) {
            double pQuantile = getPQuantileFromRank(i+1, sortedData.length);
            quantiles[i] = normalDistribution.inverseCumulativeProbability(pQuantile);
        }

        return Arrays.asList(new ArrayRealVector(sortedData), new ArrayRealVector(quantiles));
    }

    public static List<RealVector> getValuesForDetrendedQQDiagram(Double[] data, double variance) {

        Double[] sortedData = data.clone();
        Arrays.sort(sortedData);
        double mean = getMean(sortedData);
        double standardDeviation = Math.sqrt(variance);
        NormalDistribution normalDistribution = new NormalDistribution(mean, standardDeviation);

        double[] quantiles = new double[data.length];

        for (int i = 0; i < sortedData.length; i++) {
            double pQuantile = getPQuantileFromRank(i+1, sortedData.length);
            quantiles[i] = sortedData[i] - normalDistribution.inverseCumulativeProbability(pQuantile);
        }

        return Arrays.asList(new ArrayRealVector(sortedData), new ArrayRealVector(quantiles));
    }

    // todo: which possibility?
    private static double getPQuantileFromRank(int rank, int count) {
        return ((double) rank - 0.5) / (double) count;
    }

    private static double getMean(Double[] data) {
        return Arrays.stream(data).reduce(0.0, Double::sum) / data.length;
    }
}
