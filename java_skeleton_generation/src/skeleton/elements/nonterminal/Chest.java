package skeleton.elements.nonterminal;

import skeleton.elements.SkeletonPart;
import util.BoundingBox;
import util.TransformationMatrix;

import javax.vecmath.Point3f;

public class Chest extends NonTerminalElement {

    private final String id = "chest";

    public Chest(TransformationMatrix transform, Point3f jointRotationPoint, BoundingBox boundingBox, SkeletonPart parent, SkeletonPart ancestor) {
        super(transform, jointRotationPoint, boundingBox, parent, ancestor);
    }

    public String getID() {
        return id;
    }

    public boolean isMirrored() { return false; }
}
