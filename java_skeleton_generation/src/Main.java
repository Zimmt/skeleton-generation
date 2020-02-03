import skeleton.SkeletonGenerator;
import util.ObjGenerator;
import util.pca.*;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) throws IOException {

        pca();
        System.out.println("Finished");
    }

    private static void pca() throws IOException {
        List<PcaDataPoint> dataPoints = PcaDataReader.readInputData();
        PcaHandler pcaHandler = new PcaHandler(dataPoints);
        pcaHandler.visualize();
    }

    private static void pcaOnlyWings() throws IOException {
        List<PcaDataPoint> dataPoints = PcaDataReader.readInputData();
        dataPoints = dataPoints.stream().filter(p -> p.getWings() > 0).collect(Collectors.toList());
        PcaHandler pcaHandlerOnlyWings = new PcaHandler(dataPoints);
        pcaHandlerOnlyWings.visualize();
    }

    private static void pcaNoWings() throws IOException {
        List<PcaDataPoint> dataPoints = PcaDataReader.readInputData();
        dataPoints = dataPoints.stream().filter(p -> p.getWings() <= 0).collect(Collectors.toList());
        PcaHandler pcaHandlerNoWings = new PcaHandler(dataPoints);
        pcaHandlerNoWings.visualize();
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
}
