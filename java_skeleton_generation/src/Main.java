import skeleton.InitialElement;
import skeleton.SimpleBone;
import skeleton.SkeletonGenerator;
import util.ObjGenerator;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        SimpleBone bone = new InitialElement().toSimpleBone();
        SkeletonGenerator skeletonGenerator = new SkeletonGenerator(bone);

        ObjGenerator objGenerator = new ObjGenerator();
        objGenerator.generateObjFrom(skeletonGenerator);
    }
}
