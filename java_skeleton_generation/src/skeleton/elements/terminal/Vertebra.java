package skeleton.elements.terminal;

import skeleton.elements.joints.SpineOrientedJoint;
import skeleton.elements.nonterminal.NonTerminalElement;
import util.BoundingBox;
import util.TransformationMatrix;

import java.util.Optional;

/**
 * Wirbel
 */
public class Vertebra extends TerminalElement {

    private final String kind = "vertebra";
    SpineOrientedJoint joint;

    public Vertebra(TransformationMatrix transform, BoundingBox boundingBox, TerminalElement parent, NonTerminalElement ancestor, SpineOrientedJoint joint) {
        super(transform, boundingBox, parent, ancestor);
        this.joint = joint;
    }

    public String getKind() {
        return kind;
    }

    public SpineOrientedJoint getJoint() {
        return joint;
    }

    public boolean isMirrored() { return false; }

    public Vertebra calculateMirroredElement(TerminalElement parent, Optional<TerminalElement> mirroredParent) {
        System.out.println("Tried to mirror an element that should not be mirrored!");
        return null;
    }
}
