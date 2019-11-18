import skeleton.SkeletonGenerator;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {

        SkeletonGenerator skeletonGenerator = new SkeletonGenerator();
        skeletonGenerator.doOneStep();
        System.out.println(skeletonGenerator.toString());
    }
}
