package skeleton.elements.terminal;

import skeleton.elements.joints.SpineOrientedJoint;
import skeleton.elements.nonterminal.NonTerminalElement;
import util.BoundingBox;
import util.TransformationMatrix;

import java.util.Optional;

/**
 * Wirbel
 */
public class RootVertebra extends TerminalElement {

    private final String kind = "vertebra";
    SpineOrientedJoint frontPartJoint;
    SpineOrientedJoint backPartJoint;

    public RootVertebra(TransformationMatrix transform, BoundingBox boundingBox, TerminalElement parent, NonTerminalElement ancestor,
                        SpineOrientedJoint frontPartJoint, SpineOrientedJoint backPartJoint) {
        super(transform, boundingBox, parent, ancestor);
        this.frontPartJoint = frontPartJoint;
        this.backPartJoint = backPartJoint;
    }

    public String getKind() {
        return kind;
    }

    public SpineOrientedJoint getFrontPartJoint() {
        return frontPartJoint;
    }

    public SpineOrientedJoint getBackPartJoint() {
        return backPartJoint;
    }

    public boolean isMirrored() { return false; }

    public RootVertebra calculateMirroredElement(TerminalElement parent, Optional<TerminalElement> mirroredParent) {
        System.out.println("Tried to mirror an element that should not be mirrored!");
        return null;
    }
}
