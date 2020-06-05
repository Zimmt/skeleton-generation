package skeleton;

import util.GUI;
import util.ObjGenerator;
import util.pca.PcaConditions;
import util.pca.PcaDataPoint;
import util.pca.PcaDataReader;
import util.pca.PcaHandler;

import java.awt.*;
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
        EventQueue.invokeLater(() -> gui.startGUI());
    }

    /**
     * Attention! This can take a while!
     */
    public void measureRuntime() throws IOException {
        int[] ns = new int[]{10, 100, 1000};
        for (int i : ns) {
            System.out.println(String.format("%d times input read took %fms", i, (float) measureReadInput(i)));
        }
        for (int i : ns) {
            System.out.println(String.format("%d times pca took %fms", i, (float) measurePCA(i)));
        }
        for (int i : ns) { // all cubes
            System.out.println(String.format("%d skeleton generations all cubes took %fms", i, (float) measureAlgorithmWithoutPCA(i, true, true)));
        }
        for (int i : ns) { // low res
            System.out.println(String.format("%d skeleton generations low res took %fms", i, (float) measureAlgorithmWithoutPCA(i, false, true)));
        }
        for (int i : ns) { // high res
            System.out.println(String.format("%d skeleton generations high res took %fms", i, (float) measureAlgorithmWithoutPCA(i, false, false)));
        }
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
        pcaHandler.runPCA();

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
                skeletonGenerator = new SkeletonGenerator(pcaHandler, gui.getPcaDataPointName(), userInput, gui.getCreateVariationsInput());
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

    private long measureReadInput(int n) throws IOException {
        long time = System.currentTimeMillis();
        for (int i = 0; i < n; i++) {
            PcaDataReader.readInputData(true);
        }
        return System.currentTimeMillis() - time;
    }

    private long measurePCA(int n) throws IOException {
        List<PcaDataPoint> points = PcaDataReader.readInputData(true);

        long time = System.currentTimeMillis();

        for (int i = 0; i < n; i++) {
            PcaHandler pcaHandler = new PcaHandler(points, new PcaConditions());
            pcaHandler.runPCA();
        }
        return System.currentTimeMillis() - time;
    }

    private long measureAlgorithmWithoutPCA(int n, boolean allCubes, boolean lowRes) throws IOException {
        List<PcaDataPoint> points = PcaDataReader.readInputData(true);
        PcaHandler pcaHandler = new PcaHandler(points, new PcaConditions());
        pcaHandler.runPCA();
        UserInput userInput = new UserInput();

        long time = System.currentTimeMillis();
        for (int i = 0; i < n; i++) {
            SkeletonGenerator skeletonGenerator = new SkeletonGenerator(pcaHandler, userInput);

            while (!skeletonGenerator.isFinished()) {
                boolean stepDone = skeletonGenerator.doOneStep();
                if (!stepDone) { // there might be missing rules
                    System.err.println("Could not measure time!");
                    break;
                }
            }
            skeletonGenerator.calculateMirroredElements();
            new ObjGenerator().generateObjFrom(skeletonGenerator, "skeleton" + i, allCubes, lowRes);
        }
        return System.currentTimeMillis() - time;
    }
}
