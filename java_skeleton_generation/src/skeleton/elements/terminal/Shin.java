package skeleton.elements.terminal;

import skeleton.elements.SkeletonPart;
import util.BoundingBox;
import util.TransformationMatrix;

import javax.vecmath.Point3f;

/**
 * Schienbein
 */
public class Shin extends TerminalElement {

    private final String id = "shin";

    public Shin(TransformationMatrix transform, Point3f jointRotationPoint, BoundingBox boundingBox, SkeletonPart parent, SkeletonPart ancestor) {
        super(transform, jointRotationPoint, boundingBox, parent, ancestor);
    }

    public String getID() {
        return id;
    }

    public boolean isMirrored() { return true; }
}
