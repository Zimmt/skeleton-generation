package skeleton.elements.terminal;

import skeleton.elements.ExtremityKind;
import skeleton.elements.joints.arm.ShoulderJoint;
import skeleton.elements.nonterminal.NonTerminalElement;
import util.BoundingBox;
import util.TransformationMatrix;

import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;
import java.util.*;

public class Shoulder extends TerminalElement {

    private final String kind = "shoulder";
    private ShoulderJoint joint;
    private boolean onNeck;

    public Shoulder(TransformationMatrix transform, BoundingBox boundingBox, TerminalElement parent, NonTerminalElement ancestor,
                    ExtremityKind extremityKind, boolean onNeck) {
        super(transform, boundingBox, parent, ancestor);
        if (extremityKind != null) {
            this.joint = ShoulderJoint.newSpecificShoulderJoint(this, Shoulder.getJointPosition(boundingBox), extremityKind, onNeck);
        }
        this.onNeck = onNeck;
    }

    private Shoulder(TransformationMatrix transform, BoundingBox boundingBox, TerminalElement parent, NonTerminalElement ancestor,
                     boolean onNeck, boolean isMirroredVersion) {
        super(transform, boundingBox, parent, ancestor);
        super.isMirroredVersion = isMirroredVersion;
        this.onNeck = onNeck;
    }

    public String getKind() {
        return kind;
    }

    public ShoulderJoint getJoint() {
        return joint;
    }

    public boolean canBeMirrored() { return true; }

    public Shoulder calculateMirroredElement(TerminalElement parent, Optional<TerminalElement> mirroredParent) {
        if (parent.canBeMirrored() && mirroredParent.isEmpty()) {
            System.err.println("Cannot mirror child when mirrored parent is not given!");
        }
        return new Shoulder(
                calculateMirroredTransform(parent),
                this.getBoundingBox().cloneBox(), // coordinate system is reflected so box must not be reflected!
                mirroredParent.orElse(parent), this.getAncestor(), onNeck, true);
    }

    /**
     * @return the translation to move the joint between this element and its parent from this origin somewhere else.
     */
    public static Vector3f getLocalTranslationFromJoint(BoundingBox boundingBox) {
        return new Vector3f(-0.5f * boundingBox.getXLength(), -0.5f * boundingBox.getYLength(), -0.5f * boundingBox.getZLength());
    }

    /**
     * @return the relative position for the joint between this element and it's child
     */
    private static Point3f getJointPosition(BoundingBox childBoundingBox) {
        return new Point3f(0.9f * childBoundingBox.getXLength(), 0.2f * childBoundingBox.getYLength(), 0.5f * childBoundingBox.getZLength());
    }
}
