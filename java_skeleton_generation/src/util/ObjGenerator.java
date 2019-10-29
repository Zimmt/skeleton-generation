package util;

import de.javagl.obj.Obj;
import de.javagl.obj.ObjWriter;
import de.javagl.obj.Objs;
import skeleton.Joint;
import skeleton.SimpleBone;
import skeleton.SkeletonGenerator;
import skeleton.TerminalElement;

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
        int vertexCount = -1;

        for (TerminalElement element : skeletonParts) {
            if (element instanceof SimpleBone) {
                SimpleBone bone = (SimpleBone) element;
                Position s = bone.getStart();
                Position e = bone.getEnd();

                obj.addVertex(s.x(), s.y(), s.z()); vertexCount++;
                obj.addVertex(e.x(), e.y(), e.z()); vertexCount++;
                obj.addFace(vertexCount-1, vertexCount);

            } else if (element instanceof Joint) {
                Joint joint = (Joint) element;
                Position p = joint.getPosition();

                obj.addVertex(p.x(), p.y(), p.z()); vertexCount++;
                obj.addFace(vertexCount);

            } else {
                System.err.println("Unknown skeleton part found.");
            }
        }

        String path = "skeleton.obj";
        OutputStream objOutputStream = new FileOutputStream(path);
        ObjWriter.write(obj, objOutputStream);
    }

}
