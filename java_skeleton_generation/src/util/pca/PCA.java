package util.pca;

import org.apache.commons.math3.linear.*;
import org.apache.commons.math3.stat.correlation.Covariance;

import java.util.Arrays;
import java.util.Random;

public class PCA {

    /**
     * taken from https://stackoverflow.com/questions/10604507/pca-implementation-in-java
     * @param inputData one row represents one data point
     */
    public static EigenDecomposition run(double[][] inputData) {
        if (inputData.length == 0) {
            System.err.println("Input data for PCA is empty!");
            return null;
        }

        RealMatrix data = MatrixUtils.createRealMatrix(inputData);

        Covariance covariance = new Covariance(data, false);
        RealMatrix covarianceMatrix = covariance.getCovarianceMatrix();
        EigenDecomposition ed = new EigenDecomposition(covarianceMatrix);

        double[] eigenvalues = ed.getRealEigenvalues();
        Integer[] sortedEigenvalueIndices = new Integer[eigenvalues.length];
        for (int i = 0; i < sortedEigenvalueIndices.length; i++) {
            sortedEigenvalueIndices[i] = i;
        }
        Arrays.sort(sortedEigenvalueIndices, (o1, o2) -> -Double.compare(eigenvalues[o1], eigenvalues[o2]));
        System.out.println("The sorted eigenvalues are:");
        for (Integer sortedEigenvalueIndex : sortedEigenvalueIndices) {
            System.out.print(eigenvalues[sortedEigenvalueIndex] + ", ");
        }

        int wantedEigenvectors = 6;
        System.out.println("\nThe first " + wantedEigenvectors + " eigenvectors are:");
        for (int i = 0; i < wantedEigenvectors && i < data.getColumnDimension(); i++) {
            System.out.println( "> " + (i+1) + " -------------------------------------------------");
            printEigenvector(ed.getEigenvector(sortedEigenvalueIndices[i]));
        }

        return ed;
    }

    public static double[][] generateRandomData(int rows, int columns) {
        Random random = new Random();
        double[][] pointsArray = new double[rows][columns];

        for (int i = 0; i < pointsArray.length; i++) {
            for (int j = 0; j < pointsArray[0].length; j++) {
                pointsArray[i][j] = random.nextDouble();
            }
        }
        return pointsArray;
    }

    /**
     * (This is needed to calculate weighted sum of eigenvectors for data points.)
     * @param inputData one row represents one data point
     * @return new matrix where the mean point is subtracted from each original data point
     */
    private static RealMatrix preprocessData(double[][] inputData) {

        RealMatrix originalMatrix = MatrixUtils.createRealMatrix(inputData);
        RealVector mean = new ArrayRealVector(originalMatrix.getColumnDimension());
        for (int i = 0; i < originalMatrix.getColumnDimension(); i++) {
            double[] column = originalMatrix.getColumn(i);
            double columnMean = Arrays.stream(column).sum() / column.length;
            mean.setEntry(i, columnMean);
        }

        RealMatrix meanSubMatrix = new Array2DRowRealMatrix(originalMatrix.getRowDimension(), originalMatrix.getColumnDimension());
        for (int i = 0; i < originalMatrix.getRowDimension(); i++) {
            RealVector row = originalMatrix.getRowVector(i);
            meanSubMatrix.setRowVector(i, row.subtract(mean));
        }

        return meanSubMatrix;
    }

    private static void printEigenvector(RealVector vector) {
        if (vector.getDimension() != 27) {
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
                    System.out.println("arms");
                    break;
                case 23:
                    System.out.println("length_front_legs");
                    break;
                case 24:
                    System.out.println("length_back_legs");
                    break;
                case 25:
                    System.out.println("length_wings");
                    break;
                case 26:
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