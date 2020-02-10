package util.pca;

import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PcaHandler {

    private List<PcaDataPoint> dataPoints;
    private PCA pca;
    private DataExporter dataExporter;
    private Visualization visualization;

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

    public void exportAnimalVisualizationToFiles() throws IOException {
        if (visualization == null) {
            visualize();
        }
        List<RealVector> eigenvectorScales = getEigenvectorScalesForPoints(visualization.getSliderCount());
        for (int i = 0; i < dataPoints.size(); i++) {
            visualization.setSliderValues(eigenvectorScales.get(i).toArray());
            visualization.exportToImage("../PCA/temporary_visualization_exports/" + dataPoints.get(i).getName() + ".jpg");
        }
    }

    public void visualize() {
        PcaDataPoint mean = PcaDataPoint.getMean(dataPoints);
        this.visualization = Visualization.start(pca, mean);
    }

    /**
     * @return for each data point a list of eigenvector scales (in the same order as in dataPoints)
     */
    public List<RealVector> getEigenvectorScalesForPoints(double minEigenvalueSize) {
        RealVector meanVector = new ArrayRealVector(PcaDataPoint.getMean(dataPoints).getScaledDataForPCA());
        int dimension = pca.getEigenvalues(minEigenvalueSize).size();
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

    public List<RealVector> getEigenvectorScalesForPoints(int eigenvectorCount) {
        return getEigenvectorScalesForPoints(pca.getEigenvalue(eigenvectorCount));
    }

    public List<double[]> getMinMaxEigenvectorScales(double minEigenvalueSize) {
        List<RealVector> eigenvectorScales = getEigenvectorScalesForPoints(minEigenvalueSize);
        double[] mins = eigenvectorScales.get(0).toArray();
        double[] maxs = eigenvectorScales.get(0).toArray();

        for (int i = 1; i < eigenvectorScales.size(); i++) {
            RealVector scale = eigenvectorScales.get(i);

            for (int j = 0; j < scale.getDimension(); j++) {
                if (scale.getEntry(j) < mins[j]) {
                    mins[j] = scale.getEntry(j);
                } else if (scale.getEntry(j) > maxs[j]) {
                    maxs[j] = scale.getEntry(j);
                }
            }
        }
        return Arrays.asList(mins, maxs);
    }

    /**
     * Checks if the input data can correctly be reconstructed by the PCA results.
     * For each data point adds the scaled eigenvectors to mean and checks if result is the same as input.
     */
    public boolean testPcaWithInputData() {
        List<RealVector> eigenvectorScales = getEigenvectorScalesForPoints(0.0);
        PcaDataPoint mean = PcaDataPoint.getMean(dataPoints);
        boolean passed = true;

        for (int i = 0; i < dataPoints.size(); i++) {
            RealVector scales = eigenvectorScales.get(i);

            List<RealVector> scaledEigenvectors = new ArrayList<>(eigenvectorScales.size());
            for (int j = 0; j < scales.getDimension(); j++) {
                scaledEigenvectors.add(pca.getEigenvector(j).mapMultiply(scales.getEntry(j)));
            }
            PcaDataPoint testPoint = mean.getMovedPoint(scaledEigenvectors);

            if (!testPoint.equals(dataPoints.get(i))) {
                System.err.println("Input and output points not equal!");
                passed = false;
            }
            if (testPoint.containsIncorrectData()) {
                System.err.println("Points contain incorrect data!");
                passed = false;
            }
        }
        return passed;
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

    public List<Double> getEigenvalues(double minEigenvalueSize) {
        return pca.getEigenvalues(minEigenvalueSize);
    }

    public double getEigenvalue(int number) {
        return pca.getEigenvalue(number);
    }

    private PCA preparePCA(List<PcaDataPoint> dataPoints) {

        double[][] pcaData = new double[dataPoints.size()][PcaDataPoint.getDimension()];
        for (int i = 0; i < dataPoints.size(); i++) {
            pcaData[i] = dataPoints.get(i).getScaledDataForPCA();
        }
        return new PCA(pcaData);
    }
}
