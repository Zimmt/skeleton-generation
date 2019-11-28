package skeleton.elements.nonterminal;

import skeleton.SkeletonGenerator;
import util.BoundingBox;
import util.TransformationMatrix;

public class WholeBody extends NonTerminalElement {

    private final String id = "whole body";

    // the transform here specifies the position in relation to the origin
    public WholeBody(TransformationMatrix transform, BoundingBox boundingBox, SkeletonGenerator generator) {
        super(transform, boundingBox, generator);
    }

    public String getID() {
        return id;
    }

    public boolean isMirrored() { return false; }
}
