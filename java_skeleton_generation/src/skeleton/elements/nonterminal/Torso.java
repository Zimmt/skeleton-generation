package skeleton.elements.nonterminal;

import skeleton.elements.SkeletonPart;
import util.BoundingBox;
import util.TransformationMatrix;

import javax.media.j3d.Transform3D;

public class Torso extends NonTerminalElement {

    private final String id = "torso";

    // the transform here specifies the position in relation to the origin
    public Torso(TransformationMatrix transform, BoundingBox boundingBox, SkeletonPart ancestor) {
        super(transform, boundingBox, null, ancestor);
    }

    public String getID() {
        return id;
    }

    public boolean isMirrored() { return false; }
}
