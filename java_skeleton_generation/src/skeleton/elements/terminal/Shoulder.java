package skeleton.elements.terminal;

import skeleton.elements.nonterminal.NonTerminalElement;
import util.BoundingBox;
import util.TransformationMatrix;

import javax.vecmath.Point3f;

public class Shoulder extends TerminalElement {

    private final String kind = "shoulder";

    public Shoulder(TransformationMatrix transform, Point3f jointRotationPoint, BoundingBox boundingBox, TerminalElement parent, NonTerminalElement ancestor) {
        super(transform, jointRotationPoint, boundingBox, parent, ancestor);
    }

    public String getKind() {
        return kind;
    }

    public boolean isMirrored() { return true; }

    public Shoulder calculateMirroredElement(TerminalElement parent) {
        return new Shoulder(
                calculateMirroredTransform(),
                calculateMirroredJointRotationPoint(),
                this.getBoundingBox().cloneBox(), // coordinate system is reflected so box must not be reflected!
                parent, this.getAncestor());
    }
}
