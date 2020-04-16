import skeleton.SkeletonGeneratorHandler;
import util.pca.PcaConditions;
import util.pca.PcaDataPoint;
import util.pca.PcaDataReader;
import util.pca.PcaHandler;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) throws IOException {

        new SkeletonGeneratorHandler().run();
        System.out.println("Finished");
    }

    private static void pca(boolean logWeight) throws IOException {
        List<PcaDataPoint> dataPoints = PcaDataReader.readInputData(logWeight);
        PcaHandler pcaHandler = new PcaHandler(dataPoints, new PcaConditions());
        pcaHandler.visualize();
    }

    private static void pcaOnlyWings(boolean logWeight) throws IOException {
        List<PcaDataPoint> dataPoints = PcaDataReader.readInputData(logWeight);
        dataPoints = dataPoints.stream().filter(p -> p.getWings() > 0).collect(Collectors.toList());
        PcaHandler pcaHandlerOnlyWings = new PcaHandler(dataPoints, new PcaConditions());
        pcaHandlerOnlyWings.visualize();
    }

    private static void pcaNoWings(boolean logWeight) throws IOException {
        List<PcaDataPoint> dataPoints = PcaDataReader.readInputData(logWeight);
        dataPoints = dataPoints.stream().filter(p -> p.getWings() <= 0).collect(Collectors.toList());
        PcaHandler pcaHandlerNoWings = new PcaHandler(dataPoints, new PcaConditions());
        pcaHandlerNoWings.visualize();
    }
}
