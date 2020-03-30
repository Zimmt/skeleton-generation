package skeleton.elements.terminal;

import skeleton.elements.joints.DummyJoint;
import util.BoundingBox;

import javax.vecmath.Point3f;
import java.util.Optional;

/**
 * Wirbel
 */
public class ShoulderVertebra extends Vertebra {

    private String kind;
    DummyJoint shoulderJoint;

    public ShoulderVertebra(Vertebra vertebra) {
        super(vertebra.getTransform(), vertebra.getBoundingBox(), vertebra.getParent(), vertebra.getAncestor(), vertebra.getJoint());
        this.kind = vertebra.getKind();
        this.shoulderJoint = new DummyJoint(this, ShoulderVertebra.getShoulderJointPosition(vertebra.getBoundingBox()));
    }

    public String getKind() {
        return kind;
    }

    public DummyJoint getShoulderJoint() {
        return shoulderJoint;
    }

    public boolean canBeMirrored() { return false; }

    public ShoulderVertebra calculateMirroredElement(TerminalElement parent, Optional<TerminalElement> mirroredParent) {
        System.out.println("Tried to mirror an element that should not be mirrored!");
        return null;
    }

    /**
     * @return the relative position for the joint between this element and it's child
     */
    private static Point3f getShoulderJointPosition(BoundingBox boundingBox) {
        return new Point3f(boundingBox.getXLength()/2f, boundingBox.getYLength(), 0f);
    }
}
