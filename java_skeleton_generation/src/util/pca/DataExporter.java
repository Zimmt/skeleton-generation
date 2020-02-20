package util.pca;

import org.apache.commons.math3.linear.RealVector;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class DataExporter {

    List<PcaDataPoint>  data;

    public DataExporter(List<PcaDataPoint> data) {
        this.data = data;
    }

    /**
     * Writes each vector into one line and separates each value by spaces
     * @param heading first line in resulting file
     */
    public void exportRowDataToFile(String filePathAndName, String heading, List<RealVector> rows) throws IOException {
        File file = new File(filePathAndName);
        BufferedWriter writer = new BufferedWriter(new FileWriter(file));
        writer.write(heading);
        writer.newLine();

        for (RealVector vector : rows) {
            for (double value : vector.toArray()) {
                writer.write(value + " ");
            }
            writer.newLine();
        }

        writer.close();
    }

    public void exportColumnDataToFile(String filePathAndName, String heading, List<RealVector> columns) throws IOException {
        File file = new File(filePathAndName);
        BufferedWriter writer = new BufferedWriter(new FileWriter(file));
        writer.write(heading);
        writer.newLine();

        for (int row = 0; row < columns.get(0).getDimension(); row++) {
            for (RealVector column : columns) {
                writer.write(column.getEntry(row) + " ");
            }
            writer.newLine();
        }

        writer.close();
    }

    public void exportInterestingNumbers(String filePathAndName, PcaHandler pcaHandler) throws IOException {
        File file = new File(filePathAndName);
        BufferedWriter writer = new BufferedWriter(new FileWriter(file));
        writer.write("Interesting PCA numbers\n");
        writer.write("-----------------------\n");
        writer.newLine();

        writer.write(String.format("Number of data points is %d.\n", pcaHandler.getDataPoints().size()));
        writer.newLine();

        writer.write("### Eigenvalues and eigenvectors ###\n");
        int eigenvalueCount = (int) pcaHandler.getEigenvalues(0.0).stream().filter(d -> d > 0.0).count();
        writer.write(String.format("There are %d eigenvalues bigger than 0. The smallest is %f.\n", eigenvalueCount, pcaHandler.getEigenvalue(eigenvalueCount-1)));
        List<Double> sortedEigenvalues = pcaHandler.getEigenvalues(0.01);
        int biggestEigenvalueCount = sortedEigenvalues.size();
        writer.write(String.format("There are %d eigenvalues bigger than 0.01.\n", biggestEigenvalueCount));
        sortedEigenvalues = pcaHandler.getEigenvalues(0.001);
        writer.write(String.format("There are %d eigenvalues bigger than 0.001.\n", sortedEigenvalues.size()));
        writer.write("The eigenvalue, square root of the eigenvalue and the minimum and maximum amplitude for each eigenvector is the following:\n");
        List<double[]> minMaxScales = pcaHandler.getMinMaxEigenvectorScales(0.001);
        for (int i = 0; i < minMaxScales.get(0).length; i++) {
            writer.write(String.format("%02d. %f, %f, [%f, %f]\n", i+1,
                    sortedEigenvalues.get(i), Math.sqrt(sortedEigenvalues.get(i)),
                    minMaxScales.get(0)[i], minMaxScales.get(1)[i]));
        }
        writer.newLine();

        writer.write(String.format("The biggest 4 and the smallest 3 entries of the first %d eigenvectors are:\n", biggestEigenvalueCount));
        for (int i = 0;  i < biggestEigenvalueCount; i++) {
            writer.write(String.format("%d. ", i+1));
            RealVector eigenvector = pcaHandler.getEigenvector(i);
            Integer[] sortedIndices = getSortedEntryIndices(eigenvector);
            writer.write("biggest: ");
            for (int b = 0; b < 4; b++) {
                writer.write(String.format("%s (%f), ", PcaDataPoint.getDimensionName(sortedIndices[b]), eigenvector.getEntry(sortedIndices[b])));
            }
            writer.write("\n   smallest: ");
            for (int s = eigenvector.getDimension()-3; s < eigenvector.getDimension(); s++) {
                writer.write(String.format("%s (%f), ", PcaDataPoint.getDimensionName(sortedIndices[s]), eigenvector.getEntry(sortedIndices[s])));
            }
            writer.newLine();
        }
        writer.newLine();
        writer.newLine();

        writer.write("### Distances to mean ###\n");
        PcaDataPoint[] minMaxDistance = pcaHandler.getExamplesWithExtremeDistancesToMean();
        writer.write(String.format("Min distance to mean has %s.\n", minMaxDistance[0].getName()));
        writer.write(String.format("Second biggest distance to mean has %s.\n", minMaxDistance[1].getName()));
        writer.write(String.format("Max distance to mean has %s.\n", minMaxDistance[2].getName()));
        writer.newLine();

        writer.write("### Properties of input points ###\n");
        List<PcaDataPoint> dataPoints = pcaHandler.getDataPoints();
        int pointsWithWings = (int) dataPoints.stream().filter(p -> p.getWings() > 0).count();
        writer.write(String.format("There are %d points with wings.\n", pointsWithWings));
        writer.write(String.format("There are %d points without wings.\n", dataPoints.size() - pointsWithWings));
        writer.newLine();

        int pointsWithoutLegs = (int) dataPoints.stream().filter(p -> p.getFlooredLegs() <= 0).count();
        int pointsWithTwoLegs = (int) dataPoints.stream().filter(p -> p.getFlooredLegs() >= 2).count();
        writer.write(String.format("There are %d points without legs.\n", pointsWithoutLegs));
        writer.write(String.format("There are %d points with two legs.\n", dataPoints.size() - pointsWithoutLegs - pointsWithTwoLegs));
        writer.write(String.format("There are %d points with four legs.\n", pointsWithTwoLegs));
        writer.newLine();

        int fish = (int) dataPoints.stream().filter(p -> p.getAnimalClass() == AnimalClass.FISH).count();
        int amphibians = (int) dataPoints.stream().filter(p -> p.getAnimalClass() == AnimalClass.AMPHIBIAN).count();
        int reptilians = (int) dataPoints.stream().filter(p -> p.getAnimalClass() == AnimalClass.REPTILIAN).count();
        int birds = (int) dataPoints.stream().filter(p -> p.getAnimalClass() == AnimalClass.BIRD).count();
        int mammals = (int) dataPoints.stream().filter(p -> p.getAnimalClass() == AnimalClass.MAMMAL).count();
        writer.write(String.format("There are %d fish.\n", fish));
        writer.write(String.format("There are %d amphibians.\n", amphibians));
        writer.write(String.format("There are %d reptilians.\n", reptilians));
        writer.write(String.format("There are %d birds.\n", birds));
        writer.write(String.format("There are %d mammals.\n", mammals));
        writer.newLine();

        writer.write("### Data to reconstruct input from visualization ### (more eigenvectors might be needed to get a good result)\n");
        List<RealVector> eigenvectorScales = pcaHandler.getEigenvectorScalesForPoints(0.01);
        for(int i = 0; i < pcaHandler.getDataPoints().size(); i++) {
            RealVector scalesForPoint = eigenvectorScales.get(i);
            writer.write(String.format("%s is represented by the following eigenvector scales:\n    ",
                    pcaHandler.getDataPoints().get(i).getName()));
            for (int d = 0; d < scalesForPoint.getDimension(); d++) {
                writer.write(String.format("%f, ", scalesForPoint.getEntry(d)));
            }
            writer.newLine();
        }

        writer.newLine();
        writer.close();
    }

    private Integer[] getSortedEntryIndices(RealVector vector) {
        Integer[] sortedIndices = new Integer[vector.getDimension()];
        for (int i = 0; i < sortedIndices.length; i++) {
            sortedIndices[i] = i;
        }
        Arrays.sort(sortedIndices, (o1, o2) -> -Double.compare(Math.abs(vector.getEntry(o1)), Math.abs(vector.getEntry(o2))));
        return sortedIndices;
    }
}
