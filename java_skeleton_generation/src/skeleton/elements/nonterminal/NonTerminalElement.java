package skeleton.elements.nonterminal;

import skeleton.SkeletonGenerator;
import skeleton.elements.SkeletonPart;
import util.TransformationMatrix;

import javax.vecmath.Point3f;

public abstract class NonTerminalElement extends SkeletonPart {

    public NonTerminalElement(TransformationMatrix transform, SkeletonGenerator generator) {
        super(transform, generator);
    }

    public NonTerminalElement(TransformationMatrix transform, Point3f jointRotationPoint,
                              SkeletonPart parent, SkeletonPart ancestor) {
        super(transform, jointRotationPoint, parent, ancestor);
    }

    public boolean isTerminal() {
        return false;
    }
}
