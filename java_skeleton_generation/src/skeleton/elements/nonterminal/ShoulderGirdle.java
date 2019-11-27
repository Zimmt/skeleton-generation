package skeleton.elements.nonterminal;

import skeleton.elements.SkeletonPart;
import util.BoundingBox;
import util.TransformationMatrix;

public class ShoulderGirdle extends NonTerminalElement {

    private final String id = "shoulder girdle";

    public ShoulderGirdle(TransformationMatrix transform, BoundingBox boundingBox, SkeletonPart parent, SkeletonPart ancestor) {
        super(transform, boundingBox, parent, ancestor);
    }

    public String getID() {
        return id;
    }

    public boolean isMirrored() { return false; }
}
