package skeleton.elements.terminal;

import skeleton.SpinePart;
import skeleton.elements.joints.DummyJoint;
import skeleton.elements.joints.SpineOrientedJoint;
import skeleton.elements.nonterminal.NonTerminalElement;
import util.BoundingBox;
import util.TransformationMatrix;

import javax.vecmath.Point3f;
import java.util.Optional;

/**
 * Wirbel
 */
public class ShoulderVertebra extends Vertebra {

    private final String kind = "vertebra";
    DummyJoint shoulderJoint;

    public ShoulderVertebra(TransformationMatrix transform, BoundingBox boundingBox, TerminalElement parent, NonTerminalElement ancestor,
                            boolean positiveXDir, SpinePart spinePart, float jointSpinePosition, Point3f shoulderJointPosition) {
        super(transform, boundingBox, parent, ancestor, positiveXDir, spinePart, jointSpinePosition);
        this.shoulderJoint = new DummyJoint(this, shoulderJointPosition);
    }

    public ShoulderVertebra(Vertebra vertebra) {
        super(vertebra.getTransform(), vertebra.getBoundingBox(), vertebra.getParent(), vertebra.getAncestor(), vertebra.getJoint());
        this.shoulderJoint = new DummyJoint(this, ShoulderVertebra.getShoulderJointPosition(vertebra.getBoundingBox()));
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

    /**
     * @return the relative position for the joint between this element and it's child
     */
    private static Point3f getShoulderJointPosition(BoundingBox boundingBox) {
        return new Point3f(boundingBox.getXLength()/2f, 0f, 0f);
    }
}
