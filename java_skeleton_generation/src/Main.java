import skeleton.SkeletonGenerator;
import util.ObjGenerator;
import util.pca.*;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) throws IOException {

        runSkeletonGenerator(true);
        System.out.println("Finished");
    }

    private static void pca(boolean logWeight) throws IOException {
        List<PcaDataPoint> dataPoints = PcaDataReader.readInputData(logWeight);
        PcaHandler pcaHandler = new PcaHandler(dataPoints);
        pcaHandler.visualize();
    }

    private static void pcaOnlyWings(boolean logWeight) throws IOException {
        List<PcaDataPoint> dataPoints = PcaDataReader.readInputData(logWeight);
        dataPoints = dataPoints.stream().filter(p -> p.getWings() > 0).collect(Collectors.toList());
        PcaHandler pcaHandlerOnlyWings = new PcaHandler(dataPoints);
        pcaHandlerOnlyWings.visualize();
    }

    private static void pcaNoWings(boolean logWeight) throws IOException {
        List<PcaDataPoint> dataPoints = PcaDataReader.readInputData(logWeight);
        dataPoints = dataPoints.stream().filter(p -> p.getWings() <= 0).collect(Collectors.toList());
        PcaHandler pcaHandlerNoWings = new PcaHandler(dataPoints);
        pcaHandlerNoWings.visualize();
    }

    private static void runSkeletonGenerator(boolean logWeight) throws IOException {
        List<PcaDataPoint> dataPoints = PcaDataReader.readInputData(logWeight);
        PcaHandler pcaHandler = new PcaHandler(dataPoints);

        int skeletonCount = 10;
        for (int i = 0; i < skeletonCount; i++) {
            System.out.println("- " + i + " --------------------------------------------------------------");
            SkeletonGenerator skeletonGenerator = new SkeletonGenerator(pcaHandler);
            while (!skeletonGenerator.isFinished()) {
                boolean stepDone = skeletonGenerator.doOneStep();
                if (!stepDone) { // there might be missing rules
                    break;
                }
            }
            skeletonGenerator.calculateMirroredElements();

            ObjGenerator objGenerator = new ObjGenerator();
            objGenerator.generateObjFrom(skeletonGenerator, "skeleton" + i);
        }
    }
}
