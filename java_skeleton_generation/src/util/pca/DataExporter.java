package util.pca;

import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class DataExporter {

    List<PcaDataPoint>  data;

    public DataExporter(List<PcaDataPoint> data) {
        this.data = data;
    }

    public void projectAndExportToFile(String filePathAndName, RealVector xDimension, RealVector yDimension, RealVector zDimension) throws IOException {
        File file = new File(filePathAndName);
        BufferedWriter writer = new BufferedWriter(new FileWriter(file));
        writer.write("# Projected PCA examples");
        writer.newLine();

        RealVector mean = new ArrayRealVector(PcaDataPoint.getMean(data).getScaledDataForPCA());
        for (PcaDataPoint point : data) {
            RealVector rawPoint = new ArrayRealVector(point.getScaledDataForPCA()).subtract(mean);
            double x = rawPoint.dotProduct(xDimension);
            double y = rawPoint.dotProduct(yDimension);
            double z = rawPoint.dotProduct(zDimension);

            writer.write("" + x + " " +  y + " " + z);
            writer.newLine();
        }
        writer.close();
    }

    public void exportToFile(String filePathAndName) throws IOException {
        File file = new File(filePathAndName);
        BufferedWriter writer = new BufferedWriter(new FileWriter(file));
        writer.write("# Original PCA examples\n");
        writer.write("# " + PcaDataPoint.getDimensionNames());
        writer.newLine();

        for (PcaDataPoint point : data) {
            double[] rawPoint = point.getOriginalData();
            for (double value : rawPoint) {
                writer.write(value + " ");
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
        List<Double> sortedEigenvalues = pcaHandler.getEigenvalues(0.001);
        writer.write(String.format("The %d eigenvalues bigger than 0.001 are:\n    ", sortedEigenvalues.size()));
        for (double eigenvalue : sortedEigenvalues) {
            writer.write(String.format("%f, ", eigenvalue));
        }
        writer.newLine();
        sortedEigenvalues = pcaHandler.getEigenvalues(0.01);
        writer.write(String.format("The %d eigenvalues bigger than 0.01 are:\n    ", sortedEigenvalues.size()));
        for (double eigenvalue : sortedEigenvalues) {
            writer.write(String.format("%f, ", eigenvalue));
        }
        writer.newLine();
        writer.newLine();

        PcaDataPoint[] minMaxDistance = pcaHandler.getExamplesWithExtremeDistancesToMean();
        writer.write(String.format("Min distance to mean has %s.\n", minMaxDistance[0].getName()));
        writer.write(String.format("Second biggest distance to mean has %s.\n", minMaxDistance[1].getName()));
        writer.write(String.format("Max distance to mean has %s.\n", minMaxDistance[2].getName()));
        writer.newLine();

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

        writer.close();
    }
}
