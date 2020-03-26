package util;

import de.javagl.obj.*;
import skeleton.SkeletonGenerator;
import skeleton.SpineData;
import skeleton.elements.terminal.TerminalElement;

import javax.vecmath.Point2f;
import javax.vecmath.Point3f;
import java.io.*;
import java.util.Collections;
import java.util.List;

public class ObjGenerator {

    public ObjGenerator() {}

    public void generateObjFrom(SkeletonGenerator skeleton, String fileName) throws IOException {
        if (!skeleton.isFinished()) {
            System.err.println("Cannot generate .obj from unfinished skeleton!");
            return;
        }

        Obj obj = Objs.create();

        addSpineVertices(obj, skeleton);

        Obj cube = ObjReader.read((new FileInputStream(new File("./cube.obj"))));
        Obj vertebra = ObjReader.read((new FileInputStream(new File("./vertebra_1x1.obj"))));

        List<TerminalElement> skeletonParts = skeleton.getTerminalParts();
        for (TerminalElement element : skeletonParts) {
            obj.setActiveGroupNames(Collections.singletonList(element.getKind() + element.getId()));
            if (!(element.getKind().equals("vertebra"))) {
                create3DRepresentation(obj, element, cube);
            } else {
                create3DRepresentation(obj, element, vertebra);
            }
        }

        String outputPath = fileName + ".obj";
        OutputStream objOutputStream = new FileOutputStream(outputPath);
        ObjWriter.write(obj, objOutputStream);
    }

    private void create3DRepresentation(Obj obj, TerminalElement element, Obj elementObj) throws IOException {
        TransformationMatrix worldTransform = element.calculateWorldTransform();
        BoundingBox worldBoundingBox = element.getBoundingBox().cloneBox();
        worldBoundingBox.transform(element.calculateWorldTransform());

        int zero = obj.getNumVertices();

        float[] vertexData = ObjData.getVerticesArray(elementObj); // three consecutive entries are the x,y,z values of one vertex
        for (int i = 0; i < vertexData.length; i += 3) {
            Point3f newPosition = new Point3f(
                    vertexData[i] * worldBoundingBox.getXLength(),
                    vertexData[i+1] * worldBoundingBox.getYLength(),
                    vertexData[i+2] * worldBoundingBox.getZLength());
            worldTransform.applyOnPoint(newPosition);
            obj.addVertex(newPosition.x, newPosition.y, newPosition.z);
        }

        for (int i = 0; i < elementObj.getNumFaces(); i++) {
            ObjFace face = elementObj.getFace(i);
            int[] newVertexIndices = new int[face.getNumVertices()];
            for (int j = 0; j < face.getNumVertices(); j++) {
                newVertexIndices[j] = zero + face.getVertexIndex(j);
            }
            obj.addFace(ObjFaces.create(newVertexIndices, null, null));
        }
    }

    private void createBoundingBoxRepresentation(Obj obj, TerminalElement element) {
        Point3f worldPosition = element.getWorldPosition();
        BoundingBox worldBoundingBox = element.getBoundingBox().cloneBox();
        worldBoundingBox.transform(element.calculateWorldTransform());

        Point3f xCorner = new Point3f(worldPosition); xCorner.add(worldBoundingBox.getXVector());
        Point3f yCorner = new Point3f(worldPosition); yCorner.add(worldBoundingBox.getYVector());
        Point3f zCorner = new Point3f(worldPosition); zCorner.add(worldBoundingBox.getZVector());
        Point3f xyCorner = new Point3f(xCorner); xyCorner.add(worldBoundingBox.getYVector());
        Point3f xzCorner = new Point3f(xCorner); xzCorner.add(worldBoundingBox.getZVector());
        Point3f yzCorner = new Point3f(yCorner); yzCorner.add(worldBoundingBox.getZVector());
        Point3f xyzCorner = new Point3f(xyCorner); xyzCorner.add(worldBoundingBox.getZVector());

        int zero = obj.getNumVertices();

        // 0. origin, 1. x, 2. y, 3. z, 4. xy, 5. xz, 6. yz, 7. xyz
        obj.addVertex(worldPosition.x, worldPosition.y, worldPosition.z);
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

    private void addSpineVertices(Obj obj, SkeletonGenerator skeleton) {
        SpineData spine = skeleton.getSkeletonMetaData().getSpine();
        obj.setActiveGroupNames(Collections.singletonList("spine"));
        int precision = 10;

        CubicBezierCurve[] spineParts = spine.getAll();
        for (int p = 0; p < 3; p++) {
            int upperBound = precision;
            if (p == 2) {
                upperBound = precision+1;
            }
            for (int i = 0; i < upperBound; i++) {
                float t = (float) i / precision;
                Point2f spinePoint = spineParts[p].apply(t);
                obj.addVertex(spinePoint.x, spinePoint.y, 0f);
                if (p > 0 || i > 0) {
                    obj.addFace(p*precision + i-1, p*precision + i);
                }
            }
        }
    }
}
