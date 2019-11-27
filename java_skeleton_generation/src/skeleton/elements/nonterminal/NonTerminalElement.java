package skeleton.elements.nonterminal;

import skeleton.elements.SkeletonPart;
import util.BoundingBox;
import util.TransformationMatrix;

public abstract class NonTerminalElement extends SkeletonPart {


    public NonTerminalElement(TransformationMatrix transform, BoundingBox boundingBox, SkeletonPart parent, SkeletonPart ancestor) {
        super(transform, boundingBox, parent, ancestor);
    }

    public boolean isTerminal() {
        return false;
    }
}
