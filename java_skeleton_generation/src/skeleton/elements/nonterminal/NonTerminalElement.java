package skeleton.elements.nonterminal;

import skeleton.SkeletonGenerator;
import skeleton.elements.SkeletonPart;
import util.BoundingBox;
import util.TransformationMatrix;

import javax.vecmath.Point3f;

public abstract class NonTerminalElement extends SkeletonPart {

    public NonTerminalElement(TransformationMatrix transform, BoundingBox boundingBox, SkeletonGenerator generator) {
        super(transform, boundingBox, generator);
    }

    public NonTerminalElement(TransformationMatrix transform, Point3f jointRotationPoint, BoundingBox boundingBox,
                              SkeletonPart parent, SkeletonPart ancestor) {
        super(transform, jointRotationPoint, boundingBox, parent, ancestor);
    }

    public boolean isTerminal() {
        return false;
    }
}
