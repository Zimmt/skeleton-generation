package skeleton.elements.nonterminal;

import skeleton.elements.SkeletonPart;
import util.BoundingBox;
import util.TransformationMatrix;

public class Leg extends NonTerminalElement {

    private final String id = "leg";

    public Leg(TransformationMatrix transform, BoundingBox boundingBox, SkeletonPart parent, SkeletonPart ancestor) {
        super(transform, boundingBox, parent, ancestor);
    }

    public String getID() {
        return id;
    }

    public boolean isMirrored() { return true; }
}
