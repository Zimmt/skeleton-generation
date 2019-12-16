package skeleton.elements.nonterminal;

import skeleton.SkeletonGenerator;
import skeleton.elements.SkeletonPart;
import skeleton.elements.terminal.TerminalElement;
import util.TransformationMatrix;

import javax.vecmath.Point3f;

public abstract class NonTerminalElement extends SkeletonPart {

    public NonTerminalElement(TransformationMatrix transform, SkeletonGenerator generator) {
        super(transform, generator);
    }

    public NonTerminalElement(TransformationMatrix transform, Point3f jointRotationPoint,
                              TerminalElement parent, NonTerminalElement ancestor) {
        super(transform, jointRotationPoint, parent, ancestor);
    }

    public boolean isTerminal() {
        return false;
    }
}
