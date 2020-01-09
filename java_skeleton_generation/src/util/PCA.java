package util;

import org.apache.commons.math3.linear.EigenDecomposition;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.stat.correlation.Covariance;

import java.util.Random;

public class PCA {

    // taken from https://stackoverflow.com/questions/10604507/pca-implementation-in-java
    public static EigenDecomposition run(double[][] inputData) {
        RealMatrix realMatrix = MatrixUtils.createRealMatrix(inputData);

        Covariance covariance = new Covariance(realMatrix, false);
        RealMatrix covarianceMatrix = covariance.getCovarianceMatrix();
        EigenDecomposition ed = new EigenDecomposition(covarianceMatrix);

        System.out.println("The eigenvectors are:");
        for (int i = 0; i < inputData[0].length; i++) {
            System.out.println(ed.getEigenvector(i));
        }

        return ed;
    }

    public static double[][] generateRandomData(int columns, int rows) {
        Random random = new Random();
        double[][] pointsArray = new double[columns][rows];

        for (int i = 0; i < pointsArray.length; i++) {
            for (int j = 0; j < pointsArray[0].length; j++) {
                pointsArray[i][j] = random.nextFloat();
            }
        }
        return pointsArray;
    }
}
