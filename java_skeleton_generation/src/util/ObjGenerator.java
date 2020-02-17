package util;

import de.javagl.obj.Obj;
import de.javagl.obj.ObjWriter;
import de.javagl.obj.Objs;
import skeleton.SkeletonGenerator;
import skeleton.SpinePosition;
import skeleton.elements.terminal.TerminalElement;

import javax.vecmath.Point2f;
import javax.vecmath.Point3f;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;
import java.util.List;

public class ObjGenerator {

    public ObjGenerator() {}

    public void generateObjFrom(SkeletonGenerator skeleton) throws IOException {
        if (!skeleton.isFinished()) {
            System.err.println("Cannot generate .obj from unfinished skeleton!");
            return;
        }

        Obj obj = Objs.create();

        addSpineVertices(obj, skeleton);

        List<TerminalElement> skeletonParts = skeleton.getTerminalParts();

        for (TerminalElement element : skeletonParts) {
            Point3f position = element.getWorldPosition();
            BoundingBox boundingBox = element.getBoundingBox().cloneBox();
            boundingBox.transform(element.calculateWorldTransform());

            Point3f xCorner = new Point3f(position); xCorner.add(boundingBox.getXVector());
            Point3f yCorner = new Point3f(position); yCorner.add(boundingBox.getYVector());
            Point3f zCorner = new Point3f(position); zCorner.add(boundingBox.getZVector());
            Point3f xyCorner = new Point3f(xCorner); xyCorner.add(boundingBox.getYVector());
            Point3f xzCorner = new Point3f(xCorner); xzCorner.add(boundingBox.getZVector());
            Point3f yzCorner = new Point3f(yCorner); yzCorner.add(boundingBox.getZVector());
            Point3f xyzCorner = new Point3f(xyCorner); xyzCorner.add(boundingBox.getZVector());

            int zero = obj.getNumVertices();
            obj.setActiveGroupNames(Collections.singletonList(element.getKind() + element.getId()));

            // 0. origin, 1. x, 2. y, 3. z, 4. xy, 5. xz, 6. yz, 7. xyz
            obj.addVertex(position.x, position.y, position.z);
            obj.addVertex(xCorner.x, xCorner.y, xCorner.z);
            obj.addVertex(yCorner.x, yCorner.y, yCorner.z);
            obj.addVertex(zCorner.x, zCorner.y, zCorner.z);
            obj.addVertex(xyCorner.x, xyCorner.y, xyCorner.z);
            obj.addVertex(xzCorner.x, xzCorner.y, xzCorner.z);
            obj.addVertex(yzCorner.x, yzCorner.y, yzCorner.z);
            obj.addVertex(xyzCorner.x, xyzCorner.y, xyzCorner.z);

            obj.addFace(zero+0, zero+1, zero+5, zero+3); // front
            obj.addFace(zero+2, zero+4, zero+7, zero+6); // back
            obj.addFace(zero+0, zero+1, zero+4, zero+2); // bottom
            obj.addFace(zero+3, zero+5, zero+7, zero+6); // top
            obj.addFace(zero+0, zero+2, zero+6, zero+3); // left
            obj.addFace(zero+1, zero+4, zero+7, zero+5); // right

        }

        String path = "skeleton.obj";
        OutputStream objOutputStream = new FileOutputStream(path);
        ObjWriter.write(obj, objOutputStream);
    }

    private void addSpineVertices(Obj obj, SkeletonGenerator skeleton) {
        SpinePosition spine = skeleton.getSkeletonMetaData().getSpine();
        obj.setActiveGroupNames(Collections.singletonList("spine"));
        int precision = 8;

        for (CubicBezierCurve spinePart : spine.getAll()) {
            for (int i = 0; i <= precision; i++) {
                float t = (float) i / precision;
                Point2f spinePoint = spinePart.apply(t);
                obj.addVertex(spinePoint.x, spinePoint.y, 0f);
                if (i > 0) {
                    obj.addFace(i-1, i);
                }
            }
        }

    }
}
