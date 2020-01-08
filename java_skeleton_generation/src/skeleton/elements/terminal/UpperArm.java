package skeleton.elements.terminal;

import skeleton.elements.nonterminal.NonTerminalElement;
import util.BoundingBox;
import util.TransformationMatrix;

import javax.vecmath.Point3f;

public class UpperArm extends TerminalElement {

    private final String kind = "upper arm";

    public UpperArm(TransformationMatrix transform, Point3f jointRotationPoint, BoundingBox boundingBox, TerminalElement parent, NonTerminalElement ancestor) {
        super(transform, jointRotationPoint, boundingBox, parent, ancestor);
    }

    public String getKind() {
        return kind;
    }

    public boolean isMirrored() { return true; }

    public UpperArm calculateMirroredElement(TerminalElement parent) {
        // transformation matrix and joint rotation point must not be changed if parent is mirrored
        if (parent.isMirrored()) {
            return new UpperArm(
                    new TransformationMatrix(this.getTransform()),
                    new Point3f(this.getJointRotationPoint()),
                    this.getBoundingBox().cloneBox(),
                    parent, this.getAncestor()
            );
        }
        return new UpperArm(
                calculateMirroredTransform(),
                calculateMirroredJointRotationPoint(),
                this.getBoundingBox().cloneBox(), // coordinate system is reflected so box must not be reflected!
                parent, this.getAncestor());
    }
}
