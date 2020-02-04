package util.pca;

import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.EigenDecomposition;
import org.apache.commons.math3.linear.RealVector;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PcaHandler {

    private List<PcaDataPoint> dataPoints;
    private PCA pca;
    private DataExporter dataExporter;

    /**
     * Initializes everything needed for PCA and visualization
     * and runs the PCA
     */
    public PcaHandler(List<PcaDataPoint> dataPoints) {
        this.dataPoints = dataPoints;
        this.pca = preparePCA(dataPoints);
        pca.run();
        this.dataExporter = new DataExporter(dataPoints);
    }

    public void exportOriginalPcaData() throws IOException {
        dataExporter.exportToFile("../PCA/original_pcaPoints.txt");
    }

    public void exportPCADataProjection() throws IOException {
        dataExporter.projectAndExportToFile("../PCA/projected_pcaPoints.txt", pca.getEigenvector(0), pca.getEigenvector(1), pca.getEigenvector(2));
    }

    public void exportImagesFromVisualization(Visualization visualization) throws IOException {
        double[] testSetting1 = {0.5, -0.5, 0.0, 0.0, 0.0, 0.0};
        double[] testSetting2 = {-0.5, 0.5, 0.0, 0.0, 0.0, 0.0};
        double[] testSetting3 = {0.0, 0.0, 0.0, 0.0, 0.0, 0.0};
        visualization.exportImagesWithEigenvectorSettings(Arrays.asList(testSetting1, testSetting2, testSetting3), "../PCA/temporary_visualization_exports/", "test");
    }

    public void exportInterestingNumbers() throws IOException {
        dataExporter.exportInterestingNumbers("../PCA/interesting_numbers.txt", this);
    }

    public Visualization visualize() {
        PcaDataPoint mean = PcaDataPoint.getMean(dataPoints);
        EigenDecomposition ed = pca.getEigenDecomposition();

        return Visualization.start(ed, mean);
    }

    /**
     * @return for each data point a list of eigenvector scales (in the same order as in dataPoints)
     */
    public List<RealVector> getEigenvectorScalesForPoints() {
        RealVector meanVector = new ArrayRealVector(PcaDataPoint.getMean(dataPoints).getScaledDataForPCA());
        int dimension = (int) pca.getEigenvalues().stream().filter(v -> v >= 0.01).count();
        List<RealVector> results = new ArrayList<>(dimension);

        for (PcaDataPoint point : dataPoints) {
            RealVector animal = new ArrayRealVector(point.getScaledDataForPCA());
            RealVector relativeAnimal = animal.subtract(meanVector);
            RealVector eigenvectorScales = pca.getEigenDecomposition().getVT().operate(relativeAnimal);
            RealVector sortedEigenvectorScales = new ArrayRealVector(dimension);
            for (int i = 0; i < sortedEigenvectorScales.getDimension(); i++) {
                sortedEigenvectorScales.setEntry(i, eigenvectorScales.getEntry(pca.getEigenvectorIndex(i)));
            }
            results.add(sortedEigenvectorScales);
        }
        return results;
    }

    /**
     * Calculates which point has the minimum and which the maximum distance to the mean point.
     * The scaled coordinates (that are also used for PCA) are used.
     */
    public PcaDataPoint[] getExamplesWithExtremeDistancesToMean() {
        double maxDistance = 0;
        double secondBiggestDistance = 0;
        double minDistance = Double.POSITIVE_INFINITY;
        PcaDataPoint maxPoint = null;
        PcaDataPoint secondMaxPoint = null;
        PcaDataPoint minPoint = null;
        RealVector mean = new ArrayRealVector(PcaDataPoint.getMean(dataPoints).getScaledDataForPCA());
        for (PcaDataPoint point : dataPoints) {
            double distance = new ArrayRealVector(point.getScaledDataForPCA()).getDistance(mean);
            if (distance > maxDistance) {
                secondBiggestDistance = maxDistance;
                secondMaxPoint = maxPoint;
                maxDistance = distance;
                maxPoint = point;
            } else if (distance > secondBiggestDistance) {
                secondBiggestDistance = distance;
                secondMaxPoint = point;
            }
            if (distance < minDistance) {
                minDistance = distance;
                minPoint = point;
            }
        }

        return new PcaDataPoint[] {minPoint, secondMaxPoint, maxPoint};
    }

    public List<PcaDataPoint> getDataPoints() {
        return dataPoints;
    }

    public List<Double> getEigenvalues() {
        return pca.getEigenvalues();
    }

    private PCA preparePCA(List<PcaDataPoint> dataPoints) {

        double[][] pcaData = new double[dataPoints.size()][PcaDataPoint.getDimension()];
        for (int i = 0; i < dataPoints.size(); i++) {
            pcaData[i] = dataPoints.get(i).getScaledDataForPCA();
        }
        return new PCA(pcaData);
    }
}
