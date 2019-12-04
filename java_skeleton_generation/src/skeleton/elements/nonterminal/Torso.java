package skeleton.elements.nonterminal;

import skeleton.elements.SkeletonPart;
import util.BoundingBox;
import util.TransformationMatrix;

import javax.vecmath.Tuple2f;

public class Torso extends NonTerminalElement {

    private final String id = "torso";
    private Tuple2f spineInterval; // first and last parameter for the curve of the spine that lies inside of the torso

    // the transform here specifies the position in relation to the origin
    public Torso(Tuple2f spineInterval, TransformationMatrix transform, BoundingBox boundingBox, SkeletonPart ancestor) {
        super(transform, null, boundingBox, null, ancestor);

        this.spineInterval = spineInterval;
    }

    public String getID() {
        return id;
    }

    public Tuple2f getSpineInterval() {
        return spineInterval;
    }

    public float getSpineIntervalLength() {
        return spineInterval.y - spineInterval.x;
    }

    public boolean isMirrored() { return false; }
}
