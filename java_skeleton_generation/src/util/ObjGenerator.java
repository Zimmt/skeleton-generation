package util;

import de.javagl.obj.*;
import skeleton.SkeletonGenerator;
import skeleton.SpineData;
import skeleton.elements.terminal.TerminalElement;

import javax.vecmath.Point2f;
import javax.vecmath.Point3f;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;
import java.util.List;

public class ObjGenerator {

    private BoneLibrary boneLibrary;

    public ObjGenerator() {
        this.boneLibrary = new BoneLibrary();
    }

    public void generateObjFrom(SkeletonGenerator skeleton, String fileName, boolean allCubes) throws IOException {
        if (!skeleton.isFinished()) {
            System.err.println("Cannot generate .obj from unfinished skeleton!");
            return;
        }

        Obj obj = Objs.create();
        addSpineVertices(obj, skeleton);

        List<TerminalElement> skeletonParts = skeleton.getTerminalParts();
        for (TerminalElement element : skeletonParts) {
            obj.setActiveGroupNames(Collections.singletonList(element.getKind() + element.getId()));
            create3DRepresentation(obj, element, allCubes);
        }

        String outputPath = fileName + ".obj";
        OutputStream objOutputStream = new FileOutputStream(outputPath);
        ObjWriter.write(obj, objOutputStream);
    }

    private void create3DRepresentation(Obj obj, TerminalElement element, boolean allCubes) {
        TransformationMatrix worldTransform = element.calculateWorldTransform();
        BoundingBox worldBoundingBox = element.getBoundingBox().cloneBox();
        worldBoundingBox.transform(element.calculateWorldTransform());

        int zero = obj.getNumVertices();

        Obj elementObj = boneLibrary.getDefaultObj();
        if (!allCubes) {
            elementObj = boneLibrary.getBoneObj(element.getKind(), element.isMirroredVersion());
        }
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
