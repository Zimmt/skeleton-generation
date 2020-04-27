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

    private List<PcaDataPoint> pcaDataPoints;
    private GUI gui;

    public void run() throws IOException {
        this.pcaDataPoints = PcaDataReader.readInputData(true);
        String[] exampleNames = pcaDataPoints.stream().map(PcaDataPoint::getName).toArray(String[]::new);
        this.gui = new GUI(exampleNames, c -> {
            try {
                runSkeletonGenerator();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private void runSkeletonGenerator() throws IOException {
        UserInput userInput = new UserInput(
                gui.getLegInput(), gui.getWingInput(), gui.getArmInput(), gui.getFinInput(),
                gui.getTwoExtremitiesPerGirdleAllowed(), gui.getSecondShoulderInput(),
                gui.getNeckInput(), gui.getTailInput(), gui.getHeadKindInput());

        PcaConditions conditions;
        if (!(gui.getReadFromFile() || gui.getConstructFromExample())) {
             conditions = new PcaConditions(userInput.getNeckYLength(), userInput.getTailXLength(),
                    userInput.getWingConditionForPCA(), userInput.getLegConditionForPCA());
        } else {
            conditions = new PcaConditions();
        }
        PcaHandler pcaHandler = new PcaHandler(pcaDataPoints, conditions);

        int skeletonCount = gui.getSkeletonCount();
        for (int i = 0; i < skeletonCount; i++) {
            System.out.println(String.format("- %d --------------------------------------------------------------", i));
            String metaDataFilePath = gui.getInputFilePath();

            SkeletonGenerator skeletonGenerator;
            if (gui.getReadFromFile()) {
                if (gui.getCreateVariationsInput()) {
                    skeletonGenerator = new SkeletonGenerator(metaDataFilePath, pcaDataPoints);
                } else {
                    skeletonGenerator = new SkeletonGenerator(metaDataFilePath);
                }
            } else if (gui.getConstructFromExample()) {
                pcaHandler.runPCA();
                skeletonGenerator = new SkeletonGenerator(pcaHandler, gui.getPcaDataPointName(), userInput, gui.getCreateVariationsInput());
            } else {
                pcaHandler.runPCA();
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
            objGenerator.generateObjFrom(skeletonGenerator, gui.getSkeletonFileName() + i, gui.getAllCubes(), gui.getLowResoultion());

            if (gui.getSaveToFile()) {
                String saveToFileName;
                if (skeletonCount > 1) {
                    saveToFileName = String.format("%s%d.txt", gui.getMetaDataFileName(), i);
                } else {
                    saveToFileName = String.format("%s.txt", gui.getMetaDataFileName());
                }
                skeletonGenerator.getSkeletonMetaData().saveToFile(saveToFileName);
            }
        }
        System.out.println("Finished.");
    }
}
