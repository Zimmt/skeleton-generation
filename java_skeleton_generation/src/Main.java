import skeleton.SkeletonGenerator;
import util.ObjGenerator;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {

        SkeletonGenerator skeletonGenerator = new SkeletonGenerator();
        while (!skeletonGenerator.isFinished()) {
            skeletonGenerator.doOneStep();
        }
        System.out.println(skeletonGenerator.toString());

        ObjGenerator objGenerator = new ObjGenerator();
        objGenerator.generateObjFrom(skeletonGenerator);

        System.out.println("Finished");
    }
}
