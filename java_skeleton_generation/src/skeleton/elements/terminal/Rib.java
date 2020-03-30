package skeleton.elements.terminal;

import skeleton.elements.joints.DummyJoint;
import skeleton.elements.nonterminal.NonTerminalElement;
import util.BoundingBox;
import util.TransformationMatrix;

import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;
import java.util.Optional;

public class Rib extends TerminalElement {

    private final String kind = "rib";
    private DummyJoint shoulderJoint;

    public Rib(TransformationMatrix transform, BoundingBox boundingBox, TerminalElement parent, NonTerminalElement ancestor) {
        super(transform, boundingBox, parent, ancestor);
        this.shoulderJoint = new DummyJoint(this, Rib.getJointPosition(boundingBox));
    }

    private Rib(TransformationMatrix transform, BoundingBox boundingBox, TerminalElement parent, NonTerminalElement ancestor, boolean isMirroredVersion) {
        super(transform, boundingBox, parent, ancestor);
        super.isMirroredVersion = isMirroredVersion;
        this.shoulderJoint = new DummyJoint(this, Rib.getJointPosition(boundingBox));
    }

    public String getKind() {
        return kind;
    }

    public DummyJoint getShoulderJoint() {
        return shoulderJoint;
    }

    public boolean canBeMirrored() {
        return true;
    }

    public Rib calculateMirroredElement(TerminalElement parent, Optional<TerminalElement> mirroredParent) {
        if (parent.canBeMirrored() && mirroredParent.isEmpty()) {
            System.err.println("Cannot mirror child when mirrored parent is not given!");
        }
        return new Rib(
                calculateMirroredTransform(parent),
                this.getBoundingBox().cloneBox(), // coordinate system is reflected so box must not be reflected!
                mirroredParent.orElse(parent), this.getAncestor(), true);
    }

    /**
     * @return the translation to move the joint between this element and its parent from this origin somewhere else.
     */
    public static Vector3f getLocalTranslationFromJoint(BoundingBox boundingBox) {
        return new Vector3f(-0.2f * boundingBox.getXLength(), -0.9f * boundingBox.getYLength(), -boundingBox.getZLength());
    }

    public static Point3f getJointPosition(BoundingBox boundingBox) {
        return new Point3f(0.1f * boundingBox.getXLength(), boundingBox.getYLength(), 0.3f * boundingBox.getZLength());
    }
}
