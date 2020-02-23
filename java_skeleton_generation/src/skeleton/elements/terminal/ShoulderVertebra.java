package skeleton.elements.terminal;

import skeleton.elements.joints.DummyJoint;
import skeleton.elements.joints.SpineOrientedJoint;
import skeleton.elements.nonterminal.NonTerminalElement;
import util.BoundingBox;
import util.TransformationMatrix;

import java.util.Optional;

/**
 * Wirbel
 */
public class ShoulderVertebra extends Vertebra {

    private final String kind = "vertebra";
    DummyJoint shoulderJoint;

    public ShoulderVertebra(TransformationMatrix transform, BoundingBox boundingBox, TerminalElement parent,
                            NonTerminalElement ancestor, SpineOrientedJoint joint, DummyJoint shoulderJoint) {
        super(transform, boundingBox, parent, ancestor, joint);
        this.shoulderJoint = shoulderJoint;
    }

    public ShoulderVertebra(Vertebra vertebra, DummyJoint shoulderJoint) {
        super(vertebra.getTransform(), vertebra.getBoundingBox(), vertebra.getParent(), vertebra.getAncestor(), vertebra.getJoint());
        this.shoulderJoint = shoulderJoint;
    }

    public String getKind() {
        return kind;
    }

    public DummyJoint getShoulderJoint() {
        return shoulderJoint;
    }

    public boolean isMirrored() { return false; }

    public ShoulderVertebra calculateMirroredElement(TerminalElement parent, Optional<TerminalElement> mirroredParent) {
        System.out.println("Tried to mirror an element that should not be mirrored!");
        return null;
    }
}
