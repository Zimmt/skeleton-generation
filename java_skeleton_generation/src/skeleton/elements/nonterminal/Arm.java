package skeleton.elements.nonterminal;

import skeleton.elements.SkeletonPart;
import util.TransformationMatrix;

import javax.vecmath.Point3f;

public class Arm extends NonTerminalElement {

    private final String kind = "arm";

    public Arm(TransformationMatrix transform, Point3f jointRotationPoint, SkeletonPart parent, SkeletonPart ancestor) {
        super(transform, jointRotationPoint, parent, ancestor);
    }

    public String getKind() {
        return kind;
    }

    public boolean isMirrored() { return true; }
}
