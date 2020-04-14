package skeleton;

import util.GUI;
import util.ObjGenerator;
import util.pca.PcaConditions;
import util.pca.PcaDataPoint;
import util.pca.PcaDataReader;
import util.pca.PcaHandler;

import java.io.IOException;
import java.util.List;

public class SkeletonGeneratorHandler {

    private GUI gui;

    public void run() {
        this.gui = new GUI(c -> {
            try {
                runSkeletonGenerator();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private void runSkeletonGenerator() throws IOException {
        boolean readMetaDataFromFile = false;
        boolean createVariationsFromFile = false;
        boolean saveSkeletonMetaDataToFile = false;

        boolean allCubes = true;
        boolean lowResolution = false;
        UserInput userInput = null;
        PcaHandler pcaHandler = null;
        List<PcaDataPoint> dataPoints = null;


        if (!readMetaDataFromFile) {
            boolean allowTwoExtremitiesPerGirdle = true;
            Integer userInputFlooredLegs = gui.getLegInput();
            Integer userInputWings = gui.getWingInput();
            Integer userInputArms = gui.getArmInput();
            Integer userInputFins = gui.getFinInput();
            Boolean userInputSecondShoulder = null;
            Double userInputNeckYLength = null;
            Double userInputTailXLength = null;
            String userInputHead = "horse_skull";

            dataPoints = PcaDataReader.readInputData(true);

            userInput = new UserInput(userInputFlooredLegs, userInputWings, userInputArms, userInputFins, allowTwoExtremitiesPerGirdle,
                    userInputSecondShoulder, userInputNeckYLength, userInputTailXLength, userInputHead);

            PcaConditions conditions = new PcaConditions(userInput.getNeckYLength(), userInput.getTailXLength(),
                    userInput.getWingConditionForPCA(), userInput.getLegConditionForPCA());
            pcaHandler = new PcaHandler(dataPoints, conditions);
        }

        int skeletonCount = 1;
        for (int i = 0; i < skeletonCount; i++) {
            System.out.println("- " + i + " --------------------------------------------------------------");
            String metaDataFileName = String.format("skeletonMetaData%d.txt", i);
            SkeletonGenerator skeletonGenerator;
            if (createVariationsFromFile) {
                skeletonGenerator = new SkeletonGenerator(metaDataFileName, dataPoints);
            } else if (readMetaDataFromFile) {
                skeletonGenerator = new SkeletonGenerator(metaDataFileName);
            } else {
                skeletonGenerator = new SkeletonGenerator(pcaHandler, userInput);
            }

            while (!skeletonGenerator.isFinished()) {
                boolean stepDone = skeletonGenerator.doOneStep();
                if (!stepDone) { // there might be missing rules
                    break;
                }
            }
            skeletonGenerator.calculateMirroredElements();

            ObjGenerator objGenerator = new ObjGenerator();
            objGenerator.generateObjFrom(skeletonGenerator, "skeleton" + i, allCubes, lowResolution);

            if (saveSkeletonMetaDataToFile) {
                skeletonGenerator.getSkeletonMetaData().saveToFile(metaDataFileName);
            }
        }
    }
}
