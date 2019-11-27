package skeleton.elements.terminal;

import skeleton.elements.SkeletonPart;
import util.BoundingBox;
import util.TransformationMatrix;

/**
 * Wirbel
 */
public class Vertebra extends TerminalElement {

    private final String id = "vertebra";

    public Vertebra(TransformationMatrix transform, BoundingBox boundingBox, SkeletonPart parent, SkeletonPart ancestor) {
        super(transform, boundingBox, parent, ancestor);
    }

    public String getID() {
        return id;
    }

    public boolean isMirrored() { return false; }
}
