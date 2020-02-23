package skeleton.elements.terminal;

import skeleton.elements.joints.Joint;
import skeleton.elements.nonterminal.NonTerminalElement;
import util.BoundingBox;
import util.TransformationMatrix;

import java.util.Optional;

/**
 * Schienbein
 */
public class Shin extends TerminalElement {

    private final String kind = "shin";
    private Joint joint;

    public Shin(TransformationMatrix transform, BoundingBox boundingBox, TerminalElement parent, NonTerminalElement ancestor, Joint joint) {
        super(transform, boundingBox, parent, ancestor);
        this.joint = joint;
    }

    public String getKind() {
        return kind;
    }

    public Joint getJoint() {
        return joint;
    }

    public boolean isMirrored() { return true; }

    public Shin calculateMirroredElement(TerminalElement parent, Optional<TerminalElement> mirroredParent) {
        if (parent.isMirrored() && mirroredParent.isEmpty()) {
            System.err.println("Cannot mirror child when mirrored parent is not given!");
        }
        return new Shin(
                calculateMirroredTransform(parent),
                this.getBoundingBox().cloneBox(), // coordinate system is reflected so box must not be reflected!
                mirroredParent.orElse(parent), this.getAncestor(),
                joint.calculateMirroredJoint(parent, mirroredParent.orElse(parent)));
    }
}
