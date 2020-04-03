package skeleton.elements.terminal;

import skeleton.elements.ExtremityKind;
import skeleton.elements.joints.arm.WristJoint;
import skeleton.elements.nonterminal.NonTerminalElement;
import util.BoundingBox;
import util.TransformationMatrix;

import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;
import java.util.Optional;

/**
 * scaling:
 * x: same as upper arm x
 * y: determined by PCA data
 * z: same as x
 */
public class LowerArm extends TerminalElement {

    private final String kind = "lower_arm";
    private WristJoint joint;

    public LowerArm(TransformationMatrix transform, BoundingBox boundingBox, TerminalElement parent, NonTerminalElement ancestor, ExtremityKind extremityKind) {
        super(transform, boundingBox, parent, ancestor);
        this.joint = WristJoint.newSpecificWristJoint(this, LowerArm.getJointPosition(boundingBox), extremityKind);
    }

    private LowerArm(TransformationMatrix transform, BoundingBox boundingBox, TerminalElement parent, NonTerminalElement ancestor, ExtremityKind extremityKind, boolean isMirroredVersion) {
        super(transform, boundingBox, parent, ancestor);
        this.joint = WristJoint.newSpecificWristJoint(this, LowerArm.getJointPosition(boundingBox), extremityKind);
        super.isMirroredVersion = isMirroredVersion;
    }

    public String getKind() {
        return kind;
    }

    public WristJoint getJoint() {
        return joint;
    }

    public boolean canBeMirrored() { return true; }

    public LowerArm calculateMirroredElement(TerminalElement parent, Optional<TerminalElement> mirroredParent) {
        if (parent.canBeMirrored() && mirroredParent.isEmpty()) {
            System.err.println("Cannot mirror child when mirrored parent is not given!");
        }
        return new LowerArm(
                calculateMirroredTransform(parent),
                this.getBoundingBox().cloneBox(), // coordinate system is reflected so box must not be reflected!
                mirroredParent.orElse(parent), this.getAncestor(), joint.getExtremityKind(), true);
    }

    /**
     * @return the translation to move the joint between this element and its parent from this origin somewhere else.
     */
    public static Vector3f getLocalTranslationFromJoint(BoundingBox boundingBox) {
        return new Vector3f(-0.2f * boundingBox.getXLength(), -boundingBox.getYLength(), -boundingBox.getZLength()/2f);
    }

    /**
     * @return the relative position for the joint between this element and it's child
     */
    private static Point3f getJointPosition(BoundingBox boundingBox) {
        return new Point3f(1.2f * boundingBox.getXLength(), 0f, boundingBox.getZLength()/2f);
    }
}
