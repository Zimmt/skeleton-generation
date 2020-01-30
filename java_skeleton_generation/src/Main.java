import org.apache.commons.math3.linear.EigenDecomposition;
import skeleton.SkeletonGenerator;
import util.ObjGenerator;
import util.pca.PCA;
import util.pca.PcaDataPoint;
import util.pca.PcaDataReader;
import util.pca.Visualization;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) throws IOException {

        List<PcaDataPoint> dataPoints = PcaDataReader.readInputData();
        visualize(dataPoints);
        System.out.println("Finished");
    }

    private static void exportImagesFromVisualization(Visualization visualization) throws IOException {
        double[] testSetting1 = {0.5, -0.5, 0.0, 0.0, 0.0, 0.0};
        double[] testSetting2 = {-0.5, 0.5, 0.0, 0.0, 0.0, 0.0};
        double[] testSetting3 = {0.0, 0.0, 0.0, 0.0, 0.0, 0.0};
        visualization.exportImagesWithEigenvectorSettings(Arrays.asList(testSetting1, testSetting2, testSetting3), "../PCA/temporary_visualization_exports/", "test");
    }

    private static Visualization visualizeOnlyWings(List<PcaDataPoint> dataPoints) throws IOException {
        dataPoints = dataPoints.stream().filter(p -> p.getWings() > 0).collect(Collectors.toList());
        return visualize(dataPoints);
    }

    private static Visualization visualizeOnlyNoWings(List<PcaDataPoint> dataPoints) throws IOException {
        dataPoints = dataPoints.stream().filter(p -> p.getWings() <= 0).collect(Collectors.toList());
        return visualize(dataPoints);
    }

    private static Visualization visualize(List<PcaDataPoint> dataPoints) throws IOException {
        EigenDecomposition ed = runPCA(dataPoints);
        PcaDataPoint mean = PcaDataPoint.getMean(dataPoints);

        return Visualization.start(ed, mean);
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

    private static EigenDecomposition runPCA(List<PcaDataPoint> dataPoints) throws IOException {

        double[][] pcaData = new double[dataPoints.size()][PcaDataPoint.getDimension()];
        for (int i = 0; i < dataPoints.size(); i++) {
            pcaData[i] = dataPoints.get(i).getScaledDataForPCA();
        }
        return PCA.run(pcaData);
    }
}
