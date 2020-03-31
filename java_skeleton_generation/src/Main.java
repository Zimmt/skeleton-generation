import skeleton.SkeletonGenerator;
import skeleton.UserInput;
import util.ObjGenerator;
import util.pca.PcaConditions;
import util.pca.PcaDataPoint;
import util.pca.PcaDataReader;
import util.pca.PcaHandler;

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

    private static void runSkeletonGenerator(boolean logWeight) throws IOException {
        boolean allCubes = false;
        Integer userInputFlooredLegs = null;
        Integer userInputWings = null;
        Integer userInputArms = null;
        Integer userInputFins = null;
        Boolean userInputSecondShoulder = null;
        Double userInputNeckYLength = null;
        Double userInputTailXLength = null;
        String userInputHead = "horse_skull";
        UserInput userInput = new UserInput(userInputFlooredLegs, userInputWings, userInputArms, userInputFins,
                userInputSecondShoulder, userInputNeckYLength, userInputTailXLength, userInputHead);

        List<PcaDataPoint> dataPoints = PcaDataReader.readInputData(logWeight);
        PcaConditions conditions = new PcaConditions(userInput.getNeckYLength(), userInput.getTailXLength(),
                userInput.getWingConditionForPCA(), userInput.getLegConditionForPCA());
        PcaHandler pcaHandler = new PcaHandler(dataPoints, conditions);

        int skeletonCount = 1;
        for (int i = 0; i < skeletonCount; i++) {
            System.out.println("- " + i + " --------------------------------------------------------------");
            SkeletonGenerator skeletonGenerator = new SkeletonGenerator(pcaHandler, userInput);
            while (!skeletonGenerator.isFinished()) {
                boolean stepDone = skeletonGenerator.doOneStep();
                if (!stepDone) { // there might be missing rules
                    break;
                }
            }
            skeletonGenerator.calculateMirroredElements();

            ObjGenerator objGenerator = new ObjGenerator();
            objGenerator.generateObjFrom(skeletonGenerator, "skeleton" + i, allCubes);
        }
    }
}
