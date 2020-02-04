package util.pca;

import org.apache.commons.math3.linear.*;
import org.apache.commons.math3.stat.correlation.Covariance;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PCA {

    private double[][] inputData; // one row represents one data point
    private EigenDecomposition ed;
    private Integer[] sortedEigenvalueIndices;
    private int eigenvalueCount; // number of eigenvalues > 0.001

    public PCA(double[][] inputData) {
        this.inputData = inputData;
    }

    /**
     * taken from https://stackoverflow.com/questions/10604507/pca-implementation-in-java
     */
    public EigenDecomposition run() {
        System.out.print("Running PCA... ");
        if (inputData.length == 0) {
            System.err.println("Input data for PCA is empty!");
            return null;
        }

        RealMatrix data = MatrixUtils.createRealMatrix(inputData); // preprocessing not needed as Covariance calculates mean

        Covariance covariance = new Covariance(data, false);
        RealMatrix covarianceMatrix = covariance.getCovarianceMatrix();
        ed = new EigenDecomposition(covarianceMatrix);

        eigenvalueCount = (int) Arrays.stream(ed.getRealEigenvalues()).filter(d -> d >= 0.001).count();
        System.out.println(String.format("%d eigenvalues/-vectors", eigenvalueCount));

        double[] eigenvalues = ed.getRealEigenvalues();
        sortedEigenvalueIndices = new Integer[eigenvalues.length];
        for (int i = 0; i < sortedEigenvalueIndices.length; i++) {
            sortedEigenvalueIndices[i] = i;
        }
        Arrays.sort(sortedEigenvalueIndices, (o1, o2) -> -Double.compare(eigenvalues[o1], eigenvalues[o2]));

        System.out.println("Complete.");
        return ed;
    }

    /**
     * Returns the eigenvector with the nth biggest eigenvalue
     */
    public RealVector getEigenvector(int n) {
        return ed.getEigenvector(sortedEigenvalueIndices[n]);
    }

    /**
     * Return the nth biggest eigenvalue
     */
    public double getEigenvalue(int n) {
        return ed.getRealEigenvalue(sortedEigenvalueIndices[n]);
    }

    /**
     * @return all eigenvalues bigger than 0.001 - sorted
     */
    public List<Double> getEigenvalues() {
        List<Double> result = new ArrayList<>(eigenvalueCount);

        for (int i = 0; i < eigenvalueCount; i++) {
            double eigenvalue = getEigenvalue(i);
            if (eigenvalue > 0.001) {
                result.add(eigenvalue);
            } else {
                break;
            }
        }

        return result;
    }

    /**
     * Returns the index of the nth biggest eigenvalue
     */
    public int getEigenvectorIndex(int n) {
        return sortedEigenvalueIndices[n];
    }

    public int getEigenvalueCount() {
        return eigenvalueCount;
    }

    /**
     * Is not set if PCA didn't run!
     */
    public EigenDecomposition getEigenDecomposition() {
        return ed;
    }

    private void printData(EigenDecomposition ed) {
        System.out.println("The " + ed.getRealEigenvalues().length + " sorted eigenvalues are:");
        for (Integer sortedEigenvalueIndex : sortedEigenvalueIndices) {
            System.out.print(ed.getRealEigenvalue(sortedEigenvalueIndex) + ", ");
        }

        int wantedEigenvectors = 6;
        System.out.println("\nThe first " + wantedEigenvectors + " eigenvectors are:");
        for (int i = 0; i < wantedEigenvectors && i < ed.getRealEigenvalues().length; i++) {
            System.out.println( "> " + (i+1) + " -------------------------------------------------");
            printEigenvector(ed.getEigenvector(sortedEigenvalueIndices[i]));
        }
    }

    private static void printEigenvector(RealVector vector) {
        if (vector.getDimension() != PcaDataPoint.getDimension()) {
            System.err.println("Eigenvector does not have correct dimension.");
        }

        Integer[] sortedIndices = new Integer[vector.getDimension()];
        for (int i = 0; i < sortedIndices.length; i++) {
            sortedIndices[i] = i;
        }
        Arrays.sort(sortedIndices, (o1, o2) -> -Double.compare(Math.abs(vector.getEntry(o1)), Math.abs(vector.getEntry(o2))));

        String yellow = "\u001B[33m";
        String green = "\u001B[32m";
        String white = "\u001B[0m";

        for (int i = 0; i < vector.getDimension(); i++) {
            switch(i) {
                case 0:
                    System.out.println("neck");
                    break;
                case 6:
                    System.out.println("back");
                    break;
                case 14:
                    System.out.println("tail");
                    break;
                case 20:
                    System.out.println("wings");
                    break;
                case 21:
                    System.out.println("floored_legs");
                    break;
                case 22:
                    System.out.println("length_front_legs");
                    break;
                case 23:
                    System.out.println("length_back_legs");
                    break;
                case 24:
                    System.out.println("weight");
                    break;
            }

            if (i == sortedIndices[0] || i == sortedIndices[1] || i == sortedIndices[2] || i == sortedIndices[3]) { // big number
                System.out.println(yellow + vector.getEntry(i) + white);
            } else if (i == sortedIndices[sortedIndices.length-3] || i == sortedIndices[sortedIndices.length - 2] || i == sortedIndices[sortedIndices.length-1]) {
                System.out.println(green + vector.getEntry(i) + white);
            } else {
                System.out.println(vector.getEntry(i));
            }
        }
    }
}
