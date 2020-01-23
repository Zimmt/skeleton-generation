import org.apache.commons.math3.linear.EigenDecomposition;
import skeleton.SkeletonGenerator;
import util.ObjGenerator;
import util.pca.PCA;
import util.pca.PcaDataPoint;
import util.pca.PcaDataReader;
import util.pca.Visualization;

import javax.swing.*;
import javax.vecmath.Point2d;
import java.awt.*;
import java.awt.geom.CubicCurve2D;
import java.awt.geom.Line2D;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException {

        visualizeMeanPointOfInputData();
        System.out.println("Finished");
    }

    private static void visualizeMeanPointOfInputData() throws IOException {

        List<PcaDataPoint> dataPoints = PcaDataReader.readInputData();
        PcaDataPoint mean = PcaDataPoint.getMean(dataPoints);

        Visualization visualization = Visualization.initialize();
        visualization.showPoint(mean);
    }

    private static void runSkeletonGenerator() throws IOException {
        SkeletonGenerator skeletonGenerator = new SkeletonGenerator();
        while (!skeletonGenerator.isFinished()) {
            boolean stepDone = skeletonGenerator.doOneStep();
            if (!stepDone) { // there might be missing rules
                break;
            }
        }
        skeletonGenerator.calculateMirroredElements();
        System.out.println(skeletonGenerator.toString());

        ObjGenerator objGenerator = new ObjGenerator();
        objGenerator.generateObjFrom(skeletonGenerator);
    }

    private static void runPCA() throws IOException {
        List<PcaDataPoint> dataPoints = PcaDataReader.readInputData();
        List<PcaDataPoint> wingPoints = new ArrayList<>();
        List<PcaDataPoint> otherPoints = new ArrayList<>();
        for (PcaDataPoint point : dataPoints) {
            if (point.hasWings()) {
                wingPoints.add(point);
            } else {
                otherPoints.add(point);
            }
        }

        System.out.println("COMPLETE PCA **************************");
        double[][] pcaData = new double[dataPoints.size()][PcaDataPoint.getDimension()];
        for (int i = 0; i < dataPoints.size(); i++) {
            pcaData[i] = dataPoints.get(i).getScaledDataForPCA();
        }
        EigenDecomposition ed = PCA.run(pcaData);

        System.out.println("WING PCA***************************");
        System.out.println(wingPoints.size() + " data points");
        double[][] wingPcaData = new double[wingPoints.size()][PcaDataPoint.getDimension()];
        for (int i = 0; i < wingPoints.size(); i++) {
            wingPcaData[i] = wingPoints.get(i).getScaledDataForPCA();
        }
        EigenDecomposition edWings = PCA.run(wingPcaData);

        System.out.println("OTHER PCA****************************");
        System.out.println(otherPoints.size() + " data points");
        double[][] otherPcaData = new double[otherPoints.size()][PcaDataPoint.getDimension()];
        for (int i = 0; i < otherPoints.size(); i++) {
            otherPcaData[i] = otherPoints.get(i).getScaledDataForPCA();
        }
        EigenDecomposition edOther = PCA.run(otherPcaData);
    }
}
