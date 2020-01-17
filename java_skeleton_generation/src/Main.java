import util.pca.PcaDataPoint;
import util.pca.PcaDataReader;

import java.io.IOException;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException {

        /*SkeletonGenerator skeletonGenerator = new SkeletonGenerator();
        while (!skeletonGenerator.isFinished()) {
            boolean stepDone = skeletonGenerator.doOneStep();
            if (!stepDone) { // there might be missing rules
                break;
            }
        }
        skeletonGenerator.calculateMirroredElements();
        System.out.println(skeletonGenerator.toString());

        ObjGenerator objGenerator = new ObjGenerator();
        objGenerator.generateObjFrom(skeletonGenerator);*/

        List<PcaDataPoint> dataPoints = PcaDataReader.readInputData();
        double[][] pcaData = new double[dataPoints.size()][PcaDataPoint.getDimension()];
        for (int i = 0; i < dataPoints.size(); i++) {
            pcaData[i] = dataPoints.get(i).getScaledDataForPCA();
        }

        System.out.println("Finished");
    }
}
