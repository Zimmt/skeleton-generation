package skeleton.elements.terminal;

import skeleton.elements.SkeletonPart;
import util.BoundingBox;
import util.TransformationMatrix;

/**
 * Oberschenkel
 */
public class Thigh extends TerminalElement {

    private final String id = "thigh";

    public Thigh(TransformationMatrix transform, BoundingBox boundingBox, SkeletonPart parent, SkeletonPart ancestor) {
        super(transform, boundingBox, parent, ancestor);
    }

    public String getID() {
        return id;
    }

    public boolean isMirrored() { return true; }
}
