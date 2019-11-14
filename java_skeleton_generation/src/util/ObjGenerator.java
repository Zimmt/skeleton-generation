package util;

import de.javagl.obj.Obj;
import de.javagl.obj.ObjWriter;
import de.javagl.obj.Objs;
import skeleton.SimpleBone;
import skeleton.SkeletonGenerator;
import skeleton.SkeletonPart;
import skeleton.TerminalElement;

import javax.media.j3d.Transform3D;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

public class ObjGenerator {

    public ObjGenerator() {
    }

    public void generateObjFrom(SkeletonGenerator skeleton) throws IOException {
        if (!skeleton.isFinished()) {
            System.err.println("Cannot generate .obj from unfinished skeleton!");
            return;
        }

        List<TerminalElement> skeletonParts = skeleton.getTerminalParts();
        Obj obj = Objs.create();

        for (TerminalElement element : skeletonParts) {
            if (element instanceof SimpleBone) {
                SimpleBone bone = (SimpleBone) element;
                BoundingBox boundingBox = bone.getBoundingBox().cloneBox();
                SkeletonPart part = bone;

                // find absolute position of bounding box
                Transform3D transform = new Transform3D(part.getTransform());
                transform.invert();
                boundingBox.transform(transform);

                while (part.hasParent()) {
                    part = part.getParent();
                    transform = new Transform3D(part.getTransform());
                    transform.invert();
                    boundingBox.transform(transform);
                }

                boundingBox.addDataToObj(obj);

            } else {
                System.err.println("Unknown skeleton part found.");
            }
        }

        String path = "skeleton.obj";
        OutputStream objOutputStream = new FileOutputStream(path);
        ObjWriter.write(obj, objOutputStream);
    }
}
