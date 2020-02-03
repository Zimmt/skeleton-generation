package util.pca;

import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.EigenDecomposition;
import org.apache.commons.math3.linear.RealVector;

import java.io.IOException;
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
     * Calculates which point has the minimum and which the maximum distance to the mean point.
     * The scaled coordinates (that are also used for PCA) are used.
     */
    public PcaDataPoint[] getExamplesWithExtremeDistancesToMean() {
        double maxDistance = 0;
        double minDistance = Double.POSITIVE_INFINITY;
        PcaDataPoint maxPoint = null;
        PcaDataPoint minPoint = null;
        RealVector mean = new ArrayRealVector(PcaDataPoint.getMean(dataPoints).getScaledDataForPCA());
        for (PcaDataPoint point : dataPoints) {
            double distance = new ArrayRealVector(point.getScaledDataForPCA()).getDistance(mean);
            if (distance > maxDistance) {
                maxDistance = distance;
                maxPoint = point;
            }
            if (distance < minDistance) {
                minDistance = distance;
                minPoint = point;
            }
        }
        System.out.println("The point with the min distance to mean is " + minPoint.getName() + " with distance " + minDistance);
        System.out.println("The point with the max distance to mean is " + maxPoint.getName() + " with distance " + maxDistance);
        return new PcaDataPoint[] {minPoint, maxPoint};
    }

    public List<PcaDataPoint> getDataPoints() {
        return dataPoints;
    }

    private PCA preparePCA(List<PcaDataPoint> dataPoints) {

        double[][] pcaData = new double[dataPoints.size()][PcaDataPoint.getDimension()];
        for (int i = 0; i < dataPoints.size(); i++) {
            pcaData[i] = dataPoints.get(i).getScaledDataForPCA();
        }
        return new PCA(pcaData);
    }
}
