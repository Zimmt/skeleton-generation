package util.pca;

import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class PcaHandler {

    private List<PcaDataPoint> dataPoints;
    private PcaDataPoint mean;
    private PCA pca;
    private DataExporter dataExporter;
    private Visualization visualization;

    private Random random = new Random();

    /**
     * Initializes everything needed for PCA and visualization
     * and runs the PCA
     */
    public PcaHandler(List<PcaDataPoint> dataPoints) {
        this.dataPoints = dataPoints;
        this.mean = PcaDataPoint.getMean(dataPoints);
        this.pca = preparePCA(dataPoints);
        pca.run();
        this.dataExporter = new DataExporter(dataPoints);
    }

    public PcaDataPoint getRandomPcaDataPoint() {
        double[] eigenvectorScales = getRandomScalesForEachEigenvector();
        List<RealVector> scaledEigenvectors = new ArrayList<>(eigenvectorScales.length);
        for (int j = 0; j < pca.getEigenvectorCount(); j++) {
            scaledEigenvectors.add(pca.getEigenvector(j).mapMultiply(eigenvectorScales[j]));
        }
        return mean.getMovedPoint(scaledEigenvectors);
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

    public RealVector getEigenvector(int number) {
        return pca.getEigenvector(number);
    }

    public void exportOriginalPcaData() throws IOException {

        String heading = String.format("# Original PCA examples\n# %s\n", PcaDataPoint.getDimensionNames());
        List<RealVector> data = new ArrayList<>(dataPoints.size());

        for (PcaDataPoint point : dataPoints) {
            data.add(new ArrayRealVector(point.getOriginalData()));
        }

        dataExporter.exportRowDataToFile("../PCA/original_pcaPoints.txt", heading, data);
    }

    public void exportPCADataProjection(String tag) throws IOException {
        String heading = String.format("# Projected PCA examples\n# The input examples projected on the first three eigenvectors (and in the 4th column the value of %s)\n", tag);

        List<RealVector> projections = getEigenvectorScalesForPoints(3);
        List<RealVector> projectionsWithTag = new ArrayList<>(dataPoints.size());

        for (int i = 0; i < dataPoints.size(); i++) {
            int t = 0;
            switch (tag) {
                case "wing":
                    t = (int) dataPoints.get(i).getWings();
                    break;
                case "leg":
                    t = (int) dataPoints.get(i).getFlooredLegs();
                    break;
                case "animal_class":
                    t = dataPoints.get(i).getAnimalClass().ordinal();
                    break;
                default:
                    System.err.println("Invalid tag name!");
                    break;
            }
            RealVector projectionWithTag = projections.get(i).append(t);
            projectionsWithTag.add(projectionWithTag);
        }
        dataExporter.exportRowDataToFile(String.format("../PCA/projected_pcaPoints_with_%s_tag.txt", tag), heading, projectionsWithTag);
    }

    public void exportQQDiagramDataToFiles(boolean detrended) throws IOException {
        List<double[]> pcaInputData = new ArrayList<>(dataPoints.size());
        for (PcaDataPoint point : dataPoints) {
            pcaInputData.add(point.getScaledDataForPCA());
        }
        for (int d = 0;  d < PcaDataPoint.getDimension(); d++) {
            int dimension = d;
            Double[] xs = pcaInputData.stream().map(v -> v[dimension]).collect(Collectors.toList()).toArray(new Double[dataPoints.size()]);
            List<RealVector> qqData;
            String heading;
            String fileName;
            if (detrended) {
                qqData = StatisticalEvaluation.getValuesForDetrendedQQDiagram(xs, pca.getVariance(d));
                heading = String.format("# detrended QQ Diagram data for dimension %d", d);
                fileName = String.format("QQ_diagram_data_detrended%d.txt", d);
            } else {
                qqData = StatisticalEvaluation.getValuesForQQDiagram(xs, pca.getVariance(d));
                heading = String.format("# QQ Diagram data for dimension %d", d);
                fileName = String.format("QQ_diagram_data%d.txt", d);
            }
            dataExporter.exportColumnDataToFile("../PCA/" + fileName, heading, qqData);
        }
    }

    // does the same as exportQQDiagramDataToFiles but only for weight
    // useful if log and linear weight should be compared
    public void exportQQDiagramDataForWeightToFile(boolean detrended) throws IOException {
        Double[] weights = new Double[dataPoints.size()];
        for (int i = 0; i < dataPoints.size(); i++) {
            weights[i] = dataPoints.get(i).getWeight();
        }
        List<RealVector> qqData;
        String heading;
        String fileName;
        if (detrended) {
            qqData = StatisticalEvaluation.getValuesForDetrendedQQDiagram(weights, pca.getVariance(28));
            heading = "# detrended QQ Diagram data for weight";
            fileName = "QQ_diagram_data_detrended_weight.txt";
        } else {
            qqData = StatisticalEvaluation.getValuesForQQDiagram(weights, pca.getVariance(28));
            heading = "# QQ Diagram data for weight";
            fileName = "QQ_diagram_data_weight.txt";
        }
        dataExporter.exportColumnDataToFile("../PCA/" + fileName, heading, qqData);
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
        this.visualization = Visualization.start(pca, mean);
    }

    public void printFirstEigenvectors(int count) {
        System.out.println("-----------------------------------------");
        for (int i = 0; i < count; i++) {
            pca.printEigenvector(i);
            System.out.println("-----------------------------------------");
        }
    }

    /**
     * @return for each data point a list of eigenvector scales (in the same order as in dataPoints)
     */
    public List<RealVector> getEigenvectorScalesForPoints(double minEigenvalueSize) {
        RealVector meanVector = new ArrayRealVector(mean.getScaledDataForPCA());
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
        List<RealVector> eigenvectorScales = getEigenvectorScalesForPoints(pca.getEigenvalue(eigenvectorCount));
        if (eigenvectorScales.get(0).getDimension() > eigenvectorCount) {
            eigenvectorScales = eigenvectorScales.stream().map(v -> v.getSubVector(0, v.getDimension()-1)).collect(Collectors.toList());
        }
        return eigenvectorScales;
    }

    /**
     * @return for each eigenvector with an eigenvalue >= minEigenvalueSize the min and max value taken by a data point
     */
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
        RealVector meanVector = new ArrayRealVector(mean.getScaledDataForPCA());
        for (PcaDataPoint point : dataPoints) {
            double distance = new ArrayRealVector(point.getScaledDataForPCA()).getDistance(meanVector);
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

    private PCA preparePCA(List<PcaDataPoint> dataPoints) {

        double[][] pcaData = new double[dataPoints.size()][PcaDataPoint.getDimension()];
        for (int i = 0; i < dataPoints.size(); i++) {
            pcaData[i] = dataPoints.get(i).getScaledDataForPCA();
        }
        return new PCA(pcaData);
    }

    private double[] getRandomScalesForEachEigenvector() {
        double[] scales = new double[pca.getEigenvectorCount()];

        for (int i = 0; i < scales.length; i++) {
            double variance = pca.getEigenvalue(i);
            double r = random.nextGaussian(); // generates normally distributed value with mean 0 and standard deviation 1
            r = variance * r;
            scales[i] = r;
        }
        return scales;
    }
}
