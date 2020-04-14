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

        UserInput userInput = null;
        PcaHandler pcaHandler = null;
        List<PcaDataPoint> dataPoints = null;

        if (!readMetaDataFromFile) {
            dataPoints = PcaDataReader.readInputData(true);

            userInput = new UserInput(
                    gui.getLegInput(), gui.getWingInput(), gui.getArmInput(), gui.getFinInput(),
                    gui.getTwoExtremitiesPerGirdleAllowed(), gui.getSecondShoulderInput(),
                    gui.getNeckInput(), gui.getTailInput(), gui.getHeadKindInput());

            PcaConditions conditions = new PcaConditions(userInput.getNeckYLength(), userInput.getTailXLength(),
                    userInput.getWingConditionForPCA(), userInput.getLegConditionForPCA());
            pcaHandler = new PcaHandler(dataPoints, conditions);
        }

        int skeletonCount = gui.getSkeletonCount();
        for (int i = 0; i < skeletonCount; i++) {
            System.out.println(String.format("- %d --------------------------------------------------------------", i));
            String metaDataFileName = "skeletonMetaData.txt";
            SkeletonGenerator skeletonGenerator;
            if (gui.getCreateVariationsInput()) {
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
            objGenerator.generateObjFrom(skeletonGenerator, "skeleton" + i, gui.getAllCubes(), gui.getLowResoultion());

            if (gui.getSaveToFile()) {
                String saveToFileName;
                if (skeletonCount > 1) {
                    saveToFileName = String.format("%s%d.txt", gui.getSaveFileName(), i);
                } else {
                    saveToFileName = String.format("%s.txt", gui.getSaveFileName());
                }
                skeletonGenerator.getSkeletonMetaData().saveToFile(saveToFileName);
            }
        }
    }
}
