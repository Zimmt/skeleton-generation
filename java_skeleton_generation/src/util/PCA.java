package util;

import org.apache.commons.math3.linear.*;
import org.apache.commons.math3.stat.correlation.Covariance;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;

public class PCA {

    /**
     * taken from https://stackoverflow.com/questions/10604507/pca-implementation-in-java
     * @param inputData one row represents one data point
     * @return
     */
    public static EigenDecomposition run(double[][] inputData) {
        if (inputData.length == 0) {
            System.err.println("Input data for PCA is empty!");
            return null;
        }

        RealMatrix data = preprocessData(inputData);

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
        for (int i = 0; i < sortedEigenvalueIndices.length; i++) {
            System.out.print(eigenvalues[sortedEigenvalueIndices[i]] + ", ");
        }

        System.out.println("\nThe sorted " + eigenvalues.length + " eigenvectors are:");
        for (int i = 0; i < data.getColumnDimension(); i++) {
            System.out.println(ed.getEigenvector(sortedEigenvalueIndices[i]));
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
}
