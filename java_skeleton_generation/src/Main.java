import skeleton.SkeletonGenerator;

public class Main {
    public static void main(String[] args) {

        SkeletonGenerator skeletonGenerator = new SkeletonGenerator();
        while (!skeletonGenerator.isFinished()) {
            skeletonGenerator.doOneStep();
        }
        System.out.println(skeletonGenerator.toString());
    }
}
