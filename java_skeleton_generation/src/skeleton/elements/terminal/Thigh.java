package skeleton.elements.terminal;

import skeleton.elements.ExtremityKind;
import skeleton.elements.joints.leg.KneeJoint;
import skeleton.elements.nonterminal.NonTerminalElement;
import util.BoundingBox;
import util.TransformationMatrix;

import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;
import java.util.Optional;

/**
 * Oberschenkel
 */
public class Thigh extends TerminalElement {

    private final String kind = "thigh";
    private KneeJoint joint;

    public Thigh(TransformationMatrix transform, BoundingBox boundingBox, TerminalElement parent, NonTerminalElement ancestor, ExtremityKind extremityKind) {
        super(transform, boundingBox, parent, ancestor);
        this.joint = KneeJoint.newSpecificKneeJoint(this, Thigh.getJointPosition(boundingBox), extremityKind);
    }

    private Thigh(TransformationMatrix transform, BoundingBox boundingBox, TerminalElement parent, NonTerminalElement ancestor, boolean isMirroredVersion) {
        super(transform, boundingBox, parent, ancestor);
        super.isMirroredVersion = isMirroredVersion;
    }

    public String getKind() {
        return kind;
    }

    public KneeJoint getJoint() {
        return joint;
    }

    public boolean canBeMirrored() { return true; }

    public Thigh calculateMirroredElement(TerminalElement parent, Optional<TerminalElement> mirroredParent) {
        if (parent.canBeMirrored() && mirroredParent.isEmpty()) {
            System.err.println("Cannot mirror child when mirrored parent is not given!");
        }
        return new Thigh(
                calculateMirroredTransform(parent),
                this.getBoundingBox().cloneBox(), // coordinate system is reflected so box must not be reflected!
                mirroredParent.orElse(parent), this.getAncestor(), true);
    }

    /**
     * @return the translation to move the joint between this element and its parent from this origin somewhere else.
     */
    public static Vector3f getLocalTranslationFromJoint(BoundingBox boundingBox) {
        return new Vector3f(-0.6f * boundingBox.getXLength(), -boundingBox.getYLength(), -0.4f * boundingBox.getZLength());
    }

    /**
     * @return the relative position for the joint between this element and it's child
     */
    public static Point3f getJointPosition(BoundingBox boundingBox) {
        return new Point3f(boundingBox.getXLength()/2f, 0f, boundingBox.getZLength()/2f);
    }
}
