package skeleton.elements.terminal;

import skeleton.elements.joints.Joint;
import skeleton.elements.joints.SpineOrientedJoint;
import skeleton.elements.nonterminal.NonTerminalElement;
import util.BoundingBox;
import util.TransformationMatrix;

import java.util.Optional;

/**
 * Hüfte
 */
public class Pelvic extends TerminalElement {

    private final String kind = "pelvic";
    private SpineOrientedJoint tailJoint;
    private Joint legJoint;

    public Pelvic(TransformationMatrix transform, BoundingBox boundingBox, TerminalElement parent, NonTerminalElement ancestor, SpineOrientedJoint tailJoint, Joint legJoint) {
        super(transform, boundingBox, parent, ancestor);
        this.tailJoint = tailJoint;
        this.legJoint = legJoint;
    }

    public String getKind() {
        return kind;
    }

    public SpineOrientedJoint getTailJoint() {
        return tailJoint;
    }

    public Joint getLegJoint() {
        return legJoint;
    }

    public boolean isMirrored() { return false; }

    public Pelvic calculateMirroredElement(TerminalElement parent, Optional<TerminalElement> mirroredParent) {
        System.out.println("Tried to mirror an element that should not be mirrored!");
        return null;
    }
}
