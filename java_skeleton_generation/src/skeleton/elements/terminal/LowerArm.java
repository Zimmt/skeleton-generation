package skeleton.elements.terminal;

import skeleton.elements.joints.DummyJoint;
import skeleton.elements.nonterminal.NonTerminalElement;
import util.BoundingBox;
import util.TransformationMatrix;

import java.util.Optional;

public class LowerArm extends TerminalElement {

    private final String kind = "lower arm";
    private DummyJoint joint;

    public LowerArm(TransformationMatrix transform, BoundingBox boundingBox, TerminalElement parent, NonTerminalElement ancestor, DummyJoint joint) {
        super(transform, boundingBox, parent, ancestor);
        this.joint = joint;
    }

    public String getKind() {
        return kind;
    }

    public DummyJoint getJoint() {
        return joint;
    }

    public boolean isMirrored() { return true; }

    public LowerArm calculateMirroredElement(TerminalElement parent, Optional<TerminalElement> mirroredParent) {
        if (parent.isMirrored() && mirroredParent.isEmpty()) {
            System.err.println("Cannot mirror child when mirrored parent is not given!");
        }
        return new LowerArm(
                calculateMirroredTransform(parent),
                this.getBoundingBox().cloneBox(), // coordinate system is reflected so box must not be reflected!
                mirroredParent.orElse(parent), this.getAncestor(),
                joint.calculateMirroredJoint(parent, mirroredParent.orElse(parent)));
    }
}
