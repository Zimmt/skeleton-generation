package util.pca;

import org.apache.commons.math3.linear.EigenDecomposition;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PCA {

    private RealMatrix covariance;
    private EigenDecomposition ed;
    private Integer[] sortedEigenvalueIndices;

    public PCA(RealMatrix covariance) {
        this.covariance = covariance;
    }

    public EigenDecomposition run() {
        System.out.print("Running PCA... ");

        // taken from https://stackoverflow.com/questions/10604507/pca-implementation-in-java
        ed = new EigenDecomposition(covariance);

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

    public int getEigenvectorCount() {
        return ed.getRealEigenvalues().length;
    }

    /**
     * Return the nth biggest eigenvalue (zero based)
     */
    public double getEigenvalue(int n) {
        return ed.getRealEigenvalue(sortedEigenvalueIndices[n]);
    }

    /**
     * @return all eigenvalues bigger than minEigenvalueSize - sorted
     */
    public List<Double> getEigenvalues(double minEigenvalueSize) {
        List<Double> result = new ArrayList<>(ed.getRealEigenvalues().length);

        for (int i = 0; i < ed.getRealEigenvalues().length; i++) {
            double eigenvalue = getEigenvalue(i);
            if (eigenvalue >= minEigenvalueSize) {
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

    /**
     * Is not set if PCA didn't run!
     */
    public EigenDecomposition getEigenDecomposition() {
        return ed;
    }

    public double getVariance(int inputDimension) {
        if (covariance == null) {
            System.err.println("Cannot get variance when pca did not run!");
            return 0;
        }
        return covariance.getEntry(inputDimension, inputDimension);
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

    public void printEigenvector(int n) {
        PCA.printEigenvector(getEigenvector(n));
    }

    /**
     * Prints the biggest 4 entries in yellow and the smallest 3 in green
     */
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
                    System.out.println("length_upper_arm");
                    break;
                case 23:
                    System.out.println("length_lower_arm");
                    break;
                case 24:
                    System.out.println("length_hand");
                    break;
                case 25:
                    System.out.println("length_upper_leg");
                    break;
                case 26:
                    System.out.println("length_lower_leg");
                    break;
                case 27:
                    System.out.println("length_foot");
                    break;
                case 28:
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
