import skeleton.SkeletonGenerator;
import util.ObjGenerator;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        SkeletonGenerator skeletonGenerator = new SkeletonGenerator();
        int maxSteps = 5;

        while (!skeletonGenerator.isFinished() && skeletonGenerator.getStepCount() < maxSteps) {
            skeletonGenerator.doOneStep();
        }
        if (skeletonGenerator.isFinished()) {
            ObjGenerator objGenerator = new ObjGenerator();
            objGenerator.generateObjFrom(skeletonGenerator);
        } else {
            System.err.println("Skeleton was not finished after maximum number of steps.");
        }
    }
}
