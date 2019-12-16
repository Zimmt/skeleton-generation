package skeleton.elements.nonterminal;

import skeleton.SkeletonGenerator;
import util.BoundingBox;
import util.TransformationMatrix;

public class WholeBody extends NonTerminalElement {

    private final String kind = "whole body";

    // the transform here specifies the position in relation to the world origin
    public WholeBody(TransformationMatrix transform, SkeletonGenerator generator) {
        super(transform, generator);
    }

    public String getKind() {
        return kind;
    }

    public boolean isMirrored() { return false; }
}
